package com.webflux.study.operator.split;

import com.webflux.study.operator.SampleData;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.math.MathFlux;

/**
 * <h2>window()</h2>
 * <p>
 *     Upstream에서 emit되는 첫 번째 데이터부터 maxSize 숫자만큼의 데이터를 포함하는 새로운 Flux로 분할함.
 *     Reactor에서는 분할된 Flux를 윈도우라고 부름.
 * </p>
 * <p>
 *     maxSize가 3이기 때문에 Downstream의 요청 개수 2(N) X 3(maxSize) = 6 만큼의 데이터를 Upstream에 요청하고,
 *     요청 개수만큼 emit되는 데이터는 maxSize만큼 분할되어서 윈도우에 포함됨.
 * </p>
 * <p>
 *     마지막 윈도우에 포함된 데이터의 개수는 maxSize보다 더 작거나 같음.
 * </p>
 */
@Slf4j
public class Window {
    public static void main(String[] args) {
        example1();
        log.info("---------------------------------------");
        example2();
    }

    /**
     * <h3>실행 결과</h3>
     * <p>
     *     window() Operator의 파라미터로 입력한 maxSize만큼의 데이터를 포함한 새로운 FLux로 분할하고, 분할된 Flux가 각각의 데이터를
     *     Downstream으로 emit하고 있는 것을 확인할 수 있음.
     * </p>
     */
    static void example1() {
        Flux.range(1, 11)
                .window(3)
                .flatMap(flux -> {
                    log.info("======================");
                    return flux;
                })
                .subscribe(new BaseSubscriber<>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        subscription.request(2);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        log.info("# onNext: {}", value);
                        request(2);
                    }
                });
    }

    /**
     *
     */
    static void example2() {
        Flux.fromIterable(SampleData.monthlyBookSales2021)
                .window(3)
                .flatMap(flux -> MathFlux.sumInt(flux)) // 3개씩 분할된 데이터의 합계를 구함.//mathFlux를 사용하려면 dependency에 implementation 'io.projectreactor.addons:reactor-extra:3.4.8'을 추가해야함.
                .subscribe(new BaseSubscriber<>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        subscription.request(2);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        log.info("# onNext: {}", value);
                        request(2);
                    }
                });
    }
}
