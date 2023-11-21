package com.webflux.study.operator.multicast;

import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * <h2>refCount()</h2>
 * <p>
 *     파라미터로 입력된 숫자만큼의 구독이 발생하는 시점에 Upstream에 연결되며, 모든 구독이 취소되거나 Upstream의 데이터 emit이 종료되면
 *     연결이 해제됨.
 * </p>
 * <p>
 *     주로 무한 스트림 상황에서 모든 구독이 취소될 경우 연결을 해제하는 데 사용할 수 있음.
 * </p>
 */
@Slf4j
public class RefCount {
    /**
     * <h3>구문 분석</h3>
     * <p>
     *     1개의 구독이 발생하는 시점에 Upstream 소스에 연결되도록 했음.
     * </p>
     * <p>
     *     그런데 첫 번째 구독이 발생한 이후 2.1초 후에 구독을 해제하였음.
     * </p>
     * <p>
     *     이 시점에는 모든 구독이 취소된 상태이기 때문에 연결이 해제되고, 두 번째 구독이 발생할 경우에는 Upstream 소스에 다시 연결됨.
     * </p>
     * <br>
     * <h3>실행 결과</h3>
     * <p>
     *     Subscriber 1은 interval() Operator가 emit한 숫자 3까지 전달받아 출력하지만, 이후에는 구독이 취소되기 때문에 연결이 해제됨.
     * </p>
     * <p>
     *     그런데 refCount() 의 파라미터를 1로 지정했기 때문에 두 번째 구독이 발생하면 Upstream 소스에 새롭게 연결되어 interval() Operator가
     *     0부터 다시 숫자를 emit함.
     * </p>
     * <p>
     *     refCount()의 동작이 이해되지 않으면, publish().refCount(1)을 주석처리하고 publish().autoConnect(1)을 주석 해제한 뒤 코드 다시 실행.
     * </p>
     *
     */
    public static void main(String[] args) throws InterruptedException {
        Flux<Long> publisher =
                Flux
                        .interval(Duration.ofMillis(500))
//                        .publish().autoConnect(1);
                        .publish().refCount(1);

        Disposable disposable =
                publisher.subscribe(data -> log.info("# subscriber 1: {}", data));

        Thread.sleep(2100L);
        disposable.dispose();

        publisher.subscribe(data -> log.info("# subscriber 2: {}", data));

        Thread.sleep(2500L);

    }
}
