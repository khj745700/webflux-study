package com.webflux.study.sinks;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import static reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST;

/**
 * <h2>Sinks.One 예제</h2>
 * - emit 된 데이터 중에서 단 하나의 데이터만 Subscriber 에게 전달한다. 나머지 데이터는 Drop.
 */
@Slf4j
public class SinksOne {
    public static void main(String[] args) throws InterruptedException {
        Sinks.One<String> sinkOne = Sinks.one();
        Mono<String> mono = sinkOne.asMono(); /** {@link AboutSinks} Mono 의 의미 체계를 가진다는 의미.*/

        sinkOne.emitValue("Hello Reactor", FAIL_FAST); // emitValue() 두번째 파라미터는 에러 발생 시 어떻게 처리할 것이지에 대한 핸들러
        sinkOne.emitValue("Hi Reactor", FAIL_FAST);    // 에러 발생 시에 빠르게 실패 처리 하는 것. (재시도 안하고 즉시 실패 처리)
        sinkOne.emitValue(null, FAIL_FAST);

        mono.subscribe(data -> log.info("# Subscriber1 {}", data));
        mono.subscribe(data -> log.info("# Subscriber2 {}", data));
    }
}
