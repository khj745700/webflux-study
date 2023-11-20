package com.webflux.study.operator.error;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * <h2>onErrorContinue()</h2>
 * 예외가 발생했을 때, 예외를 발생시킨 데이터를 건너뛰고 Upstream에서 emit된 다음 데이터를 처리한다.
 * <p>
 *     Reactor 공식 문서에서는 onErrorContinue() Operator가 명확하지 않은 Sequence의 동작으로 개발자가 의도하지 않은 상황을
 *     발생시킬 수 있기 때문에 onErrorContinue() Operator를 신중하게 사용하기를 권고함.
 * </p>
 * <p>
 *     대부분의 에러는 Operator 내부에서 doOnError() Operator를 통해 로그를 기록하고 onErrorResume() Operator 등으로 처리할 수 있다고
 *     명시함.
 * </p>
 */
@Slf4j
public class OnErrorContinue {
    public static void main(String[] args) {
        Flux
                .just(1, 2, 4, 0, 6, 12)
                .map(num -> 12 / num)
                .onErrorContinue((error, num) ->
                        log.error("error: {}, num: {}", error, num))
                .subscribe(data -> log.info("# onNext: {}", data),
                        error -> log.error("# onError: ", error));
    }
}
