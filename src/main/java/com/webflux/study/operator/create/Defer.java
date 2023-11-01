package com.webflux.study.operator.create;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.LocalDateTime;


/**
 * <h2>Defer</h2>
 * <ul>
 *     <li>defer() Operator는 Operator를 선언한 시점에 데이터를 emit 하는 것이 아닌, 구독하는 시점에 emit 하는 Flux 또는 Mono를 생성</li>
 *     <li>defer()는 데아터 emit을 지연시키기 때문에 꼭 필요한 시점에 데이터를 emit하여 불필요한 프로세스를 줄일 수 있음.</li>
 * </ul>
 */
@Slf4j
public class Defer {

    public static void main(String[] args) throws InterruptedException{
        example1();
        log.info("----------------------------------------");
        example2();
    }

    /**
     * <h3>실행 결과</h3>
     * <p>
     *     just() Operator는 사실 Hot Publisher 이기 때문에 Subscribe의 구독 여부와는 상관없이 데이터를 emit 하게 됨.
     *     그리고 구독이 발생하면 emit된 데이터를 다시 재생해서 Subscriber에게 전달하는 것임. 따라서 jystMono의 결과가 같음.
     * </p>
     * <p>
     *     defer() Operator는 구독이 발생하기 전까지 데이터의 emit을 지연시키기 때문에 just() Operator를 Defer()로 감싸게 되면
     *     실제 구독이 발생해야 데이터를 emit 하게 됨.
     * </p>
     */
    private static void example1() throws InterruptedException {
        log.info("# start : {}", LocalDateTime.now());
        Mono<LocalDateTime> justMono = Mono.just(LocalDateTime.now());
        Mono<LocalDateTime> deferMono = Mono.defer(() -> Mono.just(LocalDateTime.now()));

        Thread.sleep(2000);

        justMono.subscribe(data -> log.info("# onNext just1 : {}", data));
        deferMono.subscribe(data -> log.info("# onNext defer1 : {}", data));

        Thread.sleep(1000);

        justMono.subscribe(data -> log.info("# onNext just2 : {}", data));
        deferMono.subscribe(data -> log.info("# onNext defer2 : {}", data));
    }

    /**
     *<h3>실행 결과</h3>
     * switchIfEmpty() Operator 내부에서 defer()를 사용하는 예제 코드임.
     *<p>
     *     defer() 를 사용하는 코드는 주석을 처리해 두고, Upstream에서 emit되는 데이터가 없으면 switchIfEmpty() 내부에서 sayDefault() 메서드를
     *     호출하도록 하였음. <br>
     *     얼핏 보면 Upstream에서 "Hello" 문자열이 emit 되기 때문에 syaDefault() 메서드가 호출되지 않을 것 같지만, 코드를 실행하면 sayDefault()
     *     메서드가 호출됨. 결과적으로 메서드가 불필요하게 호출되는 문제가 발생함.
     *</p>
     * <p>
     *     defer 부분을 주석 해제하고 그 다시 실행하면, sayDefault() 메서드를 defer() 로 감싸기 때문에 Upstream 에서 emit 되는 데이터가 있다면
     *     sayDefault() 메서드는 호출되지 않음.
     * </p>
     */
    private static void example2() throws InterruptedException {
        log.info("# start : {}", LocalDateTime.now());
        Mono
                .empty()
                //.just("Hello")
                .delayElement(Duration.ofSeconds(3))
//                .switchIfEmpty(sayDefault())
                .switchIfEmpty(Mono.defer(() -> sayDefault()))
                .subscribe(data -> log.info("#onNext : {}", data));

        Thread.sleep(3500);
    }

    private static Mono<String> sayDefault() {
        log.info("# say Hi");
        return Mono.just("Hi");
    }
}
