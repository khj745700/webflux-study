package com.webflux.study.operator.filter;

import com.webflux.study.operator.SampleData;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
public class TakeWhile {
    public static void main(String[] args) {
        Flux
                .fromIterable(SampleData.btcTopPricesPerYear)
                .takeWhile(tuple -> tuple.getT2() < 20_000_000) //upstrema에서 emit된 데이터가 false라면 sequence가 종료됨. ->  takeWhile() Operator는 사용한 데이터는 Downstream으로 emit되지 않음.
                .subscribe(tuple -> log.info("# onNext: {}, {}",
                        tuple.getT1(), tuple.getT2()));
    }
}
