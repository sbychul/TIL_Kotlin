// fun 클래스명.함수명() 형태로 정의, 해당 클래스의 원래 함수인 것처럼 사용 가능.
// 입력된 문자열이 null이거나 공백이라면 null 반환
// 유효한 문자열이라면 앞뒤 공백을 제거 후 문자열을 이름, 나이는 20인 새 Person 객체 반환.
// run은 객체의 상태를 변경하거나 계산하여 최종 결과값을 반환할 때 사용.
fun String?.toCleanPerson(): Person? =
    if (this.isNullOrBlank()) { null }
    else this.run { Person(trim(), 20) }

fun main() {
    val list = mutableListOf<Person?>() // 빈 배열 생성
    list.add("       ".toCleanPerson())
    list.add("    아이고   ".toCleanPerson())

    // null 잘 무시하는지, 무시했다면 객체 생성이 잘 되었는지 확인하기 위한 부분
    for (i in list) { println(i?.name) }
}