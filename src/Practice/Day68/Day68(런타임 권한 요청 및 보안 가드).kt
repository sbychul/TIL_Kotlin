package Practice.Day68

// 모바일 런타임 권한 요청 및 보안 가드 파이프라인 (Permission Guard Engine)
// 강의계획서의 [5주 차: 객체지향/상태 캡슐화 및 Sealed 클래스], [6주 차: 컬렉션 데이터 변환],
// 그리고 안드로이드 앱의 필수 요소인 "모바일 런타임 권한 요청 및 보안 가드 파이프라인 (App Permission Guard)"입니다.
// 안드로이드 6.0(마시멜로) 이후부터 사용되는 런타임 권한(Dangerous Permissions) 체계를 모델링했습니다.
// 카메라, 위치, 저장소 등의 권한 요청 시 거부 횟수(rationaleCount)에 따라 영구 거부(설정창 이동 유도)로 전이되는 안드로이드 고유의 상태 머신을 다룹니다.

// 앱 기능 실행 전 필요한 권한들의 승인 상태를 확인하고, 거부 이력에 따라 사용자에게 설명 팝업(Rationale)을 띄울지, 시스템 권한 다이얼로그를 띄울지,
// 아니면 설정창으로 안내할지 결정하는 권한 관리자를 구현하세요.

// 권한 유형
enum class PermissionType { CAMERA, LOCATION, STORAGE, NOTIFICATIONS }

// 권한 처리 결과 sealed 인터페이스
sealed interface PermissionDecision {
    data object Granted : PermissionDecision // 이미 허용되어 실행 가능
    // 최초 요청, 시스템 기본 권한 팝업을 띄움
    data class RequestSystemDialog(val permission: PermissionType) : PermissionDecision
    // 사용자가 이전에 거부함, 왜 권한이 필요한 지 설명하는 팝업이 필요할 때.
    data class ShowRationale(val permission: PermissionType, val message: String) : PermissionDecision
    // 2회 이상 거부됨(다시 묻지 않음 상태), 앱 설정 화면으로 보내야 함.
    data class NavigateToSettings(val permission: PermissionType) : PermissionDecision
}

// 권한 상태
data class PermissionRecord(
    val type: PermissionType,
    var isGranted: Boolean = false, // 허용되었는지 (기본값 false)
    var denialCount: Int = 0 // 누적 거부 횟수
)

// 권한 관리자 클래스
class AppPermissionManager {
    // 등록된 권한 기록들을 관리하는 내부 컬렉션을 가짐.
    private val records = mutableMapOf<PermissionType, PermissionRecord>()
    // init 블록을 통하여 초기 생성 시 모든 PermissionType에 대해 기본 PermissionRecord를 등록해 둠.
    init { for (type in PermissionType.entries) { records[type] = PermissionRecord(type) } }

    // 권한 상태를 확인하는 메서드
    fun checkPermission(permission: PermissionType) : PermissionDecision {
        // 밑에서 safe call 하라길래 넣은 엘비스 연산자..
        val record = records[permission] ?: throw Exception("해당 권한명을 찾을 수 없습니다.")
        // 이미 허용됐다면 그대로 끝.
        if (record.isGranted) { return PermissionDecision.Granted }
        // 아니라면 denialCount에 따라 분기.
        val result = when (record.denialCount) {
            0 -> PermissionDecision.RequestSystemDialog(permission)
            1 -> PermissionDecision.ShowRationale(permission, "${permission.name} 권한이 필요한 이유를 설명합니다.")
            else -> PermissionDecision.NavigateToSettings(permission)
        }
        return result
    }

    // 해당 권한 허용 여부를 반영하는 메서드
    fun handleUserResponse(permission: PermissionType, isUserGranted: Boolean) {
        records[permission]?.let { record ->
            record.isGranted = isUserGranted
            if (isUserGranted) { // 허용했다면 거부 횟수를 0으로 만든다.
                record.denialCount = 0
            } else { // 아니면 1 추가.
                record.denialCount++
            }
        }
    }

    // 승인되지 않은 권한들의 목록을 반환하는 메서드
    fun getDeniedPermissions(): List<PermissionType> = records.filter { (key, value) -> !value.isGranted }.map { (key, _) -> key }

    // 권한에 따라 람다 실행할지 말지 결정하는 메서드
    fun executeWithPermission(permission: PermissionType, onGrantedAction: () -> Unit): PermissionDecision = checkPermission(permission).also {
        when (it) {
            is PermissionDecision.Granted -> onGrantedAction() // 이미 허용된 경우에만 람다를 실행
            else -> {} // 아니면 뭐 아무 것도 안 한다..
        } // 이후 그대로 반환됨.
    }
}

// 테스트 케이스
fun main() {
    val manager = AppPermissionManager()

    println("=== 1. 카메라 권한 최초 요청 (거부 0회) ===")
    // 최초 요청 -> RequestSystemDialog
    val d1 = manager.checkPermission(PermissionType.CAMERA)
    println("결정: $d1")

    // 사용자가 1회 거부함
    manager.handleUserResponse(PermissionType.CAMERA, isUserGranted = false)

    println("\n=== 2. 카메라 권한 재요청 (거부 1회 누적) ===")
    // 1회 거부 상태 -> ShowRationale
    val d2 = manager.checkPermission(PermissionType.CAMERA)
    println("결정: $d2")

    // 사용자가 또 거부함 (총 2회 거부)
    manager.handleUserResponse(PermissionType.CAMERA, isUserGranted = false)

    println("\n=== 3. 카메라 권한 삼차 요청 (거부 2회 누적, 영구 거부) ===")
    // 2회 이상 거부 -> NavigateToSettings
    val d3 = manager.checkPermission(PermissionType.CAMERA)
    println("결정: $d3")

    println("\n=== 4. 고차 함수 기반 실행 테스트 (위치 권한) ===")
    // 위치 권한 승인 처리
    manager.handleUserResponse(PermissionType.LOCATION, isUserGranted = true)

    // 위치 권한으로 액션 실행 (승인 상태이므로 람다 동작)
    val resultGranted = manager.executeWithPermission(PermissionType.LOCATION) {
        println("-> [동작 실행] GPS 좌표 수신 성공: (37.5665, 126.9780)")
    }
    println("실행 결과: $resultGranted")

    // 거부 상태인 카메라 권한으로 액션 실행 시도 (람다 미실행)
    val resultDenied = manager.executeWithPermission(PermissionType.CAMERA) {
        println("-> [동작 실행] 이 문구는 절대 출력되지 않아야 합니다.")
    }
    println("미승인 실행 결과: $resultDenied")

    println("\n=== 5. 현재 미승인된 권한 목록 ===")
    println(manager.getDeniedPermissions())
}