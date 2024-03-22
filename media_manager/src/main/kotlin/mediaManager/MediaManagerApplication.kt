package mediaManager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MediaManagerApplication

fun main(args: Array<String>) {
    runApplication<MediaManagerApplication>(*args)
}
