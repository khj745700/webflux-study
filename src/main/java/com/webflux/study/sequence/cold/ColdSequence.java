package com.webflux.study.sequence.cold;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.Arrays;

@Slf4j
public class ColdSequence {
    public static void main(String[] args) throws InterruptedException {
        /**
         * Cold 는 처음부터 새로 시작해야 하며 같은 작업이 반복되는 의미를 지니고 있음.
         *
         */
        Flux<String> coldFlux =
                Flux
                        .fromIterable(Arrays.asList("KOREA", "JAPAN", "CHINESE")) // fromIterable() 메서드는 Iterable 을 상속받은 Collection 들의 데이터를 Emit 하는 메서드
                        .map(String::toLowerCase);


        coldFlux.subscribe(country -> log.info("# Subscriber1: {}", country));
        System.out.println("----------------------------------------------------------------------");
        Thread.sleep(2000L);
        coldFlux.subscribe(country -> log.info("# Subscriber2: {}", country));
    }
}
