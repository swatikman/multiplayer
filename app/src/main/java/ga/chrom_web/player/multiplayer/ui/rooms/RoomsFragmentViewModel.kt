package ga.chrom_web.player.multiplayer.ui.rooms

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import ga.chrom_web.player.multiplayer.Utils
import ga.chrom_web.player.multiplayer.api.ApiManager
import ga.chrom_web.player.multiplayer.data.Response
import ga.chrom_web.player.multiplayer.data.Room
import ga.chrom_web.player.multiplayer.di.App
import io.reactivex.disposables.Disposable
import java.util.ArrayList
import javax.inject.Inject

class RoomsFragmentViewModel : AndroidViewModel {

    @Inject
    lateinit var apiManager: ApiManager
    var rooms: MutableLiveData<ArrayList<Room>>
        private set
    private var roomsDisposable: Disposable? = null



    constructor(application: Application?) : super(application) {
        App.getComponent().inject(this)
        rooms = MutableLiveData()
        refresh()
    }

    fun refresh() {
        roomsDisposable?.let { disposable ->
            if (!disposable.isDisposed) {
                disposable.dispose()
            }
        }
        roomsDisposable = apiManager.rooms().subscribe(
                { response ->
                    when (response.status) {
                        Response.Status.OK -> {
                            this.rooms.value = response.data
                        }
                        Response.Status.ERROR -> {
                            this.rooms.value = null
                        }
                    }
                },
                {
                    Utils.debugLog("EERRORO")
                    it.printStackTrace()
                }
        )
    }
}