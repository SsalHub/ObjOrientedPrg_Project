
class Schedule (
    private var title:String,
    private var beginTime:String,
    private var endTime:String,
    private var detail:String,
) {
    fun initSchedule(): Boolean
    {

        return true
    }

    fun editSchedule(title:String?, beginTime:String?, endTime:String?, detail:String?): Boolean
    {
        if (title != null)
        {
            this.title = title
        }
        if (beginTime != null)
        {
            this.beginTime = beginTime
        }
        if (endTime != null)
        {
            this.endTime = endTime
        }
        if (detail != null)
        {
            this.detail = detail
        }

        return true
    }

    fun getSchedule(): Schedule
    {
        var tmp = Schedule("1", "2", "3", "4")


        return tmp
    }

    fun removeSchedule(): Boolean
    {

        return true
    }
}