package com.webflux.study.operator.peek;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

/**
 * <h2>doOnXXXX()</h2>
 * <p>
 *  Reactor에서 Upstream Publisher 에서 emit 되는 데이터의 변경 없이 부수 효과(side Effect)만을 수행하기 위한 Operator들.
 *  <q>
 *      <h3>부수 효과</h3>
 *      <p>
 *          함수형 프로그래밍에서는 함수가 정해진 결과를 돌려주는 것 이외의 어떤 일을 하게 되면 부수 효과가 있는 함수라고 부름.
 *          함수형 프로그래밍에서는 외부의 상태를 변경하거나 함수로 전달되는 파라미터의 상태가 변경되는 것 역시 부수 효과가 있다고 말함.
 *          또한 함수가 값을 리턴하지 않는 Void 형이면 어떤 식으로든 다른 일을 할 수 밖에 없기 때문에 리턴 값이 없는 함수는 부수 효과가 있다고 봐도 무방.
 *      </p>
 *  </q>
 * </p>
 * <p>
 *     doOnNext() 으로 시작하는 Operator는 Consumer 또는 Runnable 타입의 함수형 인터페이스를 파라미터로 가지기 때문에 별도의 리턴값이 없음.
 *     따라서 UpStream Publisher 로부터 emit 되는 데이터를 통해 Upstream Publisher의 내부 동작을 엿볼 수 있으며 로그를 출력하는 등의 디버깅
 *     용도로 많이 사용됨.
 * </p>
 * <table>
 *     <tr>
 *         <th>Operator</th>
 *         <th>설명</th>
 *     </tr>
 *     <tr>
 *         <td>doOnSubscribe</td>
 *         <td>Publisher가 구독 중일 때 트리거되는 동작을 추가할 수 있음.</td>
 *     </tr>
 *     <tr>
 *         <td>doOnRequest()</td>
 *         <td>Publisher가 요청을 수신할 때 트리거되는 동작을 추가할 수 있음.</td>
 *     </tr>
 *     <tr>
 *         <td>doOnNext()</td>
 *         <td>Publisher가 데이터를 emit할 때 트리거되는 동작을 추가할 수 있음.</td>
 *     </tr>
 *     <tr>
 *         <td>doOnComplete()</td>
 *         <td>Publisher가 성공적으로 완료했을 때 트리거되는 동작을 추가할 수 있음.</td>
 *     </tr>
 *     <tr>
 *         <td>doOnError()</td>
 *         <td>Publisher가 에러가 발생한 상태로 종료되었을 때 트리거되는 동작을 추가할 수 있음.</td>
 *     </tr>
 *     <tr>
 *         <td>doOnCancel()</td>
 *         <td>Publisher가 취소되었을 때 트리거되는 동작을 추가할 수 있음.</td>
 *     </tr>
 *     <tr>
 *         <td>doOnTerminate()</td>
 *         <td>Publisher가 성공적으로 완료되었을 때 에러가 발생한 상태로 종료되었을 때 트리거 되는 동작을 추가할 수 있음.</td>
 *     </tr>
 *     <tr>
 *         <td>doOnEach()</td>
 *         <td>Publisher가 데이터를 emit할 때, 성공적으로 완료되었을 때, 에러가 발생한 상태로 종료되었을 때 트리거되는 동작을 추가할 수 있음.</td>
 *     </tr>
 *     <tr>
 *         <td>doOnDiscard()</td>
 *         <td>Upstream에 있는 전체 Operator 체인의 동작 중에서 Operator에 의해 폐기되는 요소를 조건부로 정리할 수 있음.</td>
 *     </tr>
 *     <tr>
 *         <td>doAfterTerminate()</td>
 *         <td>Downstream을 성공적으로 완료한 직후 또는 에러가 발생하여 Publisher가 종료된 직후에 트리거되는 동작을 추가할 수 있음.</td>
 *     </tr>
 *     <tr>
 *         <td>doFirst()</td>
 *         <td>Publisher가 구독되기 전에 트리거되는 동작을 추가할 수 있음.</td>
 *     </tr>
 *     <tr>
 *         <td>doFinally()</td>
 *         <td>에러를 포함해서 어떤 이유든 간에 Publisher가 종료된 후 트리거되는 동작을 추가할 수 있음.</td>
 *     </tr>
 * </table>
 *
 */

@Slf4j
public class DoOnXXXX {
    public static void main(String[] args) {
        Flux.range(1, 5)
                .doFinally(signalType -> log.info("# doFinally 1: {}", signalType))
                .doFinally(signalType -> log.info("# doFinally 2: {}", signalType))
                .doOnNext(data -> log.info("# range > doOnNext(): {}", data))
                .doOnRequest(data -> log.info("# doOnRequest: {}", data))
                .doOnSubscribe(subscription -> log.info("# doOnSubscribe 1"))
                .doFirst(() -> log.info("# doFirst()"))
                .filter(num -> num % 2 == 1)
                .doOnNext(data -> log.info("# filter > doOnNext(): {}", data))
                .doOnComplete(() -> log.info("# doOnComplete()"))
                .subscribe(new BaseSubscriber<>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(1);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        log.info("# hookOnNext: {}", value);
                        request(1);
                    }
                });
    }
}
