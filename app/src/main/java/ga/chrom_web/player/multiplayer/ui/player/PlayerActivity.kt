package ga.chrom_web.player.multiplayer.ui.player

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import ga.chrom_web.player.multiplayer.R

class PlayerActivity : AppCompatActivity() {

    private lateinit var mPlayerFragment: PlayerFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        if (savedInstanceState == null) {
            mPlayerFragment = PlayerFragment()
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, mPlayerFragment)
                    .commit()
        }
    }

    override fun onBackPressed() {
        if (!mPlayerFragment.onBackPressed()) {
            super.onBackPressed()
        }
    }
}
