package space.pankui.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

/**
 * @author pankui
 * @date 13/05/2018
 * <pre>
 *
 * </pre>
 */


@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedisReactiveDemo {

    @Id
    private String id;
    private String title;
    private String content;

    @CreatedDate
    private LocalDateTime createdDate;
}
