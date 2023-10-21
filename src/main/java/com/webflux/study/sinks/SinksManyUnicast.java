package com.webflux.study.sinks;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import static reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST;

/**
 * <h2>Sinks.Many 예제</h2>
 *  - unicast()통해 단 하나의 Subscriber 만 데이터를 전달 받을 수 있다
 */
@Slf4j
public class SinksManyUnicast {
    /**
     * <h2>ManySpec</h2>
     * <ul>
     *     <li>UnicastSpec</li>
     *     <li>MulticastSpec</li>
     *     <li>MulticastReplaySpec</li>
     * </ul>
     * Unicast : 하나의 특정 시스템만 정보를 받는 방식. <br>
     * Multicast : 일부 시스템들만 정로를 받는 방식. <br>
     * Broadcast : 네트워크에 연결된 모든 시스템이 정보를 받는 방식.
     *
     */
    public static void main(String[] args) throws InterruptedException {
        Sinks.Many<Integer> unicastSink = Sinks.many().unicast().onBackpressureBuffer();
        Flux<Integer> fluxView = unicastSink.asFlux();

        unicastSink.emitNext(1, FAIL_FAST);
        unicastSink.emitNext(2, FAIL_FAST);


        fluxView.subscribe(data -> log.info("# Subscriber1: {}", data));

        unicastSink.emitNext(3, FAIL_FAST);

        //fluxView.subscribe(data -> log.info("# Subscriber2: {}", data)); // Unicast 기 때문에 주석을 풀면 에러가 남.
    }
}
