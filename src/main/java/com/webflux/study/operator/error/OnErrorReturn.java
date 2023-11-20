package com.webflux.study.operator.error;

import com.webflux.study.operator.Book;
import com.webflux.study.operator.SampleData;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * <h2>onErrorReturn()</h2>
 * <p>
 *     에러 이벤트가 발생했을 때, 에러 이벤트를 Downstream으로 전파하지 않고 대체 값을 emit함. Java에서 예외가 발생했을 때, try ~ catch 문의
 *     catch 블록에서 예외에 해당하는 대체 값을 리턴하는 방식과 유사함.
 * </p>
 */
@Slf4j
public class OnErrorReturn {
    public static void main(String[] args) {
        example1();
        log.info("================================================");
        example2();
    }
    /**
     * <h3>실행 결과</h3>
     * <p>
     *     도서 목록의 도서 중에는 필명이 필수 입력 값이 아니기 대문에 null 값이 포함될 수 있음. 하지만 필명이 없는 도서를 사전에 필터링 하지 않았기
     *     때문에 map() Operator에서 영문 필명을 대문자로 바꿀 때 null 값이 있으면 NullPointerException이 발생하게 됨.
     * </p>
     * <p>
     *     onErrorReturn() Operator를 통해 NullPointerException이 발생하게 되면 에러 이벤트가 Downstream으로 전파되는 대신에 "No pen
     *     name"이라는 문자열 값으로 대체해서 emit하도록 했음.
     * </p>
     */
    static void example1() {
        getBooks()
                .map(book -> book.getPenName().toUpperCase())
                .onErrorReturn("No pen name")
                .subscribe(log::info);
    }

    private static Flux<Book> getBooks() {
        return Flux.fromIterable(SampleData.books);
    }

    static void example2() {
        getBooks()
                .map(book -> book.getPenName().toUpperCase())
                .onErrorReturn(NullPointerException.class, "No pen name")
                .onErrorReturn(IllegalArgumentException.class, "Illegal pen name")
                .subscribe(log::info);
    }
}
