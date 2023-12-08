import java.io.File
import java.time.LocalDate
import com.google.gson.Gson
import input.*


object Calendar {
    private var eventList:MutableList<Event> = mutableListOf()
    private var taskList:MutableList<Task> = mutableListOf()
    private val eventPath:String = "datas\\events.json"
    private val taskPath:String = "datas\\tasks.json"
    private var idCount:Int = 0

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

        idCount += eventList.size
        idCount += taskList.size
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
            val formattedDate = "%04d%02d%02d".format(year, month, currentDay)
            events = eventList.count { it.beginTime.startsWith(formattedDate) }
            tasks = taskList.count { it.beginTime.startsWith(formattedDate) }

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

    fun searchEvent(keyword:String): List<Event> {
        /*println("[ 이벤트 검색 ]")
        print("검색어를 입력하세요: ")
        val keyword = readln()*/

        val foundEvents = Calendar.eventList.filter { it.title.contains(keyword, ignoreCase = true) }
        println("[ 이벤트 검색 결과 ]")
        if (foundEvents.isEmpty()) {
            println("검색된 이벤트가 없습니다.")
        } else {
            for (event in foundEvents) {
                println("[이벤트 ID]: ${event.id}")
                println("[제목]: ${event.title}")
                println("[기간]: ${convertToExternalFormat(event.beginTime)} ~ ${convertToExternalFormat(event.endTime)}")
                println("[세부 내용]: ${event.detail}")
            }
        }
        return foundEvents
    }

    fun searchTask(keyword:String): List<Task> {
        /*println("[ 일정 검색 ]")
        print("검색어를 입력하세요: ")
        val keyword = readln()*/

        val foundTasks = Calendar.taskList.filter { it.title.contains(keyword, ignoreCase = true) }
        println("[ 일정 검색 결과 ]")
        if (foundTasks.isEmpty()) {
            println("검색된 일정이 없습니다.")
        } else {
            for (task in foundTasks) {
                println("[일정 ID]: ${task.id}")
                println("[제목]: ${task.title}")
                println("[시작 시간]: ${convertToExternalFormat(task.beginTime)}")
                println("[세부 내용]: ${task.detail}")
            }
        }
        return foundTasks
    }

    fun printDailyEvents(year:Int,month: Int,day: Int)
    {
        val formattedDate = "%04d%02d%02d".format(year, month, day)
        val dailyEvents = eventList.filter { it.beginTime.startsWith(formattedDate) }

        if (dailyEvents.isEmpty()) {
            println("해당 날짜에 등록된 이벤트가 없습니다.")
        } else {
            println("${year}년 ${month}월 ${day}일에 등록된 이벤트 :")
            dailyEvents.forEach {
                println("[이벤트 ID]: ${it.id}")
                println("[제목]: ${it.title}")
                println("[기간]: ${convertToExternalFormat(it.beginTime)} ~ ${convertToExternalFormat(it.endTime)}")
                println("[상세]: ${it.detail}\n")
                print("")
            }
        }
    }
    fun printDailyTasks(year:Int,month:Int,day:Int)
    {
        val formattedDate = "%04d%02d%02d".format(year, month, day)
        val dailyEvents = taskList.filter { it.beginTime.startsWith(formattedDate) }

        if (dailyEvents.isEmpty()) {
            println("해당 날짜에 등록된 일정이 없습니다.")
        } else {
            println("${formattedDate}의 등록된 일정:")
            dailyEvents.forEach {
                println("[일정 ID]: ${it.id}")
                println("[제목]: ${it.title}")
                println("[기간]: ${convertToExternalFormat(it.beginTime)}")
                println("[상세]: ${it.detail}\n")
                print("")
            }
        }
    }
    fun addEvent(title:String, beginTime:String, endTime:String, detail:String)
    {
        val e = Event(idCount, title, convertToInternalFormat(beginTime), convertToInternalFormat(endTime), detail)
        idCount += 1
        eventList.add(e)
        eventList.sortedBy { it.beginTime }
        saveEventDataFile()
    }

    fun addTask(title:String, beginTime:String, detail:String)
    {
        val t = Task(idCount, title, convertToInternalFormat(beginTime), detail)
        idCount += 1
        taskList.add(t)
        taskList.sortedBy { it.beginTime }
        saveTaskDataFile()
    }

    fun editEvent(year:Int, month:Int, day:Int)
    {
        /* 1. 날짜에 존재하는 이벤트들을 탐색함 */
        val d = "%04d%02d%02d".format(year, month, day)
        val events:Array<Event> = eventList.filter { it.beginTime.contains(d) }.toTypedArray()
        if (events.isEmpty())
        {
            println("해당 날짜에 이벤트가 존재하지 않습니다.\n")
            return
        }

        /* 2. 이벤트들에 0, 1, 2... n 까지 번호를 매기고, 정수 k를 입력받음 */
        var target:Int?
        var selected:Int
        println("\n[ ${year}.${month}.${day} 에 존재하는 이벤트 목록 ]")
        print("다음 중 수정하고자 하는 이벤트의 id를 입력하시오. (-1:처음으로) >>")
        while (true)
        {
            for (e in eventList)
                println("[${e.id}] ${e.title}")
            selected = readln().toInt()
            if (selected == -1)
            {
                println("처음으로 돌아갑니다.\n")
                return
            }
            if (eventList.none { it.id == selected })
            {
                println("id가 일치하는 이벤트가 없습니다!\n다시 입력해주세요.\n")
                continue
            }

            // if found event
            target = eventList.indices.find { eventList[it].id == selected }?.let {
                /* 3. 수정할 정보 입력받은 후 수정 완료 */
                println("[ 수정할 이벤트 정보 입력 ]")
                print("1. 이벤트 제목을 입력하시오 >> ")
                eventList[it].title = readln()
                print("2. 이벤트 시작 시간 (yyyy/MM/dd hh:mm:ss) >> ")
                eventList[it].beginTime = convertToInternalFormat(readln())
                print("3. 이벤트 종료 시간 (yyyy/MM/dd hh:mm:ss) >> ")
                eventList[it].endTime = convertToInternalFormat(readln())
                print("4. 이벤트 설명 >> ")
                eventList[it].detail = readln()
                it
            }
            if (target == null)
            {
                println("이벤트 수정 중 오류가 발생했습니다!\n처음으로 돌아갑니다.\n")
                loadEventDataFile()
                return
            }
            break
        }

        /* 4. 데이터파일 갱신 */
        println("정상적으로 이벤트 수정이 완료되었습니다!\n")
        println("[이벤트 ID]: ${eventList[target!!].id}")
        println("[제목]: ${eventList[target].title}")
        println("[기간]: ${eventList[target].beginTime} ~ ${eventList[target].endTime}")
        println("[상세]: ${eventList[target].detail}\n")
        saveEventDataFile()
    }

    fun editEvent(e:Event)
    {
        val result = eventList.indexOf(e).let {
            /* 3. 수정할 정보 입력받은 후 수정 완료 */
            println("[ 수정할 이벤트 정보 입력 ]")
            print("1. 이벤트 제목을 입력하시오 >> ")
            eventList[it].title = readln()
            print("2. 이벤트 시작 시간 (yyyy/MM/dd hh:mm:ss) >> ")
            eventList[it].beginTime = convertToInternalFormat(readln())
            print("3. 이벤트 종료 시간 (yyyy/MM/dd hh:mm:ss) >> ")
            eventList[it].endTime = convertToInternalFormat(readln())
            print("4. 이벤트 설명 >> ")
            eventList[it].detail = readln()
            it
        }
        /* 4. 데이터파일 갱신 */
        println("정상적으로 이벤트 수정이 완료되었습니다!\n")
        println("[이벤트 ID]: ${eventList[result].id}")
        println("[제목]: ${eventList[result].title}")
        println("[기간]: ${eventList[result].beginTime} ~ ${eventList[result].endTime}")
        println("[상세]: ${eventList[result].detail}\n")
        saveEventDataFile()
    }

    fun editTask(year:Int, month:Int, day:Int)
    {
        /* 1. 날짜에 존재하는 일정들을 탐색함 */
        val d = "%04d%02d%02d".format(year, month, day)
        val tasks:Array<Task> = taskList.filter { it.beginTime.contains(d) }.toTypedArray()
        if (tasks.isEmpty())
        {
            println("해당 날짜에 일정이 존재하지 않습니다.\n")
            return
        }
        /* 2. 이벤트들에 0, 1, 2... n 까지 번호를 매기고, 정수 k를 입력받음 */
        var target:Int?
        var selected:Int
        println("\n[ ${year}.${month}.${day} 에 존재하는 이벤트 목록 ]")
        print("다음 중 수정하고자 하는 이벤트의 id를 입력하시오. (-1:처음으로) >>")
        while (true)
        {
            for (t in taskList)
                println("[${t.id}] ${t.title}")
            selected = readln().toInt()
            if (selected == -1)
            {
                println("처음으로 돌아갑니다.\n")
                return
            }
            if (taskList.none { it.id == selected })
            {
                println("id가 일치하는 일정이 없습니다!\n다시 입력해주세요.\n")
                continue
            }

            // if found task
            target = taskList.indices.find { taskList[it].id == selected }?.let {
                /* 3. 수정할 정보 입력받은 후 수정 완료 */
                println("[ 수정할 일정 정보 입력 ]")
                print("1. 일정 제목을 입력하시오 >> ")
                taskList[it].title = readln()
                print("2. 일정 시작 시간 (yyyy/MM/dd hh:mm:ss) >> ")
                taskList[it].beginTime = convertToInternalFormat(readln())
                print("3. 이벤트 설명 >> ")
                taskList[it].detail = readln()
                it
            }
            if (target == null)
            {
                println("일정 수정 중 오류가 발생했습니다!\n처음으로 돌아갑니다.\n")
                loadTaskDataFile()
                return
            }
            break
        }
        /* 4. 데이터파일 갱신 */
        println("정상적으로 일정 수정이 완료되었습니다!\n")
        println("[일정 ID]: ${taskList[target!!].id}")
        println("[제목]: ${taskList[target].title}")
        println("[시작 시간]: ${taskList[target].beginTime}")
        println("[상세]: ${taskList[target].detail}\n")
        saveTaskDataFile()
    }

    fun editTask(t:Task)
    {
        val result = taskList.indexOf(t).let {
            /* 3. 수정할 정보 입력받은 후 수정 완료 */
            println("[ 수정할 일정 정보 입력 ]")
            print("1. 일정 제목을 입력하시오 >> ")
            taskList[it].title = readln()
            print("2. 일정 시작 시간 (yyyy/MM/dd hh:mm:ss) >> ")
            taskList[it].beginTime = convertToInternalFormat(readln())
            print("3. 일정 설명 >> ")
            taskList[it].detail = readln()
            it
        }
        /* 4. 데이터파일 갱신 */
        println("정상적으로 일정 수정이 완료되었습니다!\n")
        println("[일정 ID]: ${taskList[result].id}")
        println("[제목]: ${taskList[result].title}")
        println("[시작 시간]: ${taskList[result].beginTime}")
        println("[상세]: ${taskList[result].detail}\n")
        saveTaskDataFile()
    }

    fun removeEvent(year:Int, month:Int, day:Int)
    {
        /* 1. 날짜에 존재하는 이벤트들을 탐색함 */
        val d = "%04d%02d%02d".format(year, month, day)
        val events:Array<Event> = eventList.filter { it.beginTime.contains(d) }.toTypedArray()
        if (events.isEmpty())
        {
            println("해당 날짜에 이벤트가 존재하지 않습니다.\n")
            return
        }
        /* 2. 이벤트들에 0, 1, 2... n 까지 번호를 매기고, 정수 k를 입력받음 */
        var result:Int?
        var selected:Int
        println("\n[ ${year}.${month}.${day} 에 존재하는 이벤트 목록 ]")
        for (e in events)
            println("[${e.id}] ${e.title}")
        while (true)
        {
            print("위 목록 중 삭제를 원하는 이벤트의 id를 입력해 주세요.(-1:처음으로) >> ")
            selected = readln().toInt()
            if (selected == -1)
            {
                println("처음으로 돌아갑니다.")
                return
            }
            if (eventList.none { it.id == selected })
            {
                println("id가 일치하는 이벤트가 없습니다!\n다시 입력해주세요.\n")
                continue
            }
            // if found event
            result = eventList.indices.find { eventList[it].id == selected }?.let {
                /* 3. 해당 요소를 List에서 제거 */
                eventList.removeAt(it)
                it
            }
            if (result == null)
            {
                println("오류 발생! [${selected}] 이벤트가 정상적으로 삭제되지 않았습니다.")
                loadEventDataFile()
                return
            }
            break
        }
        /* 4. 데이터파일 갱신 */
        println("[${selected}] 이벤트가 정상적으로 삭제되었습니다!")
        saveEventDataFile()
    }

    fun removeEvent(e:Event)
    {
        val idx = e.id
        // if found event
        val result = eventList.indexOf(e).let {
            /* 3. 해당 요소를 List에서 제거 */
            eventList.removeAt(it)
            it
        }
        /* 4. 데이터파일 갱신 */
        println("[${idx}] 이벤트가 정상적으로 삭제되었습니다!")
        saveEventDataFile()
    }

    fun removeTask(year:Int, month:Int, day:Int)
    {
        /* 1. 날짜에 존재하는 일정들을 탐색함 */
        val d = "%04d%02d%02d".format(year, month, day)
        val tasks:Array<Task> = taskList.filter { it.beginTime.contains(d) }.toTypedArray()
        if (tasks.isEmpty())
        {
            println("해당 날짜에 일정이 존재하지 않습니다.\n")
            return
        }
        /* 2. 일정들에 0, 1, 2... n 까지 번호를 매기고, 정수 k를 입력받음 */
        var result:Int?
        var selected:Int
        println("\n[ ${year}.${month}.${day} 에 존재하는 일정 목록 ]")
        print("다음 중 삭제를 원하는 일정의 id를 입력해 주세요.(-1:처음으로) >> ")
        while (true)
        {
            for (e in taskList)
                println("[${e.id}] ${e.title}")
            selected = readln().toInt()
            if (selected == -1)
            {
                println("처음으로 돌아갑니다.")
                return
            }
            if (taskList.none { it.id == selected })
            {
                println("id가 일치하는 일정이 없습니다!\n다시 입력해주세요.\n")
                continue
            }
            // if found task
            result = taskList.indices.find { taskList[it].id == selected }?.let {
                /* 3. 해당 요소를 List에서 제거 */
                taskList.removeAt(it)
                it
            }
            if (result == null)
            {
                println("오류 발생! [${selected}] 일정이 정상적으로 삭제되지 않았습니다.")
                loadTaskDataFile()
                return
            }
            break
        }
        /* 4. 데이터파일 갱신 */
        println("[${selected}] 일정이 정상적으로 삭제되었습니다!")
        saveTaskDataFile()
    }

    fun removeTask(t:Task)
    {
        val idx = t.id
        // if found task
        val result = taskList.indexOf(t).let {
            /* 3. 해당 요소를 List에서 제거 */
            taskList.removeAt(it)
            it
        }
        /* 4. 데이터파일 갱신 */
        println("[${idx}] 일정이 정상적으로 삭제되었습니다!")
        saveTaskDataFile()
    }

    private fun convertToInternalFormat(external:String):String = external.replace("/", "").replace(":", "")

    private fun convertToExternalFormat(internal:String):String = "%s/%s/%s %s:%s:%s".format(
        internal.substring(0..3),
        internal.substring(4..5),
        internal.substring(6..7),
        internal.substring(9..10),
        internal.substring(11..12),
        internal.substring(13..14)
    )

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
    /*var year = inputValidYear()
    var month = inputValidMonth()*/
    //Calendar.printCalendar(year, month)
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
        println("메뉴 번호를 선택하시오 (1:종료, 2:달력 조회, 3:이벤트/일정 조회, 4:이벤트/일정 추가, 5:제목으로 검색, 6:이벤트/일정 수정, 7:이벤트/일정 삭제")
        print(" >> ")
        try {
            select = readln().toInt()
        } catch (e: java.lang.NumberFormatException)
        {
            println("올바른 값을 입력해주세요!\n")
            continue
        }

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
                    print("조회할 달력의 년도를 입력하시오(1~) >> ")
                    year = readln().toInt()
                    if (isValidYear(year)) break
                    println("올바른 년도를 입력해주세요!\n")
                }
                while(true)
                {
                    print("조회할 달력의 월을 입력하시오(1~12) >> ")
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
            /* 이벤트/일정 추가 */
            4 -> {
                println("[ 이벤트/일정 추가 ]")
                var select = 0
                while (true)
                {
                    print("이벤트/일정 중 어떤 것을 추가하시겠습니까? (1:이벤트, 2:일정, 3:이전으로) >> ")
                    select = readln().toInt()
                    when (select)
                    {
                        /* 이벤트 추가 */
                        1 -> {
                            println("[ 새로운 이벤트 정보 입력 ]")
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
                            println("[ 새로운 일정 정보 입력 ]")
                            print("1. 일정 제목을 입력하시오 >> ")
                            val title = readln()
                            print("2. 일정 시작 시간 (yyyy/MM/dd hh:mm:ss) >> ")
                            val beginTime = readln()
                            print("3. 일정 설명 >> ")
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
            /* 이벤트/일정 검색 */
            5 -> {
                println("[ 이벤트/일정 검색 ]")
                print("검색어(title)를 입력하세요 >> ")
                val keyword = readln()

                val events = Calendar.searchEvent(keyword)
                println()
                val tasks = Calendar.searchTask(keyword)

                if (events.isNotEmpty() || tasks.isNotEmpty())
                {
                    var selected:Int
                    println("\n[ 검색 결과 출력 완료 ]")
                    print("위 결과들 중 수정/삭제를 원하는 ID를 입력해주세요.(-1:그냥 돌아가기) >> ")
                    selected = readln().toInt()
                    if (selected == -1)
                    {
                        println("첫 화면으로 돌아갑니다.\n")
                        continue
                    }
                    else if (events.any { it.id == selected })
                    {
                        val e = events.find { it.id == selected }!!

                        println("이벤트 [${e.id}] 선택됨.")
                        while (true) {
                            print("어떤 작업을 하시겠습니까? (1:이벤트 수정, 2:이벤트 삭제, 3:처음으로) >> ")
                            selected = readln().toInt()
                            when (selected) {
                                /* 수정 */
                                1 -> {
                                    Calendar.editEvent(e)
                                    break
                                }
                                /* 삭제 */
                                2 -> {
                                    Calendar.removeEvent(e)
                                    break
                                }
                                /* 처음으로 */
                                3 -> {
                                    println("처음으로 돌아갑니다.")
                                    break
                                }
                                /* 그 외 입력 */
                                else -> {
                                    println("잘못된 입력입니다! 다시 입력해주세요.")
                                    continue
                                }
                            }
                        }
                    }
                    else if (tasks.any { it.id == selected })
                    {
                        val t = tasks.find { it.id == selected }!!

                        println("일정 [${t.id}] 선택됨.")
                        while (true) {
                            print("어떤 작업을 하시겠습니까? (1:일정 수정, 2:일정 삭제, 3:처음으로) >> ")
                            selected = readln().toInt()
                            when (selected) {
                                /* 수정 */
                                1 -> {
                                    Calendar.editTask(t)
                                    break
                                }
                                /* 삭제 */
                                2 -> {
                                    Calendar.removeTask(t)
                                    break
                                }
                                /* 처음으로 */
                                3 -> {
                                    println("처음으로 돌아갑니다.")
                                    break
                                }
                                /* 그 외 입력 */
                                else -> {
                                    println("잘못된 입력입니다! 다시 입력해주세요.")
                                    continue
                                }
                            }
                        }
                    }
                }
                println()
            }
            /* 이벤트/일정 수정 */
            6 -> {
                println("[ 이벤트/일정 수정 ]")
                var (select, year, month, day) = arrayOf(0, 0, 0, 0)

                while (true)
                {
                    try {
                        println("특정 날짜의 이벤트/일정을 선택합니다.")
                        print("선택할 날짜를 입력하시오.(yyyy/MM/dd) >> ")
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
                while (true)
                {
                    print("해당 날짜의 이벤트/일정 중 어떤 것을 수정할 지 선택하시오.(1:이벤트, 2:일정, 3:처음으로) >> ")
                    select = readln().toInt()

                    when (select)
                    {
                        /* 이벤트 */
                        1 -> {
                            Calendar.editEvent(year, month, day)
                            break
                        }
                        /* 일정 */
                        2 -> {
                            Calendar.editTask(year, month, day)
                            break
                        }
                        /* 처음으로 돌아가기 */
                        3 -> {
                            println("처음으로 돌아갑니다.\n")
                            break
                        }
                        /* 그 외 입력 */
                        else -> {
                            println("잘못된 입력입니다! 다시 입력해주세요.\n")
                            continue
                        }
                    }
                }
                println()
            }
            /* 이벤트/일정 삭제 */
            7 -> {
                println("[ 이벤트/일정 삭제 ]")
                var (select, year, month, day) = arrayOf(0, 0, 0, 0)

                while (true)
                {
                    try {
                        println("특정 날짜의 이벤트/일정을 선택합니다.")
                        print("선택할 날짜를 입력하시오.(yyyy/MM/dd) >> ")
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
                while (true)
                {
                    print("해당 날짜의 이벤트/일정 중 어떤 것을 삭제할 지 선택하시오.(1:이벤트, 2:일정, 3:처음으로) >> ")
                    select = readln().toInt()

                    when (select)
                    {
                        /* 이벤트 */
                        1 -> {
                            Calendar.removeEvent(year, month, day)
                            break
                        }
                        /* 일정 */
                        2 -> {
                            Calendar.removeTask(year, month, day)
                            break
                        }
                        /* 처음으로 돌아가기 */
                        3 -> {
                            println("처음으로 돌아갑니다.\n")
                            break
                        }
                        /* 그 외 입력 */
                        else -> {
                            println("잘못된 입력입니다! 다시 입력해주세요.\n")
                            continue
                        }
                    }
                }
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
