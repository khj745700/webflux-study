package com.webflux.study.operator.split;

import com.webflux.study.operator.SampleData;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * <h2>groupBy()</h2>
 * <p>
 *     groupBy(keyMapper) Operator는 emit되는 데이터를 keyMapper로 생성한 key를 기준으로 그룹화한 GroupedFlux를 리턴하며, 이
 *     GroupedFlux를 통해서 그룹별로 작업을 수행할 수 있음.
 * </p>
 * <p>
 *     groupBy(keyMapper, valueMapper) Operator는 그룹화하면서 valueMapper를 통해 그룹화되는 데이터를 다른 형태로 가공 처리할 수 있음.
 * </p>
 */
@Slf4j
public class GroupBy {
    public static void main(String[] args) {
     example1();
     log.info("==========================================");
     example2();
     log.info("==========================================");
     example3();
    }

    /**
     * <h3>실행 결과</h3>
     * <p>
     *     도서를 저자명으로 그룹화한 후에 List로 변환하여 Subscriber에게 전달함.
     * </p>
     */
    private static void example1() {
        Flux.fromIterable(SampleData.books)
                .groupBy(book -> book.getAuthorName())
                .flatMap(groupedFlux ->
                        groupedFlux
                                .map(book -> book.getBookName() +
                                        "(" + book.getAuthorName() + ")")
                                .collectList()
                )
                .subscribe(bookByAuthor ->
                        log.info("# book by author: {}", bookByAuthor));
    }

    /**
     * <h3>실행 결과</h3>
     * <p>
     *     실행 결과는 위와 동일함.
     * </p>
     */
    private static void example2() {
        Flux.fromIterable(SampleData.books)
                .groupBy(book ->
                                book.getAuthorName(),
                        book -> book.getBookName() + "(" + book.getAuthorName() + ")")
                .flatMap(groupedFlux -> groupedFlux.collectList())
                .subscribe(bookByAuthor ->
                        log.info("# book by author: {}", bookByAuthor));
    }

    /**
     * <h3>실행 결과</h3>
     * <ol>
     *     <li>저자명을 기준으로 도서를 그룹화함.</li>
     *     <li>도서 그룹별로 총 인세 수익을 계산해서 Subscriber에게 전달하기 위해 flatMap() Operator로 평탄화 작업 진행</li>
     *     <li>여기서의 평탄화 작업은 그룹화된 데이터를 이용해 저자별 총 인세 수익을 계산하는 작업임.</li>
     *     <li>'저자명: 총 인세 수익' 형태로 Subscriber에게 전달되도록 zipWith() Operator를 이용해 두 개의 Mono를 하나로 합치고 있음.</li>
     *     <li>map() Operator를 이용해 groupedFlux에서 emit된 도서 정보 중에서 도서의 가격, 재고 수량, 인세 지급 비울로 도서
     *     한 권당 인세 금액을 계산함.</li>
     *     <li>reduce() Operator를 이용해 그룹화된 도서의 총 인세 합계를 계산함.</li>
     *     <li>계산된 총 인세 금액을 Subscriber에게 전달함.</li>
     * </ol>
     */
    private static void example3() {
        Flux.fromIterable(SampleData.books)
                .groupBy(book -> book.getAuthorName())
                .flatMap(groupedFlux ->
                        Mono
                                .just(groupedFlux.key())
                                .zipWith(
                                        groupedFlux
                                                .map(book ->
                                                        (int)(book.getPrice() * book.getStockQuantity() * 0.1))
                                                .reduce((y1, y2) -> y1 + y2),
                                        (authorName, sumRoyalty) ->
                                                authorName + "'s royalty: " + sumRoyalty)
                )
                .subscribe(log::info);
    }
}
