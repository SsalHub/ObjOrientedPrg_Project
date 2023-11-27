import java.io.Serializable

data class Task(
    val id : Int,
    var title : String,
    val beginTime : String,
    var detail : String
) : Serializable
