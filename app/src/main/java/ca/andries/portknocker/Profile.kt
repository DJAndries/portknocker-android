package ca.andries.portknocker

import android.content.Context
import java.io.Serializable

class Profile (
    var id: String? = null,
    var name: String,
    var host: String,
    var ports: List<Int> = listOf(),
    var oneTimeEnabled: Boolean = false,
    var oneTimeSequences: ArrayList<List<Int>> = arrayListOf()
) : Serializable {
    fun popNextSequence(context: Context) : List<Int> {
        if (oneTimeSequences.isEmpty()) {
            throw PortKnockerException(context.getString(R.string.no_onetime_seqs))
        }
        val seq = oneTimeSequences[0]
        oneTimeSequences.removeAt(0)
        return seq
    }

    fun peekNextSequenceText() : String {
        return if (oneTimeEnabled) {
            if (oneTimeSequences.isEmpty()) {
                return ""
            }
            oneTimeSequences[0].joinToString()
        } else {
            ports.joinToString()
        }
    }
}