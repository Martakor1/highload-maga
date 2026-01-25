package glazkov.highloadmaga

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.lang.Thread.sleep
import java.util.*
import kotlin.collections.ArrayList

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
        val start = System.nanoTime()
        val memoryBuffer = ArrayList<Int>(1)
        val appendix: ArrayList<Int> = ArrayList((0..1024 * 256).toList()) //1MB
        for (i in 0 until 10) {
            memoryBuffer.addAll(appendix)
        } //10MB
        sleep(1000)
        val end = System.nanoTime()
        return "Hello! The size of elements: " + memoryBuffer.size * 4 / 1024 / 1024 + " MB. Time taken: " + (end - start)/1_000_000 + " ms"
    }
}