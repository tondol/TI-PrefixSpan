import java.util.*

/**
 * Created by hosaka on 2015/11/24.
 */
data class SequentialPattern(val items: List<String>, val intervalIds: List<Int>, val sequenceIds: Set<String>) {
    // i個目のinterval idはi個目のitemと(i + 1)個目のitemの間のperiodに対応する
    init {
        assert(intervalIds.size == items.size - 1)
    }

    // itemsに対する簡易BMアルゴリズム
    // interval idsの一致は_containsで確認する
    fun contains(other: SequentialPattern): Boolean {
        // シフト量テーブルを作成する
        val map = hashMapOf<String, Int>()
        for (i in (0..other.items.size - 2)) {
            map.put(other.items[i], other.items.size - i - 1)
        }

        var index = 0
        while (index < items.size - other.items.size) {
            if (items.subList(index, index + other.items.size) == other.items) {
                return _contains(other, index)
            } else {
                index += map.getOrDefault(items[index + other.items.size - 1], other.items.size)
            }
        }
        return false
    }

    // interval idsの一致を確認する
    private fun _contains(other: SequentialPattern, from: Int): Boolean {
        return intervalIds.subList(from, from + other.intervalIds.size) == other.intervalIds
    }

    override fun toString(): String {
        val elements = arrayListOf<String>()
        for ((index, item) in items.withIndex()) {
            elements.add(item.toString())
            if (index < intervalIds.size) {
                elements.add(intervalIds[index].toString())
            }
        }
        return "SequentialPattern(items=$elements, sequenceIds=$sequenceIds)"
    }

    // 結果の確認用
    // ヒューマンリーダブル
    fun toString(tis: TimeIntervalSet): String {
        val elements = arrayListOf<String>()
        for ((index, item) in items.withIndex()) {
            elements.add(item.toString())
            if (index < intervalIds.size) {
                elements.add(tis.getDescription(intervalIds[index]))
            }
        }
        return "SequentialPattern(items=$elements, sequenceIds=$sequenceIds)"
    }
}