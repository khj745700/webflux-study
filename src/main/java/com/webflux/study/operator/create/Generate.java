package com.webflux.study.operator.create;

import com.webflux.study.operator.SampleData;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Locale;
import java.util.Map;


/**
 * <h2>generate()</h2>
 * 프로그래밍 방식으로 Signal 이벤트를 발생시키며, 특히 동기적으로 데이터를 하나씩 순차적으로 emit 하고자 할 경우 사용됨.
 */
@Slf4j
public class Generate {
    public static void main(String[] args) {
        example1();
        log.info("--------------------------------------------");
        example2();
        log.info("--------------------------------------------");
        example3();
    }


    private static void example1(){
        Flux
                .generate(() -> 0, (state, sink) -> { // 첫번째 파라미터에서 초깃값을 0으로 지정, 두 번째 파라미터에서 전달받은 SynchronousSink는 하나의 Signal만 동기적으로 발생시킬 수 있으며 최대 하나의 상태 값만 emit 하는 인터페이스
                    sink.next(state);
                    if(state == 10) {
                        sink.complete(); // 10일 경우 onComplete Signal을 발생시켜 Sequence 가 종료되도록 함.
                    }
                    return ++state;
                })
                .subscribe(data -> log.info("# onNext: {}", data));
    }

    private static void example2() {
        final int dan = 3;
        Flux
                .generate(() -> Tuples.of(dan, 1), (state, sink) -> { // Tuple을 객체 상태 값으로 사용함. 첫번째 파라미터인 Callable의 리턴 값과 두 번째 파라미터인 BiFunction의 리턴 값이 모두 Tuples 객체임.
                    sink.next(state.getT1() + " * " + state.getT2() + " = " + state.getT1() * state.getT2());
                    if(state.getT2() == 0) {
                        sink.complete();
                    }
                    return Tuples.of(state.getT1(), state.getT2() + 1); // Tuple 객체의 두 번째의 값을 1씩 증가시켜 주고, 9번 라인에서 이 Tuples 객체의 두 번째 값으로 조건을 지정해 onComplete Signal 을 발생시키고 있음.
                }, state -> log.info("# 구구단 {}단 종료!", state.getT1())) // Sequence가 모두 종료되면 세 번째 파라미터인 Consumer를 통해서 후처리 후 로그를 처리함.
                .subscribe(data -> log.info("# onNext : {}", data));

    }

    private static void example3() {
        Map<Integer, Tuple2<Integer, Long>> map = SampleData.getBtcTopPricesPerYearMap();

        Flux
                .generate(() -> 2019, (state, sink) -> {
                    if (state > 2021) {
                        sink.complete();
                    } else {
                        sink.next(map.get(state));
                    }

                    return ++ state;
                })
                .subscribe(data -> log.info("# onNext : {} ", data ));
    }
}
