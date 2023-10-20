package com.webflux.study.backpressure;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
/**
 * <p>
 * <strong>Backpressure</strong> 는 Publisher 가 끊임없이 emit 하는 무수히 많은 데이터를 적절하게 제어하여 데이터 처리에 있어 과부하가 걸리지 않도록 제어하는 데이터 처리 방식임.
 * </p>
 * <p>
 * Reactor 에서 지원하는 <strong>Backpressure</strong> 처리 방식에는 데이터 요청 개수를 제어하는 방식, <strong>Backpressure 전략</strong>을 사용하는 방식 등이 있다.
 * </p>
 * <p>
 * Backpressure <strong>IGNORE</strong> 전략은 Backpressure 를 적용하지 않는 전략임.
 * </p>
 * <p>
 * Backpressure <strong>ERROR</strong> 전략은 Downstream 의 데이터 처리 속도가 느려서 Upstream 의 emit 속도를 따라가지 못할 경우 에러를 발생시키는 전략임.
 * </p>
 * <p>
 * Backpressure <strong>DROP</strong> 전략은 Publisher 가 Downstream 으로 전달할 데이터가 버퍼에 가득 찰 경우, 버퍼 밖에서 대기중인 데이터 중에서 먼저 emit 된 데이터 부터 Drop 하는 전략임.
 * </p>
 * <p>
 * Backpressure <strong>LATEST</strong> 전략은 Publisher 가 Downstream 으로 전달할 데이터가 버퍼에 가득 찰 경우, 버퍼 박에서 대기 중인 데이터 중에서 가장 최근에(나중에) emit 된 데이터부터 버퍼에 채우는 전략
 * </p>
 * <p>
 * Backpressure <strong>BUFFER</strong> 전략은 버퍼의 데이터를 폐기하지 않고 버퍼링을 하는 전략, 버퍼가 가득 차면 버퍼 내의 데이터를 페기하는 전략, 버퍼가 가득 차면 에러를 발생시키는 전략 등으로 구분.
 * </p>
 * <ul>
 *     <li>
 *      Backpressure <strong>BUFFER DROP_LATEST</strong> 전략은 Publisher 가 Downstream 으로 전달할 데이터가 버퍼에 가득 찰 경우, 가장 최근에(나중에) 버퍼 안에 채워진 데이터를 DROP 하는 전략
 *      </li>
 *      <li>
 *      Backpressure <strong>BUFFER DROP_OLDEST</strong> 전략은 Publisher 가 Downstrema 으로 전달할 데이터가 버퍼에 가득 찰 경우, 버퍼 안에 채워진 데이터 중에서 가장 오래된 데이터를 DROP 하는 전략
 *      </li>
 * </ul>
 */
@Slf4j
public class AboutBackpressure {
    public static void main(String[] args) {
        // Subscriber 가 적절히 처리할 수 있는 수준의 데이터 개수를 Publisher 에게 요청하는 것임.
        Flux.range(1, 5)
                .doOnRequest(data -> log.info("# doOnRequest: {}", data)) // Subscriber 가 요청한 데이터의 요청 개수를 로그로 출력하도록 함.
                .subscribe(new BaseSubscriber<Integer>() { // BaseSubscriber 는 Subscriber 의 구현체
                    /**
                     * hookOnSubscribe() 메서드는 Subscriber 인터페이스에 정의된 onSubscribe() 메서드를 대신해 구독 시점에 request() 메서드를 호출해서
                     * 최초 데이터 요청 개수를 제어하는 역할.
                     */
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        log.info("# hookOnSubscribe: {}", "action");
                        request(1); // request 를 통해서 적절한 데이터를 요청하는 방식
                    }

                    /**
                     * Subscriber 인터페이스에 정의된 onNext() 메서드를 대신해 Publisher가 emit 한 데이터를 전달받아 처리한 후에
                     * Publisher 에게 또다시 데이터를 요청하는 역할
                     */
                    @SneakyThrows
                    @Override
                    protected void hookOnNext(Integer value) {
                        Thread.sleep(2000L);
                        log.info("# hookOnNext: {}", value);
                        request(1);
                    }
                });
    }
}
