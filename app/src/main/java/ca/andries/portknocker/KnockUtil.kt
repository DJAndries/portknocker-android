package ca.andries.portknocker

import android.content.Context
import java.net.InetSocketAddress
import java.net.Socket

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

        fun knockPorts(host : String, ports : List<Int>) {
            for (port in ports) {
                knockPort(host, port)
            }
        }
    }
}