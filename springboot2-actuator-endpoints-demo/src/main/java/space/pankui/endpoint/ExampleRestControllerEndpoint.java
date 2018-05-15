package space.pankui.endpoint;

import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author pankui
 * @date 15/05/2018
 * <pre>
 *
 * </pre>
 */
@Component
@RestControllerEndpoint(id = "example")
public class ExampleRestControllerEndpoint {

    @GetMapping("/echo")
    public ResponseEntity<String> echo(@RequestParam("text") String text) {
        return ResponseEntity.ok().header("echo", text).body(text);
    }


}
