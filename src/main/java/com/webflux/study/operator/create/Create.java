package com.webflux.study.operator.create;


import com.webflux.study.operator.CryptoCurrencyPriceEmitter;
import com.webflux.study.operator.CryptoCurrencyPriceListener;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.List;

/**
 * <h2>create() Operator</h2>
 * generate() Operator 처럼 프로그래밍 방식으로 Signal 이벤트를 발생시키지만 generate() Operator 와 몇 가지 차이점이 있음.
 * <ul>
 *     <li>generate() Operator는 데이터를 동기적으로 한 번에 한 건씩 emit 할 수 있는 반면에, create()는 한 번에 여러 건의 데이터를 비동기적으로 emit할 수 있음.</li>
 * </ul>
 */
@Slf4j
public class Create {

    final static List<Integer> DATA_SOURCE = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
    public static void main(String[] args) throws InterruptedException {
        example1();
        log.info("--------------------------------------------");
        example2();
        log.info("--------------------------------------------");
        example3();
    }



    static int SIZE = 0;
    static int COUNT = -1;

    /**
     * <h3>create() pull 방식 동작 과정</h3>
     * <ol>
     *     <li>BaseSubscriber의 hookOnSubscribe() 메서드 내부에서 request(2)를 호출하여 한번에 두 개의 메서드를 요청함.</li>
     *     <li>Subscriber 쪽에서 request() 메서드를 호출하면 create() Operator 내부에서 sink.onRequest() 메서드의 람다 표현식이 실행됨.</li>
     *     <li>Subscriber가 요청한 개수만큼 데이터를 emit함.</li>
     *     <li>Subscriber의 hookOnNext() 메서드 내부에서 emit 된 데이터를 로그로 출력한 후, 다시 request(2)를 호출하여 두 개의 데이터를 요청함.</li>
     *     <li>2에서 4의 과정이 반복되다가 dataSource List의 숫자를 모두 emit 하면 onComplete Signal을 발생시킴.</li>
     *     <li>BaseSubscriber의 hookOnComplete() 메서드 내부에서 종료 로그를 출력함.</li>
     *     <li>sink.onDispose()의 람다 표현식이 실행되어 후처리 로그를 출력함.</li>
     * </ol>
     *
     * <p>이 예제에서의 create() 는 Subscriber가 request() 메서드를 통해 요청을 보내면 Publisher가 해당 요청 개수만큼의 데이터를 emit 하는 일종의 pull 방식으로 데이터를 처리함.</p>
     *
     */
    private static void example1(){
        log.info("# start");
        Flux.create((FluxSink<Integer> sink) -> {
            sink.onRequest(n -> {
                try {
                    Thread.sleep(1000L);
                    for (int i = 0 ; i < n; i++){
                        if (COUNT >= 9) {
                            sink.complete();
                        } else {
                            COUNT++;
                            sink.next(DATA_SOURCE.get(COUNT));
                        }
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

            sink.onDispose(() -> log.info("# clean up")); // disopse의 의미는 FluxSink의 관점에서 FluxSink가 더이상 사용되지 않는다는 의미임.
        }).subscribe(new BaseSubscriber<Integer>() { // Subscriber 가 직접 요청 개수를 지정하기 위해서 BaseSubscriber 를 사용함.
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(2);
            }

            @Override
            protected void hookOnNext(Integer value) {
                SIZE ++;
                log.info("# onNext : {}", value);
                if(SIZE == 2) {
                    request(2);
                    SIZE = 0;
                }
            }

            @Override
            protected void hookOnComplete() {
                log.info("# onComplete");
            }
        });
    }

    /**
     * <h3>create() push 방식 동작 과정</h3>
     * 특정 암호 화폐의 가격 변동이 있을 때마다 변동되는 가격 데이터를 Subscriber에게 비동기적으로 emit 하는 상황을 시뮬레이션한 예제 코드임.
     * <ol>
     *     <li>구독이 발생하면 create() Operator의 파라미터인 람다 표현식이 실행됨.</li>
     *     <li>암호 화폐의 가격 변동이 발생하면 변동된 가격 데이터를 emit할 수 있도록 CryptoCurrencyPriceEmitter가 CryptoCurrencyPriceListener클래스의 onPrice() 메서드가 호출됨.</li>
     *     <li>암호 화폐의 가격 변동이 발생하기 전에 3초의 지연 시간을 가짐.</li>
     *     <li>암호 화폐의 가격 변동이 발생하는 것을 시뮬레이션하기 위해 CryptoCurrencyPriceEmitter의 flowInto() 메서드를 호출함.</li>
     *     <li>2초의 지연 시간을 가진 후, 해당 암호 화폐의 데이터 처리를 종료함.</li>
     * </ol>
     *
     * <h3>실행 결과</h3>
     * Listener를 통해 들어오는 데이터를 리스닝하고 있다가 실제로 들어오는 데이터가 있을 경우에만 데이터를 emit 하는 일종의 push 방식으로 데이터를 처리함.
     */
    private static void example2() throws InterruptedException{
        CryptoCurrencyPriceEmitter priceEmitter = new CryptoCurrencyPriceEmitter();

        Flux.create((FluxSink<Integer> sink) -> {
            priceEmitter.setListener(new CryptoCurrencyPriceListener() {
                @Override
                public void onPrice(List<Integer> priceList) {
                    priceList.stream().forEach(sink::next);
                }

                @Override
                public void onComplete() {
                    sink.complete();
                }
            });
        })
                .publishOn(Schedulers.parallel())
                .subscribe(
                        data -> log.info("# onNext: {}", data),
                        error -> {},
                        () -> log.info("# oncomplete")
                );

        Thread.sleep(3000L); // 암호 화폐의 가격 변동이 발생하기 전에 3초의 지연 시간을 가짐.

        priceEmitter.flowInto();  // 암호 화폐의 가격 변동이 발생하는 것을 시뮬레이션하기 위해 CryptoCurrencyPriceEmitter 의 flowInto() 메서드를 호출함.

        Thread.sleep(2000L); // 2초의 지연 시간을 가진 후, 해당 암호 화폐의 데이터 처리를 종료함.
        priceEmitter.complete();
    }



    static int start = 3;
    static int end = 4;
    /**
     * <h3>동작 방식</h3>
     * <ol>
     *     <li>구독이 발생하면 publishOn() 에서 설정한 숫자만큼의 데이터를 요청함.</li>
     *     <li>create() Operator 내부에서 sink.onRequest() 메서드의 람다 표현식이 실행됨.</li>
     *     <li>요청한 개수보다 2개 더 많은 데이터를 emit함.</li>
     *     <li>Subscriber가 emit 된 데이터를 전달받아 로그로 출력함.</li>
     *     <li>이때 Backpressure DROP 전략이 적용되었으므로, 요청 개수를 초과하여 emit된 데이터는 DROP 됨.</li>
     *     <li>다시 publishOn() 에서 지정한 숫자만큼의 데이터를 요청함.</li>
     *     <li>creat() Operator 내부에서 onComplete Signal이 발생하지 않았기 때문에 2에서 6의 과정을 반복하다가 설정한 지연 시간이 지나면
     *          main 스레드가 종료되어 코드 실행이 종료됨.</li>
     * </ol>
     */
    private static void example3() throws InterruptedException {
        Flux.create((FluxSink<Integer> sink) -> {
            sink.onRequest(n -> {
                log.info("# requested: " + n);
                try {
                    Thread.sleep(500L);
                    for (int i = start; i <= end; i++) {
                        sink.next(i);
                    }
                    start += 4;
                    end +=4;
                } catch (InterruptedException e) {}
            });
            sink.onDispose(() -> log.info("# clean up"));
        }, FluxSink.OverflowStrategy.DROP) // Backpressure 방식 중 Drop 방식을 선택함.
                .subscribeOn(Schedulers.boundedElastic())
                .publishOn(Schedulers.parallel(), 2)
                .subscribe(data -> log.info("# onNext: {}", data));
        Thread.sleep(3000L);
    }

}
