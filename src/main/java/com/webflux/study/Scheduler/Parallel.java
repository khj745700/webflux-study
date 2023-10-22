package com.webflux.study.Scheduler;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * <h2>parallel()</h2>
 * {@link SubscribeOn}과 {@link PublishOn} Operator는 동시성을 가지는 논리적인 스레드에 해당됨. <br>
 * parallel() Operator는 병렬성을 가지는 물리적인 스레드에 해당됨.
 *
 * <p>
 *     parallel() 의 경우, RR (Round Robin, 시분할 스케줄링) 방식으로 CPU 코어 개수(논리적인 코어 = 물리적인 스레드)만큼의 스레드를 병렬로 실행함.<br>
 *     emit 되는 데이터를 CPU 의 논리적인 코어(물리적인 스레드)에 맞게 사전에 골고루 분배하는 역할만 담당.
 * </p>
 */
@Slf4j
public class Parallel {
    /**
     * <h3>Parallel() 실행 결과</h3>
     *
     */
    public static void main(String[] args) throws InterruptedException {
        Flux.fromArray(new Integer[]{1, 3, 5, 7, 9, 11, 13, 15, 17, 19})
                .parallel(4)//.parallel() 숫자 없으면 CPU의 물리적인 스레드의 수에 맞게 할당 // parallel() Operator 만 추가한다고 해서 emit 되는 데이터를 병렬로 처리하는 것은 아님.
                .runOn(Schedulers.parallel()) // 실제로 병렬 작업을 수행할 스레드의 할당은 runOn() Operator 가 담당.
                .subscribe(data -> log.info("# onNext: {}", data));

        Thread.sleep(100L);
    }
}
