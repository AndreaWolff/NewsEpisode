package nz.co.newsepisodes.common.model

data class Episode(
    val id: String,
    val title: String,
    val showName: String,
    val description: String,
    val durationMinutes: Int,
    val publishedAt: String,
    val isSaved: Boolean = false,
)
