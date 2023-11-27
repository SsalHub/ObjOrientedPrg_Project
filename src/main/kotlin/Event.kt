import java.io.Serializable
data class Event(
    val id : Int,
    var title : String,
    val beginTime : Int,
    val endTime : Int,
    var detail:String
) : Serializable
