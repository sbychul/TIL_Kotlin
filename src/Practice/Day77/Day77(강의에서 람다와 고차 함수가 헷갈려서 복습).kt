package Practice.Day77

import kotlin.reflect.KFunction2

// 강의 복습 과제: 지능형 텍스트 토큰 파이프라인 (Text Token Pipeline)
// 4주 차 강의 자료(고차 함수, 람다 식 기초, 람다 식 활용)의 핵심 개념을 점검할 수 있는 복습용 집중 과제입니다.
// 강의 슬라이드에 등장한 함수 참조(::)와 Reflection 타입(KFunction), Nullable 함수 타입(((...) -> ...)?), Trailing Lambda(후행 람다)와 암시적 인자 it,
// 밑줄(_) 인자 생략, 확장 함수와 람다의 결합, 그리고 외부 변수를 캡처하는 클로저(Closure)를 한 문제 안에 유기적으로 엮었습니다.

// 단어 변환 순수 함수 정의, ::pureMask ::pureUpper 형태로 전달할 수 있도록 함.
fun pureMask(word: String, replacement: String): String = replacement
fun pureUpper(word: String, dummy: String): String = word.uppercase()

// String의 확장 함수. 공백 기준 문자열 분리 및 조건(predicate 함수)을 만족하는 토큰만 필터링하여 리스트로 반환
fun String.cleanTokens(predicate: (String) -> Boolean = { it.isNotBlank() }) : List<String>
    // 문자열을 공백으로 쪼개 인자로 받은 람다 함수로 필터링한 리스트를 반환
    = this.split(" ").filter { predicate(it) }

// 고차 함수 및 클로저 기반 변환 함수
fun buildTokenTransformer(
    prefix: String,
    converter: ((String, String) -> String)? = null
): (String) -> String {
    // 함수 내부에서 호출 횟수를 카운트하는 변수를 둔다
    var callCount = 0
    // 함수 자체를 반환한다. 따라서 return 키워드 뒤에 람다 식 자체를 둔다.
    return { token ->
        callCount++ // 호출될 때마다 카운트 1 증가.
        if (converter != null) {
            // null이 아니라면 인자로 받은 converter 함수에 token과 prefix를 집어넣은 문자열을 반환하고
            "${converter(token, prefix)} (#$callCount)"
        } else {
            // 아니라면 아무 것도 적용하지 않은 단순 문자열을 반환한다.
            "$prefix: $token (#$callCount)"
        }
    }
}

fun main() {
    val rawLog = "INFO   user_login   DEBUG   404_error   FAIL   auth_token"

    // [복습 1] 확장 함수 + Trailing Lambda + it 활용
    // 길이가 4 이상인 토큰만 필터링
    val longTokens = rawLog.cleanTokens { it.length >= 4 }
    println("1. 필터링된 토큰: $longTokens")

    // [복습 2] 함수 참조(::) 및 Reflection 타입 검증
    val maskRef: KFunction2<String, String, String> = ::pureMask
    println("2. 함수 참조 검증 -> 이름: ${maskRef.name}, 파라미터 수: ${maskRef.parameters.size}")

    // [복습 3] 고차 함수 + 클로저(카운트 누적) + Nullable 함수 인자(기본값 null)
    // converter가 null인 경우 -> 기본 접두사 포맷팅과 카운트 누적
    val defaultTransformer = buildTokenTransformer(prefix = "LOG")
    println("\n3. 기본 트랜스포머 (클로저 동작 확인):")
    println("  " + defaultTransformer("system_init"))
    println("  " + defaultTransformer("service_start"))

    // [복습 4] 함수 참조(::pureMask) 전달
    val maskTransformer = buildTokenTransformer(prefix = "****", converter = ::pureMask)
    println("\n4. 마스킹 트랜스포머 (함수 참조 전달):")
    println("  " + maskTransformer("secret_password"))
    println("  " + maskTransformer("private_key"))

    // [복습 5] 람다 식 + 미사용 인자 밑줄(_) 처리
    // converter의 두 번째 인자인 prefix를 사용하지 않고 토큰만 대문자로 변환
    val upperTransformer = buildTokenTransformer(prefix = "IGNORED") { token, _ ->
        token.uppercase()
    }
    println("\n5. 대문자 트랜스포머 (밑줄 인자 생략 활용):")
    println("  " + upperTransformer("user_session"))
    println("  " + upperTransformer("network_packet"))
}