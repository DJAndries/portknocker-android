package ca.andries.portknocker.util

import android.content.Context
import ca.andries.portknocker.PortKnockerException
import ca.andries.portknocker.R

class PortParseUtil {
    companion object {
        fun validateAndParsePorts(context: Context, content: String) : List<Int> {
            val portInputs = content.split(",")
            val result = mutableListOf<Int>()
            for (portInput in portInputs) {
                if (portInput.isEmpty()) continue
                val parsedPort = try {
                    val v = portInput.trim().toInt()
                    if (v < 1 || v > 65535) throw NumberFormatException()
                    v
                } catch (e : NumberFormatException) {
                    throw PortKnockerException(
                        context.getString(
                            R.string.bad_port,
                            portInput
                        )
                    )
                }
                result.add(parsedPort)
            }
            if (result.isEmpty()) {
                throw PortKnockerException(
                    context.getString(
                        R.string.no_ports_specified
                    )
                )
            }
            return result
        }

        fun validateAndParseOneTimeSequences(context: Context, content: String) : ArrayList<List<Int>> {
            val lines = content.split("\n").filter { v -> v.isNotEmpty() }
            if (lines.isEmpty()) {
                throw PortKnockerException(
                    context.getString(
                        R.string.no_ports_specified
                    )
                )
            }
            return ArrayList(lines.map { line ->
                validateAndParsePorts(
                    context,
                    line
                )
            })
        }
    }
}