package com.webflux.study.Scheduler;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * <h2>publishOn() 과 subscribeOn() 동작 이해</h2>
 * <hr style='margin-top:-10'>
 * <ul style='margin-top:0'>
 *     <li>publishOn() Operator 는 한 개 이상 사용할 수 있으며, <strong>실행 스레드를 목적에 맞게 적절하게 분리할 수 있다.</strong></li>
 *     <li>subscribeOn() Operator 와 publishOn() Operator를 함께 사용해서 원본 Publisher 에서 데이터를 emit 하는 스레드와
 *     emit된 데이터를 가공 처리하는 스레드를 적절하게 분리할 수 있음.</li>
 *     <li>subscribeOn()은 Operator 체인상에서 어떤 위치에 있던 간에 구독 시점 직후, 즉 <strong>Publisher가 데이터를 emit 하기전에 실행 스레드를 변경한다.</strong></li>
 * </ul>
 *
 */
@Slf4j
public class PublishOnWithSubscribeOn {
    public static void main(String[] args) throws InterruptedException {
        /**
         * <h3>publishOn() 과 subscribeOn()을 사용하지 않을 경우</h3>
         * Operator 체인의 각 단계별로 실행되는 스레드를 확인하기 위해 doOnNext() Operator를 사용
         *
         * <p>
         *     모두 main 스레드에서 동작함을 확인할 수 있음.
         * </p>
         *
         */
        Flux
                .fromArray(new Integer[] {1, 3, 5, 7})
                .doOnNext(data -> log.info("# doOnNext fromArray: {}", data))
                .filter(data -> data > 3)
                .doOnNext(data -> log.info("# doOnNext filter: {}", data))
                .map(data -> data * 10)
                .doOnNext(data -> log.info("# doOnNext map: {}", data))
                .subscribe(data -> log.info("# onNext: {}", data));



        log.info("publishOn() 1개 -------------------------------------------");


        /**
         * <h3>publishOn()을 사용할 경우</h3>
         * <p>
         *     Operator 체인에 publishOn() 에서 지정한 Scheduler 유형의 스레드가 실행됨.
         *     publishOn() 이후의 스레드는 모두 parallel-1임.
         * </p>
         */
        Flux
                .fromArray(new Integer[] {1, 3, 5, 7})
                .doOnNext(data -> log.info("# doOnNext fromArray: {}", data))
                .publishOn(Schedulers.parallel())
                .filter(data -> data > 3)
                .doOnNext(data -> log.info("# doOnNext filter: {}", data))
                .map(data -> data * 10)
                .doOnNext(data -> log.info("# doOnNext map: {}", data))
                .subscribe(data -> log.info("# onNext: {}", data));

        Thread.sleep(500L);

        log.info("publishOn() 2개 -------------------------------------------");


        /**
         * <h3>publishOn()을 두 개 사용할 경우</h3>
         * <p>
         *     Operator 체인에 publishOn() 에서 지정한 Scheduler 유형의 스레드가 실행됨.
         *     1번 publishOn() 이후의 스레드는 parallel-3임.
         *     2번 publishOn() 이후의 스레드는 parallel-2임.
         * </p>
         */
        Flux
                .fromArray(new Integer[] {1, 3, 5, 7})
                .doOnNext(data -> log.info("# doOnNext fromArray: {}", data))
                .publishOn(Schedulers.parallel())
                .filter(data -> data > 3)
                .doOnNext(data -> log.info("# doOnNext filter: {}", data))
                .publishOn(Schedulers.parallel())
                .map(data -> data * 10)
                .doOnNext(data -> log.info("# doOnNext map: {}", data))
                .subscribe(data -> log.info("# onNext: {}", data));

        Thread.sleep(500L);

        log.info("subscribeOn() 이후 publishOn() -------------------------------------------");


        /**
         * <h3>subscribeOn() 이후 publishOn() 을 사용할 경우</h3>
         * <p>
         *     Operator 체인에 publishOn() 에서 지정한 Scheduler 유형의 스레드가 실행됨.
         *     subscribeOn() 스레드는 첫 구독할 때 지정한 boundedElastic-1에 발생.
         *     2번 publishOn() 이후의 스레드는 parallel-4임.
         * </p>
         */
        Flux
                .fromArray(new Integer[] {1, 3, 5, 7})
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(data -> log.info("# doOnNext fromArray: {}", data))
                .filter(data -> data > 3)
                .doOnNext(data -> log.info("# doOnNext filter: {}", data))
                .publishOn(Schedulers.parallel())
                .map(data -> data * 10)
                .doOnNext(data -> log.info("# doOnNext map: {}", data))
                .subscribe(data -> log.info("# onNext: {}", data));

        Thread.sleep(500L);
    }
}
