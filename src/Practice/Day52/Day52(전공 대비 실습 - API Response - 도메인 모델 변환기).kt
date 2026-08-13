package Practice.Day52

// 응용 과제 #1: API Response ➡️ UI Domain Model 변환기
// 가상의 서버 API 반응 데이터(NetworkResponse)를 입력받아, 성공/실패 여부에 따라 적절한 UI 상태 객체(UiState)로 변환하고, 데이터 정렬 및 필터링까지 수행하는 함수를 작성하세요.

// 기본 데이터 구조
// 서버 API 응답 데이터 (DTO)
data class UserDto(
    val id: Long,
    val rawName: String?,      // 이름 (null 가능)
    val age: Int,
    val isActive: Boolean,
    val role: String           // "ADMIN", "USER", "GUEST" 등
)

// UI에서 사용할 불변 도메인 모델
data class UserUiModel(
    val id: Long,
    val displayName: String,   // rawName이 null이면 "익명 유저"로 대체
    val isAdult: Boolean,       // age >= 19
    val roleLabel: String      // "ADMIN" -> "[관리자]", "USER" -> "[일반]", 그 외 -> "[게스트]"
)

// Sealed Interface를 활용한 상태 패턴
sealed interface UiState {
    data class Success(val users: List<UserUiModel>, val totalCount: Int) : UiState
    data class Error(val message: String) : UiState
    object Loading : UiState
}

// 메인 메뉴, 함수 구현하기
fun processApiResponse(rawUsers: List<UserDto>?, isNetworkSuccess: Boolean): UiState {
    if (!isNetworkSuccess || rawUsers.isNullOrEmpty()) { // 네트워크 실패 처리: 실패거나 유저 리스트가 null 또는 비어 있다면
        return UiState.Error("데이터를 불러오지 못했습니다.")
    }
    val activeList = rawUsers.filter { it.isActive } // isActive == true인 것만 포함해서 UserDto 리스트로 변환
        .toList()

    val dtoList = mutableListOf<UserUiModel>() // 빈 리스트 생성
    for (user in activeList) {
        dtoList.add(UserUiModel(
            id = user.id, // id는 그대로 받음.
            // rawName이 null이거나 빈 문자열이면 "익명 유저"로 변환
            displayName = if (user.rawName.isNullOrBlank()) "익명 유저" else user.rawName,
            isAdult = if (user.age >= 19) true else false, // 19세 이상만 true
            // "ADMIN" -> "[관리자]", "USER" -> "[일반]", 그 외 -> "[게스트]"
            roleLabel = if (user.role == "ADMIN") "[관리자]" else if (user.role == "USER") "일반" else "[게스트]"
        ))
    }

    val sortedList = dtoList.sortedWith(
            compareByDescending<UserUiModel> { it.isAdult } // 성인 여부 우선 정렬
                .thenBy { it.displayName } // 성인 여부가 같다면 이름 기준으로 오름차순 정렬
            )

    if (sortedList.isEmpty()) { return UiState.Error("활성화된 유저가 없습니다.") }
    return UiState.Success(sortedList, sortedList.size)
}

// 테스트 케이스
fun main() {
    val sampleDtos = listOf(
        UserDto(1, "김철수", 25, isActive = true, role = "USER"),
        UserDto(2, null, 17, isActive = true, role = "GUEST"),
        UserDto(3, "이영희", 30, isActive = false, role = "ADMIN"), // 비활성화 유저
        UserDto(4, "박민수", 19, isActive = true, role = "ADMIN"),
        UserDto(5, "", 15, isActive = true, role = "USER")
    )

    val result = processApiResponse(sampleDtos, isNetworkSuccess = true)
    println(result)
}