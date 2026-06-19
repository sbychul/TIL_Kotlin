fun main() {
    // listOf로 생성한 리스트는 불변(추가, 삭제, 변경 불가).
    // mutableListOf로 생성한 리스트는 가변. (인덱스를 통하여 변경 가능, 요소 추가, 삭제 가능)
    val list = listOf(12, 45, 7, 23, 99, 34, 5)

    // 최댓값, 최솟값을 저장할 변수. 리스트의 첫 번째 값으로 초기화.
    var max = list[0]
    var min = list[0]

    // 반복 돌며 찾기. 사실 반복 없이 .maxOrNull() / .minOrNull() 함수를 이용하여 한 번에 찾을수도 있다.
    for (i in list) {
        if (i > max) max = i
        if (i < min) min = i
    }

    // 출력부
    println("최댓값: ${max}, 최솟값: ${min}")
}