package ga.chrom_web.player.multiplayer

import ga.chrom_web.player.multiplayer.di.App
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.*
import java.net.HttpURLConnection
import java.net.URI
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlin.concurrent.thread

@Singleton
class SmilesLoader {

    companion object {
        private const val SMILES_DATA_LINK = "http://player.chrom-web.ga/api/smiles"
        private const val SMILES_VERSION_LINK = "http://player.chrom-web.ga/api/check"
        private const val SMILES_NAMES_FILENAME = "smiles"
    }

    private var smilesNamesFile: File
    private var smilesNames: LinkedHashMap<String, String>? = null

    @Inject
    @field:[Named("internalDir")]
    lateinit var internalDir: File

    @Inject
    @field:[Named("externalCacheDir")]
    lateinit var cacheDir: File

    @Inject
    lateinit var appExecutors: AppExecutors

    @Inject
    lateinit var prefs: SharedPreferenceHelper

    @Inject
    constructor() {
        App.getComponent().inject(this)
        smilesNamesFile = File(internalDir, SMILES_NAMES_FILENAME)
        if (smilesNamesFile.exists()) {
            smilesNames = readSmilesNames()
        }
    }

    public fun getSmilesPaths(withBigSmiles: Boolean, onSmilesPathReadyListener: OnSmilesPathReadyListener?) {
        thread {
            if (!areSmilesNamesLoaded()) {
                loadSmilesNames()
            }
            val smilesNames = readSmilesNames()
            smilesNames.forEach {
                smilesNames[it.key] = cacheDir.absolutePath + "/" + it.value
            }

            if (withBigSmiles) {
                // put smilesName into bigSmiles
                // to maintain smiles sorted
                val bigSmiles = createBigSmiles(smilesNames)
                bigSmiles.putAll(smilesNames)
                onSmilesPathReadyListener?.onSmilesReady(bigSmiles)
            } else {
                onSmilesPathReadyListener?.onSmilesReady(smilesNames)
            }
        }
    }

    private fun createBigSmiles(smilesPaths: LinkedHashMap<String, String>): LinkedHashMap<String, String> {
        // creates a copy of smiles and adds to each smile ending 'Big', so 'Smile' => 'SmileBig'
        val bigSmiles = LinkedHashMap<String, String>()
        for (smileName in smilesPaths.keys) {
            bigSmiles.put(smileName + "Big", smilesPaths[smileName]!!)
        }
        return bigSmiles
    }

    /**
     * Reads smiles names from file
     */
    private fun readSmilesNames(): LinkedHashMap<String, String> {
        return JsonUtil.parseSmilesFromFile(FileUtils.read(smilesNamesFile))
    }


    public fun loadSmile(smile: String, onSmileReadyListener: (File) -> Unit) {
        thread {
            // TODO : maybe store all smiles names as a field if they are already loaded ??
            val nameOnServer = readSmilesNames()[smile]
            val smilesServerDir = prefs.smilesServerPath
            val uri = URI.create("$smilesServerDir$nameOnServer")
            val file = File(cacheDir, nameOnServer)
            if (!file.exists()) {
                downloadSmile(uri, file)
            }
            appExecutors.mainThread.execute {
                onSmileReadyListener.invoke(file)
            }
        }
    }

    /**
     * Load smiles names from network
     */
    private fun loadSmilesNames() {

        val request: Request = Request.Builder()
                .url(SMILES_DATA_LINK)
                .build()

        val response = OkHttpClient().newCall(request).execute()
        if (response.isSuccessful && response.body() != null) {
            val body = response.body()!!.string()

            val version: Int = JsonUtil.getIntFromJsonObject(body, "version")
            val serverSmilePath: String = JsonUtil.getStringFromJsonObject(body, "server")

            prefs.isNewerSmilesVersion(version)
            prefs.saveSmilesServerPath(serverSmilePath)

            val smiles = JsonUtil.getSmilesJsonArray(body)
            saveSmilesNames(smiles)
        }
        response.close()

    }

    /**
     *  Make a network request to check if smiles version same on client and on server
     */
    public fun checkSmilesVersion() {
        val request: Request = Request.Builder()
                .url(SMILES_VERSION_LINK)
                .build()

        val response = OkHttpClient().newCall(request).execute()
        if (response.isSuccessful && response.body() != null) {
            val body = response.body()!!.string()
            val smilesVersion = JsonUtil.parseSmileVersion(body)
            if (prefs.isNewerSmilesVersion(smilesVersion)) {
                // update all smiles
                loadSmilesNames()
            }
        }
        response.close()
    }

    private fun areSmilesNamesLoaded() = smilesNamesFile.exists()

    /**
     *  Save smiles name to file
     */
    private fun saveSmilesNames(jsonSmiles: String) {
        FileUtils.write(smilesNamesFile, jsonSmiles)
    }

    /**
     *  Download smile from server and saves to file
     */
    private fun downloadSmile(link: URI, file: File) {
        Utils.debugLog("Downloading smile: " + link)
        var input: InputStream? = null
        var output: OutputStream? = null
        var connection: HttpURLConnection? = null
        try {
            val url = link.toURL()
            connection = url.openConnection() as HttpURLConnection
            connection.connect()

            if (connection.responseCode < HttpURLConnection.HTTP_OK
                    || connection.responseCode >= HttpURLConnection.HTTP_MULT_CHOICE) {
                //TODO: handle
            }
            input = connection.inputStream
            output = FileOutputStream(file)

            val data = ByteArray(4096)
            var count: Int
            while (true) {
                count = input!!.read(data)
                if (count == -1) {
                    return
                }
                output.write(data, 0, count)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                output?.close()
                input?.close()
            } catch (ignored: IOException) {
            }
            connection?.disconnect()
        }
    }

    interface OnSmilesPathReadyListener {
        fun onSmilesReady(smiles: LinkedHashMap<String, String>)
    }

}
