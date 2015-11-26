import java.util.*

/**
 * Created by hosaka on 2015/11/24.
 */
class PseudoSequence(id: String, pairs: List<Pair<String, Int>>, val timestamp: Int) : Sequence(id, pairs) {
    override fun toString(): String {
        return "PseudoSequence(id=$id, pairs=$pairs, timestamp=$timestamp)"
    }
}