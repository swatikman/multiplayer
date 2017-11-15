package ga.chrom_web.player.multiplayer.ui.signin

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import ga.chrom_web.player.multiplayer.R
import ga.chrom_web.player.multiplayer.ui.player.PlayerActivity
import ga.chrom_web.player.multiplayer.ui.rooms.RoomsActivity

class MainActivity : AppCompatActivity() {

    private var intentLink: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        intent?.extras?.let {
            intentLink = it[Intent.EXTRA_TEXT].toString()
        }

        if (PreferenceManager.getDefaultSharedPreferences(this).contains("nick")) {
            moveToRooms()

        }
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, LoginFragment())
                .commit()
    }

/*    fun startPlayer() {
        val intent = Intent(this, PlayerActivity::class.java)
        intentLink?.let {
            intent.putExtra(PlayerActivity.SHARE_LINK, it)
        }
        startActivity(intent)
        finish()
    }*/

    fun moveToRooms() {
        val intent = Intent(this, RoomsActivity::class.java)
        intentLink?.let {
            intent.putExtra(PlayerActivity.SHARE_LINK, it)
        }
        startActivity(intent)
        finish()
    }
}
