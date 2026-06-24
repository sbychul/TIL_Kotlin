fun main() {
    // Kotlin의 객체 생성 방식. 클래스명을 바로 호출, 변수에 담는다.
    val goat = Person("Faker", 30)

    // 프로퍼티를 활용한 출력
    println("[${goat.name}]의 나이는 [${goat.age}]세 이며, 성인 여부는 [${goat.isAdult}] 입니다.")
    // 함수를 활용한 출력 (결과는 똑같음. 어떤 방식으로 사용되는지(불러오는지) 차이를 확인)
    println("[${goat.name}]의 나이는 [${goat.age}]세 이며, 성인 여부는 [${goat.checkAdult()}] 입니다.")
}