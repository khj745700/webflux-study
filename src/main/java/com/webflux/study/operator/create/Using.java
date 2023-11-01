package com.webflux.study.operator.create;


import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * <h2>using()</h2>
 * 파라미터로 전달받은 resource를 emit 하는 Flux를 생성함.
 * <p>
 *     첫 번째 파라미터는 읽어올 resource
 *     두 번째 파라미터는 읽어 온 resource를 emit 하는 Flux
 *     세 번째 파라미터는 종료 Signal(onComplete 또는 onError)이 발생할 경우, resource를 해제하는 등의 후처리를 할 수 있게 해줌.
 * </p>
 */
@Slf4j
public class Using {
    /**
     * <h3>실행 결과</h3>
     * <ol>
     *     <li>루트 디렉토리에 있는 using_example.txt 파일을 한 라인씩 읽어 옴. 이때 읽어온 라인 데이터는 Stream<String> 형태의 Stream 객체임.</li>
     *     <li>using_example.txt 파일에서 읽어 온 Stream 객체를 fromStream() Operator의 데이터 소스로 전달한 후, 라인 문자열 데이터를 emit 함.</li>
     *     <li>라인 문자열 데이터의 emit이 끝나면 onComplete Signal 이벤트가 발생하므로 Stream 객체를 전달받아 Stream을 닫는 처리를 함.</li>
     *     <li>파일을 다 읽을 때 까지 1에서 3까지의 과정을 반복함.</li>
     * </ol>
     */
    public static void main(String[] args) {
        Path path = Paths.get("using_example.txt");

        Flux
                .using(() -> Files.lines(path), Flux::fromStream, Stream::close)
                .subscribe(log::info);

    }
}
