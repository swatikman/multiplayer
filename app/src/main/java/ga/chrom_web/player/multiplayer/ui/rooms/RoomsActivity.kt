package ga.chrom_web.player.multiplayer.ui.rooms

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import ga.chrom_web.player.multiplayer.R

class RoomsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rooms)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = getString(R.string.activity_rooms_title)
        setSupportActionBar(toolbar)
        // TODO: handle share
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, RoomsFragment())
                    .commit()
        }
    }
}
