package Practice.Day38

// [서론]
// 코틀린은 예외 처리를 하나의 값(Value)처럼 다룰 수 있도록 runCatching { ... }과 Result<T> 타입을 제공.
// 이를 활용하면 예외 상황 발생 시 복구(getOrElse), 변환(map), 또는 부가 작업(onSuccess, onFailure)을 메서드 체이닝 방식으로 깔끔하게 처리할 수 있다.

// 커스텀 예외 클래스 선언
class InvalidDataException(message: String) : Exception(message)

class ServerResponseHandler {
    // 메서드 1.
    fun parseJsonPrice(rawJson: String?): Result<Int> =
        // runCatching 블록: 블록 내 실행 코드를 감싸며, 예외가 던져지면 런타임 크래시 대신, Result.Failure(Exception) 객체로 래핑하여 반환한다.
        runCatching {
            // 널이거나 비어 있는 문자열이라면 커스텀 예외 클래스에 메시지를 담아 예외를 던진다.
            if (rawJson.isNullOrBlank()) { throw InvalidDataException("JSON 데이터가 비어 있습니다.") }

            // 널이나 빈 칸이 아니면 toInt로 변환하여 반환.
            // 만약 숫자가 아니라 파싱이 안 된다면 NumberFormatException 발생, 자동으로 Result.Failure 객체로 래핑된다.
            // 파싱이 잘 되면 Result.Success(value) 객체로 래핑된다.
            rawJson.toInt()
        }
    // Result 객체란 무엇이냐?
    // 성공과 실패를 모두 담을 수 있는 상자, 에러를 담는 주머니, 연산 결과의 캡슐화.
    // runCatching 블록의 결과에 따라 Success, 또는 Failure가 되어서, 성공하면 결과를 담고, 실패하면 그 이유(예외)를 담는다.

    // Result<T> 핵심 함수 세 가지:
    // getOrNull(): 성공 시 결과값을, 실패 시 null을 반환
    // exceptionOrNull(): 실패 시 발생한 예외 객체를, 성공 시 null을 반환
    // getOrElse { ... }: 실패 시 예외를 전달받아 기본값/대체 로직 실행


    // 메서드 2.
    // 메서드 1에서 받아낸 Result 객체를 활용,
    // 메서드 1의 파싱이 성공한다면 문자열이 숫자로 파싱된 그대로의 정수가 반환.
    // 실패한다면 defaultPrice값이 반환된다.
    fun getSafePriceOrDefault(rawJson: String?, defaultPrice: Int): Int = parseJsonPrice(rawJson).getOrDefault(defaultPrice)
}

// 테스트 케이스
fun main() {
    val handler = ServerResponseHandler()

    println("=== 1. 정상 파싱 테스트 ===")
    val result1 = handler.parseJsonPrice("25000")
    println("성공 여부: ${result1.isSuccess}") // 출력: true
    println("파싱 결과: ${result1.getOrNull()}") // 출력: 25000

    println("\n=== 2. 숫자가 아닌 입력 예외 처리 테스트 ===")
    val result2 = handler.parseJsonPrice("InvalidNumber")
    println("실패 여부: ${result2.isFailure}") // 출력: true
    println("예외 메시지: ${result2.exceptionOrNull()?.javaClass?.simpleName}")
    // 출력: NumberFormatException

    println("\n=== 3. 안전한 기본값 반환(getOrElse / getOrDefault) 테스트 ===")
    println(handler.getSafePriceOrDefault("50000", 0)) // 출력: 50000
    println(handler.getSafePriceOrDefault("   ", 10000)) // 출력: 10000 (InvalidDataException 발생 후 복구)
    println(handler.getSafePriceOrDefault("bad_input", 10000)) // 출력: 10000 (NumberFormatException 발생 후 복구)
}