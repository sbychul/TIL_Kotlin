package Practice.Day73.model

data class UserAccount(
    val id: String,
    val username: String,
    val isBanned: Boolean,
    val tier: String // "VIP", "STANDARD", "NEWBIE"
)
