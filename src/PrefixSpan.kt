import java.util.*

/**
 * Created by hosaka on 2015/11/24.
 */
class PrefixSpan(val db: List<Sequence>, val tis: TimeIntervalSet) {
    private val DEBUG = false

    fun mine(minSupport: Int, func: (SequentialPattern) -> Unit) {
        val patterns = arrayListOf<SequentialPattern>()
        _mine(minSupport, { pattern ->
            patterns.add(pattern)
        })

        // closedなもののみを抽出する
        for (p1 in patterns) {
            var closed = true
            for (p2 in patterns) {
                if (p1 == p2) {
                    continue;
                }
                if (p1.sequenceIds.size == p2.sequenceIds.size && p2.contains(p1)) {
                    closed = false
                    break
                }
            }
            if (closed) {
                func(p1)
            }
        }
    }

    private fun _mine(minSupport: Int, func: (SequentialPattern) -> Unit) {
        if (DEBUG) {
            println("-- mine1-db")
            println(Utils.getStringOfSequences(db))
        }

        for ((item, sequenceIds) in getFrequentItems(minSupport)) {
            if (DEBUG) {
                println("-- mine1-frequent-item")
                println(item + "=" + sequenceIds)
            }

            val prefix = SequentialPattern(listOf(item), listOf(), sequenceIds)
            val projectedDB = project(item, sequenceIds)
            func(prefix)
            _mine(projectedDB, prefix, minSupport, func)
        }
    }

    private fun _mine(db: Set<PseudoSequence>, prefix: SequentialPattern, minSupport: Int,
                      func: (SequentialPattern) -> Unit) {
        if (DEBUG) {
            println("-- mine2-db")
            println(Utils.getStringOfPseudoSequences(db))
        }

        for ((pair, sequenceIds) in getFrequentPairs(db, minSupport)) {
            if (DEBUG) {
                println("-- mine2-frequent-pair")
                println("" + pair + "=" + sequenceIds)
            }

            val (item, intervalId) = pair
            val _prefix = SequentialPattern(prefix.items + item, prefix.intervalIds + intervalId, sequenceIds)
            val projectedDB = project(db, item, intervalId, sequenceIds)
            func(_prefix)
            _mine(projectedDB, _prefix, minSupport, func)
        }
    }

    private fun getFrequentItems(minSupport: Int): Map<String, Set<String>> {
        val map = hashMapOf<String, MutableSet<String>>()
        for (seq in db) {
            for ((item, timestamp) in seq.pairs) {
                val sequenceIds = map.get(item)
                if (sequenceIds != null) {
                    sequenceIds.add(seq.id)
                } else {
                    map.put(item, linkedSetOf(seq.id))
                }
            }
        }
        return map.filter { it.value.size >= minSupport }
    }

    private fun getFrequentPairs(db: Set<PseudoSequence>, minSupport: Int): Map<Pair<String, Int>, Set<String>> {
        val map = hashMapOf<Pair<String, Int>, MutableSet<String>>()
        for (seq in db) {
            for ((item, timestamp) in seq.pairs) {
                // seqのtimestampとpairのtimestamp差から該当するinterval idを調べる
                val intervalId = tis.getIntervalId(timestamp - seq.timestamp)
                val _pair = Pair(item, intervalId)
                val sequenceIds = map.get(_pair)
                if (sequenceIds != null) {
                    sequenceIds.add(seq.id)
                } else {
                    map.put(_pair, linkedSetOf(seq.id))
                }
            }
        }
        return map.filter { it.value.size >= minSupport }
    }

    private fun project(item: String, sequenceIds: Set<String>): Set<PseudoSequence> {
        val projectedDB = linkedSetOf<PseudoSequence>()
        for (seq in db.filter { sequenceIds.contains(it.id) }) {
            for ((index, pair) in seq.pairs.withIndex()) {
                // itemと一致するpairを検索する
                val (_item, timestamp) = pair
                // 空のpseudo sequenceを生成しないようにする
                if (_item == item && index + 1 < seq.pairs.size) {
                    val _pairs = seq.pairs.subList(index + 1, seq.pairs.size)
                    projectedDB.add(PseudoSequence(seq.id, _pairs, timestamp))
                }
            }
        }
        return projectedDB
    }

    private fun project(db: Set<PseudoSequence>, item: String,
                        intervalId: Int, sequenceIds: Set<String>): Set<PseudoSequence> {
        val projectedDB = linkedSetOf<PseudoSequence>()
        for (seq in db.filter { sequenceIds.contains(it.id) }) {
            for ((index, pair) in seq.pairs.withIndex()) {
                // item, interval idと一致するpairを検索する
                val (_item, timestamp) = pair
                val _intervalId = tis.getIntervalId(timestamp - seq.timestamp)
                // 空のpseudo sequenceを生成しないようにする
                if (_item == item && _intervalId == intervalId && index + 1 < seq.pairs.size) {
                    val _pairs = seq.pairs.subList(index + 1, seq.pairs.size)
                    projectedDB.add(PseudoSequence(seq.id, _pairs, timestamp))
                }
            }
        }
        return projectedDB
    }
}