package Practice.Day46

// 문제: 사용자 데이터 검증 및 변환 파이프라인 (Data Pipeline)
// 회원가입 서비스에서 이메일 목록을 검증하고, 마스킹(보안 처리)된 이메일 주소 리스트로 변환하는 확장 함수 파이프라인을 작성하세요.

// 문자열에 @가 있고, @를 기준으로 아이디와 도메인이 1자 이상 존재하는지 검사하는 함수
fun String.isValidEmail(): Boolean {
    val email = this.split("@") // @로 구분한 문자열의 배열을 생성
    // 문자열의 배열의 크기가 2인지("@"로 나누었을 때 2개로 잘 나뉘었는지, 즉 @가 있는지) 확인
    // 그리고 @를 기준으로 아이디와 도메인이 1자 이상 존재하는지 확인한 후 해당 결과를 반환
    return email.size == 2 && email[0].isNotEmpty() && email[1].isNotEmpty()
}

// 이메일의 아이디(@ 앞부분) 중 앞 2글자만 남기고 나머지는 *로 변환한 후 도메인과 다시 결합하는 함수 (아이디가 2자 이하일 경우 아이디 전체를 *로 처리)
fun String.maskEmail(): String {
    val email = this.split("@")
    val maskedId = if (email[0].length <= 2) { // 2자 이하라면 전체를 **로 처리.
        "*".repeat(email[0].length) // * 연산자는 안 되므로(없다!!) repeat 함수를 사용한다.
    } else { // 2자 이상일 경우
        // 앞 2글자를 take하고 나머지는 *로 처리.
        email[0].take(2) + "*".repeat(email[0].length - 2)
    }

    // 도메인까지 합쳐 완성된 masked 이메일 주소를 반환
    return "${maskedId}@${email[1]}"
}

// 리스트에 사용하는 고차 확장 함수.
// predicate 조건을 만족하는 요소만 필터링, transform 함수를 적용해 최종 리스트를 반환.
// 두 자리에 함수가 들어간다. 테스트 케이스에서 isValidEmail 함수의 조건을 통과했을 때, maskEmail 처리를 거친 이메일 리스트를 반환.
inline fun <T> List<T>.processData(predicate: (T) -> Boolean, transform: (T) -> String): List<String> {
    val result = mutableListOf<String>() // 빈 리스트 하나를 형성.
    for (email in this) { // this(리스트)에 있는 것을 순회하며 확인
        // predicate 조건에 리스트에 있는 이메일을 넣어 검사.
        // 조건에 만족한다면 transform 함수를 적용하여 result 리스트에 해당 이메일을 추가.
        if (predicate(email)) { result.add(transform(email)) }
    }
    return result

    // 단순하게 this.filter(predicate).map(transform) 라고 쓸 수도 있다.
}

// 테스트 케이스
fun main() {
    val rawEmails = listOf(
        "user123@gmail.com",
        "invalid-email",
        "a@test.com",
        "developer@kotlin.org",
        "@nodomain.com"
    )

    // processData 고차 확장 함수를 활용해 유효한 이메일만 필터링하고 마스킹 처리
    val processedEmails = rawEmails.processData(
        predicate = { it.isValidEmail() },
        transform = { it.maskEmail() }
    )

    println(processedEmails)
    // 기대 출력: [us*****@gmail.com, *@test.com, de*******@kotlin.org]
}