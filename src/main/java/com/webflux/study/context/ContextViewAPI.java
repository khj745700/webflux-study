package com.webflux.study.context;


import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * <h2>자주사용되는 ContextView 관련 API</h2>
 * <strong>데이터를 읽을 때는 무조건 ContextView를 쓴다!!</strong>
 * <table>
 *     <tr>
 *         <th>
 *             ContextView API
 *         </th>
 *         <th>
 *             설명
 *         </th>
 *     </tr>
 *     <tr>
 *         <td>
 *             get(key)
 *         </td>
 *         <td>
 *             ContextView 에서 key에 해당하는 value를 반환함.
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>
 *             getOrEmpty(key)
 *         </td>
 *         <td>
 *             ContextView 에서 key에 해당하는 value를 Optional로 래핑해서 반환.
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>
 *             getOrDefault(key, defaultValue)
 *         </td>
 *         <td>
 *             ContextView에서 key에 해당하는 value를 가져온다. key에 해당하는 value가 없으면 default value를 가져옴.
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>
 *             hashKey(key)
 *         </td>
 *         <td>
 *             ContextView에서 특정 key가 존재하는지를 확인한다.
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>
 *             isEmpty()
 *         </td>
 *         <td>
 *             Context가 비어 있는지 확인한다.
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>
 *             size()
 *         </td>
 *         <td>
 *             Context 내에 있는 key/value의 개수를 반환한다.
 *         </td>
 *     </tr>
 *
 * </table>
 */
@Slf4j
public class ContextViewAPI {
    public static void main(String[] args) throws InterruptedException {
        final String key1 = "company";
        final String key2 = "firstName";
        final String key3 = "lastName";

        Mono
                .deferContextual(ctx ->
                        Mono.just(ctx.get(key1) + ", " +
                                ctx.getOrEmpty(key2).orElse("no firstName") + " " +
                                ctx.getOrDefault(key3, "no lastName"))
                )
                .publishOn(Schedulers.parallel())
                .contextWrite(context -> context.put(key1, "Apple"))
                .subscribe(data -> log.info("# onNext: {}" , data));

        Thread.sleep(100L);
    }
}
