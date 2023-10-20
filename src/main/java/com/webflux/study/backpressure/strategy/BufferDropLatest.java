package com.webflux.study.backpressure.strategy;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;


/**
 * <h2>Buffer Drop Latest 전략</h2>
 * Unbounded request 일 경우, Downstream 에 Backpressure Buffer DROP_LATEST 전략을 적용하는 예제
 *  - Downstream 으로 전달 할 데이터가 버퍼에 가득 찰 경우,
 *    버퍼 안에 있는 데이터 중에서 가장 최근에(나중에) 버퍼로 들어온 데이터부터 Drop 시키고 확보된 공간에 emit 된 데이터를 채우는 전략
 */
@Slf4j
public class BufferDropLatest {
    public static void main(String[] args) throws InterruptedException {
        Flux
                .interval(Duration.ofMillis(300L))
                .doOnNext(data -> log.info("# emitted by original Flux: {}", data)) // source emit data 체크
                .onBackpressureBuffer(2, // buffer size
                        dropped -> log.info("** Overflow & Dropped: {} **", dropped), // overflow 시 drop 하는 아이템 체크
                        BufferOverflowStrategy.DROP_LATEST) // buffer 전략
                .doOnNext(data -> log.info("[ # emitted by Buffer: {} ]", data)) // source emit data 체크
                .publishOn(Schedulers.parallel(), false, 1)
                .subscribe(data -> { // onNext signal
                            try {
                                Thread.sleep(1000L);
                            } catch (InterruptedException e) {}
                            log.info("# onNext: {}", data);
                        },
                        error -> log.error("# onError", error)); // onError Signal

        Thread.sleep(2500L);
    }
}
