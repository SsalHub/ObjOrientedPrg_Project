import java.io.Serializable
data class Event(
    val id : Int,
    var title : String,
    var beginTime : String,
    var endTime : String,
    var detail:String
) : Serializable
