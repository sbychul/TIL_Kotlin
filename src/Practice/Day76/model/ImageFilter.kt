package Practice.Day76.model

// 이미지 변환 필터 데이터 클래스
// 해당 필터 통과 시 용량 비율(0.5면 50% 압축)과 이미지 변환 로직 함수를 멤버 프로퍼티로 가짐.
data class ImageFilter(val name: String, val sizeFactor: Double, val action: (ImagePayload) -> ImagePayload) {
    // 연산자 오버로딩, 두 필터를 하나로 합친 새로운 필터를 반환
    operator fun plus(other: ImageFilter): ImageFilter = ImageFilter(
        name = "${name} + ${other.name}",
        sizeFactor = this.sizeFactor * other.sizeFactor,
        action = { payload -> other.action(this.action(payload)) }
    )
}
