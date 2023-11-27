import java.io.Serializable
data class Event(
    val id : Int,
    var title : String,
    val beginTime : String,
    val endTime : String,
    var detail:String
) : Serializable
