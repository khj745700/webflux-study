package com.webflux.study.operator.error;

import com.webflux.study.operator.Book;
import com.webflux.study.operator.SampleData;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.stream.Collectors;

/**
 * <h2>retry()</h2>
 * <p>
 *     Publisher가 데이터를 emit하는 과정에서 에러가 발생하면 파라미터로 입력한 횟수만큼 원본 Flux의 Sequence를 다시 구독함.
 *     만약 파라미터로 Long.MAX_VALUE를 입력하면 재구독을 무한 반복함.
 * </p>
 * <p>
 *     timeout() operator와 함께 사용하여 네트워크 지연으로 인해 정해진 시간 안에 응답을 받지 못하면 일정 횟수만큼 재요청해야 하는
 *     상황에서 유용하게 사용할 수 있음.
 * </p>
 */
@Slf4j
public class Retry {

    public static void main(String[] args) throws InterruptedException {
        example1();
        log.info("==========================================");
        example2();
    }

    /**
     * <h3>실행 결과</h3>
     * <p>
     *      데아터 emit 중에 에러가 발생하면 원본 Flux의 Sequence를 1회 재구독하도록 했음.
     * </p>
     */
    static void example1() throws InterruptedException{
        final int[] count = {1};
        Flux
                .range(1, 3)
                .delayElements(Duration.ofSeconds(1))
                .map(num -> {
                    try {
                        if (num == 3 && count[0] == 1) { // 내부 숫자가 3일 경우에만 의도적으로 1초의 지연시간을 줌.
                            count[0]++;
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException e) {}

                    return num;
                })
                .timeout(Duration.ofMillis(1500)) // 1.5초 동안 upstream으로부터 emit되는 데이터가 없으면 TimeoutException이 발생하도록 함.
                .retry(1)
                .subscribe(data -> log.info("# onNext: {}", data),
                        (error -> log.error("# onError: ", error)),
                        () -> log.info("# onComplete"));

        Thread.sleep(7000);
    }

    /**
     * <h3>실행 결과</h3>
     * getBooks() 메서드에서 Http 요청을 통해 도서 목록을 조회하는 중에 네트워크 지연으로 일정 시간이 지나면 1회 재요청하는 것을 시뮬레이션한 코드
     * <p>
     *
     * </p>
     */
    public static void example2() throws InterruptedException {
        getBooks()
                .collect(Collectors.toSet()) // 데이터 중복을 제거하기 위해 collect(Collectors.toSet())을 사용함.
                .subscribe(bookSet -> bookSet.stream()
                        .forEach(book -> log.info("book name: {}, price: {}",
                                book.getBookName(), book.getPrice())));

        Thread.sleep(12000);
    }

    private static Flux<Book> getBooks() {
        final int[] count = {0};
        return Flux                                     // HTTP 요청을 시뮬레이션하는 코드
                .fromIterable(SampleData.books)
                .delayElements(Duration.ofMillis(500))
                .map(book -> {
                    try {
                        count[0]++;
                        if (count[0] == 3) {
                            Thread.sleep(2000);
                        }
                    } catch (InterruptedException e) {
                    }

                    return book;
                })                                      // 여기 까지
                .timeout(Duration.ofSeconds(2))     //2초 동안 데이터 emit되는 데이터가 없다면 1회 재시도히여 도서 목록을 다시 조회할 수 있음.
                .retry(1)
                .doOnNext(book -> log.info("# getBooks > doOnNext: {}, price: {}",
                        book.getBookName(), book.getPrice()));
    }
}
