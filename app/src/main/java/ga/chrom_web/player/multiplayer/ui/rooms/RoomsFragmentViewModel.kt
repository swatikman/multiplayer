package ga.chrom_web.player.multiplayer.ui.rooms

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import ga.chrom_web.player.multiplayer.Utils
import ga.chrom_web.player.multiplayer.api.ApiManager
import ga.chrom_web.player.multiplayer.data.Room
import ga.chrom_web.player.multiplayer.di.App
import java.util.ArrayList
import javax.inject.Inject

class RoomsFragmentViewModel : AndroidViewModel {

    @Inject
    lateinit var apiManager: ApiManager
    var rooms: MutableLiveData<ArrayList<Room>>
        private set

    constructor(application: Application?) : super(application) {
        App.getComponent().inject(this)
        rooms = MutableLiveData()
        apiManager.rooms().subscribe(
                { rooms ->
                    this.rooms.value = rooms
                },
                {
                    Utils.debugLog("EERRORO")
                    it.printStackTrace()
                },
                {

                }
        )
    }
}