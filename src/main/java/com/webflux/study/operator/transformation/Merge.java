package com.webflux.study.operator.transformation;

import com.webflux.study.operator.SampleData;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * <h2>merge()</h2>
 * 파라미터로 입력되는 Publisher의 Sequence에서 emit된 데이터를 인터리빙 방식으로 병합함.
 * <blockquote>
 *     <h3>인터리빙?</h3>
 *     <p>
 *         인터리브는 '겨치러 배치하다' 라는 의미가 있음. 컴퓨터 하드디스크 상의 데이터를 서로 인접하지 않게 배열해 성능을 향상시키거나 인접한 메모리 위치를
 *         서로 다른 메모리 뱅크에 두어 여러 곳으르 접근할 수 있게 해주는 것을 인터리빙이라고 함.
 *     </p>
 *     <p>
 *         그런데 주의해야 할 것은 인터리빙 방식이라고 해서 각각의 Publisher가 emit 하는 데이터를 하나씩 번갈아 가며 merge  한다는 것이 아니라
 *         emit된 시간 순서대로 merge한다는 것임.
 *     </p>
 * </blockquote>
 * <p>
 *     merge() Operator는 concat() Operator 처럼 먼저 입력된 Publisher의 sequence가 종료될 때까지 나머지 Publisher의 Sequence가
 *     subscribe 되지 않고 대기하는 것이 아니라, 모든 Publisher의 Sequence가 즉시 subscribe됨.
 * </p>
 */
@Slf4j
public class Merge {
    public static void main(String[] args) throws InterruptedException{
        example1();
        log.info("----------------------------------------------------");
        example2();
    }

    private static void example1() throws InterruptedException{
        Flux
                .merge(
                        Flux.just(1,2,3,4).delayElements(Duration.ofMillis(300L)),
                        Flux.just(5,6,7).delayElements(Duration.ofMillis(500L))
                )
                .subscribe(data -> log.info("# onNext: {}", data));

        Thread.sleep(2000L);
    }
    private static void example2() throws InterruptedException {
        String[] usaStates = {
                "Ohio", "Michigan", "New Jersey", "Illinois", "New Hampshire", "Virginia", "Vermont", "North Carolina",
                "Ontario", "Georgia"
        };

        Flux
                .merge(getMeltDownRecoveryMessage(usaStates))
                .subscribe(log::info);

        Thread.sleep(2000L);

    }

    private static List<Mono<String>> getMeltDownRecoveryMessage(String[] usaStates) {
        List<Mono<String>> messages = new ArrayList<>();
        for (String state : usaStates) {
            messages.add(SampleData.nppMap.get(state));
        }

        return messages;
    }
}
