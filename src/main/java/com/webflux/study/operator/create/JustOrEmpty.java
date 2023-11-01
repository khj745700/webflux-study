package com.webflux.study.operator.create;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * <h2>JustOrEmpty</h2>
 * <p>
 *     just()의 확장 Operator 로서 just() 와 달리 emit 할 데이터가 null 인 경우 NullpointerException을 발생하지 않고 onCompleteSignal
 *     을 전송함.
 * </p>
 */
@Slf4j
public class JustOrEmpty {
    public static void main(String[] args) {
        Mono
                .justOrEmpty(null)
                .subscribe(data -> {},
                        error -> {},
                        () -> log.info("# onComplete"));
    }
}
