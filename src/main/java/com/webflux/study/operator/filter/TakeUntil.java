package com.webflux.study.operator.filter;

import com.webflux.study.operator.SampleData;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 *
 */
@Slf4j
public class TakeUntil {
    public static void main(String[] args) {
        Flux
                .fromIterable(SampleData.btcTopPricesPerYear)
                .takeUntil(tuple -> tuple.getT2() > 20_000_000) // 파라미터가 true가 될 때 까지  Upstream에서 emit된 데이터를 Downstream으로 emit함. 즉, 마지막 True에 사용된 데이터도 출력됨.
                .subscribe(tuple -> log.info("# onNext: {}, {}",
                        tuple.getT1(), tuple.getT2()));
    }
}
