// 자바의 enum과 동일, 단순히 이름만 나열할 수도 있고, 내부에 프로퍼티를 가질 수도 있다.
// 세 가지 상태 (준비 중, 배달 중, 완료)를 갖는다.
// enum 클래스와 sealed 클래스의 차이를 확인하는 것이 오늘의 진도.
// 이걸 쓰지는 않는다. sealed 클래스가 좀 더 딥한 enum 느낌. 이런 게 있다는 것만 알아두셔
enum class DeliveryStatus { PREPARING, DELIVERING, COMPLETED }

// Sealed class, 상속 가능한 자식들을 컴파일 시점에 전부 확정해 둔 클래스.
// 클래스(상태)의 종류를 제한. when에서 모든 경우를 안전하게 처리하도록 만드는 코틀린의 sum type을 구현.
// enum보다 표현력이 강한 상태 모델링 도구.
sealed class OrderResult // 뼈대 선언

// Sealed Class의 하위 클래스들은 데이터 구조를 각자 다르게 가질 수 있음.
// 배달 성공 시: 배달 완료 시간을 담는 프로퍼티를 가짐.
data class Success(val completeTime: String) : OrderResult()
// 실패 시: 실패 이유를 담는 프로퍼티를 가짐.
data class Failure(val reason: String) : OrderResult()

// OrderResult 타입을 매개변수로 담는 함수
// 결과에 따른 다른 각기 다른 출력을 진행하도록 한다.
fun handleResult(result: OrderResult) {
    // when 식: 자바의 switch문이 대체된 것.
    // sealed class나 enum class를 when 식과 조합하면 else문이 필요 없다.
    // 컴파일러가 자식 클래스의 종류를 알고 있어, 누락된 분기가 있다면 컴파일 에러가 발생하게 된다.
    when (result) {
        is Success -> println("[성공] 배달이 완료되었습니다. (완료 시간: [${result.completeTime}])")
        is Failure -> println("[실패] 배달에 실패했습니다. 사유: [${result.reason}])")
        // OrderResult 클래스의 모든 하위 클래스를 체크하였기 때문에 else문이 필요 없다.
    }
}

fun main() {
    // 각기 다른 상태 객체를 생성
    val success = Success("18:30")
    val failure = Failure("주소 불명명")

    // 상태에 따른 출력 결과 확인
    handleResult(success)
    handleResult(failure)
}