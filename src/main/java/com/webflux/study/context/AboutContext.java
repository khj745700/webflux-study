package com.webflux.study.context;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * <h2>Context 란?</h2>
 * <p style='margin-top:-10'>
 *     원문 : "어떤 것을 이해하는 데 도움이 될 만한 관련 정보나 이벤트, 상황" <br>
 *     Context 는 어떠한 상황에서 그 상황을 처리하기 위해 필요한 정보들을 말함.
 * </p>
 *<br>
 * <h2 style='margin-top:10'>Reactor의 Context</h2>
 * <p style='margin-top:-20'>
 *     <ul>
 *     <li>Operator 체인에 전파되는 키와 값 형태의 저장소임.</li>
 *     <li>Reactor 의 Context 는 ThreadLocal 과 다소 유사한 면이 있지만, 각각의 실행되는 스레드와 매핑되는 ThreadLocal 과 달리
 *     Subscriber 와 매핑됨. <br>
 *     <strong>즉, 구독이 발생할 때마다 해당 구독과 연결된 하나의 Context 가 생긴다</strong></li>
 *     <li>contextWriter() Operator를 사용해서 원본 데이터 소스 레벨에서 Context의 데이터를 읽을 수 있음.</li>
 *     <li>transformDeferredContextual() Operator를 사용해서 Operator 체인의 중간에서 데이터를 읽을 수 있음. </li>
 *     </ul>
 * </p>
 * <br><br>
 *
 * <h3>Context 에 쓰인 데이터 읽기</h3>
 * <ol style='margin-top:-10'>
 *     <li>원본 데이터 소스 레벨에서 읽는 방식</li>
 *     - deferContextual() Operator 사용해서 함. <br>
 *     - 파라미터가 ContextView 타입 객체임. 저장된 객체를 읽을 때는 ContextView 를 사용한다는 사실.
 *     <li>Operator 체인 중간에서 읽는 방식</li>
 *     - transformDeferredContextual() Operator 를 사용함.
 * </ol>
 */
@Slf4j
public class AboutContext {
    /**
     * <h3>실행 결과</h3>
     * Context 에 저장된 데이터를 정상적으로 두 번 읽어 오는 것을 알 수 있음.
     * subscribeOn() Operator 가 subscriber 구독 직후 Publisher 에서 데이터를 emit 하는데, 그 스레드를 지정하몄음.
     * publishOn() Operator 이후 오는 downstream 들은 parallel-1로 동작하는 것을 확인할 수 있음.
     * Operator 체인 상의 서로 다른 스레드들이 Context에 저장된 데이터에 쉽게 접근할 수 있음을 알 수 있음.
     */
    public static void main(String[] args) throws InterruptedException {
        Mono
                .deferContextual(ctx ->
                        Mono
                                .just("Hello" + " " + ctx.get("firstName"))
                                .doOnNext(data -> log.info("# just doOnNext : {}", data))
                )
                .subscribeOn(Schedulers.boundedElastic())
                .publishOn(Schedulers.parallel())
                .transformDeferredContextual(
                        (mono, ctx) -> mono.map(data -> data + " " + ctx.get("lastName"))
                )
                .contextWrite(context -> context.put("lastName", "Jobs"))
                .contextWrite(context -> context.put("firstName", "Steve"))
                .subscribe(data -> log.info("# onNext: {}", data));

        Thread.sleep(100L);
    }
}
