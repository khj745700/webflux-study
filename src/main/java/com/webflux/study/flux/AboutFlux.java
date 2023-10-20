package com.webflux.study.flux;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class AboutFlux {
    public static void main(String[] args) {
        Flux.just(6,9,13)
                .map(num -> num % 2)
                .subscribe(System.out::println);

        Flux.fromArray(new Integer[]{3, 6, 7, 9}) // 배열 데이터 처리를 위해 fromArray() 사용.
                .filter(num -> num > 6)
                .map(num -> num * 2)
                .subscribe(System.out::println);

        Flux<String> flux = // 두 개의 Mono 결합도 concatWith() 로 가능함.
                Mono.justOrEmpty("Steve")
                        .concatWith(Mono.justOrEmpty("Jobs"));
        flux.subscribe(System.out::println);

        Flux.concat(  // Flux 의 결합도 가능함.
                        Flux.just("Mercury", "Venus", "Earth"),
                        Flux.just("Mars", "Jupiter", "Saturn"),
                        Flux.just("Uranus", "Neptune", "Pluto"))
                .collectList() // collectionList() 를 사용하면 Publisher 에서 emit 하는 데이터를 모아서 하나의 List 로 만들어줌. 하나기 때문에 Mono 로 반환됨.
                .subscribe(planets -> System.out.println(planets));

        Flux
                .fromArray(new Integer[]{1,2,3,4})
                .delayElements(Duration.ofSeconds(1)) // delayElements 로 배열의 데이터가 1초마다 Emit 되도록 함.
                .subscribe(System.out::println);
    }
}
