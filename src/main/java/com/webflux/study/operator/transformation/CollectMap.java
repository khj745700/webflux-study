package com.webflux.study.operator.transformation;

import com.webflux.study.operator.SampleData;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * <h2>collectMap()</h2>
 * <p>
 *     Flux에서 emit된 데이터를 기반으로 key와 vlaue를 생성하여 Map의 Element로 추가한 후, 최종적으로 Map을 emit하는 Mono를 반환함.
 * </p>
 * <p>
 *     만약 Upstream Sequence가 비어있다면 비어 있는 Map을 Downstream으로 emit함.
 * </p>
 */
@Slf4j
public class CollectMap {
    public static void main(String[] args) {
        Flux
                .range(0, 26)
                .collectMap(key -> SampleData.morseCodes[key],
                        value -> transformToLetter(value))
                .subscribe(map -> log.info("# onNext: {}", map));
    }


    private static String transformToLetter(int value) {
        return Character.toString((char) ('a' + value));
    }
}
