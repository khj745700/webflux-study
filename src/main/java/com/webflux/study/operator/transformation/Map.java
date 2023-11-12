package com.webflux.study.operator.transformation;

import com.webflux.study.operator.SampleData;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * <h2>map()</h2>
 * <p>
 *     map() Operator는 Upstream에서 emit된 데이터를 mapper Function을 사용하여 변환한 후, Downstream으로 emit 함.
 * </p>
 * <p>
 *     map() Operator 내부에서 에러 발생 시 Sequence가 종료되지 않고 계속 진행되도록 하는 기능을 지원함.
 * </p>
 */
@Slf4j
public class Map {
    public static void main(String[] args) {
        example1();
        log.info("------------------------------------------");
        example2();
    }


    private static void example1() {
        Flux
                .just("1-Circle", "3-Circle", "5-Circle")
                .map(circle -> circle.replace("Circle", "Rectangle"))
                .subscribe(data -> log.info("# onNext : {}", data));
    }

    private static void example2() {
        final double buyPrice = 50_000_000;
        Flux
                .fromIterable(SampleData.btcTopPricesPerYear)
                .filter(tuple -> tuple.getT1() == 2021)
                .doOnNext(data -> log.info("# doOnNext: {}", data))
                .map(tuple -> calculateProfitRate(buyPrice, tuple.getT2()))
                .subscribe(data -> log.info("# onNext: {}$", data));
    }

    private static double calculateProfitRate(final double buyPrice, Long topPrice) {
        return (topPrice - buyPrice) / buyPrice * 100;
    }
}
