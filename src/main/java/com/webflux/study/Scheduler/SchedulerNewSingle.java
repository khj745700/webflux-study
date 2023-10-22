package com.webflux.study.Scheduler;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;


/**
 * <h3>Scheduler.newSingle()</h3>
 *  Scheduler.newSingle()은 호출할 때마다 매번 하나의 쓰레드를 새롭게 생성함.
 *
 *  <h3>daemon?</h3>
 *  <p>
 *      데몬 스레드는 보조 스레드라고 불리는데, 주 스레드가 종료되면 자동으로 종료되는 특성이 있음.
 *      아래에선 main 스레드가 죽으면 같이 죽도록 설정되어 있음.
 *  </p>
 */
@Slf4j
public class SchedulerNewSingle {
    public static void main(String[] args) throws InterruptedException {
        doTask("task1")
                .subscribe(data -> log.info("# onNext: {}", data));

        doTask("task2")
                .subscribe(data -> log.info("# onNext: {}", data));

        Thread.sleep(200L);
    }

    private static Flux<Integer> doTask(String taskName) {
        return Flux.fromArray(new Integer[] {1, 3, 5, 7})
                .publishOn(Schedulers.newSingle("new-single", true))
                .filter(data -> data > 3)
                .doOnNext(data -> log.info("# {} doOnNext filter: {}", taskName, data))
                .map(data -> data * 10)
                .doOnNext(data -> log.info("# {} doOnNext map: {}", taskName, data));
    }
}
