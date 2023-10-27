package com.webflux.study.testexample;


import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.StepVerifierOptions;
import reactor.test.publisher.TestPublisher;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * <h2>StepVerifier를 사용한 테스팅</h2>
 * <ul>
 *     <li>Reactor Sequence에서 발생하는 Signal 이벤트를 테스트 할 수 있다.</li>
 *     <li>withVirtualTime() 과 VirtualTimeScheduler() 등을 이용해서 시간 기반 테스트를 진행할 수 있다.</li>
 *     <li>thenConsumeWhile()을 이용해서 Backpressure 테스트를 진행할 수 있다.</li>
 *     <li>expectAccessibleContext()를 이용해서 Sequence에 전파되는 Context를 테스트 할 수 있다.</li>
 *     <li>recordWith(), consumeRecordedWith(), expectRecordedMatches() 등을 이용해서 Record 기반 테스트를 할 수 있다.</li>
 * </ul>
 * <br><br><br>
 * <h2>expectXXXX() 메서드</h2>
 * <table>
 *     <tr>
 *         <th>메서드</th>
 *         <th>설명</th>
 *     </tr>
 *     <tr>
 *         <td>expectedSubscription()</td>
 *         <td>구독이 이루어짐을 기대한다.</td>
 *     </tr>
 *     <tr>
 *         <td>expectNext(T t)</td>
 *         <td>onNext Signal을 통해 전달되는 값이 파라미터로 전달된 값과 같음을 기대한다.</td>
 *     </tr>
 *      <tr>
 *         <td>expectComplete()</td>
 *         <td>onComplete Signal이 전송되기를 기대한다.</td>
 *     </tr>
 *      <tr>
 *         <td>expectNextCount(long count)</td>
 *         <td>구독 시점 또는 이전(previous) exoectNext()를 통해 기대값이 평가된 데이터 이후부터 emit된 수를 기대한다.</td>
 *     </tr>
 *      <tr>
 *         <td>exepectNoEvent(Duration duration)</td>
 *         <td>주어진 시간동안 Signal 이벤트가 발생하지 않았음을 기대한다.</td>
 *     </tr>
 *      <tr>
 *         <td>expectAccessibleContext()</td>
 *         <td>구독 시점 이후에 Context가 전파되었음을 기대한다.</td>
 *     </tr>
 *     <tr>
 *         <td>expectNextSequence(Iterable<? expends T> iterable)</td>
 *         <td>emit된 데이터들이 파라미터로 전달된 Iterable의 요소와 매치됨을 기대한다.</td>
 *     </tr>
 * </table>
 * <br><br><br>
 * <h2>verifyXXXX() 메서드</h2>
 * 테스드 대상 Operator 체인에 대한 검증을 트리거하는 메서드.
 * <table>
 *     <tr>
 *         <th>메서드</th>
 *         <th>설명</th>
 *     </tr>
 *     <tr>
 *         <td>verify()</td>
 *         <td>검증을 트리거한다.</td>
 *     </tr>
 *     <tr>
 *         <td>verifyComplete()</td>
 *         <td>검증을 트리거하고, onComplete Signal을 기대한다.</td>
 *     </tr>
 *     <tr>
 *         <td>verifyError()</td>
 *         <td>검증을 트리거하고, onError Signal을 기대한다.</td>
 *     </tr>
 *     <tr>
 *         <td>verifyTimeout(Duration duration)</td>
 *         <td>검증을 트리거하고, 주어진 시간이 초과되어도 Publisher가 종료되지 않음을 기대한다.</td>
 *     </tr>
 * </table>
 */
public class StepVerifierTest {

    /**
     * <h3>Signal 이벤트 테스트</h3>
     */
    @Test
    public void sayHelloReactorTest() {
        StepVerifier
                .create(Mono.just("Hello Reactor")) // 테스트 대상 Sequence 생성
                .expectNext("Hello Reactor")    // emit 된 데이터 검증
                .expectComplete()   // onComplete Signal 검증
                .verify();          // 검증 실행.
    }

    /**
     * <h3>실행 결과</h3>
     * <p>as() 를 사용해서 이전 기댓값 평가 단계에 대한 설명(description)을 추가할 수 있음.
     * 만약 테스트에 실패하게 되면 실패한 단계에 해당하는 설명이 로그로 출력됨.
     * <br>
     * <code>
     *     expectation "# expect Hi" failed (expected value: Hi; actual value: Hello)
     * </code>
     * </p>
     */
    @Test
    public void sayHelloTest() {
        StepVerifier
                .create(GeneralTestExample.sayHello())
                .expectSubscription()
                .as("# expect subscription")
                .expectNext("Hi")
                .as("# expect Hi")
                .expectNext("Reactor")
                .as("# expect Reactor")
                .verifyComplete();
    }


    @Test
    public void divideByTwoTest() {
        Flux<Integer> source = Flux.just(2, 4, 6, 8, 10);
        StepVerifier
                .create(GeneralTestExample.divideByTwo(source))
                .expectSubscription()
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectNext(4)
//                .expectNext(1, 2, 3, 4) // expectNext()는 1개 이상의 emit된 데이터를 평가할 수 있기 때문에 바꿔도 동작함.
                .expectError() // 0이기 때문에 ArithmeticException 이 터지므로 OK.
                .verify();
    }

    @Test
    public void takeNumberTest() {
        Flux<Integer> source = Flux.range(0, 1000);
        StepVerifier
                .create(GeneralTestExample.takeNumber(source, 500),
                        StepVerifierOptions.create().scenarioName("Verify from 0 to 499"))
                .expectSubscription() // 구독이 발생했음을 기대함.
                .expectNext(0) // 숫자 0이 emit 되었음을 기대함.
                .expectNextCount(498) // 498개의 숫자가 emit 되었음을 기대함.
                .expectNext(500) // expectNext() 로 숫자 500이 emit 되었음을 기대함.
                .expectComplete() // expectComplete() 으로 onComplete Signal이 전송되었음을 기대함.
                .verify();
    }

// 시간 기반 테스트 ---------------------------------------------------------------------------------------------------------
    @Test
    public void getCOVID19CountTest1() {
        StepVerifier
                .withVirtualTime(() -> TimeBasedTestExample.getCOVID19Count(
                                Flux.interval(Duration.ofHours(1)).take(1) // 1시간 이후에 시작한다.
                        )
                )
                .expectSubscription()
                .then(() -> VirtualTimeScheduler // 가상 시간 스케줄러
                        .get()
                        .advanceTimeBy(Duration.ofHours(1))) // 1시간 미리 앞당긴다.
                .expectNextCount(11)
                .expectComplete()
                .verify();

    }

    @Test
    public void getCOVID19CountTest2() {
        StepVerifier
                .create(TimeBasedTestExample.getCOVID19Count(
                                Flux.interval(Duration.ofMinutes(1)).take(1)
                        )
                )
                .expectSubscription()
                .expectNextCount(11)
                .expectComplete()
                .verify(Duration.ofSeconds(3)); //1분이후 시작인데 3초이내에 해결되기를 검증하니 시간초과 되었음.
    }

    @Test
    public void getVoteCountTest() {
        StepVerifier
                .withVirtualTime(() -> TimeBasedTestExample.getVoteCount(
                                Flux.interval(Duration.ofMinutes(1))
                        )
                )
                .expectSubscription()
                .expectNoEvent(Duration.ofMinutes(1)) // 1분동안 어떤 Signal Event 발생하지 않았음을 기대. 그리고 지정한 시간만큼 시간을 앞당김.
                .expectNoEvent(Duration.ofMinutes(1))
                .expectNoEvent(Duration.ofMinutes(1))
                .expectNoEvent(Duration.ofMinutes(1))
                .expectNoEvent(Duration.ofMinutes(1))
                .expectNextCount(5)
                .expectComplete()
                .verify();
    }

// Backpressure 테스트 ---------------------------------------------------------------------------------------------------
    @Test
    public void generateNumberTest1() {
        StepVerifier
                .create(BackpressureTestExample.generateNumber(), 1L) // ERROR 전략이기 때문에 Overflow Exception을 던질 것임.
                .thenConsumeWhile(num -> num >= 1)
                .verifyComplete();
    }

    @Test
    public void generateNumberTest2() {
        StepVerifier
                .create(BackpressureTestExample.generateNumber(), 1L)
                .thenConsumeWhile(num -> num >= 1)
                .expectError() //에러가 발생함을 기대하는 코드
                .verifyThenAssertThat() //검증을 트리거 한 후 추가적인 Assertion을 할 수 있음.
                .hasDroppedElements(); // Drop 되는 데이터가 Backpressure의 Error 전략에서 존재함.
    }

// Context 테스트 --------------------------------------------------------------------------------------------------------
    @Test
    public void getSecretMessageTest() {
        Mono<String> source = Mono.just("hello");

        StepVerifier
                .create(
                        ContextTestExample
                                .getSecretMessage(source)
                                .contextWrite(context ->
                                        context.put("secretMessage", "Hello, Reactor"))
                                .contextWrite(context -> context.put("secretKey", "aGVsbG8="))
                )
                .expectSubscription()
                .expectAccessibleContext()
                .hasKey("secretKey")
                .hasKey("secretMessage")
                .then() // Sequence의 다음 Signal의 에빈트의 기댓값을 평가할 수 있도록 함.
                .expectNext("Hello, Reactor")
                .expectComplete()
                .verify();
    }

// Record 기반 테스트 -----------------------------------------------------------------------------------------------------

    /**
     * epexctNext()로 emit된 데이터의 단순 기댓값만 평가하는 것이 아니라 조금 더 구체적인 조건으로 Assertion 해야 하는 경우가 많음.
     * 이 경우 recordWith() 사용 가능. recordWith()는 파라미터로 전달한 Java의 컬렉션에 emit 된 데이터를 추가 하는 세션을 시작함.
     */
    @Test
    public void getCityTest() {
        StepVerifier
                .create(RecordTestExample.getCapitalizedCountry(
                        Flux.just("korea", "england", "canada", "india")))
                .expectSubscription()
                .recordWith(ArrayList::new) // emit 된 데이터에 대한 기록을 시작.
                .thenConsumeWhile(country -> !country.isEmpty()) // 파라미터로 전달한 Predicate과 일치하는 데이터는 다음 단계에서 소비할 수 있도록 함.
                .consumeRecordedWith(countries -> {             // 컬렉션에 기록된 데이터를 소비함. 여기서는 모든 데이터의 첫 글자가 대문자인이 여부 확인함.
                    assertThat(
                            countries
                                    .stream()
                                    .allMatch(country ->
                                            Character.isUpperCase(country.charAt(0))),
                            is(true)
                    );
                })
                .expectComplete()
                .verify();
    }

    @Test
    public void getCountryTest() {
        StepVerifier
                .create(RecordTestExample.getCapitalizedCountry(
                        Flux.just("korea", "england", "canada", "india")))
                .expectSubscription()
                .recordWith(ArrayList::new)
                .thenConsumeWhile(country -> !country.isEmpty())
                .expectRecordedMatches(countries -> // assertThat이 아닌 녀석을 사용. 코드 더 간결해짐.
                        countries
                                .stream()
                                .allMatch(country ->
                                        Character.isUpperCase(country.charAt(0))))
                .expectComplete()
                .verify();
    }

}
