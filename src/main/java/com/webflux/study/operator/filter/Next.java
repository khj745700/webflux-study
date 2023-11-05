package com.webflux.study.operator.filter;

import com.webflux.study.operator.SampleData;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * <h2>next()</h2>
 * Upstream에서 emit되는 데이터 중에서 첫 번째 데이터만 Downstream으로 emit함. 만일 Upstream에서 emit되는 데이터가 empty라면 Downstream으로 empty Mono를 emit함.
 */
@Slf4j
public class Next {
    public static void main(String[] args) {
        Flux
                .fromIterable(SampleData.btcTopPricesPerYear)
                .next()
                .subscribe(tuple -> log.info("# onNext: {}, {}", tuple.getT1(), tuple.getT2()));
    }
}
