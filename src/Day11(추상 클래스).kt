// 코틀린의 클래스와 메서드는 기본적으로 final(상속 불가)
// 상속 가능한 클래스를 만드려면 앞에 open 키워드를 붙여줘야 한다.
// 추상 클래스는 기본적으로 open이기 때문에 생략해도 됨.
abstract class GameCharacter(val name: String, var hp: Int) {
    // 추상 메서드 선언. 하위 클래스는 구현해야 한다.
    abstract fun attack()
}

// 상속은 콜론 사용. 부모 클래스의 생성자에 name과 hp를 넘김.
// (하위 클래스(매개변수)) : (상위 클래스 생성자: (클래스명(매개변수))
class Warrior(name: String, hp: Int) : GameCharacter(name, hp) {
    // @Override 어노테이션이 아닌 override 키워드를 강제한다.
    override fun attack() {
        println("${name}이(가) 검을 휘둘러 피해를 입힙니다.")
    }
}

// 하위 클래스에 변수를 추가할 때는, 주 생성자에서 부모 클래스의 생성자에 넘길 매개변수는 val/var 없이 작성,
// 새로 추가할 변수에만 키워드를 붙여주면 된다.
class Wizard(name: String, hp: Int, var mp: Int) : GameCharacter(name, hp) {
    override fun attack() {
        println("${name}이(가) 마법 공격을 가합니다. (남은 MP: ${mp})")
    }
}

fun main() {
    val characters = listOf<GameCharacter>(Warrior("Faker", 1000), Wizard("Keria", 500, 500))
    // 리스트를 순회하며 공격 메서드 호출, 오버라이딩이 성공적으로 적용되었는지 확인.
    for (c in characters) {
        c.attack()
    }
    // 추가로, 다운캐스팅을 할 때 사용했던 instanceof 연산자는 is 키워드(스마트 캐스트)를 사용한다.
    // if (c is Wizard) { ... }
}
