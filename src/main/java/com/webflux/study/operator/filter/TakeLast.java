package com.webflux.study.operator.filter;

import com.webflux.study.operator.SampleData;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;


/**
 * <h2>takeLast()</h2>
 * takeLast() Operator는 Upstream에서 emit된 데이터 중에서 파라미터로 입력한 개수만큼 가장 마지막에 emit된 데이터를 Downstream으로 emit함.
 */
@Slf4j
public class TakeLast {
    public static void main(String[] args) {
        Flux
                .fromIterable(SampleData.btcTopPricesPerYear)
                .takeLast(2)
                .subscribe(tuple -> log.info("# onNext: {}, {}",
                        tuple.getT1(), tuple.getT2()));
    }
}
