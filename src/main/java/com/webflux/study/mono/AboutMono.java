package com.webflux.study.mono;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Collections;

public class AboutMono {
    public static void main(String[] args) {
        Mono.just("Hello Reactor") // Mono는 0 또는 1개의 데이터를 emit 하는 Publisher -> RxJava 에서는 Maybe + Single 이라 생각하면 됨.
                .subscribe(System.out::println);

        Mono
                .empty()
                .subscribe(
                        /**
                         *  subscribe의 람다 표현식 파라미터 에서
                         *  1번 람다식은 onNext Signal을 전송하면 실행됨.
                         *  2번 람다식은 onError Signal을 전송하면 실행됨.
                         *  3번 람다식은 onComplete Signal을 전송하면 실행됨.
                         */
                        none -> System.out.println("# emitted onNext signal"), // 데이터를 전달 받을 것이 없으므로,
                        error -> {},
                        () -> System.out.println("# emitted onComplete signal") // onComplete signal이 가는 것임.
                );


        URI worldTimeUri = UriComponentsBuilder.newInstance().scheme("http")
                .host("worldtimeapi.org")
                .port(80)
                .path("/api/timezone/Asia/Seoul")
                .build()
                .encode()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));


        Mono.just(
                        /**
                         * Non Blocking 방식의 I/O는 아니기에 이점은 얻을 수 없으나,
                         * Mono 를 사용하여 Http 요청/응답 처리하면 하나의 Operator 체인으로 처리 가능
                         * DownStream 에 큰 영향이 없다는 소리.
                         */
                        restTemplate
                                .exchange(worldTimeUri,
                                        HttpMethod.GET,
                                        new HttpEntity<String>(headers),
                                        String.class)
                )
                .map(response -> {
                    DocumentContext jsonContext = JsonPath.parse(response.getBody());
                    String dateTime = jsonContext.read("$.datetime");
                    return dateTime;
                })
                .subscribe(
                        data -> System.out.println("# emitted data: " + data),
                        error -> {
                            System.out.println(error);
                        },
                        () -> System.out.println("# emitted onComplete signal")
                );

    }
}
