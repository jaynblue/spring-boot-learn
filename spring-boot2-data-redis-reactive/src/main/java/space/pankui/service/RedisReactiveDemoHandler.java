package space.pankui.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import space.pankui.dao.RedisReactiveDemoRepository;
import space.pankui.model.RedisReactiveDemo;


import java.net.URI;

/**
 * @author pankui
 * @date 13/05/2018
 * <pre>
 *
 * </pre>
 */

@Component
public class RedisReactiveDemoHandler {

    @Autowired
    private  RedisReactiveDemoRepository posts;


    public Mono<ServerResponse> all(ServerRequest req) {
        return ServerResponse.ok().body(this.posts.findAll(), RedisReactiveDemo.class);
    }

    public Mono<ServerResponse> create(ServerRequest req) {
        return req.bodyToMono(RedisReactiveDemo.class)
                .flatMap(post -> this.posts.save(post))
                .flatMap(p -> ServerResponse.created(URI.create("/posts/" + p.getId())).build());
    }

    public Mono<ServerResponse> get(ServerRequest req) {
        return this.posts.findById(req.pathVariable("id"))
                .flatMap(post -> ServerResponse.ok().body(Mono.just(post), RedisReactiveDemo.class))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> update(ServerRequest req) {

        return Mono
                .zip(
                        (data) -> {
                            RedisReactiveDemo p = (RedisReactiveDemo) data[0];
                            RedisReactiveDemo p2 = (RedisReactiveDemo) data[1];
                            p.setTitle(p2.getTitle());
                            p.setContent(p2.getContent());
                            return p;
                        },
                        this.posts.findById(req.pathVariable("id")),
                        req.bodyToMono(RedisReactiveDemo.class)
                )
                .cast(RedisReactiveDemo.class)
                .flatMap(post -> this.posts.save(post))
                .flatMap(post -> ServerResponse.noContent().build());

    }

    public Mono<ServerResponse> delete(ServerRequest req) {
        return ServerResponse.noContent().build(this.posts.deleteById(req.pathVariable("id")));
    }

}
