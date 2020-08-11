package ca.andries.portknocker.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import ca.andries.portknocker.util.KnockUtil
import ca.andries.portknocker.PortKnockerException
import ca.andries.portknocker.util.PortParseUtil
import ca.andries.portknocker.R
import kotlinx.android.synthetic.main.fragment_quick_knock.*
import kotlinx.android.synthetic.main.fragment_quick_knock.hostTxt
import kotlinx.android.synthetic.main.fragment_quick_knock.hostTxtLayout
import kotlinx.android.synthetic.main.fragment_quick_knock.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class QuickKnockFragment(val onKnock : () -> Unit) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_quick_knock, container, false)
        v.knockBtn.setOnClickListener {
            if (validate()) {
                GlobalScope.launch { startKnock() }
            }
        }
        return v
    }

    private fun setButtonEnable(enabled : Boolean) {
        knockBtn.isEnabled = enabled
        knockBtn.text = getString(if (enabled) R.string.knock_knock else R.string.knocking)
    }

    private fun validate() : Boolean {
        var errors = 0

        val hostValid = hostTxt.text.toString().isNotEmpty()
        hostTxtLayout.isErrorEnabled = !hostValid
        hostTxtLayout.error = if (!hostValid) getString(R.string.no_host_specified) else null
        errors += if (!hostValid) 1 else 0

        try {
            PortParseUtil.validateAndParsePorts(
                requireContext(),
                portsTxt.text.toString()
            )
            portsTxtLayout.isErrorEnabled = false
            portsTxtLayout.error = null
        } catch (e : PortKnockerException) {
            portsTxtLayout.isErrorEnabled = true
            portsTxtLayout.error = e.msg
            errors += 1
        }

        return errors == 0
    }

    private fun startKnock() {
        activity?.runOnUiThread {
            setButtonEnable(false)
        }

        KnockUtil.knockPorts(
            requireContext(), hostTxt.text.toString(),
            PortParseUtil.validateAndParsePorts(
                requireContext(),
                portsTxt.text.toString()
            )
        )

        activity?.runOnUiThread {
            Toast.makeText(context, getString(R.string.open_sesame, portsTxt.text.toString()), Toast.LENGTH_LONG).show()
            hostTxt.setText("")
            portsTxt.setText("")
            setButtonEnable(true)
            onKnock()
        }
    }
}