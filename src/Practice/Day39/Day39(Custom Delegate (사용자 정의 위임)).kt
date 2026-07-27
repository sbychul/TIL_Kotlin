package Practice.Day39

import kotlin.reflect.KProperty

// 커스텀 위임 클래스
class BoundedValue(var value: Int, val min: Int, val max: Int) {
    // 객체 생성과 동시에 value를 min과 max 사이의 값으로 제한. (사이 값이라면 value 그대로 반환)
    init { value = value.coerceIn(min, max) }

    // operator 키워드: by 키워드 뒤에 오는 객체는 반드시 operator fun getValue와 setValue 연산자 함수를 갖고 있어야
    // 컴파일러가 대입/읽기 연산을 매핑해 줌.
    // KProperty<*>: 코틀린 리플렉션 객체, .name을 통해 어떤 프로퍼티에서 이 위임자가 호출되었는지 알아낼 수 있음.
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Int = value
    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: Int) {
        val result = newValue.coerceIn(min, max)
        // 만약 범위 밖이라면 경고 로그 출력
        if (newValue !in min..max) {println("[경고] hp는 ${min}에서 ${max} 사이어야 합니다. (시도: ${newValue} -> 적용: ${result})")}
        value = result // 이후 coerceIn을 이용하여 제한된 범위 내 저장된 값을 value에 대입.
    }
}

class GameCharacter(val name: String) {
    // 내부 프로퍼티를 커스텀 위임 클래스를 이용하여 초기화
    var hp: Int by BoundedValue(100, 0, 100)
    var mp: Int by BoundedValue(50, 0, 50)
}

// 테스트 케이스
fun main() {
    val hero = GameCharacter("전사")

    println("=== 1. 초기 정상 상태 ===")
    println("${hero.name} HP: ${hero.hp}, MP: ${hero.mp}") // HP: 100, MP: 50

    println("\n=== 2. 최대값 초과 대입 테스트 ===")
    hero.hp = 150 // [경고] hp는(은) 0 ~ 100 범위를 벗어났습니다. (시도: 150 -> 적용: 100)
    println("현재 HP: ${hero.hp}") // 100

    println("\n=== 3. 최소값 미만 대입 테스트 ===")
    hero.hp = -30 // [경고] hp는(은) 0 ~ 100 범위를 벗어났습니다. (시도: -30 -> 적용: 0)
    println("현재 HP: ${hero.hp}") // 0

    println("\n=== 4. 정상 범위 대입 테스트 ===")
    hero.hp = 70 // 경고 출력 없이 정상 변경
    println("현재 HP: ${hero.hp}") // 70
}