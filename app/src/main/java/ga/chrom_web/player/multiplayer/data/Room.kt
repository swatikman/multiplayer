package ga.chrom_web.player.multiplayer.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Room : Serializable {

    companion object {
        public const val INTENT_KEY = "room"
    }

    var name: String? = null

    var description: String? = null

    @SerializedName("users")
    var usersCount: Int = 0

    @SerializedName("users_max")
    var usersMax: Int = 0
}