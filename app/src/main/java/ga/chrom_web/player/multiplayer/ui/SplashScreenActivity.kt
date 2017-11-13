package ga.chrom_web.player.multiplayer.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ga.chrom_web.player.multiplayer.ui.signin.MainActivity

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
