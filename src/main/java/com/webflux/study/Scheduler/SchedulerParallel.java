package com.webflux.study.Scheduler;


import lombok.extern.slf4j.Slf4j;

/**
 * <h3>Schedulers.parallel()</h3>
 * <p>
 *     {@link SchedulerBoundedElastic}이 Blocking I/O 작업에 최적화 되어 있다면, <br>
 *     이 친구는 Non-Blocking I/O 에 최적화되어 있는 Scheduler 로서 CPU 코어 수만큼의 스레드를 할당함.
 * </p>
 */
@Slf4j
public class SchedulerParallel {
}
