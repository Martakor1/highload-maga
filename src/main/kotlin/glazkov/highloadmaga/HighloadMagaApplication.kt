package glazkov.highloadmaga

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HighloadMagaApplication

fun main(args: Array<String>) {
    runApplication<HighloadMagaApplication>(*args)
}
