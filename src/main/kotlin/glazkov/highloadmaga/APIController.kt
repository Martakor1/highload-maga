package glazkov.highloadmaga

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class APIController {

    val logger: Logger = LoggerFactory.getLogger(APIController::class.java)

    @PostMapping("/users")
    fun createUser(@RequestBody req: CreateUserRequest): User {
        return User(UUID.randomUUID(), req.name)
    }

    data class CreateUserRequest(val name: String, val password: String)

    data class User(val id: UUID, val name: String)

    @GetMapping("/hello")
    fun createOrder(): String {
        return "Hello!"
    }
}