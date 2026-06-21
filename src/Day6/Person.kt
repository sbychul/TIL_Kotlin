package Day6

// 클래스 이름 옆 괄호로 생성자 설정하는 방식. 이름과 나이를 받아 객체 생성.
// 멤버 변수 선언 + 생성자 정의 + 값 대입이 한 번에 끝나게 됨.
class Person(val name: String, val age: Int) {
    // 내부 상태를 이용하여 실시간으로 계산, 성인 여부를 판별하는 프로퍼티.
    // getter 메소드 필요 없음. 변수처럼 정의하되, 내부에 get() 블록 자체를 정의하여 커스텀 게터 생성.
    val isAdult: Boolean // person.isAdult 형태로 사용. (일반 변수를 불러오는 것)
        get() = this.age >= 20 // 객체의 age가 20 이상 여부를 판별하는 get() 블록을 정의하여 boolean 값을 반환.

    // 프로퍼티 방식이 아닌 함수로 구현하고자 한다면:
    fun checkAdult(): Boolean = age >= 20 // 일반 함수 형태로 클래스 내부에 정의
}