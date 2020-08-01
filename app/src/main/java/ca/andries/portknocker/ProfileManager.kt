package ca.andries.portknocker

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.ArrayList

class ProfileManager {

    companion object {
        var context: Context? = null

        private fun getSharedPrefs(): SharedPreferences {
            return context?.getSharedPreferences(
                context?.getString(R.string.main_prefs),
                Context.MODE_PRIVATE
            )
                ?: throw RuntimeException("No context set!")
        }

        fun listProfiles(): ArrayList<Profile> {
            val sharedPrefs = getSharedPrefs()
            val strVal = sharedPrefs.getString(context?.getString(R.string.profiles_key), "[]")
            return Gson().fromJson(strVal, object : TypeToken<ArrayList<Profile>>() {}.type)
        }

        fun saveProfile(profile : Profile) {
            val profiles = listProfiles()

            if (profile.id == null) {
                profile.id = UUID.randomUUID().toString()
            }

            val existingProfileIndex = profiles.indexOfFirst { v -> v.id.equals(profile.id, true) }
            if (existingProfileIndex != -1) {
                profiles.removeAt(existingProfileIndex)
                profiles.add(existingProfileIndex, profile)
            } else {
                profiles.add(profile)
            }

            val sharedPrefs = getSharedPrefs()
            val serializedProfiles = Gson().toJson(profiles)
            sharedPrefs.edit().putString(context?.getString(R.string.profiles_key), serializedProfiles).commit()
        }

        fun deleteProfile(index: Int) {
            val profiles = listProfiles()

            profiles.removeAt(index)

            val sharedPrefs = getSharedPrefs()
            val serializedProfiles = Gson().toJson(profiles)

            sharedPrefs.edit().putString(context?.getString(R.string.profiles_key), serializedProfiles).commit()
        }
    }
}