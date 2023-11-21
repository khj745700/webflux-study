package com.webflux.study.operator.multicast;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * <h2>autoConnect()</h2>
 * <p>
 *     파라미터로 지정하는 숫자만큼의 구독이 발생하는 시점에 UpStream 소스에 자동으로 연결되기 때문에 별도의 connect() 호출이 필요하지 않음.
 * </p>
 */
@Slf4j
public class AutoConnect {
    public static void main(String[] args) throws InterruptedException{
        Flux<String> publisher =
                Flux
                        .just("Concert part1", "Concert part2", "Concert part3")
                        .delayElements(Duration.ofMillis(300L))
                        .publish()
                        .autoConnect(2);

        Thread.sleep(500L);
        publisher.subscribe(data -> log.info("# audience 1 is watching {}", data));

        Thread.sleep(500L);
        publisher.subscribe(data -> log.info("# audience 2 is watching {}", data));

        Thread.sleep(500L);
        publisher.subscribe(data -> log.info("# audience 3 is watching {}", data));

        Thread.sleep(1000L);


    }
}
