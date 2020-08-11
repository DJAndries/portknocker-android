package ca.andries.portknocker.util

import android.content.Context
import ca.andries.portknocker.models.HistoryItem
import ca.andries.portknocker.data.StoredDataManager
import java.net.InetSocketAddress
import java.net.Socket
import java.util.*

class KnockUtil {
    companion object {
        fun knockPort(host : String, port : Int) : Boolean {
            return try {
                val sock = Socket()
                sock.connect(InetSocketAddress(host, port), 1000)
                true
            } catch (e : Exception) {
                false
            }
        }

        fun knockPorts(context: Context, host : String, ports : List<Int>) {
            for (port in ports) {
                knockPort(
                    host,
                    port
                )
            }
            StoredDataManager.addHistory(context,
                HistoryItem(
                    host,
                    ports.joinToString(", "),
                    Date()
                )
            )
        }
    }
}