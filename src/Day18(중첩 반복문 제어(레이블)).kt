fun main() {
    // 2차원 리스트 선언
    val grid = listOf(listOf("A", "B", "C"), listOf("D", "TARGET", "F"), listOf("G", "H", "I"))

    // forEach 블록 내부에서 break를 쓰면 컴파일 에러가 발생한다.
    // 반복문이나 람다 블록 앞에 이름@ 형태로 태그를 붙여 준다.
    // 그렇게 하면 블록 내부에서 return@이름 을 통해 그 지점까지의 흐름을 제어할 수 있다.
    // 단, forEach(고차 함수) 블록 내부에서 레이블 리턴을 사용한다면 다른 언어의 continue처럼 작동하기 때문에
    // 완전히 반복문을 탈출하려면 run 블록과 레이블을 감싸 주어야 한다. (일반적인 for문의 중첩에는 break@이름 을 직접 사용 가능)
    run myLoop@ { // run 블록에 myLoop이라는 이름을 붙여 주었다. 내부의 중첩 반복문을 제어한다.
        // 2차원 배열이기에 중첩 반복문으로 제어한다.
        grid.forEach { row -> // 각각의 열(리스트)에 대하여
            row.forEach { item -> // 해당 열(리스트)의 요소들을 확인
                if (item == "TARGET") return@myLoop // "TARGET"을 발견한다면 run 블록을 탈출한다.
            }
        }
    }
    // 끝
    println("검색을 종료합니다.")
}