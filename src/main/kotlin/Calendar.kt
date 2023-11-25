import java.time.LocalDate
import java.util.*

class Calendar(private val year: Int, private val month: Int) {

    fun printCalendar() {
        val startDate = LocalDate.of(year, month, 1)
        val lastDay = startDate.plusMonths(1).minusDays(1)
        val firstDayOfWeek = startDate.dayOfWeek.value % 7

        println("${year}년 ${month}월 달력")
        println("Sun  Mon  Tue  Wed  Thu  Fri  Sat")

        for (i in 1..firstDayOfWeek) {
            print("     ")
        }

        var currentDay = startDate.dayOfMonth
        while (currentDay <= lastDay.dayOfMonth) {
            print(String.format("%02d   ", currentDay))
            if ((currentDay + firstDayOfWeek) % 7 == 0) {
                println()
            }
            currentDay++
        }
        println()
    }
}

fun main() {
    val scanner = Scanner(System.`in`)

    // 년도 입력 받기
    print("조회할 달력의 년도를 입력하시오>> ")
    val year = scanner.nextInt()

    // 월 입력 받기
    print("조회할 달력의 월을 입력하시오>> ")
    val month = scanner.nextInt()

    // Calendar 클래스를 사용하여 달력 출력
    val calendar = Calendar(year, month)
    calendar.printCalendar()
}