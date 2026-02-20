package glazkov.highloadmaga

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
//import springfox.documentation.swagger2.annotations.EnableSwagger2

@SpringBootApplication
//@EnableSwagger2
class HighloadMagaApplication

fun main(args: Array<String>) {
    runApplication<HighloadMagaApplication>(*args)
}
