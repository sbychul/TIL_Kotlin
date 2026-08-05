package Practice.Day45

// 문제: 인벤토리 무기 강화 시스템 (Item Enhancement System)
// 플레이어 객체를 받아서 플레이어가 보유한 무기를 안전하게 강화한 후, 업데이트된 Item 객체를 반환하는 enhanceWeapon 함수를 작성하세요.

// 기본 데이터 구조
data class Item(val name: String, val level: Int, val power: Int)
data class Inventory(val weapon: Item?, val armor: Item?)
data class Player(val name: String, val inventory: Inventory?)

// 오늘의 메인 메뉴, 플레이어, 인벤토리, 무기 중 하나라도 널이면 널 반환
// 아니라면 무기의 레벨을 1 올리고, enhanceAmount만큼 무기의 공격력을 올린 새 아이템 객체를 반환.
fun enhanceWeapon(player: Player?, enhancementAmount: Int): Item? = 
    // ?. : Safe Call, ?. 앞의 객체가 널이 아니면 뒤의 프로퍼티나 메서드에 접근, 널이면 널 반환.
    player?.inventory?.weapon?.let { // null이 아니면 람다 블록 실행
        it.copy(
            level = it.level + 1,
            power = it.power + enhancementAmount
        )
    }

// 테스트 케이스
fun main() {
    // 1. 정상적으로 무기를 보유한 플레이어
    val player1 = Player("유저A", Inventory(weapon = Item("전설의 검", 1, 100), armor = null))

    // 2. 인벤토리는 있으나 무기가 없는 플레이어
    val player2 = Player("유저B", Inventory(weapon = null, armor = Item("갑옷", 1, 50)))

    // 3. 인벤토리 자체가 없는 플레이어
    val player3 = Player("유저C", inventory = null)

    println(enhanceWeapon(player1, 50))
    // 기대 출력: Item(name=전설의 검, level=2, power=150)

    println(enhanceWeapon(player2, 50))
    // 기대 출력: null

    println(enhanceWeapon(player3, 50))
    // 기대 출력: null
}