package com.webflux.study.operator.transformation;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;


/**
 * <h2>flatMap()</h2>
 * <p>
 *     flapMap() Operator 영역 위쪽의 Upstream에서 데이터가 emit되어 flatMap() Operator로 전달되면 flatMap() Operator로 전달되면
 *     flatMap() 내부에서 Inner Sequence를 생성 한 후, 한 개 이상의 변환된 데이터를 emit 하고 있음을 알 수 있음.
 * </p>
 * <p>
 *     즉, Upstream에서 emit된 데이터 한 건이 Inner Sequence에서 여러 건의 데이터로 변환된다는 것을 알 수 있음.
 * </p>
 * <p>
 *     그런데 Upstream에서 emit된 데이터는 Inner Sequence에서 평탄화 작업을 거치면서 하나의 Sequence로 병합되어 Downstream으로 emit됨.
 * </p>
 */
@Slf4j
public class FlatMap {
    public static void main(String[] args) throws InterruptedException{
        example1();
        log.info("====================================");
        example2();
    }

    /**
     * <h3>실행 결과</h3>
     * Upstream인 just() Operator에서 두 개의 데이터를 emit하면 flatMap() 내부의 Inner Sequence에서 세 개의 데이터를 emit함.
     * <p>즉, 2(Upstream에서 emit되는 데이터 수) X 3(Inner Sequence에서 emit 되는 데이터 수) = 6개의 데이터가 최종적으로 Subscriber에게 전달됨.</p>
     */
    private static void example1() {
        Flux
                .just("Good", "Bad")
                .flatMap(feeling -> Flux
                        .just("Morning", "Afternoon", "Evening")
                        .map(time -> feeling + " " + time))
                .subscribe(log::info);
    }

    /**
     * <h3>실행 결과</h3>
     * 구구단이 2단부터 9단까지 차례대로 출럭되는 것이 아닌 순서가 제각각인 것을 알 수 있음.
     * <p>
     *     이처럼 flatMap() 내부의 Inner Sequence를 비동기적으로 실행하면 데이터의 emit의 순서를 보장하지 않는다는 것을 확인할 수 있었음.
     * </p>
     */
    private static void example2() throws InterruptedException{
        Flux
                .range(2, 8)
                .flatMap(dan -> Flux
                        .range(1, 9)
                        .publishOn(Schedulers.parallel())
                        .map(n -> dan + " * " + n + " = " + dan * n))
                .subscribe(log::info);

                Thread.sleep(100L);
    }
}
