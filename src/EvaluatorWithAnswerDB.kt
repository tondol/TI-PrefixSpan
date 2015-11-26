import java.util.*

/**
 * Created by hosaka on 2015/11/26.
 */
class EvaluatorWithAnswerDB(val db: List<Sequence>, val tis: TimeIntervalSet) {
    private val DEBUG = false

    // patternの先頭itemにより射影した結果を引数として_matchをコールする
    fun match(pattern: SequentialPattern): Set<String> {
        assert(pattern.items.size != 0)

        if (DEBUG) {
            println("-- match1-db")
            println(Utils.getStringOfSequences(db))
        }

        val projectedDB = linkedSetOf<PseudoSequence>()
        // 長さ不足のsequenceはフィルタリングしておく
        for (seq in db.filter { it.pairs.size >= pattern.items.size }) {
            for ((index, pair) in seq.pairs.withIndex()) {
                val (item, timestamp) = pair
                if (item == pattern.items[0]) {
                    val _pairs = seq.pairs.subList(index + 1, seq.pairs.size)
                    projectedDB.add(PseudoSequence(seq.id, _pairs, timestamp))
                }
            }
        }

        return _match(projectedDB, pattern, listOf(pattern.items[0]), listOf())
    }

    // patternの次のitemとinterval idにより射影した結果を引数として再帰コールする
    private fun _match(db: Set<PseudoSequence>, pattern: SequentialPattern,
                       // itemsとinterval idsの組がprefixに相当する
                       items: List<String>, intervalIds: List<Int>): Set<String> {
        if (DEBUG) {
            println("-- match2-items-and-interval-ids")
            val elements = arrayListOf<String>()
            for ((index, item) in items.withIndex()) {
                elements.add(item.toString())
                if (index < intervalIds.size) {
                    elements.add(tis.getDescription(intervalIds[index]))
                }
            }
            println(elements)
            println("-- match2-db")
            println(Utils.getStringOfPseudoSequences(db))
        }

        // prefixがpatternと同じ長さになったら完了
        if (pattern.items.size == items.size) {
            return LinkedHashSet(db.map { it.id })
        }

        val projectedDB = linkedSetOf<PseudoSequence>()
        // 長さ不足のpseudo sequenceはフィルタリングしておく
        for (seq in db.filter { it.pairs.size >= pattern.items.size - items.size }) {
            for ((index, pair) in seq.pairs.withIndex()) {
                val (item, timestamp) = pair
                val intervalId = tis.getIntervalId(timestamp - seq.timestamp)
                // 空のpseudo sequenceも追加する
                if (item == pattern.items[items.size] && intervalId == pattern.intervalIds[intervalIds.size]) {
                    val _pairs = seq.pairs.subList(index + 1, seq.pairs.size)
                    projectedDB.add(PseudoSequence(seq.id, _pairs, timestamp))
                }
            }
        }
        return _match(projectedDB, pattern,
                items + pattern.items[items.size], intervalIds + pattern.intervalIds[intervalIds.size])
    }
}