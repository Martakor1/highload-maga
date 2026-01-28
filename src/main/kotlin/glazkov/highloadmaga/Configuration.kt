package glazkov.highloadmaga

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.stereotype.Component
import java.time.Duration

@Component
@EnableCaching
class Configuration {
    @Bean
    fun cacheConfiguration(): RedisCacheConfiguration {
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(60))
            .disableCachingNullValues()
//            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
//                GenericJackson2JsonRedisSerializer()
//            ));
    }
}