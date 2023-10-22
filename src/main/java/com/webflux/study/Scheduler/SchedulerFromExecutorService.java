package com.webflux.study.Scheduler;

import lombok.extern.slf4j.Slf4j;

/**
 * <h3>Schedulers.fromExecutorService()</h3>
 * <p>
 *     기존에 이미 사용하고 있는 ExecutorService 가 있다면 이 ExecutorService로 부터 Scheduler를 생성하는 방식.
 * </p>
 * <p>
 *     ExecutorService로 직접 생성할 수도 있지만 Reactor에서는 이 방식을 권장하지 않음.
 * </p>
 */
@Slf4j
public class SchedulerFromExecutorService {
}
