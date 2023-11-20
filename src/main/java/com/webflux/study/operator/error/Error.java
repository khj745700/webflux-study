package com.webflux.study.operator.error;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.zip.DataFormatException;

/**
 * <h2>error()</h2>
 * <p>
 *     파라미터로 지정된 에러로 종료하는 Flux를 생성함.
 * </p>
 * <p>
 *     마치 Java의 throw 키워드를 사용하여 예외를 의도적으로 던지는 것 같은 역할을 하며, 주로 체크 예외를 캐치해서 다시 던져야 하는 경우 사용할 수 있음.
 * </p>
 * <br>
 *     <q>
 *         <h3>체크예외란?</h3>
 *         <p>체크 예외는 Java 에서 Exception 클래스를 상속한 클래스들을 의미하며 try ~ catch 문으로 반드시 처리해야되는 예외임.</p>
 *     </q>
 */
@Slf4j
public class Error {
    public static void main(String[] args) {
        example1();
        log.info("==============================================");
        example2();
    }

    /**
     * <h3>실행 결과</h3>
     * <p>
     *     Upstream에서 emit되는 숫자에 2를 곱한 값이 3의 배수가 되는 경우  Downstream으로 emit을 허용하지 않기 때문에 error() Operator 랄
     *     사용하였음.
     * </p>
     */
    static void example1() {
        Flux
                .range(1, 5)
                .flatMap(num -> {
                    if((num * 2) % 3 == 0 ){
                        return Flux.error(
                                new IllegalArgumentException("Not allowed multiple of 3"));
                    } else {
                        return Mono.just(num * 2);
                    }
                })
                .subscribe(data -> log.info("# onNext: {}", data),
                        error -> log.error("# onError: ", error));
    }

    static void example2() {
        Flux
                .just('a', 'b', '3', 'd')
                .flatMap(letter -> {
                    try {
                        return convert(letter);
                    } catch (DataFormatException e) {
                        return Flux.error(e);
                    }
                })
                .subscribe(data -> log.info("# onNext: {}", data),
                        error -> log.error("# onError: ", error));
    }

    static Mono<String> convert(char ch) throws DataFormatException {
        if(!Character.isAlphabetic(ch)) {
            throw new DataFormatException("Not Alphabetic");
        }
        return Mono.just("Convert to " + Character.toUpperCase(ch));
    }
}
