package com.webflux.study.context;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.Context;


/**
 * <h2>자주 사용되는 Context 관련 API</h2>
 * <strong>Context에 데이터를 쓸때는 무조건 Context를 쓴다!</strong>
 * <table border="1">
 *     <tr>
 *     <th>Context API</th>
 *     <th> 설명</th>
 *     </tr>
 *     <tr>
 *          <td>
 *              put(key, value)
 *          </td>
 *          <td>
 *              key/value 형태로 Context에 값을 쓴다.
 *          </td>
 *     </tr>
 *     <tr>
 *         <td>
 *             of(key1, value1, key2, value2 ...)
 *         </td>
 *         <td>
 *             key/value 형태로 Context에 최대 5 개의 값을 쓴다.
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>
 *             putAll(ContextView)
 *         </td>
 *         <td>
 *             현재 Context와 파라미터로 입력된 ContextView를 merge한 후 새로운 Context 생성.
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>
 *             delete(key)
 *         </td>
 *         <td>
 *             Context에서 key에 해당하는 value를 삭제한다.
 *         </td>
 *     </tr>
 * </table>
 */
@Slf4j
public class ContextAPI {
    public static void main(String[] args) throws InterruptedException {
        final String key1 = "company";
        final String key2 = "firstName";
        final String key3 = "lastName";

        Mono
                .deferContextual(ctx ->
                        Mono.just(ctx.get(key1) + ", " + ctx.get(key2) + " " + ctx.get(key3))
                )
                .publishOn(Schedulers.parallel())
                .contextWrite(context ->
                        context.putAll(Context.of(key2, "Steve", key3, "Jobs").readOnly()) // Context를 읽기 작업만 가능한 ContextView로 변환해주는 API
                )
                .contextWrite(context -> context.put(key1, "Apple"))
                .subscribe(data -> log.info("# onNext: {}" , data));

        Thread.sleep(100L);
    }
}
