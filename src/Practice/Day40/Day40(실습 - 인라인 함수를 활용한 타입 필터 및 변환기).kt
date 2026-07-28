package Practice.Day40

// [서론]
// inline: 함수 호출 위치에 함수의 본문 코드를 컴파일러가 직접 복사해 넣는(Inlining) 키워드
// reified: inline 함수에만 사용할 수 있는 특수 키워드로, 런타임에도 제네릭 타입 T의 실제 정보(타입)를 유지하게 함

// 사실 .filterIsInstance<>()라는 함수가 대놓고 있지만, 직접 구현하는 데 의의를 두는 활동이다.
inline fun <reified T> List<*>.filterIsInstanceTyped(): List<T> {
    val result = mutableListOf<T>() // 빈 리스트 선언
    // this(주어진 리스트)에 있는 모든 요소를 순회,
    // 요소가 T 타입이라면 result 리스트에 삽입한다.
    for (item in this) { if (item is T) { result.add(item) } }
    return result // 반환
}

// 임의의 객체에 대해 T 타입으로 안전한 형 변환을 시도하는 함수
inline fun <reified T> Any.castOrNull(): T? {
    return if (this is T) { this as T } // 객체가 T 타입이면 this as T로 형변환
    else null // 아니라면 그냥 널
}

// 심화) 컬렉션의 요소 중 it is T인 요소만 골라내
// T 타입 요소에 대해 transform 람다를 수행하여 R 타입으로 변환
// 이 때 예외가 발생하더라도 전체 프로세스가 터지지 않도록 하는 함수.

// crossinline: inline 함수 내부에서 넘겨받은 람다(transform)가 또 다른 람다나 비동기 블록/다른 객체의 전달 인자로 쓰일 때,
// 람다 내부에서 비local 리턴(return)을 금지하도록 강제하는 키워드
inline fun <reified T, R> Iterable<*>.mapInstance(
    crossinline transform: (T) -> R
): List<R> = filterIsInstance<T>() // T 타입인 요소들만 골라내기
        // 각각의 item에 대하여 runCatching블록에서 검사.
        // Result 객체의 특성을 활용, 성공(Success) 시 결과값을, 실패(Failure) 시 null을 반환하는 함수를 사용.
        // 요소에 대한 연산의 최종 결과가 null이 아닌 것만 최종적으로 래핑
        .mapNotNull { item -> runCatching { transform(item) }.getOrNull() }

// 테스트 케이스
fun main() {
    val mixedList: List<Any> = listOf(
        "Kotlin",
        100,
        "Python",
        3.14,
        true,
        "Java",
        200
    )

    println("=== 1. reified 타입 필터링 테스트 (String만 추출) ===")
    // <String>을 명시적으로 전달하거나 타입 추론을 이용해 String 요소만 필터링
    val stringList: List<String> = mixedList.filterIsInstanceTyped<String>()
    println("문자열 요소들: $stringList")
    // 출력: [Kotlin, Python, Java]

    println("\n=== 2. reified 타입 필터링 테스트 (Int만 추출) ===")
    val intList: List<Int> = mixedList.filterIsInstanceTyped<Int>()
    println("정수형 요소들: $intList")
    // 출력: [100, 200]

    println("\n=== 3. 안전한 타입 캐스팅 (castOrNull) 테스트 ===")
    val data: Any = "Hello Kotlin"

    val castedString: String? = data.castOrNull<String>()
    println("String 캐스팅 성공: $castedString") // 출력: Hello Kotlin

    val castedInt: Int? = data.castOrNull<Int>()
    println("Int 캐스팅 실패(null 반환): $castedInt") // 출력: null

    val rawData: List<Any> = listOf(
        "100",
        "200",
        300,
        "abc", // Int 변환 시 NumberFormatException 발생 케이스!
        "500",
        true
    )

    println("\n=== 4. [심화] mapInstance 테스트 ===")
    // String 타입만 찾아서 Int로 변환 (숫자 변환 실패 시 안전하게 패스)
    val parsedInts: List<Int> = rawData.mapInstance<String, Int> { str ->
        str.toInt()
    }

    println("문자열 중 안전하게 정수로 변환된 결과: $parsedInts")
    // 출력: [100, 200, 500] ("abc"는 예외 발생 후 스킵됨, 300/true는 String이 아니라 스킵됨)
}