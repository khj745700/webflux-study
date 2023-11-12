package com.webflux.study.operator.transformation;

import com.webflux.study.operator.SampleData;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;

import java.time.Duration;

/**
 * <h2>zip()</h2>
 * <p>
 *     파라미터로 입력되는 Publisher Sequence에 emit된 데이터를 결합하는 데, 각 Publisher가 데이터를 하나씩 emit 할 때까지 기다렸다가 결합함.
 * </p>
 */
@Slf4j
public class Zip {
    public static void main(String[] args) throws InterruptedException {
        exmaple1();
        log.info("------------------------------------------");
        example2();
        log.info("------------------------------------------");
        example3();
    }

    /**
     * <h3>실행 결과</h3>
     * <p>
     *     zip() Operator의 첫 번째 파라미터인 Flux에서 0.3초에 한 번씩 데이터를 emit 하고, 두 번째 파라미터인 Flux에서 0.5초에 한 번씩 데이터를 emit함.
     * </p>
     * <p>
     *     두 개의 Flux emit 시간이 다르지만 각 Flux에서 하나씩 emit 할 때까지 기다렸다가 emit된 데이터를 Tuple2 객체로 묶어서 Subscriber에게
     *     전달하는 것을 볼 수 있음.
     * </p>
     */
    private static void exmaple1() throws InterruptedException {
        Flux
                .zip(
                        Flux.just(1,2,3).delayElements(Duration.ofMillis(300L)),
                        Flux.just(4,5,6).delayElements(Duration.ofMillis(500L))
                )
                .subscribe(tuple2 -> log.info("# onNext: {}", tuple2));

        Thread.sleep(2500);
    }

    /**
     * <h3>실행 결과</h3>
     * <p>
     *     두 개의 Flux가 emit하는 데이터를 묶어서 Subscriber에게 전달하는 것이 아니라 zip() Operator의 세 번째 파라미터로 Combinator(BiFunction 함수형 인터페이스)
     *     를 추가해서 두 개의 Flux가 emit 하는 한 쌍의 데이터를 combinator에서 전달받아 변환 작업을 거친 후, 최종 변환된 데이터를 Subscriber에게 전달함.
     * </p>
     */
    private static void example2() throws InterruptedException {
        Flux
                .zip(
                        Flux.just(1,2,3).delayElements(Duration.ofMillis(300L)),
                        Flux.just(4,5,6).delayElements(Duration.ofMillis(500L)),
                        (n1, n2) -> n1 * n2
                )
                .subscribe(tuple2 -> log.info("# onNext: {}", tuple2));

        Thread.sleep(2500);
    }

    /**
     * <h3>실행 결과</h3>
     * <p>
     *      zip() Operator의 파라미터로 총 세 개의 Flux를 전달하였음. 그리고 getInfectedPersonPerHour() 메서드의 파라미터로
     *      특정 시간 범위를 전달해서 해당 범위 내에서만 시간별로 통계를 내고 있음.
     * </p>
     */
    private static void example3() throws InterruptedException {
        getInfectedPersonsPerHour(10, 21)
                .subscribe(tuples -> {
                    Tuple3<Tuple2, Tuple2, Tuple2> t3 = (Tuple3) tuples;
                    int sum = (int) t3.getT1().getT2() +
                            (int) t3.getT2().getT2() + (int) t3.getT3().getT2();
                    log.info("# onNext: {}", t3.getT1().getT1(), sum);
                });

        Thread.sleep(2500);
    }

    private static Flux getInfectedPersonsPerHour(int start, int end) {
        return Flux.zip(
                Flux.fromIterable(SampleData.seoulInfected)
                        .filter(t2 -> t2.getT1() >= start && t2.getT1() <= end),
                Flux.fromIterable(SampleData.incheonInfected)
                        .filter(t2 -> t2.getT1() >= start && t2.getT1() <= end),
                Flux.fromIterable(SampleData.suwonInfected)
                        .filter(t2 -> t2.getT1() >= start && t2.getT1() <= end)
        );
    }
}
