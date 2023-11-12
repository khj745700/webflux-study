package com.webflux.study.operator.transformation;

import com.webflux.study.operator.SampleData;
import io.r2dbc.spi.Parameter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

import java.util.List;


/**
 * <h2>concat()</h2>
 * <p>
 *     concat() Operator는 파라미터로 입력되는 Publisher의 Sequence를 연결해서 데이터를 순차적으로 emit함. 특히 먼저 입려된 Publisher의
 *     Sequence가 종됴될 때까지 나머지 Publisher의 Sequence는 subscribe 되지 않고 대기하는 특성을 가짐.
 * </p>
 */
@Slf4j
public class Concat {
    public static void main(String[] args) {
        example1();
        log.info("--------------------------------------------------");
        example2();
    }

    private static void example1() {
        Flux
                .concat(Flux.just(1, 2, 3), Flux.just(4, 5))
                .subscribe(data -> log.info("# onNext: {}", data));
    }

    private static void example2() {
        Flux
                .concat(
                        Flux.fromIterable(getViralVector()),
                        Flux.fromIterable(getMRNA()),
                        Flux.fromIterable(getSubunit()))
                .subscribe(data -> log.info("# onNext: {}", data));
    }

    private static List<Tuple2<SampleData.CovidVaccine, Integer>> getViralVector() {
        return SampleData.viralVectorVaccines;
    }

    private static List<Tuple2<SampleData.CovidVaccine, Integer>> getMRNA() {
        return SampleData.mRNAVaccines;
    }

    private static List<Tuple2<SampleData.CovidVaccine, Integer>> getSubunit() {
        return SampleData.subunitVaccines;
    }
}
