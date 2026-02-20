package glazkov.highloadmaga

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import jakarta.persistence.EntityManager

@Service
class ReplicaUserService(
    @Qualifier("replicaEntityManagerFactory") 
    private val entityManager: EntityManager
) {

    @Transactional(transactionManager = "replicaTransactionManager", readOnly = true)
    fun getAll(limit: Int): List<UserEntity> {
        return entityManager
            .createQuery("SELECT u FROM UserEntity u", UserEntity::class.java)
            .setMaxResults(limit)
            .resultList
    }

    @Transactional(transactionManager = "replicaTransactionManager", readOnly = true)
    fun getUserById(id: UUID): UserEntity? {
        return entityManager.find(UserEntity::class.java, id)
    }

    @Transactional(transactionManager = "replicaTransactionManager", readOnly = true)
    fun getUserByIdHeavy(id: UUID, load: Int): UserEntity? {
        // Используем нативный запрос как в UserRepository с generate_series для нагрузки
        val query = entityManager.createNativeQuery(
            """
            SELECT u.* FROM users u, generate_series(1, :load) g
            WHERE u.id = CAST(:id AS uuid)
            LIMIT 1
            """, 
            UserEntity::class.java
        )
        query.setParameter("id", id)
        query.setParameter("load", load)
        
        return try {
            query.singleResult as UserEntity
        } catch (e: Exception) {
            null
        }
    }
}
