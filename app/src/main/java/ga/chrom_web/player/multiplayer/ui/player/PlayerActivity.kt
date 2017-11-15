package ga.chrom_web.player.multiplayer.ui.player

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import ga.chrom_web.player.multiplayer.R
import ga.chrom_web.player.multiplayer.Utils
import ga.chrom_web.player.multiplayer.data.Room

class PlayerActivity : AppCompatActivity() {

    companion object {
        const val SHARE_LINK = "share_link"
    }

    private var mPlayerFragment: PlayerFragment? = null
    private lateinit var viewModel: PlayerActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        if (savedInstanceState == null) {
            mPlayerFragment = PlayerFragment()
            val bundle = Bundle()

            intent?.extras?.getString(SHARE_LINK).let { youtubeLink ->
                bundle.putString(SHARE_LINK, youtubeLink)
            }
            intent?.extras?.getSerializable(Room.INTENT_KEY).let { room ->
                bundle.putSerializable(Room.INTENT_KEY, room)
            }

            mPlayerFragment?.arguments = bundle

            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, mPlayerFragment)
                    .commit()
        }
        viewModel = ViewModelProviders.of(this).get(PlayerActivityViewModel::class.java)
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // activity in singleInstance mode
        // so this is the only way to get intent when activity is visible
        setIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        intent?.extras?.getString(SHARE_LINK)?.let { link ->
            mPlayerFragment?.receiveShare(link)
        }
    }

    override fun onBackPressed() {
        val playerOnBackPressed = mPlayerFragment?.onBackPressed()
        if (playerOnBackPressed == null || !playerOnBackPressed) {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Utils.debugLog("Destroying player activity")
        if (!isChangingConfigurations) {
            viewModel.disconnect()
        }
    }
}
