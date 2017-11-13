package ga.chrom_web.player.multiplayer


import android.content.SharedPreferences
import android.graphics.Color

import javax.inject.Inject

import ga.chrom_web.player.multiplayer.di.App

class SharedPreferenceHelper {

    companion object {
        private val PREF_NICK = "nick"
        private val PREF_COLOR = "color"
        private val PREF_SERVER_SMILES_PATH = "server_smiles_path"
        private val PREF_SMILES_VERSION = "smiles_version"
    }

    @Inject
    lateinit var prefs: SharedPreferences

    val nick: String
        get() = prefs.getString(PREF_NICK, "DEFAULT")

    val color: String
        get() = prefs.getString(PREF_COLOR, "#000000")

    val smilesServerPath: String
        get() = prefs.getString(PREF_SERVER_SMILES_PATH, "")

    val smilesVersion: Int
        get() = prefs.getInt(PREF_SMILES_VERSION, 1)

    init {
        App.getComponent().inject(this)
    }

    fun saveUser(nick: String, color: Int) {
        val editor = prefs.edit()
        editor.putString(PREF_NICK, nick)
        editor.putString(PREF_COLOR, removeAlpha(color))
        editor.apply()
    }

    fun saveSmilesServerPath(path: String) {
        prefs.edit().putString(PREF_SERVER_SMILES_PATH, path).apply()
    }

    /**
     * Checks if server has new version smiles
     * @return true if version of smiles updated,
     *         false when version same
     */
    fun isNewerSmilesVersion(version: Int): Boolean {
        if (smilesVersion != version) {
            prefs.edit().putInt(PREF_SMILES_VERSION, version).apply()
            return true
        }
        return false
    }

    private fun removeAlpha(color: Int): String {
        val hexColor = Integer.toHexString(color)
        if (hexColor.length <= 6) {
            return "#" + hexColor;
        }
        val colorWithoutAlpha = "#" + hexColor.substring(2)
        return colorWithoutAlpha
    }
}
