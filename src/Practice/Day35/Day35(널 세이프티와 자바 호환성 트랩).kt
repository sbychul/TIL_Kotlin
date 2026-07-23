package Practice.Day35

class UserProfileValidator {
    // 매개변수로 널이 들어올 수 있는 name을 받아 반환, null이라면 "익명 사용자"를 반환
    fun getValidName(name: String?): String {
        if (name.isNullOrBlank()) { return "익명 사용자" }
        return name
    }

    fun getEmailDomain(email: String?) : String? {
        // null이 아니고 @를 포함한다면 @를 기준으로 문자열을 분리해 1번 인덱스(@ 뒷 부분 == 도메인)을 반환
        // 둘 중에 하나라도 만족하지 않으면 null 반환
        if (email != null && email.contains("@")) { return email.split("@")[1] }
        return null
    }

    // 자바에서 넘어온 타입을 가정한 메서드. String?으로 선언해서 Safe-call로 방어해야 한다.
    // 자바 코드는 @Nullable 또는 @NotNull 어노테이션이 붙어 있지 않으면, 코틀린 컴파일러가 이를 String! (플랫폼 타입)으로 인식한다.
    // null이 들어오는 순간 런타임 NPE가 발생하기 때문에, 반환 타입을 항상 Nullable 타입으로 받아내어 처리하도록 해야 한다.
    fun processJavaBio(javaBio: String?) : String {
        if (javaBio.isNullOrEmpty()) { return "소개글 없음" } // null이거나 비어 있으면 소개글 없음 반환
        if (javaBio.length > 10) { return javaBio.substring(0, 10) + "..."} // 10자 초과라면 10번째 글자 이후는 "..."으로 처리
        return javaBio
    }
}

// main 함수 및 테스트 구조
fun main() {
    val validator = UserProfileValidator()

    println("=== 1. 이름 검증 테스트 ===")
    println(validator.getValidName("홍길동"))     // 출력: 홍길동
    println(validator.getValidName("   "))       // 출력: 익명 사용자
    println(validator.getValidName(null))        // 출력: 익명 사용자

    println("\n=== 2. 이메일 도메인 추출 테스트 ===")
    println(validator.getEmailDomain("test@kotlincode.com")) // 출력: kotlincode.com
    println(validator.getEmailDomain("invalid-email"))       // 출력: null
    println(validator.getEmailDomain(null))                  // 출력: null

    println("\n=== 3. 자바 소개글(Platform Type) 방어 테스트 ===")
    println(validator.processJavaBio("안녕하세요, 개강이 오지 않았으면 합니다."))
    // 출력: 안녕하세요, 개강이...
    println(validator.processJavaBio(null))
    // 출력: 소개글 없음
}