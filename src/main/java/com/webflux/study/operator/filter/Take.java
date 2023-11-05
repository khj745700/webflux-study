package com.webflux.study.operator.filter;

import com.webflux.study.operator.SampleData;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * <h2>take()</h2>
 * Upstream에서 emit되는 데이터 중에서 파라미터로 입력받은 숫자만큼만 Downstream으로 emit함.
 */
@Slf4j
public class Take {
    public static void main(String[] args) throws InterruptedException {
        example1();
        log.info("------------------------------------------------");
        example2();

    }

    private static void example1() throws InterruptedException{
        Flux
                .interval(Duration.ofSeconds(1)) // 0부터 1씩 증가한 숫자를 무한히 emit 함.
                .take(3)  // 그러나, take() Operator에 파라미터로 3을 지정했기 때문에 세 개의 데이터만 Subscriber에게 전달하고 Sequence 종료.
                .subscribe(data -> log.info("# onNext: {}", data));

        Thread.sleep(4000L);
    }

    private static void example2() throws InterruptedException{
        Flux
                .interval(Duration.ofSeconds(1))
                .take(Duration.ofMillis(2500)) // 2.5초 동안 전송함.
                .subscribe(data -> log.info("# onNext: {}", data));

        Thread.sleep(3000L);
    }


}
