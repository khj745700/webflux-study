package com.webflux.study.Scheduler;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * <h2>publishOn()</h2>
 * <p>
 *     publishOn() Operator는 Signal을 전송할 때 실행되는 스레드를 제어하는 역할을 하는 Operator
 * </p>
 *
 * <p>
 *     publishOn() Operator는 코드 상에서 <strong>publishOn()을 기준으로 아래쪽인 Downstream 의 실행 스레드를 변경함.</strong>
 * </p>
 *
 */
@Slf4j
public class PublishOn {
    public static void main(String[] args) throws InterruptedException {
        Flux.fromArray(new Integer[] {1, 3, 5, 7})
                .doOnNext(data -> log.info("# doOnNext: {}", data))
                .doOnSubscribe(subscription -> log.info("# doOnSubscribe"))
                .publishOn(Schedulers.parallel())
                .subscribe(data -> log.info("# onNext: {}", data));

        Thread.sleep(500L);
    }
}
