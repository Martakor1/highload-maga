package glazkov.highloadmaga

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.jpa.EntityManagerFactoryBuilder
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import java.time.Duration
import javax.sql.DataSource


@Configuration
@EnableCaching
class RedisCacheConfiguration {
    @Bean
    fun cacheConfiguration(): RedisCacheConfiguration {
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(60))
            .disableCachingNullValues()
    }
}

@Configuration
@EnableJpaRepositories(
    basePackages = ["glazkov.highloadmaga"],
    entityManagerFactoryRef = "masterEntityManagerFactory",
    transactionManagerRef = "masterTransactionManager"
)
class MasterDataSourceConfiguration {

    @Primary
    @Bean(name = ["masterDataSource"])
    @ConfigurationProperties("spring.datasource.master")
    fun masterDataSource(): DataSource {
        return DataSourceBuilder.create().build()
    }

    @Primary
    @Bean(name = ["masterEntityManagerFactory"])
    fun masterEntityManagerFactory(
        builder: EntityManagerFactoryBuilder,
        @Qualifier("masterDataSource") dataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean {
        return builder
            .dataSource(dataSource)
            .packages("glazkov.highloadmaga")
            .persistenceUnit("master")
            .build()
    }

    @Primary
    @Bean(name = ["masterTransactionManager"])
    fun masterTransactionManager(
        @Qualifier("masterEntityManagerFactory") entityManagerFactory: LocalContainerEntityManagerFactoryBean
    ): PlatformTransactionManager {
        return JpaTransactionManager(entityManagerFactory.`object`!!)
    }
}

@Configuration
class ReplicaDataSourceConfiguration {

    @Bean(name = ["replicaDataSource"])
    @ConfigurationProperties("spring.datasource.slave")
    fun replicaDataSource(): DataSource {
        return DataSourceBuilder.create().build()
    }

    @Bean(name = ["replicaEntityManagerFactory"])
    fun replicaEntityManagerFactory(
        builder: EntityManagerFactoryBuilder,
        @Qualifier("replicaDataSource") dataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean {
        return builder
            .dataSource(dataSource)
            .packages("glazkov.highloadmaga")
            .persistenceUnit("replica")
            .build()
    }

    @Bean(name = ["replicaTransactionManager"])
    fun replicaTransactionManager(
        @Qualifier("replicaEntityManagerFactory") entityManagerFactory: LocalContainerEntityManagerFactoryBean
    ): PlatformTransactionManager {
        return JpaTransactionManager(entityManagerFactory.`object`!!)
    }
}