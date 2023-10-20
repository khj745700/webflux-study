package com.webflux.study.sequence.hot;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
public class HotExample {
    public static void main(String[] args) throws InterruptedException {
        URI worldTimeUri = UriComponentsBuilder.newInstance().scheme("http")
                .host("worldtimeapi.org")
                .port(80)
                .path("/api/timezone/Asia/Seoul")
                .build()
                .encode()
                .toUri();

        Mono<String> mono = getWorldTime(worldTimeUri).cache(); // cache() : Hot Sequence 로 변경해주고 emit 된 데이터를 캐싱한 뒤, 구독이 발생할 때마다 캐시된 데이터를 전달함.
        mono.subscribe(dateTime -> log.info("# dateTime 1: {}", dateTime));
        Thread.sleep(2000);
        mono.subscribe(dateTime -> log.info("# dateTime 2: {}", dateTime));
        Thread.sleep(2000);
    }

    private static Mono<String> getWorldTime(URI worldTimeUri) {
        return WebClient.create()
                .get()
                .uri(worldTimeUri)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    DocumentContext jsonContext = JsonPath.parse(response);
                    String dateTime = jsonContext.read("$.datetime");
                    return dateTime;
                });
    }
}
