package ga.chrom_web.player.multiplayer.ui.player


import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import ga.chrom_web.player.multiplayer.ConnectionSocketManager
import ga.chrom_web.player.multiplayer.di.App

import javax.inject.Inject

class PlayerActivityViewModel : AndroidViewModel {

    @Inject
    lateinit var connectionSocketManager: ConnectionSocketManager

    constructor(application: Application) : super(application) {
        App.getComponent().inject(this)
    }

    public fun disconnect() {
        connectionSocketManager.disconnect()
    }
}
