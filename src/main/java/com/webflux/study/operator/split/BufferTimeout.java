package com.webflux.study.operator.split;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * <h2>bufferTimeout()</h2>
 * <p>
 *     bufferTimeout(maxSize, maxTime) Operator는 Upstream에서 emit되는 첫 번째 데이터부터 maxSize 숫자만큼의 데이터 또는
 *     maxTime 내에 emit되는 데이터를 List 버퍼로 한 번에 emit함.
 * </p>
 * <p>
 *     또한 maxSize나 maxTime 중에서 먼저 조건에 부합할 때까지 emit된 데이터를 List버퍼로 emit함.
 * </p>
 */
@Slf4j
public class BufferTimeout {
    /**
     * <h3>실행 결과</h3>
     * <p>
     *     bufferTimeout의 maxSize = 3, maxTime은 0.4초 이기 때문에 range() Operator에서 emit된 숫자 중에서 10보다 더 작은 숫자는
     *     0.1초의 지연 시간을 가지므로 0.4초가 지나기 전에 버퍼의 maxSize에 도달하게 됨. 따라서 List 버퍼로 세 개씩전달되는 것을 확인할 수 있음.
     * </p>
     * <p>
     *     10 이상의 숫자는 0.3초의 지연 시간을 가지기 때문에 버퍼에 2개가 담긴 시점에 maxTime에 도달하므로 List 버퍼로 두 개씩 전달되는 것을
     *     확인할 수 있음.
     * </p>
     */
    public static void main(String[] args) {
        Flux
                .range(1, 20)
                .map(num -> {
                    try {
                        if (num < 10) {
                            Thread.sleep(100L);
                        } else {
                            Thread.sleep(300L);
                        }
                    } catch (InterruptedException e) {}
                    return num;
                })
                .bufferTimeout(3, Duration.ofMillis(400L))
                .subscribe(buffer -> log.info("# onNext: {}", buffer));
    }
}
