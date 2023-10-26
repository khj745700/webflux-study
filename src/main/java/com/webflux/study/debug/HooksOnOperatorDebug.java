package com.webflux.study.debug;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.Map;

/**
 * <h2>Hooks.onOperatorDebug() 사용의 한계 : </h2>
 * 디버그 모드의 활성화는 다음과 같이 애플리케이션 내에서 비용이 많이 드는 동작 과정을 거침. <br>
 *  - 애플리케이션 내에 있는 모든 Operator 의 스택트레이스를 캡처함. <br>
 *  - 에러가 발생하면 캡처한 정보를 기반으로 에러가 발생한 Assembly의 스택트레이스를 원본 스택트레이스 중간에 끼워 넣는다. <br><br>
 *
 * <h3>따라서 처음부터 디버그 모드를 활성화하는 것은 권하지 않는다.</h3>
 *
 */
@Slf4j
public class HooksOnOperatorDebug {
    public static Map<String, String> fruits = new HashMap<>();

    static {
        fruits.put("banana", "바나나");
        fruits.put("apple", "사과");
        fruits.put("pear", "배");
        fruits.put("grape", "포도");
    }

    /**
     * <h3>실행 결과</h3>
     * ...
     * Assembly trace from producer [reactor.core.publisher.FluxMapFuseable] : <br>
     * 	reactor.core.publisher.Flux.map(Flux.java:6514) <br>
     * 	com.webflux.study.debug.DebugMode.main(DebugMode.java:32) <br> .. 에러 시작점을 알 수 있음.
     * Error has been observed at the following site(s): <br>
     * 	*__Flux.map ⇢ at com.webflux.study.debug.DebugMode.main(DebugMode.java:42) // 에러 전파 상태를 보여줌 <br>
     * 	|_ Flux.map ⇢ at com.webflux.study.debug.DebugMode.main(DebugMode.java:43) // 에러 전파 상태를 보여줌 <br>
     */
    public static void main(String[] args) throws InterruptedException {
        Hooks.onOperatorDebug(); // 디버그 모드 활성화

        Flux
                .fromArray(new String[]{"BANANAS", "APPLES", "PEARS", "MELONS"})
                .subscribeOn(Schedulers.boundedElastic())
                .publishOn(Schedulers.parallel())
                .map(String::toLowerCase)
                .map(fruit -> fruit.substring(0, fruit.length() - 1))
                .checkpoint()
                .map(fruits::get) //
                .map(translated -> "맛있는 " + translated)
                .subscribe(
                        log::info,
                        error -> log.error("# onError:", error));

        Thread.sleep(100L);
    }
}
