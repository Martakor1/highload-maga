package glazkov.highloadmaga
import jakarta.persistence.*
import java.io.Serializable
import java.util.*

@Entity
@Table(name = "users")
class UserEntity(
    @Column(nullable = false, unique = true)
    val username: String,
    @Column(nullable = false)
    val password: String,
    @Column(nullable = false)
    val name: String,
    @Column(nullable = false)
    val age: Int,
    @Id
    @GeneratedValue
    val id: UUID? = null
) : Serializable {
    constructor() : this("default_username", "default_password", "default_name", 10, null)
}
