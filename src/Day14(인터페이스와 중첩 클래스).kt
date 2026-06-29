// 인터페이스. 자바의 그 것과 거의 똑같다.
// 차이점은 추상 메서드가 아닌 구조가 있는 기본 메서드도 가질 수 있다는 것.
// 그러나 프로퍼티에 값을 직접 대입하는 건 안 된다.
interface Notifier {
    // 아무 것도 쓰지 않으면 어차피 인터페이스는 추상 메서드. abstract 키워드를 생략해도 된다.
    fun sendNotification(message: String)
}

// 상속은 동일하게 콜론을 이용한다.
class EmailNotifier : Notifier {
    // override 키워드는 붙여줘야 한다.
    override fun sendNotification(message: String) {
        println("[이메일 발송] 외부 서버를 통해 메일을 보냅니다: [${message}]")
    }

    // 중첩 클래스. 클래스 내부에서만 의미를 가지는 데이터 구조. 독립적인 설정을 다루는 영역.
    // 바깥 outer class(EmailNotifier)의 멤버에 접근 불가.
    class Config(val host: String) {
        fun printConfig() { println("현재 이메일 호스트 서버: [${host}]") }
    }
}

fun main() {
    // 중첩 클래스인 Config 객체 생성.
    // 바깥 클래스인 EmailNotifier 인스턴스를 따로 만들지 않고도 호출 가능.
    val host = EmailNotifier.Config("smtp.gmail.com")
    host.printConfig()

    // EmailNotifier 객체 생성.
    val notifier = EmailNotifier()
    notifier.sendNotification("아이고난!!")
}