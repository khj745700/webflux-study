package com.webflux.study.sinks;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.util.stream.IntStream;

/**
 * doTask() 메서드가 루프를 돌 때마다 새로운 스레드에서 실행됨.<br>
 * 그리고 doTask() 메서드의 작업 처리 결과를 Sinks 를 통해서 Downstream 에 emit 함.
 */
@Slf4j
public class SinksExample2 {
    /**
     * <h2>실행 결과</h2>
     * <p>
     *     doTask() 메서드는 새로운 루프를 돌 때마다 새로운 스레드에서 실행되기 때문에 총 5개의 스레드(Thread-0 ~ Thread-2) 에서 실행
     *     mpa() Operator에서의 가공 처리는 parallel-2 스레드, Subscriber 에서 전달받은 데이터의 처리는 parallel-1 스레드에서 실행되어
     *     총 7개의 스레드가 실행됨.
     * </p>
     *
     */
    public static void main(String[] args) throws InterruptedException {
        int tasks = 6;

        Sinks.Many<String> unicastSink = Sinks.many().unicast().onBackpressureBuffer();
        Flux<String> fluxView = unicastSink.asFlux();
        IntStream
                .range(1, tasks)
                .forEach(n -> {
                    try {
                        new Thread(() -> {
                            unicastSink.emitNext(doTask(n), Sinks.EmitFailureHandler.FAIL_FAST);
                            log.info("# emitted: {}", n);
                        }).start();
                        Thread.sleep(100L);
                    } catch (InterruptedException e) {
                        log.error(e.getMessage());
                    }
                });

        fluxView
                .publishOn(Schedulers.parallel())
                .map(result -> result + " success!")
                .doOnNext(n -> log.info("# map(): {}", n))
                .publishOn(Schedulers.parallel())
                .subscribe(data -> log.info("# onNext: {}", data));

        Thread.sleep(200L);
    }

    private static String doTask(int taskNumber) {
        // now tasking.
        // complete to task.
        return "task " + taskNumber + " result";
    }
}
