package com.webflux.study.operator.transformation;

import com.webflux.study.operator.filter.Filter;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * <h2>and()</h2>
 * <p>
 *     Mono의 Complete Signal과 파라미터로 입력된 publisher의 Complete Signal을 결합하여 새로운 Mono<Void>를 반환함.
 *     즉, Mono와 파라미터로 입력된 Publisher의 Sequence가 모두 종료되었음을 Subscriber에게 알릴 수 있음.
 * </p>
 * <p>
 *     and() Operator 위쪽의 Sequence와 and() Operator 내부의 Sequence가 종료되어도 Downstream 쪽으로 emit 되는 데이터가 하나도 없는 것을
 *     볼 수 있음.
 * </p>
 */
@Slf4j
public class And {
    public static void main(String[] args) throws InterruptedException {
        example1();
        log.info("============================================");
        example2();
    }

    /**
     * <h3>실행 결과</h3>
     * <p>
     *     and() Operator를 기준으로 Upstream에서 1초의 지연 시간을 가진 뒤 "Task 1"을 emit하고 and() Operator 내부의 Inner Sequence
     *     에서는 0.6초에 한 번 씩 "Task 2", "Task 3"을 emit함.
     * </p>
     * <p>
     *     결과적으로 Subscriber에게 onComplete Signal만 전달되고, Upstream에서 emit된 데이터는 전달되지 않음.
     *     즉, and() Operator는 모든 Sequence가 종료되길 기다렸다가 최종적으로 onComplete Signal만 전송됨.
     * </p>
     */
    private static void example1() throws InterruptedException {
        Mono
                .just("Task 1")
                .delayElement(Duration.ofSeconds(1))
                .doOnNext(data -> log.info("# Mono doOnNext : {}", data))
                .and(
                        Flux
                                .just("Task 2", "Task 3")
                                .delayElements(Duration.ofMillis(600))
                                .doOnNext(data -> log.info("# FluxdoOnNext: {}", data))
                )
                .subscribe(
                        data -> log.info("# onNext: {}", data),
                        error -> log.error("# onError:", error),
                        () -> log.info("# onComplete")
                );

        Thread.sleep(5000);
    }

    /**
     * <h3>실행 결과</h3>
     * <p>
     *     애플리케이션 서버가 2초 뒤에 재가동되었고, 4초 뒤에 DB 서버가 재가동 되었음. 두 개의 서버가 성공적으로 재가동 된 후, 두 개 서버의 재가동이
     *     성공적으로 수행되었음을 이메일로 관리자에게 알리고 있음.
     * </p>
     */
    private static void example2() throws InterruptedException {
        restartApplicationServer()
                .and(restartDBServer())
                .subscribe(
                        data -> log.info("# onNext: {}", data),
                        error -> log.error("# onError: ", error),
                        () -> log.info("# sent an email to Administrator: " +
                                "All Servers are restarted successfully" )
                );

        Thread.sleep(6000L);
    }

    private static Mono<String> restartApplicationServer() {
        return Mono
                .just("Application Server was restarted successfully.")
                .delayElement(Duration.ofSeconds(2))
                .doOnNext(log::info);
    }

    private static Publisher<String> restartDBServer() {
        return Mono
                .just("DB Server was restarted successfully.")
                .delayElement(Duration.ofSeconds(4))
                .doOnNext(log::info);
    }
}
