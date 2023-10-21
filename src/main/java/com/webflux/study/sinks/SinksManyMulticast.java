package com.webflux.study.sinks;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import static reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST;

/**
 * Sinks 가 Publisher 역할을 할 경우 기본적으로 Hot Publisher 로 동작하며 onBackpressureBuffer() 메서드는 <br>
 * Warm up 의 특징을 가지는 {@link com.webflux.study.sequence.hot.HotSequence} HotSequence 로 동작하기 때문에 <br>
 * 첫 번째 구독이 발생한 시점에 Downstream 으로 데이터가 전달되는 것임.
 */
@Slf4j
public class SinksManyMulticast {
    public static void main(String[] args) {
        Sinks.Many<Integer> multicastSink =
                Sinks.many().multicast().onBackpressureBuffer();
        Flux<Integer> fluxView = multicastSink.asFlux();

        multicastSink.emitNext(1, FAIL_FAST);
        multicastSink.emitNext(2, FAIL_FAST);

        fluxView.subscribe(data -> log.info("# Subscriber1: {}", data));
        fluxView.subscribe(data -> log.info("# Subscriber2: {}", data));

        multicastSink.emitNext(3, FAIL_FAST);
    }
}
