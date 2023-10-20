package com.webflux.study.backpressure.strategy;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
/**
 * <h2>Latest 전략</h2>
 * Unbounded request 일 경우, Downstream 에 Backpressure Latest 전략을 적용하는 예제
 * <br>
 *  - Downstream 으로 전달 할 데이터가 버퍼에 가득 찰 경우,
 * 버퍼 밖에서 대기하는 <stronger>가장 나중에(최근에) emit 된 데이터</stronger>부터 버퍼에 채우는 전략
 * 이 때 최근 데이터를 제외한 앞에 쌓여있던 데이터는 폐기함.
 */
@Slf4j
public class Latest {
    public static void main(String[] args) throws InterruptedException {
        Flux
                .interval(Duration.ofMillis(1L))
                .onBackpressureLatest()
                .publishOn(Schedulers.parallel())
                .subscribe(data -> {
                            try {
                                Thread.sleep(5L);
                            } catch (InterruptedException e) {}
                            log.info("# onNext: {}", data);
                        },
                        error -> log.error("# onError", error));

        Thread.sleep(2000L);
    }
}
