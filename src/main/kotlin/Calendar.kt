import input.inputValidYear
import input.inputValidMonth

import java.io.File
import java.time.LocalDate
import java.util.*
import com.google.gson.Gson
import input.inputValidDay


class Calendar() {
    var eventList:MutableList<Event> = mutableListOf()
    var taskList:MutableList<Task> = mutableListOf()
    val eventPath:String = "datas\\events.json"
    val taskPath:String = "datas\\tasks.json"

    companion object {
        var eventCount:Int = 0
        var taskCount:Int = 0
    }

    /* InitCalendar() */
    init {
        /* Initialize Event List */
        val eventJson = loadEventDataFile()
        val eventArray = Gson().fromJson(eventJson, Array<Event>::class.java)
        eventArray?.toMutableList()?.let { eventList.addAll((it)) }

        /* Initialize Task List */
        val taskJson = loadTaskDataFile()
        val taskArray = Gson().fromJson(taskJson, Array<Task>::class.java)
        taskArray?.toMutableList()?.let { taskList.addAll(it) }

        eventCount = eventList.size
        taskCount = taskList.size
    }

    fun printCalendar(year:Int, month:Int) {
        val strFormat = "${year}${month}"
        val startDate = LocalDate.of(year, month, 1)
        val lastDay = startDate.withDayOfMonth(startDate.lengthOfMonth())
        val firstDayOfWeek = startDate.dayOfWeek.value % 7
        var (events, tasks) = listOf(0, 0)

        println("${year}년 ${month}월 달력")
        println("   Sun       Mon        Tue        Wed        Thu        Fri        Sat")

        for (i in 1..firstDayOfWeek) {
            print("           ")
        }

        var currentDay = startDate.dayOfMonth
        while (currentDay <= lastDay.dayOfMonth) {
            events = eventList.count { it.beginTime.startsWith(strFormat) }
            tasks = taskList.count { it.beginTime.startsWith(strFormat) }
            print(String.format(" %02d(${events}/${tasks})   ", currentDay))
            if ((currentDay + firstDayOfWeek) % 7 == 0) {
                println()
            }
            currentDay++
        }
        println()
    }
    fun searchEvent(calendar: Calendar) {
        println("[ 이벤트 검색 ]")
        print("검색어를 입력하세요: ")
        val keyword = readln()

        val foundEvents = calendar.eventList.filter { it.title.contains(keyword, ignoreCase = true) }
        if (foundEvents.isEmpty()) {
            println("검색된 이벤트가 없습니다.")
        } else {
            println("[ 검색 결과 ]")
            for (event in foundEvents) {
                println("[제목]: ${event.title}")
                println("[기간]: ${event.beginTime} ~ ${event.endTime}")
                println("[세부 내용]: ${event.detail}")
            }
        }
    }
    fun searchTask(calendar: Calendar) {
        println("[ 일정 검색 ]")
        print("검색어를 입력하세요: ")
        val keyword = readln()

        val foundTasks = calendar.taskList.filter { it.title.contains(keyword, ignoreCase = true) }
        if (foundTasks.isEmpty()) {
            println("검색된 일정이 없습니다.")
        } else {
            println("[ 검색 결과 ]")
            for (task in foundTasks) {
                println("[제목]: ${task.title}")
                println("[시작 시간]: ${task.beginTime}")
                println("[세부 내용]: ${task.detail}")

            }
        }
    }
    fun printDailyEvents(year:Int,month: Int,day: Int)
    {
        val formattedDate = "${year}${month}${day}"
        val dailyEvents = eventList.filter { it.beginTime.startsWith(formattedDate) }

        if (dailyEvents.isEmpty()) {
            println("해당 날짜에 등록된 이벤트가 없습니다.")
        } else {
            println("${formattedDate}의 등록된 이벤트:")
            dailyEvents.forEach {
                println("[제목]: ${it.title}")
                println("[기간]: ${it.beginTime} ~ ${it.endTime}")
                println("[상세]: ${it.detail}\n")
                print("")
            }
        }
    }
    fun printDailyTasks(year:Int,month:Int,day:Int)
    {
        val formattedDate = "${year}${month}${day}"
        val dailyEvents = taskList.filter { it.beginTime.startsWith(formattedDate) }

        if (dailyEvents.isEmpty()) {
            println("해당 날짜에 등록된 일정이 없습니다.")
        } else {
            println("${formattedDate}의 등록된 일정:")
            dailyEvents.forEach {
                println("[제목]: ${it.title}")
                println("[기간]: ${it.beginTime}")
                println("[상세]: ${it.detail}\n")
                print("")
            }
        }
    }
    fun addEvent(title:String, beginTime:String, endTime:String, detail:String): Unit
    {
        val e = Event(eventCount, title, beginTime, endTime, detail)
        eventCount += 1
        eventList.add(e)
        eventList.sortedBy { it.beginTime }
        saveEventDataFile()
    }

    fun addTask(title:String, beginTime:String, detail:String): Unit
    {
        val t = Task(taskCount, title, beginTime, detail)
        taskCount += 1
        taskList.add(t)
        taskList.sortedBy { it.beginTime }
        saveTaskDataFile()
    }

    fun removeEvent(year:Int, month:Int, day:Int): Unit
    {
        /* 1. 날짜에 존재하는 이벤트들을 탐색함 */
        val d = "${year}${month}${day}"
        val events:Array<Event> = eventList.filter { it.beginTime.contains(d) }.toTypedArray()
        if (events.isEmpty()) return

        /* 2. 이벤트들에 0, 1, 2... n 까지 번호를 매기고, 정수 k를 입력받음 */
        var LOOP:Boolean = true
        var selected:Int
        println("\n[ ${year}.${month}.${day} 에 존재하는 이벤트 목록 ]")
        println("* 다음 중 삭제를 원하는 이벤트의 id를 입력해 주세요.")
        while (LOOP)
        {
            for (e in eventList)
                println("[${e.id}] ${e.title}")
            selected = readln().toInt()

            // if found event
            eventList.find { it.id == selected }?.let {
                /* 3. 해당 요소를 List에서 제거 */
                eventList.remove(it)
                LOOP = false
            }
            //else
            println("* 잘못된 입력입니다! 이벤트의 id를 다시 입력해주세요.\n\n")
        }

        /* 4. 데이터파일 갱신 */
        saveEventDataFile()
    }

    fun removeTask(year:Int, month:Int, day:Int): Unit
    {
        /* 1. 날짜에 존재하는 일정들을 탐색함 */
        val d = "${year}${month}${day}"
        val tasks:Array<Task> = taskList.filter { it.beginTime.contains(d) }.toTypedArray()
        if (tasks.isEmpty()) return

        /* 2. 일정들에 0, 1, 2... n 까지 번호를 매기고, 정수 k를 입력받음 */
        var LOOP:Boolean = true
        var selected:Int
        println("\n[ ${year}.${month}.${day} 에 존재하는 일정 목록 ]")
        println("* 다음 중 삭제를 원하는 일정의 id를 입력해 주세요.")
        while (LOOP)
        {
            for (e in taskList)
                println("[${e.id}] ${e.title}")
            selected = readln().toInt()

            // if found task
            taskList.find { it.id == selected }?.let {
                /* 3. 해당 요소를 List에서 제거 */
                taskList.remove(it)
                LOOP = false
            }
            //else
            println("* 잘못된 입력입니다! 일정의 id를 다시 입력해주세요.\n\n")
        }

        /* 4. 데이터파일 갱신 */
        saveTaskDataFile()
    }

    private fun loadEventDataFile(): String
    {
        val f = File(eventPath)
        if (f.exists())
            return f.readText()

        f.writeText("")
        return ""
    }

    private fun loadTaskDataFile(): String
    {
        val f = File(taskPath)
        if (f.exists())
            return f.readText()

        f.writeText("")
        return ""
    }

    private fun saveEventDataFile(): Unit
    {
        val eventJson = Gson().toJson(eventList)
        File(eventPath).writeText(eventJson)
    }

    private fun saveTaskDataFile(): Unit
    {
        val taskJson = Gson().toJson(taskList)
        File(taskPath).writeText(taskJson)
    }
}


fun main() {
    val calendar = Calendar()
    var (year, month, day) = listOf(0, 0, 0)
    var (title, beginTime, endTime, detail) = listOf("", "", "", "")

    /* 준성님 작업했던 내용 (year, month 입력받고 달력 출력) */
    year = inputValidYear()
    month = inputValidMonth()
    calendar.printCalendar(year, month)
    calendar.searchEvent(calendar)
    //calendar.searchTask(calendar)
    //calendar.printDailyEvents(year, month, day)
    //calendar.printDailyTasks(year, month, day)
    /* 이벤트 추가
    println("[ 달력에 추가할 이벤트 정보 입력 ]")
    print("1. 이벤트 제목 : ")
    title = readln()
    print("2. 이벤트 시작 시간 : ")
    beginTime = readln()
    print("3. 이벤트 종료 시간 : ")
    endTime = readln()
    print("4. 이벤트 설명 : ")
    detail = readln()
    calendar.addEvent(title, beginTime, endTime, detail)
*/
    /* 일정 추가
    println("[ 달력에 추가할 일정 정보 입력 ]")
    print("1. 일정 제목 : ")
    title = readln()
    print("2. 일정 시작 시간 : ")
    beginTime = readln()
    print("4. 일정 설명 : ")
    detail = readln()
    calendar.addTask(title, beginTime, detail)
*/
    /* 이벤트 삭제 */
    year = inputValidYear()
    month = inputValidMonth()
    day = inputValidDay(year, month)
    calendar.removeEvent(year, month, day)



    /* 일정 삭제 */
    year = inputValidYear()
    month = inputValidMonth()
    day = inputValidDay(year, month)
    calendar.removeTask(year, month, day)
}




fun jaewuk(calendar:Calendar)
{
    var (year, month, day) = listOf(0, 0, 0)
    var (title, beginTime, endTime, detail) = listOf("", "", "", "")

    /* 이벤트 추가 */
    println("[ 달력에 추가할 이벤트 정보 입력 ]")
    print("1. 이벤트 제목 : ")
    title = readln()
    print("2. 이벤트 시작 시간 : ")
    beginTime = readln()
    print("3. 이벤트 종료 시간 : ")
    endTime = readln()
    print("4. 이벤트 설명 : ")
    detail = readln()
    calendar.addEvent(title, beginTime, endTime, detail)

    /* 일정 추가 */
    println("[ 달력에 추가할 일정 정보 입력 ]")
    print("1. 일정 제목 : ")
    title = readln()
    print("2. 일정 시작 시간 : ")
    beginTime = readln()
    print("4. 일정 설명 : ")
    detail = readln()
    calendar.addTask(title, beginTime, detail)

    /* 이벤트 삭제 */
    year = inputValidYear()
    month = inputValidMonth()
    day = inputValidDay(year, month)
    calendar.removeEvent(year, month, day)



    /* 일정 삭제 */
    year = inputValidYear()
    month = inputValidMonth()
    day = inputValidDay(year, month)
    calendar.removeTask(year, month, day)
}
