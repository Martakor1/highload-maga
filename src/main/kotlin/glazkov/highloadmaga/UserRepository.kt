package glazkov.highloadmaga
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*
@Repository
interface UserRepository : JpaRepository<UserEntity, UUID> {
    @Transactional(transactionManager = "masterTransactionManager", readOnly = true)
    fun findByUsername(username: String): UserEntity?

    /**
     * Heavy native query: joins with generate_series to force DB work before returning the user.
     * Works on PostgreSQL. `load` controls how heavy the join is (e.g. 10000).
     */
    @Query(
        value = """
            SELECT u.* FROM users u, generate_series(1, :load) g
            WHERE u.id = CAST(:id AS uuid)
            LIMIT 1
        """,
        nativeQuery = true
    )
    fun findByIdHeavy(@Param("id") id: UUID, @Param("load") load: Int): UserEntity?
}
