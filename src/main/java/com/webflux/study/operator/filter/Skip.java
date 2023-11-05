package com.webflux.study.operator.filter;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * <h2>skip()</h2>
 * skip() Operator는 Upstream에서 emit된 데이터 중에서 파라미터로 입력받은 숫자만큼 건너뛴 후, 나머지 데이터를 Downstream으로 emit 함.
 */
@Slf4j
public class Skip {
    public static void main(String[] args) throws InterruptedException{
//     example1();
//     log.info("--------------------------------------------");
     example2();
    }
    /**
     * <h3>실행 결과</h3>
     * Interval() Operator에서 0 부터 1 증가한 숫자를 emit 하기 때문에 skip() Operator에서는 0과 1을 건너뛰고 2부터 Downstream으로 emit함.
     * main스레드가 5.5초까지만 실행되므로 0, 1 두개의 데이터를 건너뛰고 2부터 4까지 총 세 개의 숫자만 Subscriber에게 전달됨.
     */
    private static void example1() throws InterruptedException{
        Flux<Long> skip = Flux
                .interval(Duration.ofSeconds(1))
                .skip(2);
        skip
                .subscribe(data -> log.info("# onNext : {}", data));

        Thread.sleep(5500L); // 상위 스레드가 살아 있어 버려서 안죽네;
    }

    private static void example2() throws InterruptedException {
        Flux
                .interval(Duration.ofMillis(300))
                .skip(Duration.ofSeconds(1)) //interval() Operator에서 1초의 시간 내에 emit 되는 데이터는 모두 건너뛰게 됨.
                .subscribe(data -> log.info("# onNext: {}", data));

        Thread.sleep(2000L);
    }
}
