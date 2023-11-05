package com.webflux.study.operator.filter;

import com.webflux.study.operator.SampleData;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import java.util.Map;

import static com.webflux.study.operator.SampleData.getCovidVaccines;

/**
 *
 */
@Slf4j
public class Filter {
    public static void main(String[] args) throws InterruptedException{
        example1();
        log.info("------------------------------------");
        example2();
    }

    private static void example1() {
        Flux
                .range(1, 20)
                .filter(num -> num % 2 != 0)
                .subscribe(data -> log.info("# onNext: {}", data));
    }

    private static void example2() {
        Flux
                .fromIterable(SampleData.btcTopPricesPerYear)
                .filter(tuple -> tuple.getT2() > 20_000_000)
                .subscribe(data -> log.info(data.getT1() + ":" + data.getT2()));
    }

}
