package ca.andries.portknocker

import android.content.Context
import java.net.InetSocketAddress
import java.net.Socket

class KnockUtil {
    companion object {
        private fun knockPort(host : String, port : Int) {
            try {
                val sock = Socket()
                sock.connect(InetSocketAddress(host, port), 1000)
            } catch (e : Exception) {

            }
        }

        fun knockPorts(host : String, ports : List<Int>) {
            for (port in ports) {
                knockPort(host, port)
            }
        }

    }
}