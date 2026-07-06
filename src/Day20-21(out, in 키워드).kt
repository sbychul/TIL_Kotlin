// 추상 클래스 선언
abstract class Character(val name: String)
// Character를 상속받는 클래스 선언
class Fighter(name: String) : Character(name)

// 20일차, 공변성 out 키워드
// out 키워드: 하위 타입을 상위 타입처럼 사용할 수 있게 하는 공변성(읽기 전용) 키워드.
// 데이터의 흐름이 외부로 나가는 방향임을 컴파일러에게 보장한다.
// T 앞에 out을 붙이면 공변성(Covariance, 하위 타입도 상위 타입으로 취급)이 확립.
// "안에 Fighter가 들어 있는 것도 Character가 들어 있는 걸로 취급해도 된다."
class CharacterProducer<out T>(private val character: T) {
    fun get(): T {
        return character
    }

    // out을 쓰면 뭐가 가능한가?
    // Fighter는 Character의 하위 타입.
    // out이 붙었으므로 CharacterProducer<Fighter>도 CharacterProducer<Character>의 하위 타입.
    // 따라서 큰 주머니에 작은 주머니를 그대로 집어넣는
    // val ch = CharacterProducer<Character> = CharacterProducer(Fighter("정찬성")) 연산이 제약 없이 가능.
}

// 21일차, 반공변성 in 키워드
// in 키워드: 상위 타입을 하위 타입처럼 사용할 수 있게 하는 반공변성 키워드. ? super T를 한 단어로 만든 것.
// T 앞에 in을 붙이면 반공변성(Contravariance, 상위 타입을 하위 타입으로, 반대 방향으로 대입이 가능)이 확립.
// 데이터의 흐름이 오직 내부로 들어오는 방향(Write-Only, Consumer)임을 컴파일러에게 약속(in)하면, 상자의 계층 구조가 알맹이의 상속 계층과 반대가 됨.
// 일반 캐릭터의 장비를 닦을 수 있는 청소기(Character)라면, 격투가(Fighter)의 장비쯤은 아무런 문제 없이 안전하게 닦을 수 있다"는 논리.
class EquipmentCleaner<in T : Character> { // : Character 를 작성, 받을 수 있는 클래스의 상한(무조건 Character 계열임)을 명시.
    fun clean(target: T) {
        // 단순 데이터 소비 영역
        println("[청소 완료] ${target.name}의 장비를 깨끗하게 닦았습니다.")
    }

    // 만약 fun get(): T 처럼 반환 타입(Output) 자리에 T를 쓰려고 하면
    // 컴파일러가 안전성이 깨진다고 판단하여 컴파일 에러 발생. (Consumer(소비만 하는 클래스임을) 보장)

    // in을 쓰면 무엇이 가능한가?
    // Character가 Fighter보다 상위 타입.
    // in이 붙었으므로 상자의 크기는 반대로 뒤집혀 EquipmentCleaner<Character>가 EquipmentCleaner<Fighter>의 하위 타입.
    // 따라서 격투가 청소기 변수에 캐릭터 청소기 객체를 그대로 집어넣는
    // val fighterCleaner: EquipmentCleaner<Fighter> = generalCleaner 연산이 컴파일 에러 없이 성립.
}

fun main() {
    // 20일차.
    // Fighter 객체를 담은 상자 생성
    val fighterProducer: CharacterProducer<Fighter> = CharacterProducer(Fighter("정찬성"))

    // 이 상자를 더 큰 개념인 캐릭터 상자 변수에 그대로 대입해보자. (out이 없다면 에러가 발생하는 부분이다)
    val generalProducer: CharacterProducer<Character> = fighterProducer

    // 더 큰 개념의 상자에 넣었음에도 이름 "정찬성"이 정상적으로 출력되는 지 확인해 보자.
    println("캐릭터명: ${generalProducer.get().name}")


    // 21일차.
    // 모든 Character를 범용적으로 다룰 수 있는 청소기 객체 생성.
    val generalCleaner: EquipmentCleaner<Character> = EquipmentCleaner()

    // 해당 범용 캐릭터 청소기를 Fighter 전용 청소기 변수에 그대로 대입.
    val fighterCleaner: EquipmentCleaner<Fighter> = generalCleaner

    // 더 작은 개념의 청소기에 대입했음에도 정상적으로 작동하는지 확인해 보자.
    fighterCleaner.clean(Fighter("정찬성"))
}