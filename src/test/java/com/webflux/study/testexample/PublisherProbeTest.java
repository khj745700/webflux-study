package com.webflux.study.testexample;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;

/**
 * <h2>PublisherProbe</h2>
 * <ul>
 *     <li>PublisherProbe를 사용하면 Sequence의 실행이 분기되는 상황에서 Publisher가 어느 경로로 실행되는지 테스트 할 수 있음.</li>
 *     <li>PublisherProbe의 assertWasSubscribed(), assertWasRequested(), assertWasNotCancelled() 등을 통해 Sequence의
 *     기대하는 실행 경로를 Assertion 할 수 있음. 순서는 상관 없음.</li>
 * </ul>
 *
 */
public class PublisherProbeTest {
    @Test
    public void publisherProbeTest() {
        PublisherProbe<String> probe =
                PublisherProbe.of(PublisherProbeTestExample.supplyStandbyPower());

        StepVerifier
                .create(PublisherProbeTestExample
                        .processTask(
                                PublisherProbeTestExample.supplyMainPower(),
                                probe.mono()) //probe.mono()에서 리턴된 Mono 객체를 processTask() 메서드의 두 번째 파라미터로 전달함.
                )
                .expectNextCount(1)
                .verifyComplete();
        //Publisher의 실행 경로를 테스트함.
        probe.assertWasSubscribed(); //구독했는지?
        probe.assertWasRequested(); // 요청을 했는지?
        probe.assertWasNotCancelled(); // 중간에 취소가 되지 않았는지를 검증.
    }
}
