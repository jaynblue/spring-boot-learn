package space.pankui

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * @author pankui
 * @date 14/05/2018
 * <pre>
 *
 * </pre>
 */

// 注意这里设置 open 否则启动错
@SpringBootApplication
open class KotlinDemoApplication

fun main(args: Array<String>) {

    runApplication<KotlinDemoApplication>(*args)
}