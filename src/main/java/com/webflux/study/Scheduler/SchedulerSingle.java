package com.webflux.study.Scheduler;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * <h3>Scheduler.single()</h3>
 *  Scheduler.single()은 스레드 하나만 생성해서 Scheduler가 제거되기 전까지 재사용하는 방식.
 */
@Slf4j
public class SchedulerSingle {

    /**
     * <h3>실행 결과</h3>
     * doTask()가 두 번 호출되었지만 single-1 이라는 하나의 스레드에서 처리 됨.
     *
     */
    public static void main(String[] args) throws InterruptedException {
        doTask("task1")
                .subscribe(data -> log.info("# onNext: {}", data));

        doTask("task2")
                .subscribe(data -> log.info("# onNext: {}", data));

        Thread.sleep(200L);
    }

    private static Flux<Integer> doTask(String taskName) {
        return Flux.fromArray(new Integer[] {1, 3, 5, 7})
                .publishOn(Schedulers.single())
                .filter(data -> data > 3)
                .doOnNext(data -> log.info("# {} doOnNext filter: {}", taskName, data))
                .map(data -> data * 10)
                .doOnNext(data -> log.info("# {} doOnNext map: {}", taskName, data));
    }
}
