package com.webflux.study.Scheduler;

import lombok.extern.slf4j.Slf4j;

/**
 * <h3>스레드의 개념</h3>
 * <ul style='margin-top:-10'>
 *     <li>CPU의 코어는 물리적인 스레드를 의미. 이 물리적인 스레드는 논리적인 코어라고도 함.</li>
 *     <li>물리적인 스레드는 병렬성과 관련, 논리적인 스레드는 동시성과 관련</li>
 * </ul>
 *
 * <h2>Scheduler란?</h2>
 * <p>비동기 프로그래밍을 위해 사용되는 스레드를 관리해주는 역할.</p>
 * <p>스레드 간의 Race Condition등을 신중하게 고려해서 코드를 작성해야 하는데, 이로 인해 코드의 복잡도가 높아지고
 * 결과적으로 예상치 못한 오류 있을 수 있음.</p>
 * <p>Reactor Scheduler를 사용하면 이러한 문제 최소화 가능.</p>
 * <br>
 * <h3>Scheduler의 종류</h3>
 * <ul style='margin-top:-10'>
 *     <li>Schedulers.immediate()는 별도의 스레드를 추가적으로 생성하지 않고, 현재 스레드에서 작업을 처리함.</li>
 *     <li>Schedulers.boundedElastic()은 ExecutorService 기반의 스레드 풀(Thread Pool)을 생성한 후,
 *     그 안에서 정해진 수만큼의 스레드를 사용하여 작업을 처리하고 작업이 종료된 스레드는 반납하여 재사용함.</li>
 *     <li>Schedulers.boundedElastic()은 Blocking I/O에 최적화되어 있음</li>
 *     <li>Schedulers.parallel()은 Non-Blocking I/O에 최적화되어 있는 Scheduler로서 CPU 코어 수만큼의 스레드를 생성함.</li>
 *     <li>Scheduler.newSingle(), Schedulers.newBoundedElastic(), Scheduler.newParallel() 메서드를 사용해서 새로운
 *     Scheduler 인스턴스를 생성할 수 있음.</li>
 * </ul>
 */
@Slf4j
public class AboutScheduler {

}
