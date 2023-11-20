package com.webflux.study.operator.time;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.net.URI;
import java.time.Duration;
import java.util.Collections;

/**
 * <h2>elapsed()</h2>
 * <p>
 *     emit된 데이터 사이의 경과 시간을 측정해서 Tuple<Long, T> 형태로 Downstream에 emit함.
 * </p>
 * <p>
 *     emit되는 첫 번째 데이터는 onSubscribe Signal과 첫 번째 데이터 사이를 기준으로 시간을 측정함. 측정된 시간의 단위는 millis 임.
 * </p>
 */
@Slf4j
public class Elapsed {
    public static void main(String[] args) throws InterruptedException {
        example1();
        log.info("===========================");
        example2();
    }

    static void example1() throws InterruptedException {
        Flux
                .range(1, 5)
                .delayElements(Duration.ofSeconds(1))
                .elapsed()
                .subscribe(data -> log.info("# onNext: {}, time: {}", data.getT2(), data.getT1()));
        Thread.sleep(6000);
    }

    static void example2() {
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


        Mono.defer(() -> Mono.just(
                                restTemplate
                                        .exchange(worldTimeUri,
                                                HttpMethod.GET,
                                                new HttpEntity<String>(headers),
                                                String.class)
                        )
                )
                .repeat(4) // 구독을 4회 반복하여 Http request를 반복적으로 전송하고 있는데, 최초 구독 + repeat() Operator 파라미터 숫자만큼의 HTTP request를 전송함.
                .elapsed()
                .map(response -> {
                    DocumentContext jsonContext =
                            JsonPath.parse(response.getT2().getBody());
                    String dateTime = jsonContext.read("$.datetime");
                    return Tuples.of(dateTime, response.getT1());
                })
                .subscribe(
                        data -> log.info("now: {}, elapsed time: {}", data.getT1(), data.getT2()),
                        error -> log.error("# onError:", error),
                        () -> log.info("# onComplete")
                );
    }
}
