// 가상 컴포즈 인프라 코드
fun Column(verticalArrangement: String = "Top", horizontalAlignment: String = "Start", content: () -> Unit) {
    println("--- [Column 시작] Arrangement: $verticalArrangement, Alignment: $horizontalAlignment ---")
    content()
    println("--- [Column 끝] ---")
}

fun Row(horizontalArrangement: String = "Start", verticalAlignment: String = "Top", content: () -> Unit) {
    println("--- [Row 시작] Arrangement: $horizontalArrangement, Alignment: $verticalAlignment ---")
    content()
    println("--- [Row 끝] ---")
}

fun Text(text: String) {
    println("  ▶ Text 렌더링: '$text'")
}

fun Button(text: String, onClick: () -> Unit) {
    println("  ▶ Button 생성: '$text'")
    onClick()
}

// 컴포즈의 모든 화면 단위는 함수로 만든다. 함수 위에 이 단어를 붙이면 컴포들러(Compose Compiler)가
// 이 함수는 화면을 그리는 특수 변환 함수로 이해한다.
// 가독성을 위해 컴포저블 함수는 첫 글자를 대문자로 시작하는 것이 관례이다.
annotation class Composable // 어노테이션 선언

// 화면을 그린다고 생각하는 함수이다. 안드로이드 스튜디오를 지금 쓰는 건 아니니, 대충 콘솔 창에 그려지는 결과를 확인하자.
@Composable
fun MainScreen() {
    // 매개변수 이름까지 명시적으로 타이핑해 주어야 한다.
    // Column 노드는 자식 노드들을 수직축(Y축)으로 계산하여 쌓는다. 무조건 위에서 아래로 내려 붙인다.
    // Arrangement: 주 축(Main Axis(Column: 수직 / Row: 수평))를 기준으로 요소들을 어떻게 쪼갤지 결정한다.
    // Alignment: 교차축(Cross Axis(Column: 수평))을 기준으로 요소들을 어디에 정렬할 지 결정한다.
    Column(verticalArrangement = "SpaceBetween", horizontalAlignment = "CenterHorid") {
        // 상단 타이틀로 Text를 배치
        Text("안드로이드 첫 실습(인데 InteliJ인...)")
        // Row 노드는 자식 노드들을 수평축(X축)으로 계산하여 나열한다. 무조건 왼쪽에서 오른쪽으로 이어 붙인다.
        Row(horizontalArrangement = "SpaceAround") {
            // Row 블록 내부에 버튼을 배치한다.
            Button("좋아요") { println("    (클릭 이벤트: 좋아요 반영)") }
            Button("공유") { println("    (클릭 이벤트: 공유 창 열기)") }
        }
    }
}

fun main() {
    // 실행.
    MainScreen()
}