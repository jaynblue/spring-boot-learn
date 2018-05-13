package space.pankui.dao;

import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import space.pankui.model.RedisReactiveDemo;

import java.util.UUID;

/**
 * @author pankui
 * @date 13/05/2018
 * <pre>
 *
 * </pre>
 */

@Component
public class RedisReactiveDemoRepository {

    ReactiveRedisOperations<String, RedisReactiveDemo> template;

    public RedisReactiveDemoRepository(ReactiveRedisOperations<String, RedisReactiveDemo> template) {
        this.template = template;
    }

    public Flux<RedisReactiveDemo> findAll() {
        return template.<String, RedisReactiveDemo>opsForHash().values("posts");
    }

    public Mono<RedisReactiveDemo> findById(String id) {
        return template.<String, RedisReactiveDemo>opsForHash().get("posts", id);
    }

    public Mono<RedisReactiveDemo> save(RedisReactiveDemo post) {
        if (post.getId() != null) {
            String id = UUID.randomUUID().toString();
            post.setId(id);
        }
        return template.<String, RedisReactiveDemo>opsForHash().put("posts", post.getId(), post)
                .log()
                .map(p -> post);

    }

    public Mono<Void> deleteById(String id) {
        return template.<String, RedisReactiveDemo>opsForHash().remove("posts", id)
                .flatMap(p -> Mono.<Void>empty());
    }

    public Mono<Boolean> deleteAll() {
        return template.<String, RedisReactiveDemo>opsForHash().delete("posts");
    }
}
