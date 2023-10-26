package com.webflux.study.debug;

/**
 * <h2>Debug Memo</h2>
 * <ul>
 *     <li>Reactor 에서는 Hooks.onOperatorDebug() 메서드를 호출해서 디버그 모드를 활성화할 수 있음.</li>
 *     <li>디버그 모드를 활성화하면 애플리케이션 내에 있는 모든 Operator의 Stack trace 를 캡쳐하므로 프로덕션 환경에서는
 *     사용하지 말아야 함.</li>
 *     <li>Reactor tools 에서 지원하는 ReactorDebugAgent를 사용하여 프로덕션 환경에서 디버그 모드를 대체할 수 있음.</li>
 *     <li>checkpoint() Operator를 사용하면 특전 Operator 체인 내의 스택트레이스만 캡처한다.</li>
 *     <li>log() Operator를 추가하면 추가한 지점의 Reactor Signal을 출력한다.</li>
 *     <li>log() Operator는 사용 개수에 제한이 없기 때문에 1개 이상의 log() Operator로 Reactor Sequence의 내부 동작을
 *     좀 더 상세하게 분석하면서 디버깅 할 수 있다.</li>
 * </ul>
 */
public class AboutDebug {
}
