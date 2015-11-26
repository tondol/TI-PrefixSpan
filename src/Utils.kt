import java.util.*

/**
 * Created by hosaka on 2015/11/26.
 */
class Utils {
    companion object {
        fun getStringOfSequences(db: List<Sequence>): String {
            return "[" + db.map { it.toString() }.joinToString(",\n ") + "]"
        }

        fun getStringOfPseudoSequences(db: Set<PseudoSequence>): String {
            return "[" + db.map { it.toString() }.joinToString(",\n ") + "]"
        }

        // 実験用
        fun generateTimeIntervalSets(size: Int, from: Int, to: Int): List<TimeIntervalSet> {
            val timeIntervalSets = arrayListOf<TimeIntervalSet>()
            val digits = ArrayList((0..size - 1).map { from })
            timeIntervalSets.add(getTimeIntervalSetFromDigits(digits))

            do {
                var i = size - 1
                while (i >= 0) {
                    if (digits.get(i) == to) {
                        digits.set(i, from)
                        i--
                    } else {
                        digits.set(i, digits.get(i) + 1)
                        break
                    }
                }
                timeIntervalSets.add(getTimeIntervalSetFromDigits(digits))
            } while (!digits.all { it == to })

            return timeIntervalSets
        }

        private fun getTimeIntervalSetFromDigits(digits: List<Int>): TimeIntervalSet {
            val periods = arrayListOf(0)
            periods.addAll(digits)
            val _periods = periods.withIndex().map {
                periods.subList(0, it.index + 1).fold(0) { acc, period -> acc + period }
            }
            return TimeIntervalSet(_periods)
        }
    }
}