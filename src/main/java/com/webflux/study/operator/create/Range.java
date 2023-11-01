package com.webflux.study.operator.create;

import com.webflux.study.operator.SampleData;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * <h2>Range</h2>
 * n 부터 1씩 증가한 연속된 수를 m개 emit 하는 Flux를 생성함. <br><br>
 * range() Operator는 명령형 언어의 for 문 처럼 특정 횟수만큼 어떤 작업을 처리하고자 할 경우에 주로 사용됨.
 */
@Slf4j
public class Range {
    public static void main(String[] args) {
        example1();
        log.info("-------------------------------------------------");
        example2();
    }


    private static void example1() {
        Flux
                .range(5, 10)
                .subscribe(data -> log.info("{}", data));
    }

    private static void example2() {
        Flux
                .range(7, 5)
                .map(idx -> SampleData.btcTopPricesPerYear.get(idx))
                .subscribe(tuple -> log.info("{}`s {}", tuple.getT1(), tuple.getT2()));
    }
}
