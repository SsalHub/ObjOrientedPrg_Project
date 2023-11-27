import java.io.Serializable

data class Task(
    val id : Int,
    var title : String,
    val beginTime : Int,
    var detail : String
) : Serializable
