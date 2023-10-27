package com.webflux.study.testexample;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;
import reactor.test.publisher.TestPublisher;

import java.util.Arrays;
import java.util.List;

/**
 * <h2>잘동작하는(Well-behaved) TestPublisher</h2>
 * <p>emit 하는 데이터가 Null 인지, 요청하는 개수보다 더 많은 데이터를 emit하는 지 등의
 * 리액티브 스트림즈 사양 위반 여부를 사전에 체크한다는 의미임.</p>
 *
 * <h3>TestPublisher가 발생시키는 Signal 유형</h3>
 * <ul>
 *     <li>next(T) 또는 next(T, T, ...): 1개 이상의 onNext signal을 발생시킴.</li>
 *     <li>emit(T ...): 1개 이상의 onNext Signal을 발생시킨 후, onComplete signal을 발생시킴</li
 *     <li>complete(): onComplete Signal을 발생시킴</li>
 *     <li>error(Throwable): onError Signal을 발생시킴</li>
 * </ul>
 *
 *
 */
public class TestPublisherTest {

    @Test
    public void divideByTwoTest1() {
        TestPublisher<Integer> source = TestPublisher.create(); // TestPublisher 생성

        StepVerifier
                .create(GeneralTestExample.divideByTwo(source.flux())) // flux 로 변환.
                .expectSubscription()
                .then(() -> source.emit(2, 4, 6, 8, 10))
                .expectNext(1, 2, 3, 4)
                .expectError()
                .verify();
    }


    /**
     * <h2>오동작하는(Misbehaving) TestPublisher</h2>
     * <p>
     *     리액티브 사양 위반 여부를 사전에 체크하지 않는다는 의미임. 따라서 리액티브 스트림즈 사양에 위반되더라도
     *     TestPublisher는 데이터를 emit할 수 있음.
     * </p>
     *
     * <h3>Misbehaving TestPublisher를 생성하기 위한 위반 조건</h3>
     * <ul>
     *     <li>ALLOW_NULL : 전송할 데이터가 null이어도 NullpointerException을 발생시키지 않고 다음 호출을 진행할 수 있도록 함.</li>
     *     <li>CLEAN_ON_TERMINATE : onComplete, onError, emit 같은 Terminal Signal을 연달아 여러 번 보낼 수 있도록 함.</li>
     *     <li>DEFER_CANCELLATION : cancel Signal을 무시하고 계속해서 Signal을 emit 할 수 있도록 함.</li>
     *     <li>REQUEST_OVERFLOW : 요청 개수보다 더 많은 Signal이 발생하더라도 IllegalStateException 을 발생시키지 않고 다음 호출을 진행할 수 있도록 함.</li>
     * </ul>
     */
    @Test
    public void divideByTwoTest2() {
//        TestPublisher<Integer> source = TestPublisher.create();
        TestPublisher<Integer> source =
                TestPublisher.createNoncompliant(TestPublisher.Violation.ALLOW_NULL);

        StepVerifier
                .create(GeneralTestExample.divideByTwo(source.flux()))
                .expectSubscription()
                .then(() -> {
                    getDataSource().stream()
                            .forEach(data -> source.next(data));
                    source.complete();
                })
                .expectNext(1, 2, 3, 4, 5)
                .expectComplete()
                .verify();
    }

    private static List<Integer> getDataSource() {
        return Arrays.asList(2, 4, 6, 8, null);
    }

}
