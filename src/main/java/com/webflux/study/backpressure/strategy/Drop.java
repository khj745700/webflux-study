package com.webflux.study.backpressure.strategy;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

/**
 * <h2>DROP 전략</h2>
 * Backpressure <strong>DROP</strong> 전략은 Publisher 가 Downstream 으로 전달할 데이터가 버퍼에 가득 찰 경우,
 * <br>
 * 버퍼 밖에서 대기중인 데이터 중에서 먼저 emit 된 데이터 부터 Drop 하는 전략임.
 * <br>
 * DROP 된 데이터는 폐기됨.
 */
@Slf4j
public class Drop {
    public static void main(String[] args) throws InterruptedException {
        Flux
                .interval(Duration.ofMillis(1L))
                .onBackpressureDrop(dropped -> log.info("# dropped: {}", dropped))
                .publishOn(Schedulers.parallel())
                .subscribe(data -> {
                            try {
                                Thread.sleep(5L);
                            } catch (InterruptedException e) {}
                            log.info("# onNext: {}", data);
                        },
                        error -> log.error("# onError", error));

        Thread.sleep(2000L);
    }
}
