import java.util.*

/**
 * Created by hosaka on 2015/11/24.
 */
open class Sequence(val id: String, val pairs: List<Pair<String, Int>>) {
    override fun toString(): String {
        return "Sequence(id=$id, pairs=$pairs)"
    }

    // static
    // 生成時にpairsをソートする
    companion object {
        fun make(id: kotlin.String, _pairs: List<Pair<String, Int>>): Sequence {
            val pairs = _pairs.sortedWith(compareBy({ it.second }, { it.first }))
            return Sequence(id, pairs)
        }
    }
}