package space.pankui;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.actuate.logging.LoggersEndpoint.LoggerLevels;

import java.util.Map;

/**
 * @author pankui
 * @date 15/05/2018
 * <pre>
 *  创建用户自定义的端点
 * </pre>
 */

//@Endpoint(id = "loggers")
public class LoggersEndpoint {

   /* @ReadOperation
    public Map<String, Object> loggers() {
        return null;
    }

    @ReadOperation
    public LoggerLevels loggerLevels(@Selector String name) {
        return null;
    }

    @WriteOperation
    public void configureLogLevel(@Selector String name, LogLevel configuredLevel) {

    }*/

}
