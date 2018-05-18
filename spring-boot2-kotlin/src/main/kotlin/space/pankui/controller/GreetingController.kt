package space.pankui.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import space.pankui.entity.Greeting
import java.util.concurrent.atomic.AtomicLong

/**
 * @author pankui
 * @date 14/05/2018
 * <pre>
 * http://localhost:8080/hello
 * </pre>
 */

@RestController
class GreetingController {

    val counter = AtomicLong()

    @GetMapping("/hello")
    fun greeting(@RequestParam(value = "name", defaultValue = "hello World") name: String) =
            Greeting(counter.incrementAndGet(), "Hello, $name")

}