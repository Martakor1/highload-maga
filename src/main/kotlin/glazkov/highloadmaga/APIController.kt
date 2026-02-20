package glazkov.highloadmaga

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.lang.Thread.sleep
import java.util.*

@RestController
class APIController(
    private val userService: UserService,
    private val replicaUserService: ReplicaUserService
) {

    val logger: Logger = LoggerFactory.getLogger(APIController::class.java)

    @PostMapping("/users")
    fun createUser(@RequestBody req: CreateUserRequest): ResponseEntity<UserResponse> {
        val dto = CreateUserRequestDto(req.username, req.password, req.name, req.age)
        val created = userService.create(dto)
        return ResponseEntity.ok(UserResponse(created.id, created.username, created.name, created.age))
    }

    @PostMapping("/users/random")
    fun createUserRandom(): ResponseEntity<UserResponse> {
        val username = "user_" + UUID.randomUUID().toString().replace("-", "").take(8)
        val password = UUID.randomUUID().toString()
        val names = listOf("Иван", "Мария", "Алексей", "Елена", "Дмитрий", "Ольга", "Сергей", "Анна")
        val name = names.random()
        val age = (18..80).random()
        val dto = CreateUserRequestDto(username, password, name, age)
        val created = userService.create(dto)
        return ResponseEntity.ok(UserResponse(created.id, created.username, created.name, created.age))
    }

    @PostMapping("/users/random_heavy")
    fun createUserRandomHeavy(): ResponseEntity<UserResponse> {
        val username = "user_" + UUID.randomUUID().toString().replace("-", "").take(8)
        val password = UUID.randomUUID().toString()
        val hugeName = (1.. 100 * 1024 * 256).map { "Иван" }.joinToString("") //~200MB name
        val age = (18..80).random()
        val dto = CreateUserRequestDto(username, password, hugeName, age)
        val created = userService.create(dto)
        return ResponseEntity.ok(UserResponse(created.id, created.username, created.name, created.age))
    }

    @PostMapping("/users/random/delete")
    fun deleteUserRandom(@RequestParam limit: Int): ResponseEntity<String> {
        val allUsers = userService.getAll(limit)
        if (allUsers.isEmpty()) {
            return ResponseEntity.ok("No users to delete")
        }
        val userToDelete = allUsers.random()
        userService.deleteUser(userToDelete.id!!)
        return ResponseEntity.ok("Deleted user with id: ${userToDelete.id}")
    }

    @GetMapping("/users")
    fun listUsers(@RequestParam limit: Int): ResponseEntity<List<UserResponse>> {
        val list = userService.getAll(limit).map { UserResponse(it.id, it.username, it.name, it.age) }
        return ResponseEntity.ok(list)
    }

    @GetMapping("/replica/users")
    fun listUsersFromReplica(@RequestParam limit: Int): ResponseEntity<List<UserResponse>> {
        val list = replicaUserService.getAll(limit).map { UserResponse(it.id, it.username, it.name, it.age) }
        return ResponseEntity.ok(list)
    }

    @GetMapping("/users/{id}/{load}")
    fun getUser(@PathVariable id: UUID, @PathVariable load: Int): ResponseEntity<UserResponse> {
        val user = userService.getUser(id, load) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(UserResponse(user.id, user.username, user.name, user.age))
    }

    @GetMapping("/replica/users/{id}/{load}")
    fun getUserFromReplica(@PathVariable id: UUID, @PathVariable load: Int): ResponseEntity<UserResponse> {
        val user = replicaUserService.getUserByIdHeavy(id, load) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(UserResponse(user.id, user.username, user.name, user.age))
    }

    @GetMapping("/users/nocache/{id}/{load}")
    fun getUserNoCache(@PathVariable id: UUID, @PathVariable load: Int): ResponseEntity<UserResponse> {
        val user = userService.getUserNoCache(id, load) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(UserResponse(user.id, user.username, user.name, user.age))
    }

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

    data class CreateUserRequest(val username: String, val password: String, val name: String, val age: Int)
    data class UserResponse(val id: UUID?, val username: String, val name: String, val age: Int)
}