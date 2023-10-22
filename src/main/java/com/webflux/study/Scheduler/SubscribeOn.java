package com.webflux.study.Scheduler;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * <h2>subscribeOn()</h2>
 * subscribeOn() Operator 는 구독이 발생한 직후 실행될 스레드를 지정하는 Operator <br>
 * <p>
 *     구독이 발생하면 원본 Publisher(Original Publisher 또는 Source Publisher)가 데이터를 최초로 emit 하는데,
 *     subscribeOn() Operator 는 구독 시점 직후에 실행되기 때문에 원본 Publisher 의 동작을 수행하기 위한 스레드라고 볼 수 있음.
 * </p>
 */
@Slf4j
public class SubscribeOn {
    /**
     * <h2>subscribeOn 실행 결과</h2>
     * <p style='margin-top:-10'>
     *     doOnSubscribe() 에서의 동작은 main 스레드에서 실행되는데, 그 이유는 예제 코드의 최초 실행 스레드가 main 스레드이기 때문. <br>
     *     subscribeOn() Operator를 추가하지 않았다면 원본 Flux의 처리 동자은 여전히 main 스레드에서 실행되겠지만, subscribeOn() 에서 원본 Flux의
     *     동작을 처리하는 스레드가 바뀌게 됨.
     * </p>
     *
     */
    public static void main(String[] args) throws InterruptedException {
        Flux.fromArray(new Integer[] {1, 3, 5, 7})
                .subscribeOn(Schedulers.boundedElastic()) // 구독이 발생한 직후에 원본 Publisher(원본 Flux) 의 동작을 처리하기 위한 스레드를 할당.
                .doOnNext(data -> log.info("# doOnNext: {}", data)) // 원본 Flux에서 emit 되는 데이터를 로그로 출력함
                .doOnSubscribe(subscription -> log.info("# doOnSubscribe")) // 구독이 발생한 시점에 추가적인 어떤 처리가 필요할 경우 해당 처리 동작을 추가할 수 있음.
                .subscribe(data -> log.info("# onNext: {}", data));

        Thread.sleep(500L);
    }
}
