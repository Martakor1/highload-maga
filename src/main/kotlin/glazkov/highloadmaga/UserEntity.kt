package glazkov.highloadmaga
import jakarta.persistence.*
import org.hibernate.annotations.Type
import java.io.Serializable
import java.util.*

@Entity
@Table(name = "users")
class UserEntity(
    @Column(nullable = false, unique = false, columnDefinition = "TEXT", length = 1000000000) //user_be70a0db
    val username: String,
    @Column(nullable = false)
    val password: String,
    @Column(nullable = false, columnDefinition = "text", length = 1000000000)
    val name: String,
    @Column(nullable = false)
    val age: Int,
    @Id
    @GeneratedValue
    val id: UUID? = null
) : Serializable {
    constructor() : this("default_username", "default_password", "default_name", 10, null)
}
