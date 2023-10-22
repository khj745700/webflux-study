package com.webflux.study.Scheduler;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * <h3>Schedulers.immediate</h3>
 * 별도의 스레드를 추가적으로 생성하지 않고, 현재 스레드에서 작업을 처리하고자 할 때 사용할 수 있음.<br><br>
 *
 * <h3>사용 이유?</h3>
 * 어떤 API 중에서 공통의 역할을 하는 API 이고, 해당 API의 파라미터로 Scheduler를 전달할 수 있다고 가정.
 * <p>
 *     이 경우 어떤 API를 사용하는 입장에서 map() 이후의 Operator 체인 작업은 원래 실행되던 스레드에서 실행하게 하고 싶을 때도 있을 것임.
 *     즉, Scheduler가 필요한 API 이긴 한데 별도의 스레드를 추가 할당하고 싶지 않을 경우에 Scheduler.immediate()를 사용함.
 * </p>
 */
@Slf4j
public class SchedulerImmediate {
    public static void main(String[] args) throws InterruptedException {
        Flux
                .fromArray(new Integer[] {1, 3, 5, 7})
                .publishOn(Schedulers.parallel())
                .filter(data -> data > 3)
                .doOnNext(data -> log.info("# doOnNext filter: {}", data))
                .publishOn(Schedulers.immediate())
                .map(data -> data * 10)
                .doOnNext(data -> log.info("# doOnNext map: {}", data))
                .subscribe(data -> log.info("# onNext: {}", data));

        Thread.sleep(200L);
    }
}
