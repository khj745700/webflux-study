package com.webflux.study.sequence.hot;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Slf4j
public class HotSequence {
    public static void main(String[] args) throws InterruptedException {
        /**
         * Hot 은 처음부터 다시 시작하지 않고 같은 작업이 반복되지 않는 느낌.
         */
        String[] singers = {"Singer A", "Singer B", "Singer C", "Singer D", "Singer E"};

        log.info("# Begin concert:");
        Flux<String> concertFlux =
                Flux
                        .fromArray(singers)
                        .delayElements(Duration.ofSeconds(1))
                        .share(); // Cold Sequence 를 Hot Sequence 로 바꿔주는 Operator

        concertFlux.subscribe(
                singer -> log.info("# Subscriber1 is watching {}'s song", singer)
        ); // 데이터가 1초마다 뜨다가

        Thread.sleep(2500); // 2.5초 후에

        concertFlux.subscribe( // 얘가 구독되는데 그러면 Subscriber2는 Single C 부터 출력이 될 것임.
                singer -> log.info("# Subscriber2 is watching {}'s song", singer)
        );

        Thread.sleep(3000);
    }
}
