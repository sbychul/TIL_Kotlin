package Practice.Day44

import kotlin.math.cos

// 문제: 게임 이벤트 처리기 (Game Event Processor)
// 다양한 게임 이벤트 리스트가 입력되었을 때, 각 이벤트의 타입에 따라 캐릭터의 최종 체력(HP)과 골드(Gold) 상태를 계산하여 반환하는 함수 processEvents를 작성하세요.

// 캐릭터 상태 불변 데이터 클래스
data class CharacterState(val hp: Int, val gold: Int)

// 게임 이벤트 (Sealed Interface)
// Sealed Interface - 상속 가능한 자식들을 컴파일 시점에 전부 확정해 둔 클래스가 Sealed Class, 이건 인터페이스다.
// 인터페이스이기에 다중 상속이 필요할 때 아주 끝내준다.
sealed interface GameEvent {
    // 마찬가지로 하위 클래스들은 데이터 구조를 각자 다르게 가질 수 있음.
    data class Heal(val amount: Int) : GameEvent       // HP 회복
    data class Damage(val amount: Int) : GameEvent     // HP 감소
    data class EarnGold(val amount: Int) : GameEvent   // 골드 획득
    data class ShopPurchase(val cost: Int) : GameEvent // 골드 소비
}

// 오늘의 메인 메뉴, 이벤트 처리 함수
fun processEvents(initialState: CharacterState, events: List<GameEvent>): CharacterState {
    var hpSum = initialState.hp
    var goldSum = initialState.gold

    for (event in events) {
        when (event) {
            is GameEvent.Heal -> hpSum += event.amount
            // 체력은 0 이하로 떨어질 수 없음.
            is GameEvent.Damage -> hpSum = maxOf(0, hpSum - event.amount)
            is GameEvent.EarnGold -> goldSum += event.amount
            // 만약 갖고 있는 돈보다 금액이 더 크다면 결제는 진행되지 않음.
            is GameEvent.ShopPurchase -> if (goldSum >= event.cost) { goldSum -= event.cost }
        }
    }

    val newState = CharacterState(hpSum, goldSum)
    return newState
}

// 테스트 케이스
fun main() {
    val initialState = CharacterState(hp = 50, gold = 100)

    val events = listOf(
        GameEvent.Damage(30),       // HP: 50 -> 20
        GameEvent.Heal(20),         // HP: 20 -> 40
        GameEvent.ShopPurchase(60), // Gold: 100 -> 40 (구매 성공)
        GameEvent.ShopPurchase(50), // Gold: 40 (골드 부족으로 실패, 변화 없음)
        GameEvent.EarnGold(30),     // Gold: 40 -> 70
        GameEvent.Damage(100)       // HP: 40 -> 0 (최솟값 0 보장)
    )

    val finalState = processEvents(initialState, events)
    println(finalState)
    // 기대 출력: CharacterState(hp=0, gold=70)
}