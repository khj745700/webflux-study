package com.webflux.study.operator.multicast;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * <h2>publish()</h2>
 * <p>
 *     publish() Operator는 구독을 하더라도 구독 시점에 즉시 데이터를 emit하지 않고 connect()를 호출하는 시점에 비로소 데이터를 emit함.
 * </p>
 * <p>
 *     그리고 Hot Sequence로 변환되기 때문에 구독 시점 이후에 emit된 데이터만 전달받을 수 있음.
 * </p>
 */
@Slf4j
public class Publish {
    public static void main(String[] args) throws InterruptedException{
        example1();
        log.info("==========================================");
        example2();
    }

    /**
     * <h3>실행 결과</h3>
     * <ol>
     *     <li>
     *         먼저 publish() Operator를 통해 0.3초에 한 번씩 1부터 5까지 emit하는 ConnectableFlux<Integer>를 리턴받음.
     *         publish() Operator를 호출했지만 아직 connect()를 호출하지 않았기 때문에 이 시점에 emit되는 데이터는 없음.
     *     </li>
     *     <li>0.5초 뒤에 첫 번째 구독이 발생함.</li>
     *     <li>0.2초 뒤에 두 번째 구독이 발생함.</li>
     *     <li>connect()가 호출됨. 이 시점부터 데이터가 0.3초에 한 번씩 emit됨.</li>
     *     <li>
     *         1초 뒤에 세 번째 구독이 발생함. 그런데 connect()가 호출된 시점부터 0.3초에 한 번씩 데이터가 emit되기 때문에 숫자 1부터
     *         3까지는 세 번째 구독 전에 이미 emit된 상태여서 세 번째 Subscriber는 전달받지 못함.
     *     </li>
     * </ol>
     */
    static void example1() throws InterruptedException {
        ConnectableFlux<Integer> flux = Flux
                .range(1, 5)
                .delayElements(Duration.ofMillis(300L))
                .publish();

        Thread.sleep(500L);
        flux.subscribe(data -> log.info("# subscriber1: {}", data));

        Thread.sleep(200L);
        flux.subscribe(data -> log.info("# subscriber2: {}", data));

        flux.connect();

        Thread.sleep(1000L);
        flux.subscribe(data -> log.info("# subscriber3: {}", data));

        Thread.sleep(2000L);
    }

    private static ConnectableFlux<String> publisher;
    private static int checkedAudience;
    static {
        publisher =
                Flux
                        .just("Concert part1", "Concert part2", "Concert part3")
                        .delayElements(Duration.ofMillis(300L))
                        .publish();
    }
    static void example2() throws InterruptedException {
        checkAudience();
        Thread.sleep(500L);
        publisher.subscribe(data -> log.info("# audience 1 is watching {}", data));
        checkedAudience++;

        Thread.sleep(500L);
        publisher.subscribe(data -> log.info("# audience 2 is watching {}", data));
        checkedAudience++;

        checkAudience();

        Thread.sleep(500L);
        publisher.subscribe(data -> log.info("# audience 3 is watching {}", data));

        Thread.sleep(1000L);
    }


    private static void checkAudience() {
        if (checkedAudience >= 2) {
            publisher.connect();
        }
    }

}
