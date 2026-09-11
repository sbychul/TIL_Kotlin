package Practice.Day73.model

data class ProcessedUserData(
    val id: String,
    val username: String,
    val tier: String,
    val activityScore: Int // 통계 클래스(ActivityStats)의 postCount * 10 + likeCount * 2 형식으로 계산.
)
