package com.webflux.study.context;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.Context;

/**
 * <h2>Context 특징</h2>
 * <ul>
 *     <li>Context는 구독이 발생할 때마다 하나의 Context 가 해당 구독에 연결된다.</li>
 *     <li>Context 는 Operator 체인의 아래에서 위로 전파된다.</li>
 *     <li>동일한 키에 대한 값을 중복해서 저장하면 Operator 체인상에서 가장 위쪽에 위차한 contextWrite()이 저장한 값으로 덮어쓴다.</li>
 *     <li>Inner Sequence 내부에서는 외부 Context에 저장된 데이터를 읽을 수 있다.</li>
 *     <li>Inner Sequence 외부에서는 Inner Sequence 내부 Context에 저장된 데이터를 읽을 수 없다.</li>
 *     <li>Context는 인증 정보 같은 직교성(독립성)을 가지는 정보를 전송하는 데 적합하다.</li>
 * </ul>
 */
@Slf4j
public class ContextFeature {
    public static final String HEADER_AUTH_TOKEN = "authToken";
    public static void main(String[] args) throws InterruptedException {
        log.info("feature 1-----------------------");
        feature1();
        log.info("feature 2-----------------------");
        feature2();
        log.info("feature 3-----------------------");
        feature3();
        log.info("feature 4-----------------------");
        feature4();
    }

    /**
     * <h3>실행 결과</h3>
     * 구독이 발생할 때마다 해당하는 하나의 Context가 하나의 구독에 연결됨을 알 수 있음. <br>
     * 밑에 주석을 풀면 에러가 나는 것을 확인할 수 있음. <br>
     * Context 는 Operator 체인의 아래에서 위로 전파됨을 알 수 있음. 그래서 위의 deferContextual()이 동작하는 것임. <br>
     * 동일한 키에 대한 값을 중복해서 저장하믄 Operator 체인에서 가장 위쪽에 위치한 contextWrite() 이 저장한 값으로 덮어 씀.
     */
    static void feature1() throws InterruptedException {
        final String key1 = "company";

        Mono<String> mono = Mono.deferContextual(ctx ->
                        Mono.just("Company: " + " " + ctx.get(key1))
                )
                .publishOn(Schedulers.parallel());


        mono.contextWrite(context -> context.put(key1, "Apple"))
                .subscribe(data -> log.info("# subscribe1 onNext: {}", data));

        mono.contextWrite(context -> context.put(key1, "Microsoft"))
                .subscribe(data -> log.info("# subscribe2 onNext: {}", data));

        //mono.subscribe(data -> log.info("# subscribe3 onNext: {}", data));

        Thread.sleep(100L);
    }

    /**
     * <h3>실행 결과</h3>
     * context에 두 개의 데이터를 저장한 것을 확인할 수 있음. <br>
     * 출력이 Bill이 아닌 Steve가 출력됨. 아래에서 위로 전파되었기 때문.
     * <strong><그래서 ContextWrite는 Operator Chain의 제일 마지막에 두는 게 좋음.</strong>
     */
    static void feature2() throws InterruptedException {
        String key1 = "company";
        String key2 = "name";

        Mono
                .deferContextual(ctx ->
                        Mono.just(ctx.get(key1)) // Apple이 전파
                )
                .publishOn(Schedulers.parallel())
                .contextWrite(context -> context.put(key2, "Bill")) // 데이터 저장 1, 이미 mono의 data에 Apple, Steve가 저장된 상태.
                .transformDeferredContextual((mono, ctx) ->
                        mono.map(data -> data + ", " + ctx.getOrDefault(key2, "Steve")) // name이라는 키로 값을 아래에서 읽어 오고 있지만 값이 없기 때문에 Apple, Steve 가 Stream에 저장.
                )
                .contextWrite(context -> context.put(key1, "Apple")) // 데이터 저장 2
                .subscribe(data -> log.info("# onNext: {}", data));

        Thread.sleep(100L);
    }

    /**
     * <h3>실행 결과</h3>
     * 주석을 해제하면 Context 에 role 이라는 키가 없기 때문에 NoSuchElementException이 발생함.
     */
    static void feature3() throws InterruptedException {
        String key1 = "company";
        Mono
                .just("Steve")
//                .transformDeferredContextual((stringMono, ctx) ->
//                        ctx.get("role"))
                .flatMap(name ->
                        Mono.deferContextual(ctx ->
                                Mono
                                        .just(ctx.get(key1) + ", " + name)
                                        .transformDeferredContextual((mono, innerCtx) ->
                                                mono.map(data -> data + ", " + innerCtx.get("role"))
                                        )
                                        .contextWrite(context -> context.put("role", "CEO"))
                        )
                )
                .publishOn(Schedulers.parallel())
                .contextWrite(context -> context.put(key1, "Apple"))
                .subscribe(data -> log.info("# onNext: {}", data));

        Thread.sleep(100L);
    }

    /**
     * <h3>실행 결과</h3>
     *
     */
    static void feature4() {
        Mono<String> mono =
                postBook(Mono.just(
                        new Book("abcd-1111-3533-2809"
                                , "Reactor's Bible"
                                ,"Kevin"))
                )
                        .contextWrite(Context.of(HEADER_AUTH_TOKEN, "eyJhbGciOi")); // Context에 저장한 인증 토큰을 두 개의 Mono를 합치는 과정에서 다시 Context로 부터 읽어 와서 사용한다는 것이 핵심.

        mono.subscribe(data -> log.info("# onNext: {}", data));
    }

    private static Mono<String> postBook(Mono<Book> book) {
        return Mono
                .zip(book,  // zip() Operator를 통해 Mono<Book> 객체와 인증 토큰 정보를 의미하는 Mono<String> 객체를 하나의 Mono로 합침. 이때 합쳐진 Mono는 Mono<Tuple2> 의 객체가 됨.
                        Mono
                                .deferContextual(ctx ->
                                        Mono.just(ctx.get(HEADER_AUTH_TOKEN)))
                )
                .flatMap(tuple -> {
                    String response = "POST the book(" + tuple.getT1().getBookName() +
                            "," + tuple.getT1().getAuthor() + ") with token: " +
                            tuple.getT2();
                    return Mono.just(response); // HTTP POST 전송을 했다고 가정
                });
    }
}

@AllArgsConstructor
@Data
class Book {
    private String isbn;
    private String bookName;
    private String author;
}
