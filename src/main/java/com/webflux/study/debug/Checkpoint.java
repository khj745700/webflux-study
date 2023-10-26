package com.webflux.study.debug;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * <h2>checkpoint()</h2>
 * checkpoint() 를 사용하면 실제 에러가 발생한 assembly 지점 또는 에러가 전파된 assembly 지점의 traceback이 추가됨.
 */
@Slf4j
public class Checkpoint {

    public static void main(String[] args) {
        defaultCheckpoint();

        log.info("without traceback--------------------------------");
        descriptionCheckpoint();

        log.info("with traceback and description ------------------");
        descriptionWithTracebackCheckpoint();

        log.info("서로 다른 Operator 체인에서의 checkpoint() 활용 예제 ----");
        anotherOperatorChainWithCheckpoint();
    }

    /**
     * <h3>출력 결과</h3>
     * 두 개 모두 Traceback 출력됨. <br>
     * 두 번째 checkpoint()는 에러가 전파되었기 때문에 출력 <br>
     * 첫 번째 checkpoint() 는 zipWith() Operator에서 직접적으로 에러가 발생헀음을 알 수 있음.
     *
     */
    public static void defaultCheckpoint() {
        Flux
                .just(2, 4, 6, 8)
                .zipWith(Flux.just(1, 2, 3, 0), (x, y) -> x/y)
                .checkpoint()
                .map(num -> num + 2)
                .checkpoint()
                .subscribe(
                        data -> log.info("# onNext: {}", data),
                        error -> log.error("# onError:", error)
                );
    }


    /**
     * <h3>출력 결과</h3>
     * Traceback 대신에 description이 출력됨<br>
     */
    public static void descriptionCheckpoint() {
        Flux
                .just(2, 4, 6, 8)
                .zipWith(Flux.just(1, 2, 3, 0), (x, y) -> x/y)
                .checkpoint("descriptionCheckpoint.zipWith.checkpoint")
                .map(num -> num + 2)
                .checkpoint("descriptionCheckpoint.map.checkpoint")
                .subscribe(
                        data -> log.info("# onNext: {}", data),
                        error -> log.error("# onError:", error)
                );
    }

    /**
     * <h3>출력 결과</h3>
     * Traceback과 description이 같이 출력됨<br>
     */
    public static void descriptionWithTracebackCheckpoint() {
        Flux
                .just(2, 4, 6, 8)
                .zipWith(Flux.just(1, 2, 3, 0), (x, y) -> x/y)
                .checkpoint("descriptionCheckpoint.zipWith.checkpoint", true)
                .map(num -> num + 2)
                .checkpoint("descriptionCheckpoint.map.checkpoint", true)
                .subscribe(
                        data -> log.info("# onNext: {}", data),
                        error -> log.error("# onError:", error)
                );
    }


    public static void anotherOperatorChainWithCheckpoint() {
        Flux<Integer> source = Flux.just(2, 4, 6, 8);
        Flux<Integer> other = Flux.just(1, 2, 3, 0);

        Flux<Integer> multiplySource = divide(source, other).checkpoint();
        Flux<Integer> plusSource = plus(multiplySource).checkpoint();


        plusSource.subscribe(
                data -> log.info("# onNext: {}", data),
                error -> log.error("# onError:", error)
        );
    }

    private static Flux<Integer> divide(Flux<Integer> source, Flux<Integer> other) {
        return source.zipWith(other, (x, y) -> x/y);
    }

    private static Flux<Integer> plus(Flux<Integer> source) {
        return source.map(num -> num + 2);
    }
}
