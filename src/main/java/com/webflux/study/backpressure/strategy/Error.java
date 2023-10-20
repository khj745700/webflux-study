package com.webflux.study.backpressure.strategy;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

/**
 * <h2>ERROR 전략</h2>
 * Backpressure <strong>ERROR</strong> 전략은 Downstream 의 데이터 처리 속도가 느려서 Upstream 의 emit 속도를 따라가지 못할 경우 에러를 발생시키는 전략임.
 * <br>
 * Publisher 는 Error Signal 을 Subscriber 에게 전송하고 삭제한 데이터는 폐기.
 */
@Slf4j
public class Error {
    public static void main(String[] args) throws InterruptedException {
        Flux
                .interval(Duration.ofMillis(1L)) // 0 부터 1씩 증가한 숫자를 0.001초에 한 번씩 아주 빠른 속도로 emit 하도록 정의
                .onBackpressureError() // ERROR 전략을 위한 Operator
                .doOnNext(data -> log.info("# doOnNext: {}", data)) // doOnNext Operator 는 Publisher 가 emit 한 데이터를 확인하거나 추가적인 동작을 정의하는 용도
                .publishOn(Schedulers.parallel()) // 별도의 스레드가 하나 더 실행된다고 생각.
                .subscribe(data -> {
                            try {
                                Thread.sleep(5L); // Backpressure 처리를 위해 고의로 속도 조절
                            } catch (InterruptedException e) {}
                            log.info("# onNext: {}", data);
                        },
                        error -> log.error("# onError", error));

        Thread.sleep(2000L);
    }
}
