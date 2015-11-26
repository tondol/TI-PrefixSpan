/**
 * Created by hosaka on 2015/11/24.
 */
class TimeIntervalSet(val periods: List<Int>) {
    // example:
    // 0, 2, 5, 9 -> { 0..0, 1..2, 3..5, 6..9, 10..* }

    val map = hashMapOf<Int, Int>()

    init {
        var from = 0
        for ((index, to) in periods.withIndex()) {
            for (i in (from..to)) {
                map.put(i, index)
            }
            from = to + 1
        }
    }

    fun getIntervalId(period: Int): Int {
        return map.getOrDefault(period, periods.size)
    }

    fun getDescription(intervalId: Int): String {
        if (intervalId == 0) {
            val to = periods[intervalId]
            return "0..$to"
        } else if (intervalId == periods.size) {
            val from = periods[intervalId - 1] + 1
            return "$from.."
        } else {
            val from = periods[intervalId - 1] + 1
            val to = periods[intervalId]
            return "$from..$to"
        }
    }

    override fun toString(): String {
        return "[" + (0..periods.size).map { getDescription(it) }.joinToString(", ") + "]"
    }
}