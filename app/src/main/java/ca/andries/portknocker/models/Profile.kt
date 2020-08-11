package ca.andries.portknocker.models

import android.content.Context
import ca.andries.portknocker.PortKnockerException
import ca.andries.portknocker.R
import java.io.Serializable

class Profile (
    var id: String? = null,
    var name: String,
    var host: String,
    var ports: List<Int> = listOf(),
    var oneTimeEnabled: Boolean = false,
    var oneTimeSequences: ArrayList<List<Int>> = arrayListOf(),

    var portCheckEnabled: Boolean = false,
    var portToCheck: Int = 0,
    var portCheckWaitInterval: Int
) : Serializable {
    fun popNextSequence(context: Context) : List<Int> {
        val result = peekNextSequence(context)
        oneTimeSequences.removeAt(0)
        return result
    }

    fun peekNextSequence(context: Context) : List<Int> {
        if (oneTimeSequences.isEmpty()) {
            throw PortKnockerException(
                context.getString(
                    R.string.no_onetime_seqs
                )
            )
        }
        return oneTimeSequences[0]
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