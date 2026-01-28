package glazkov.highloadmaga

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.lang.Thread.sleep
//import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserService(private val userRepository: UserRepository) {

    //@Transactional(readOnly = true)
    @Cacheable("getUser")
    fun getUser(id: UUID, load: Int): UserEntity? {
        //sleep(1000);  // check cache working WORKS!
        return userRepository.findByIdHeavy(id, load);

    }

    fun getUserNoCache(id: UUID, load: Int): UserEntity? {
        return userRepository.findByIdHeavy(id, load);
    }


    //@Transactional(readOnly = true)
    fun getAll(): List<UserEntity> = userRepository.findAll()

    //@Transactional
    fun create(dto: CreateUserRequestDto): UserEntity {
        val entity = UserEntity(
            username = dto.username,
            password = dto.password,
            name = dto.name,
            age = dto.age
        )
        return userRepository.save(entity)
    }

    fun deleteUser(id: UUID) {
        userRepository.deleteById(id)
    }
}

// DTO for service input
data class CreateUserRequestDto(val username: String, val password: String, val name: String, val age: Int)
