package com.webflux.study.operator.filter;

import com.webflux.study.operator.SampleData;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import java.util.Map;

import static com.webflux.study.operator.SampleData.getCovidVaccines;

/**
 * <h2>filterWhen()</h2>
 * 내부에서 Inner Sequence를 통해 조건에 맞는 데이터인지를 비동기적으로 테스트한 후, 테스트 결과가 true 라면 filterWhen()의 Upstream으로부터
 * 전달받은 데이터를 Downstream으로 emit 함.
 */
@Slf4j
public class FilterWhen{
    public static void main(String[] args) throws InterruptedException {

        Map<SampleData.CovidVaccine, Tuple2<SampleData.CovidVaccine, Integer>> vaccineMap = getCovidVaccines();
        Flux
                .fromIterable(SampleData.coronaVaccineNames) // 백신명을 emit 함.
                .filterWhen(vaccine -> Mono // 코로나 백신명을 전달받음. Operator의 Inner Sequence를 통해 백신명에 해당하는 백신의 수량이 3,000,000개 이상이라면 해당 백신명을 Subscriber에게 emit 함.
                        .just(vaccineMap.get(vaccine).getT2() >= 3_000_000)
                        .publishOn(Schedulers.parallel()))
                .subscribe(data -> log.info("# onNext: {}", data));

        Thread.sleep(1000);
    }
}
