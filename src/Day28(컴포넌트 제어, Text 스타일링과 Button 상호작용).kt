// 가상 컴포즈 인프라 및 속성 데이터 클래스
data class Color(val hex: String)
data class TextStyle(val fontSize: Int, val fontWeight: String)

fun Text(text: String, color: Color = Color("#000000"), style: TextStyle = TextStyle(14, "Normal")) {
    println("  ▶ [Text 렌더링] '$text' (색상: ${color.hex}, 크기: ${style.fontSize}sp, 굵기: ${style.fontWeight})")
}

fun Button(onClick: () -> Unit, content: () -> Unit) {
    println("  ▶ [Button 영역 생성]")
    content() // 버튼 내부에 배치된 자식 컴포넌트들을 렌더링
    onClick() // 버튼 클릭 시 수행할 람다 이벤트 트리거
}

@Composable
fun NotificationCard() {
    // 코틀린 컴포즈는 버튼을 "터치 영역을 제공하는 컨테이너"로 취급
    // 맨 마지막 매개변수가 람다식(content), 코틀린의 규칙에 따라 소괄호 뒤에 중괄호 코드로 버튼 내부의 모양을 자유롭게 그릴 수 있다.
    Button(onClick = {println("    [이벤트] 알림 카드 닫기 및 읽음 처리 완료")}) {
        // 여기부터 content 매개변수.
        // Text 컴포넌트를 배치, 매개변수로 값을 전달
        Text(
            // 컴포즈 컴포넌트들은 수십 가지의 디자인 옵션(색상, 자간, 폰트, 정렬 등)을 매개변수로 가짐.
            // 이를 일일이 순서대로 채우는 것은 불가능하므로, 지정 인자(Named Arguments) 문법이 필수.
            text = "새로운 공지사항이 있습니다. 클릭하여 확인하세요.",
            color = Color("#FF0000"),
            style = TextStyle(fontSize = 18, fontWeight = "Bold")
        )
    }
}

fun main() {
    NotificationCard()
}