package com.webflux.study.sinks;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

import java.util.stream.IntStream;

/**
 * <h2>Sinks 란?</h2>
 * <hr>
 * <strong>Sinks</strong> 는 리액티브 스트림즈의 Signal 을 프로그래밍 방식을 푸시할 수 있는 구조이며 Flux 또는 Mono 의 의미 체계를 가짐.
 * <br>
 * 프로그래밍 코드를 통해 <strong>명시적으로</strong> Signal 을 전송할 수 있음.
 * <br>
 * Reactor 에서 프로그래밍 방식으로 Signal 을 전송하는 가장 일반적인 방식은 generate() 나 create() 같은 Operator 을 통해 해왔음.
 * 이는 Reactor 에 Sinks 를 도입하기 전부터 해왔음.
 * <br><br>
 * Sinks 는 멀티스레딩 방식으로 Signal 을 전송해도 <strong>스레드 안전성을 보장</strong>하기 때문에 예기치 않은 동작으로 이어지는 것을 방지해 줌.
 *
 * <br><br><br>
 * <h2>Memo</h2>
 * <ul>
 *     <li>Sinks는 Publisher와 Subscriber의 기능을 모두 지닌 Processor의 향상된 기능을 제공</li>
 *     <li>데이터를 emit 하는 Sinks에는 크게 Sinks.One과 Sinks.Many가 있음.</li>
 *     <li>Sinks.One 에는 한 건의 데이터를 프로그래밍 방식으로 emit 함.</li>
 *     <li>Sinks.Many 에는 여러 건의 데이터를 프로그래밍 방식으로 emit 함.</li>
 *     <li>Sinks.Many의 UnicastSpec 은 단 하나의 Subscriber에게만 데이터를 emit 함.</li>
 *     <li>Sinks.Many의 MulticastSpec 은 하나 이상의 Subscriber에게 데이터를 emit 함.</li>
 *     <li>Sinks.Many의 MulticastReplaySpec 은 emit된 데이터 중에서 특정 시점으로 되돌린(replay) 데이터부터 데이터를 emit 함.</li>
 * </ul>
 */
@Slf4j
public class AboutSinks {
    /**
     * create() Operator를 사용하는 예제
     * <br>
     *  - 일반적으로 Publisher가 단일 쓰레드에서 데이터 생성한다.
     * <br>
     * 여기에서는 결과적으로 총 3개의 스레드가 동시에 실행됨.
     * <br><br>
     * <h2>분석</h2>
     * <hr>
     *     <strong>코드 실행 결과</strong>
     *     <ul>
     *     <li>doTask() 메서드 작업 처리는 boundedElastic-1 스레드에서 실행</li>
     *     <li>map() Operator 가공처리는 parallel-2 스레드에서 실행</li>
     *     <li>Subscriber 에서 전달받은 데이터의 처리는 parallel-1 에서 처리</li>
     *     </ul>
     *     create Operator 를 사용해서 프로그래밍 방식으로 Signal 을 전송할 수 있으며, Reactor Sequence 를 단계적으로 나누어서 여러 개의 스레드로 처리할 수 있음.<br>
     *     <p>위 코드에서 작업을 처리 한 후, 그 결과를 반환하는 doTask() 메서드가 싱글 스레드가 아닌 여러 개의 스레드에서 각각의 전혀 다른 작업들을 처리한 후,
     *     처리 결과를 반환하는 상황이 발생할 수 있는데, 이 같은 상황에서 적절하게 사용할 수 있는 것이 Sinks 임
     *     </p>
     */
    public static void main(String[] args) throws InterruptedException {
        int tasks = 6;
        Flux
                .create((FluxSink<String> sink) -> { // 원본 데이터를 생성하는 메서드
                    /**
                     * create() Operator가 처리해야 할 작업의 개수만큼 doTask() 메서드를 호출해서 작업을 처리한 후, 결과를 리턴받음.
                     */
                    IntStream
                            .range(1, tasks)
                            .forEach(n -> sink.next(doTask(n)));
                })
                .subscribeOn(Schedulers.boundedElastic()) // 작업을 처리하는 스레드 지정. subscribeOn() Operator에서 지정한 스레드를 사용해서 생성한 데이터를 emit
                .doOnNext(n -> log.info("# create(): {}", n))
                .publishOn(Schedulers.parallel())  // 처리 결과를 가공하는 스레드 지정
                .map(result -> result + " success!")
                .doOnNext(n -> log.info("# map(): {}", n))
                .publishOn(Schedulers.parallel()) // 가공된 결과를 전달하는 스레드 지정
                .subscribe(data -> log.info("# onNext: {}", data));

        Thread.sleep(500L);
    }

    private static String doTask(int taskNumber) {
        // now tasking.
        // complete to task.
        return "task " + taskNumber + " result";
    }
}
