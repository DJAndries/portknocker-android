package ca.andries.portknocker.data

import android.content.Context
import android.content.SharedPreferences
import ca.andries.portknocker.models.HistoryItem
import ca.andries.portknocker.models.Profile
import ca.andries.portknocker.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.ArrayList

class StoredDataManager {

    companion object {
        private fun getSharedPrefs(context: Context): SharedPreferences {
            return context.getSharedPreferences(
                context?.getString(R.string.main_prefs),
                Context.MODE_PRIVATE
            )
                ?: throw RuntimeException("No context set!")
        }

        private fun <T> listObjects(context: Context, keyId: Int, typeToken: TypeToken<ArrayList<T>>) : ArrayList<T> {
            val sharedPrefs =
                getSharedPrefs(
                    context
                )
            val strVal = sharedPrefs.getString(context.getString(keyId), "[]")
            return Gson().fromJson(strVal, typeToken.type)
        }

        private inline fun <reified T> saveObjects(context: Context, keyId: Int, list: List<T>) {
            val sharedPrefs =
                getSharedPrefs(
                    context
                )
            val serialized = Gson().toJson(list)
            sharedPrefs.edit().putString(context.getString(keyId), serialized).commit()
        }

        fun listProfiles(context: Context): ArrayList<Profile> {
            return listObjects(
                context,
                R.string.profiles_key,
                object :
                    TypeToken<ArrayList<Profile>>() {})
        }

        fun listHistory(context: Context): ArrayList<HistoryItem> {
            return listObjects(
                context,
                R.string.history_key,
                object :
                    TypeToken<ArrayList<HistoryItem>>() {})
        }

        fun addHistory(context: Context, newItem: HistoryItem) {
            var history =
                listHistory(
                    context
                )

            history.add(0, newItem)

            if (history.size > 100) {
                history.removeAt(100)
            }

            saveObjects(
                context,
                R.string.history_key,
                history
            )
        }

        fun saveProfile(context: Context, profile : Profile) {
            val profiles =
                listProfiles(
                    context
                )

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

            saveObjects(
                context,
                R.string.profiles_key,
                profiles
            )
        }

        fun deleteProfile(context: Context, index: Int) {
            val profiles =
                listProfiles(
                    context
                )

            profiles.removeAt(index)

            saveObjects(
                context,
                R.string.profiles_key,
                profiles
            )
        }

        fun clearHistory(context: Context) {
            saveObjects(
                context,
                R.string.history_key,
                listOf<HistoryItem>()
            )
        }
    }
}