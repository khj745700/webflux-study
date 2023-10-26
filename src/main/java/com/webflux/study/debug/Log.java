package com.webflux.study.debug;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * <h2>log() Operator</h2>
 * lop Operator 는 Reactor Sequence의 동작을 log로 출력함.
 */
@Slf4j
public class Log {
    public static Map<String, String> fruits = new HashMap<>();

    static {
        fruits.put("banana", "바나나");
        fruits.put("apple", "사과");
        fruits.put("pear", "배");
        fruits.put("grape", "포도");
    }

    /**
     * <h3>실행 결과</h3>
     * 마지막 melon을 요청하였으나 cancel 되었고 onError를 받았음. <br>
     * log()는 디버그 레벨을 INFO에서 정의함.<br>
     * 그러나 log(category, Level)을 작성하면 해당 log은 debug로 뜨게 됨.
     */
    public static void main(String[] args) {
        Flux.fromArray(new String[]{"BANANAS", "APPLES", "PEARS", "MELONS"})
                .map(String::toLowerCase)
                .map(fruit -> fruit.substring(0, fruit.length() - 1))
                .log("Fruit.Substring", Level.FINE) // logback 을 정의하지 않으면 debug 가 나오지 않음.
                .map(fruits::get)
                .subscribe(
                        log::info,
                        error -> log.error("# onError:", error));
    }
}
