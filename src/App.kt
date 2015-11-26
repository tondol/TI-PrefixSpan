import java.io.FileInputStream
import java.io.IOException
import java.util.*

/**
 * Created by hosaka on 2015/11/24.
 */

private fun getDBForTest(): List<Sequence> {
    return listOf(
            Sequence.make("S1", listOf(
                    Pair("A", 0),
                    Pair("A", 1), Pair("B", 1), Pair("C", 1),
                    Pair("A", 3), Pair("C", 3),
                    Pair("D", 6),
                    Pair("C", 10), Pair("F", 10)
            )),
            Sequence.make("S2", listOf(
                    Pair("E", 0), Pair("F", 0),
                    Pair("A", 4), Pair("B", 4),
                    Pair("D", 7), Pair("F", 7),
                    Pair("C", 9),
                    Pair("B", 10)
            )),
            Sequence.make("S3", listOf(
                    Pair("A", 0), Pair("D", 0),
                    Pair("C", 1),
                    Pair("B", 2), Pair("C", 2),
                    Pair("A", 3), Pair("E", 3)
            )),
            Sequence.make("S4", listOf(
                    Pair("E", 0),
                    Pair("G", 4),
                    Pair("A", 8), Pair("F", 8),
                    Pair("C", 12),
                    Pair("B", 16),
                    Pair("C", 20)
            ))
    )
}

private fun getDBFromFile(filename: String, from: Double, to: Double): List<Sequence> {
    assert(from >= 0.0 && from <= 1.0 && to >= 0.0 && to <= 1.0 && from <= to)

    val db = arrayListOf<Sequence>()
    FileInputStream(filename).use {
        val scanner = Scanner(it).useDelimiter(System.getProperty("line.separator"))
        val N = scanner.nextInt()
        for (i in (N * from).toInt()..(N * to).toInt() - 1) {
            val id = scanner.next()
            val M = scanner.nextInt()
            val pairs = arrayListOf<Pair<String, Int>>()
            for (j in 0..M - 1) {
                val item = scanner.next()
                val time = scanner.nextInt()
                pairs.add(Pair(item, time))
            }
            db.add(Sequence(id, pairs))
        }
    }
    return db
}

fun main(args: Array<String>) {
    val db = getDBForTest()

    val tis = TimeIntervalSet(listOf(0, 2, 5, 9))
    println(tis)

    val prefixSpan = PrefixSpan(db, tis)
    prefixSpan.mine((db.size * 0.5).toInt(), { println(it.toString(tis)) })
}