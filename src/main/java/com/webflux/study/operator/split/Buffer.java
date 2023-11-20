package com.webflux.study.operator.split;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * <h2>buffer()</h2>
 * <p>
 *     Upstream에서 emit되는 첫 번째 데이터부터 maxSize 숫자만큼의 데이터를 List 버퍼로 한 번에 emit함.
 * </p>
 * <p>
 *     마지막 버퍼에 포함된 데이터의 개수는 maxSize보다 더 적거나 같음.
 * </p>
 * <p>
 *     buffer()의 경우 입력으로 들어오는 데이터가 maxSize가 되기 전에 어떤 오류로 인해 들어오지 못하게 되는 상황이 발생할 경우, 애플리케이션은
 *     maxSize가 될 때까지 무한정 기다리게 됨.
 * </p>
 */
@Slf4j
public class Buffer {
    public static void main(String[] args) {
        Flux.range(1, 95)
                .buffer(10)
                .subscribe(buffer -> log.info("# onNext: {}", buffer));
    }
}
