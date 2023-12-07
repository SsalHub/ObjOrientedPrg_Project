import java.io.File
import java.time.LocalDate
import com.google.gson.Gson
import input.*


object Calendar {
    private var eventList:MutableList<Event> = mutableListOf()
    private var taskList:MutableList<Task> = mutableListOf()
    private val eventPath:String = "datas\\events.json"
    private val taskPath:String = "datas\\tasks.json"
    private var eventIDCount:Int = 0
    private var taskIDCount:Int = 0

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

        eventIDCount = eventList.size
        taskIDCount = taskList.size
    }

    fun printCalendar(year:Int, month:Int) {
        val strFormat = "${year}${month}"
        val startDate = LocalDate.of(year, month, 1)
        val lastDay = startDate.withDayOfMonth(startDate.lengthOfMonth())
        val firstDayOfWeek = startDate.dayOfWeek.value % 7
        var (events, tasks) = listOf(0, 0)

        println("${year}년 ${month}월 달력")
        println(" Sun        Mon        Tue        Wed        Thu        Fri        Sat")

        for (i in 1..firstDayOfWeek) {
            print("           ")
        }

        var currentDay = startDate.dayOfMonth
        while (currentDay <= lastDay.dayOfMonth) {
            events = eventList.count { it.beginTime.startsWith(strFormat) }
            tasks = taskList.count { it.beginTime.startsWith(strFormat) }

            if(events == 0 && tasks == 0){
                print(String.format(" %02d        ", currentDay))
            }
            else {
                print(String.format(" %02d(${events}/${tasks})   ", currentDay))
            }


            if ((currentDay + firstDayOfWeek) % 7 == 0) {
                println()
            }
            currentDay++
        }
        println()
    }
    fun searchEvent(keyword:String) {
        /*println("[ 이벤트 검색 ]")
        print("검색어를 입력하세요: ")
        val keyword = readln()*/

        val foundEvents = Calendar.eventList.filter { it.title.contains(keyword, ignoreCase = true) }
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
    fun searchTask(keyword:String) {
        /*println("[ 일정 검색 ]")
        print("검색어를 입력하세요: ")
        val keyword = readln()*/

        val foundTasks = Calendar.taskList.filter { it.title.contains(keyword, ignoreCase = true) }
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

    fun getDailyEvents(keyword:String):List<Event> = eventList.filter { it.beginTime.startsWith(keyword) }

    fun getDailyTasks(keyword:String):List<Task> = taskList.filter { it.beginTime.startsWith(keyword) }

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
    fun addEvent(title:String, beginTime:String, endTime:String, detail:String)
    {
        val e = Event(eventIDCount, title, beginTime, endTime, detail)
        eventIDCount += 1
        eventList.add(e)
        eventList.sortedBy { it.beginTime }
        saveEventDataFile()
    }

    fun addTask(title:String, beginTime:String, detail:String)
    {
        val t = Task(taskIDCount, title, beginTime, detail)
        taskIDCount += 1
        taskList.add(t)
        taskList.sortedBy { it.beginTime }
        saveTaskDataFile()
    }

    fun editEvent()
    {

    }

    fun editTask()
    {

    }

    fun removeEvent(year:Int, month:Int, day:Int)
    {
        /* 1. 날짜에 존재하는 이벤트들을 탐색함 */
        val d = "%04d%02d%02d".format(year, month, day)
        val events:Array<Event> = eventList.filter { it.beginTime.contains(d) }.toTypedArray()
        if (events.isEmpty()) return

        /* 2. 이벤트들에 0, 1, 2... n 까지 번호를 매기고, 정수 k를 입력받음 */
        var LOOP = true
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

    fun removeTask(year:Int, month:Int, day:Int)
    {
        /* 1. 날짜에 존재하는 일정들을 탐색함 */
        val d = "%04d%02d%02d".format(year, month, day)
        val tasks:Array<Task> = taskList.filter { it.beginTime.contains(d) }.toTypedArray()
        if (tasks.isEmpty()) return

        /* 2. 일정들에 0, 1, 2... n 까지 번호를 매기고, 정수 k를 입력받음 */
        var LOOP = true
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

        f.createNewFile()
        return ""
    }

    private fun loadTaskDataFile(): String
    {
        val f = File(taskPath)
        if (f.exists())
            return f.readText()

        f.createNewFile()
        return ""
    }

    private fun saveEventDataFile()
    {
        val eventJson = Gson().toJson(eventList)
        File(eventPath).writeText(eventJson)
    }

    private fun saveTaskDataFile()
    {
        val taskJson = Gson().toJson(taskList)
        File(taskPath).writeText(taskJson)
    }
}


fun main() {
    //val calendar = Calendar()
    //var (title, beginTime, endTime, detail) = listOf("", "", "", "")

    /* 준성님 작업했던 내용 (year, month 입력받고 달력 출력) */
    var year = inputValidYear()
    var month = inputValidMonth()
    Calendar.printCalendar(year, month)
    //Calendar.searchEvent()
    //Calendar.searchTask()
    //Calendar.printDailyEvents(year, month, day)
    //Calendar.printDailyTasks(year, month, day)
    //jaewuk()
    runCalendar()
}


fun jaewuk()
{
    /* 이벤트 추가 */
    println("[ 달력에 추가할 이벤트 정보 입력 ]")
    print("1. 이벤트 제목 : ")
    var title = readln()
    print("2. 이벤트 시작 시간 : ")
    var beginTime = readln()
    print("3. 이벤트 종료 시간 : ")
    var endTime = readln()
    print("4. 이벤트 설명 : ")
    var detail = readln()
    Calendar.addEvent(title, beginTime, endTime, detail)

    /* 일정 추가 */
    println("[ 달력에 추가할 일정 정보 입력 ]")
    print("1. 일정 제목 : ")
    title = readln()
    print("2. 일정 시작 시간 : ")
    beginTime = readln()
    print("4. 일정 설명 : ")
    detail = readln()
    Calendar.addTask(title, beginTime, detail)

    /* 이벤트 삭제 */
    var year = inputValidYear()
    var month = inputValidMonth()
    var day = inputValidDay(year, month)
    Calendar.removeEvent(year, month, day)

    /* 일정 삭제 */
    year = inputValidYear()
    month = inputValidMonth()
    day = inputValidDay(year, month)
    Calendar.removeTask(year, month, day)
}



fun runCalendar()
{
    var select = 0
    while (true)
    {
        println("메뉴 번호를 선택하시오 (1:종료, 2:달력 조회, 3:이벤트/일정 조회, 4: 이벤트/일정 제목으로 검색, 5:이벤트/일정 추가, 6:이벤트/일정 수정, 7:이벤트/일정 삭제)")
        print(" >> ")
        select = readln().toInt()

        when (select)
        {
            /* 종료 */
            1 -> {
                println("달력 프로그램을 종료합니다.\n\n")
                return
            }

            /* 달력 조회 */
            2 -> {
                println("[ 달력 조회 ]")
                var (year, month) = arrayOf(0, 0)
                while(true)
                {
                    print("조회할 달력의 년도를 입력하시오 >> ")
                    year = readln().toInt()
                    if (isValidYear(year)) break
                    println("올바른 년도를 입력해주세요!\n")
                }
                while(true)
                {
                    print("조회할 달력의 월을 입력하시오 >> ")
                    month = readln().toInt()
                    if (isValidMonth(month)) break
                    println("올바른 월을 입력해주세요!\n")
                }
                Calendar.printCalendar(year, month)

                println()
            }

            /* 이벤트/일정 조회 */
            3 -> {
                println("[ 이벤트/일정 조회 ]")
                var (year, month, day) = arrayOf(0, 0, 0)
                while (true)
                {
                    try {
                        print("조회할 이벤트/일정 날짜를 입력하시오(yyyy/MM/dd) >> ")
                        readln().split('/').let {
                            year = it[0].toInt()
                            month = it[1].toInt()
                            day = it[2].toInt()
                        }
                        break
                    } catch (e : NumberFormatException) {
                        println("올바른 날짜를 입력해주세요! (yyyy/MM/dd)\n")
                        continue
                    } catch(e : ArrayIndexOutOfBoundsException) {
                        println("올바른 날짜를 입력해주세요! (yyyy/MM/dd)\n")
                        continue
                    }
                }
                println("[ ${year}년 ${month}월 ${day}일에 등록된 이벤트 ]")
                Calendar.printDailyEvents(year, month, day)

                println("[ ${year}년 ${month}월 ${day}일에 등록된 일정 ]")
                Calendar.printDailyTasks(year, month, day)

                println()
            }

            /* 이벤트/일정 검색 */
            4 -> {
                println("[ 이벤트/일정 검색 ]")
                print("검색어(title)를 입력하세요 >> ")
                val keyword = readln()

                println("[ 이벤트 검색 결과 ]")
                Calendar.searchEvent(keyword)
                println()
                println("[ 일정 검색 결과 ]")
                Calendar.searchTask(keyword)

                println()
            }

            /* 이벤트/일정 추가 */
            5 -> {
                println("[ 이벤트/일정 추가 ]")
                var select = 0
                while (true)
                {
                    print("이벤트/일정 중 어떤 것을 추가하시겠습니까? (1:이벤트 2:일정, 3:이전으로) >> ")
                    select = readln().toInt()
                    when (select)
                    {
                        /* 이벤트 추가 */
                        1 -> {
                            println("[ 달력에 추가할 이벤트 정보 입력 ]")
                            print("1. 이벤트 제목을 입력하시오 >> ")
                            val title = readln()
                            print("2. 이벤트 시작 시간 (yyyy/MM/dd hh:mm:ss) >> ")
                            val beginTime = readln()
                            print("3. 이벤트 종료 시간 (yyyy/MM/dd hh:mm:ss) >> ")
                            val endTime = readln()
                            print("4. 이벤트 설명 >> ")
                            val detail = readln()
                            Calendar.addEvent(title, beginTime, endTime, detail)
                        }

                        /* 일정 추가 */
                        2 -> {
                            println("[ 달력에 추가할 일정 정보 입력 ]")
                            print("1. 일정 제목 : ")
                            val title = readln()
                            print("2. 일정 시작 시간 : ")
                            val beginTime = readln()
                            print("4. 일정 설명 : ")
                            val detail = readln()
                            Calendar.addTask(title, beginTime, detail)
                        }

                        /* 이전으로 돌아가기 */
                        3 -> {
                            println("이전으로 돌아갑니다.\n")
                            break
                        }

                        /* 그 외 입력 */
                        else -> {
                            println("올바른 값을 입력해주세요!\n")
                            continue
                        }
                    }
                    break
                }
                println()
            }

            /* 이벤트/일정 수정 */
            6 -> {
                println("[ 이벤트/일정 수정 ]")
                var (select, year, month, day, i) = arrayOf(0, 0, 0, 0, 0)

                while (true)
                {
                    try {
                        print("조회할 이벤트/일정 날짜를 입력하시오(yyyy/MM/dd) >> ")
                        readln().split('/').let {
                            year = it[0].toInt()
                            month = it[1].toInt()
                            day = it[2].toInt()
                        }
                        break
                    } catch (e : NumberFormatException) {
                        println("올바른 날짜를 입력해주세요! (yyyy/MM/dd)\n")
                        continue
                    } catch(e : ArrayIndexOutOfBoundsException) {
                        println("올바른 날짜를 입력해주세요! (yyyy/MM/dd)\n")
                        continue
                    }
                }
                val keyword = "%04d%02d%02d".format(year, month, day)

                val foundEvents = Calendar.getDailyEvents(keyword)
                val foundTasks = Calendar.getDailyTasks(keyword)
                if (foundEvents.size + foundTasks.size <= 0)
                {
                    println("해당 날짜에 등록된 이벤트/일정이 없습니다!\n")
                    println("이전으로 돌아갑니다.\n\n")
                    break
                }

                println("\n아래 중 수정할 이벤트/일정의 번호를 입력해주세요.\n")
                for (e in foundEvents)
                {
                    println("[${i}] ")
                    i += 1
                }

                println()
            }

            /* 이벤트/일정 삭제 */
            7 -> {

                println()
            }

            /* 그 외 입력 */
            else -> {
                println("올바른 값을 입력해주세요!\n")
                continue
            }
        }
    }
}
