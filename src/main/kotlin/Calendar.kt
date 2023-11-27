import java.time.LocalDate
import java.util.*

class Calendar() {

    fun initCalendar():Unit
    {

    }
    fun printCalendar(year:Int,month:Int) {
        val startDate = LocalDate.of(year, month, 1)
        val lastDay = startDate.withDayOfMonth(startDate.lengthOfMonth())
        val firstDayOfWeek = startDate.dayOfWeek.value % 7

        println("${year}년 ${month}월 달력")
        println(" Sun   Mon   Tue   Wed   Thu   Fri   Sat")

        for (i in 1..firstDayOfWeek) {
            print("      ")
        }

        var currentDay = startDate.dayOfMonth
        while (currentDay <= lastDay.dayOfMonth) {
            print(String.format(" %02d   ", currentDay))
            if ((currentDay + firstDayOfWeek) % 7 == 0) {
                println()
            }
            currentDay++
        }
        println()
    }

    fun addSchedule() {
        
    }
}
fun main() {
    val sc = Scanner(System.`in`)


    print("조회할 달력의 년도를 입력하시오>> ")
    val year = sc.nextInt()


    print("조회할 달력의 월을 입력하시오>> ")
    val month = sc.nextInt()


    val calendar = Calendar()
    calendar.printCalendar(year, month)
}