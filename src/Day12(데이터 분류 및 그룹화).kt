fun main() {
    val list = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

    // 조건이 참인 것들과 아닌 것들로 쪼개는 partition 함수.
    // 예시) val partitionedList = list.partition { it >= 5 }
    // Pair라는 객체에 담아 반환된다. partitionedList.first = 참인 리스트 / partitionedList.second = 거짓인 리스트.
    // 두 개의 데이터를 묶어 둔 객체이므로, 괄호를 이용해 두 변수로 쪼개 아래와 같이 한 번에 받을 수 있다.
    val (overFive, belowFive) = list.partition { it > 5 }

    // 기준에 따라 Map에 나눠 담는 groupBy 함수.
    // 특정 Key값에 따라 그룹화하여 Map<Key, List<Value>> 구조로 만들어 줌. (람다식의 마지막 줄 변환 결과가 그대로 Key가 됨)
    // 특정 원소가 우측 조건을 만족하면 "EVEN"이라는 Key에 대응되는 List에 해당 원소를 삽입.
    // 아니라면 "ODD"라는 Key에 대응되는 List에 삽입한다.
    val grouped = overFive.groupBy { if (it % 2 == 0) "EVEN" else "ODD" }

    println("5 이하의 숫자들: ${belowFive}\n5 초과의 숫자들 중 분류: ${grouped}")
}