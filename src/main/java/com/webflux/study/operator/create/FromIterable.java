package com.webflux.study.operator.create;

import com.webflux.study.operator.SampleData;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;


/**
 * <h2>FromIterable</h2>
 * Iterable에 포함된 데이터를 Emit 하는 Flux를 생성함. 즉, Java에서 제공하는 Iterable을 구현한 구현체를 fromIterable() 파라미터로 전달 가능함.
 */
@Slf4j
public class FromIterable {
    public static void main(String[] args) {
        Flux
                .fromIterable(SampleData.coins)
                .subscribe(coin ->
                        log.info("coin 명 : {}, 현재가 : {}", coin.getT1(), coin.getT2())
                );
    }
}
