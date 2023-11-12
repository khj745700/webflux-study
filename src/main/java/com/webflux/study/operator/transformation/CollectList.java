package com.webflux.study.operator.transformation;

import com.webflux.study.operator.SampleData;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.stream.Collectors;

/**
 * <h2>collectList()</h2>
 * <p>
 *     Flux에서 emit된 데이터를 모아서 List로 변환한 후, 변환된 List를 emit 하는 Mono를 반환함. 만약 Upstream Sequence가 비어있다면
 *     비어 있는 List를 Downstream으로 emit함.
 * </p>
 */
@Slf4j
public class CollectList {
    /**
     * <h3>실행 결과</h3>
     * collectList()를 통해 Operator를 Upstream에서 emit되는 모스 부호를 해석한 문자를 List로 변환한 후 Subscriber에게 전달.
     * Subscriber는 전달받은 List에 포함된 문자를 Stream을 사용해서 연결한 후, 모스 부호를 최종적으로 해석함.
     */
    public static void main(String[] args) {
        Flux
                .just("...", "---", "...")
                .map(code -> transformMorseCode(code))
                .collectList()
                .subscribe(list -> log.info(list.stream().collect(Collectors.joining())));
    }

    public static String transformMorseCode(String morseCode) {
        return SampleData.morseCodeMap.get(morseCode);
    }
}
