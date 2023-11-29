package input
fun isLeapYear(year:Int) : Boolean = ((year % 400 == 0) || (year%4==0 && year%100!=0))

fun lastDay(year:Int, month:Int) : Int
{
    if (month == 2)
    {
        if(isLeapYear(year)) return 29
        return 28
    }
    return when(month)
    {
        4, 6, 9, 11 -> 30
        else -> 31
    }
}

fun inputValidYear() : Int
{
    var year = -1
    do {
        print("연도(1~)를 입력하시오 >>");
        year = readln().toInt();
    } while (year < 1)
    return year
}

fun inputValidMonth() : Int
{
    var month = -1
    do {
        print("월(1~12)를 입력하시오 >>")
        month = readln().toInt();
    } while (month < 1 || 12 < month)
    return month
}

fun inputValidDay(year:Int, month:Int) : Int
{
    var day = -1
    do {
        print("연월에 알맞은 일을 입력하시오 >>");
        day = readln().toInt();
    } while (day < 1 || lastDay(year, month) < day)
    return day
}