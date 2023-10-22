package com.webflux.study.Scheduler;


import lombok.extern.slf4j.Slf4j;

/**
 * <h3>Schedulers.boundedElastic()</h3>
 * <p>
 *     Schedulers.boundedElastic()은 ExecutorService 기반의 스레드 풀을 생성한 후, 그 안에서 정해진 수 만큼의
 *     스레드를 사용하여 작업을 처리하고 작업이 종료된 스레드는 반납하여 재사용하는 방식.
 * </p>
 *
 * <p>
 *     기본적으로 CPU 코어 수 x 10 만큼의 스레드를 생성하며, 풀에 있는 모든 스레드가 작업을 처리하고 있다면 이용 가능한 스레드가
 *     생길 때 까지 최대 100,000 개 의 작업이 큐에서 대기할 수 있음.
 * </p>
 *
 * <p>
 *     실제로는 데이터베이스를 통한 질의나 HTTP 요청 같은 Blocking I/O 작업을 통해 전달받은 데이터를 데이터 소스로 사용하는 경우가 많음.<br>
 *     이럴 때 Blocking I/O 작업을 효과적으로 처리하기 위한 방식이라고 할 수 있음. <br>
 *     즉, 다른 스레드는 데이터를 가져오고 다른 스레드는 작업을 이어나가 면 될 것임.
 * </p>
 *
 */
@Slf4j
public class SchedulerBoundedElastic {
}
