package ca.andries.portknocker

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import kotlinx.android.synthetic.main.activity_add_profile.*
import kotlinx.android.synthetic.main.activity_add_profile.nameTxtLayout
import kotlinx.android.synthetic.main.fragment_profile.*

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
    }

    private fun loadExistingProfile() {
        existingProfile = intent.getSerializableExtra(EXISTING_PROFILE) as Profile? ?: return

        nameTxt.setText(existingProfile?.name)
        portsTxt.setText(existingProfile?.ports?.joinToString(","))
        hostTxt.setText(existingProfile?.host)
        oneTimePortsTxt.setText(existingProfile?.oneTimeSequences?.joinToString("\n") { v -> v.joinToString(",") })
        onetimeSwitch.isChecked = existingProfile?.oneTimeEnabled ?: false
        updateOneTime()
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

    private fun clearErrors() {
        listOf(nameTxtLayout, hostTxtLayout, oneTimePortsTxtLayout, portsTxtLayout).map { v ->
            v.error = null
            v.isErrorEnabled = false
        }
    }

    private fun validateBlanks() : Boolean {
        var isError = false

        val editLayouts = listOf(nameTxtLayout, hostTxtLayout)
        val editErrors = listOf(R.string.no_name_specified, R.string.no_host_specified)
        listOf(nameTxt, hostTxt).mapIndexed { i, v ->
            if (v.text.toString().isEmpty()) {
                editLayouts[i].isErrorEnabled = true
                editLayouts[i].error = getString(editErrors[i])
                isError = true
            }
        }

        return !isError
    }

    private fun save() {
        clearErrors()
        if (!validateBlanks()) return

        val profile = Profile(name = nameTxt.text.toString(), host = hostTxt.text.toString(), oneTimeEnabled = onetimeSwitch.isChecked)

        if (existingProfile != null) {
            profile.id = existingProfile?.id
        }

        try {
            if (!onetimeSwitch.isChecked) {
                profile.ports = PortParseUtil.validateAndParsePorts(this, portsTxt.text.toString())
            } else {
                profile.oneTimeSequences = PortParseUtil.validateAndParseOneTimeSequences(
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

        ProfileManager.saveProfile(profile)

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