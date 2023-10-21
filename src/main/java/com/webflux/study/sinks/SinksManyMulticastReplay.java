package com.webflux.study.sinks;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import static reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST;


/**
 * <h3>replay</h3>
 * replay 모드는 MP3의 replay; 현재 재생 목록이 처음부터 다시 재생되는 것
 */
@Slf4j
public class SinksManyMulticastReplay {
    /**
     * <h3>실행 결과</h3>
     * 실행 결과를 보면 첫 Subscriber의 입장에서 구독 시점에 이미 세 개의 데이터가 emit 되어 있음. <br>
     * 따라서 마지막 2개를 뒤로 되돌린 숫자가 2이므로 2부터 전달됨.
     */
    public static void main(String[] args) {
        // limit : emit 데이어 중에서 파라미터로 입력한 개수만큼 가장 나중에 emit 된 데이터부터 Subscriber에게 전달하는 기능을 함.
        // 즉, emit 된 데이터 중에서 2개만 뒤로 돌려서 전달하겠단 의미.
        // all 메서드도 존재하는데 이 메서드는 처음 emit된 데이터부터 다시 재 emit 한다는 메서드.
        Sinks.Many<Integer> replaySink = Sinks.many().replay().limit(2);
        Flux<Integer> fluxView = replaySink.asFlux();

        replaySink.emitNext(1, FAIL_FAST);
        replaySink.emitNext(2, FAIL_FAST);
        replaySink.emitNext(3, FAIL_FAST);

        fluxView.subscribe(data -> log.info("# Subscriber1: {}", data));

        replaySink.emitNext(4, FAIL_FAST);

        fluxView.subscribe(data -> log.info("# Subscriber2: {}", data));
    }
}
