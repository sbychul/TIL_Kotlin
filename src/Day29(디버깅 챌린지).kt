open class Product29(val name: String)
class SmartPhone(name: String, val os: String) : Product29(name)

class OrderSystem {
    // 가변(var) 프로퍼티로 상품 정보를 가짐
    var currentProduct: Product29? = SmartPhone("Galaxy S26", "Android")

    fun processProduct() {
        // [문제 1] 타입 검사 후 스마트 캐스트를 시도하는 부분
        if (currentProduct is SmartPhone) {
            // 아래 줄에서 컴파일 에러가 발생합니다. 왜일까요?
            println("스마트폰 OS: ${currentProduct.os}")
            // 답: var이기 때문에 if문 직후 다른 일반 Product 객체나 null이 들어가버릴 수 있기 때문에 컴파일러에서 이를 거부한다.
            // val(불변 변수)으로 세팅해 주면 오류가 발생하지 않음.
        }
    }
}

fun main() {
    val system = OrderSystem()
    system.processProduct()

    // [문제 2] 값 비교와 참조 비교의 함정
    val name1 = String(charArrayOf('K', 'o', 't', 'l', 'i', 'n')) // 새로운 문자열 객체 생성
    val name2 = String(charArrayOf('K', 'o', 't', 'l', 'i', 'n'))

    // 자바/C 프로그래머의 착각으로 만든 비교문
    // 코드가 실행된다면(1번 문제 에러 수정 후), 두 비교문은 각각 true가 나올까요, false가 나올까요?
    println("결과 A: ${name1 == name2}")
    println("결과 B: ${name1 === name2}")
    // 답: true / false 순으로 출력.
    // ==: 객체의 값이 같은 지(.equals()) 확인.
    // ===: 객체의 주소가 같은지 확인.
}