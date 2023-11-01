package com.webflux.study.operator.create;

import com.webflux.study.operator.SampleData;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;


/**
 * <h2>FromStream</h2>
 * Stream에 포함된 데이터를 Emit 하는 FLux를 생성함. Java의 Stream 특성 상 Stream은 재사용할 수 없으며 cancel, error, complete 시에
 * 자동으로 닫히게 됨.
 */
@Slf4j
public class FromStream {
    public static void main(String[] args) {
        Flux
                .fromStream(() -> SampleData.coinNames.stream())
                .filter(coin -> coin.equals("BTC") || coin.equals("ETH"))
                .subscribe(data -> log.info("{}", data));
    }
}
