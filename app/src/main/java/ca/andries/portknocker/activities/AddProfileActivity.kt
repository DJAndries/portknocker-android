package ca.andries.portknocker.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import ca.andries.portknocker.*
import ca.andries.portknocker.data.StoredDataManager
import ca.andries.portknocker.models.Profile
import ca.andries.portknocker.util.PortParseUtil
import kotlinx.android.synthetic.main.activity_add_profile.*
import kotlinx.android.synthetic.main.activity_add_profile.nameTxtLayout

class AddProfileActivity : AppCompatActivity() {

    var existingProfile : Profile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_profile)

        loadExistingProfile()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
        supportActionBar?.setTitle(R.string.edit_profile)

        saveBtn.setOnClickListener { save() }
        onetimeSwitch.setOnClickListener { updateOneTime() }
        portCheckSwitch.setOnClickListener { updatePortCheckInputs() }
    }

    private fun loadExistingProfile() {
        existingProfile = intent.getSerializableExtra(EXISTING_PROFILE) as Profile? ?: return

        nameTxt.setText(existingProfile?.name)
        portsTxt.setText(existingProfile?.ports?.joinToString(","))
        hostTxt.setText(existingProfile?.host)

        oneTimePortsTxt.setText(existingProfile?.oneTimeSequences?.joinToString("\n") { v -> v.joinToString(",") })
        onetimeSwitch.isChecked = existingProfile?.oneTimeEnabled ?: false

        portCheckSwitch.isChecked = existingProfile?.portCheckEnabled ?: false
        portToCheckTxt.setText(existingProfile?.portToCheck.toString())
        portIntervalTxt.setText(existingProfile?.portCheckWaitInterval.toString())

        if (!portCheckSwitch.isChecked) {
            portToCheckTxt.setText("")
            portIntervalTxt.setText(resources.getInteger(R.integer.default_check_port_wait_interval).toString())
        }

        updateOneTime()
        updatePortCheckInputs()
    }

    private fun updateOneTime() {
        if (onetimeSwitch.isChecked) {
            oneTimePortsTxtLayout.visibility = VISIBLE
            portsTxtLayout.visibility = GONE
        } else {
            oneTimePortsTxtLayout.visibility = GONE
            portsTxtLayout.visibility = VISIBLE
        }
    }

    private fun updatePortCheckInputs() {
        portCheckCtr.visibility = if (portCheckSwitch.isChecked) VISIBLE else GONE
    }

    private fun clearErrors() {
        listOf(nameTxtLayout, hostTxtLayout, oneTimePortsTxtLayout, portsTxtLayout, portToCheckLayout, portIntervalTxtLayout).map { v ->
            v.error = null
            v.isErrorEnabled = false
        }
    }

    private fun validateBlanks() : Boolean {
        var isError = false

        val editLayouts = listOf(nameTxtLayout, hostTxtLayout, portIntervalTxtLayout, portToCheckLayout)
        val editErrors = listOf(
            R.string.no_name_specified,
            R.string.no_host_specified,
            R.string.no_wait_interval_specified,
            R.string.no_check_port_specified
        )
        val portCheckInputStartIndex = 2

        listOf(nameTxt, hostTxt, portIntervalTxt, portToCheckTxt).mapIndexed { i, v ->

            if (v.text.toString().isEmpty()) {
                if (!(i >= portCheckInputStartIndex && !portCheckSwitch.isChecked)) {
                    editLayouts[i].isErrorEnabled = true
                    editLayouts[i].error = getString(editErrors[i])
                    isError = true
                }
            }
        }

        return !isError
    }

    private fun validatePortCheckInputs() : Boolean {
        if (!portCheckSwitch.isChecked) return true

        var isError = false

        val portToCheck = try { Integer.parseInt(portToCheckTxt.text.toString()) } catch (e: Exception) { 0 }
        val portInterval = try { Integer.parseInt(portIntervalTxt.text.toString()) } catch (e: Exception) { 0 }

        if (portToCheck < 1 || portToCheck > 65535) {
            portToCheckLayout.error = getString(R.string.bad_port, portToCheckTxt.text.toString())
            portToCheckLayout.isErrorEnabled = true
            isError = true
        }

        if (portInterval < 1) {
            portIntervalTxtLayout.error = getString(R.string.bad_interval)
            portIntervalTxtLayout.isErrorEnabled = true
            isError = true
        }

        return !isError
    }

    private fun save() {
        clearErrors()
        if (!validateBlanks()) return
        if (!validatePortCheckInputs()) return

        val profile = Profile(
            name = nameTxt.text.toString(), host = hostTxt.text.toString(),
            portToCheck = if (portCheckSwitch.isChecked) Integer.parseInt(portToCheckTxt.text.toString()) else 0,
            portCheckWaitInterval = if (portCheckSwitch.isChecked) Integer.parseInt(portIntervalTxt.text.toString())
            else resources.getInteger(R.integer.default_check_port_wait_interval),
            oneTimeEnabled = onetimeSwitch.isChecked, portCheckEnabled = portCheckSwitch.isChecked
        )

        if (existingProfile != null) {
            profile.id = existingProfile?.id
        }

        try {
            if (!onetimeSwitch.isChecked) {
                profile.ports =
                    PortParseUtil.validateAndParsePorts(
                        this,
                        portsTxt.text.toString()
                    )
            } else {
                profile.oneTimeSequences =
                    PortParseUtil.validateAndParseOneTimeSequences(
                        this,
                        oneTimePortsTxt.text.toString()
                    )
            }
        } catch (e : PortKnockerException) {
            val controlTxtLayout = if (onetimeSwitch.isChecked) oneTimePortsTxtLayout else portsTxtLayout
            controlTxtLayout.isErrorEnabled = true
            controlTxtLayout.error = e.msg
            return
        }

        StoredDataManager.saveProfile(
            this,
            profile
        )

        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        setResult(Activity.RESULT_CANCELED)
        finish()
        return true
    }

    companion object {
        const val EXISTING_PROFILE = "existing_profile"
    }
}