package ga.chrom_web.player.multiplayer.api

import ga.chrom_web.player.multiplayer.data.Room
import ga.chrom_web.player.multiplayer.di.App
import io.reactivex.Observable
import javax.inject.Inject
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.ObservableTransformer
import java.util.ArrayList
import java.util.stream.IntStream


class ApiManager {

    //    @Inject
    private lateinit var apiService: ApiService

    constructor(apiService: ApiService) {
//        App.getComponent().inject(this)
        this.apiService = apiService;
    }

//    public fun rooms(): Observable<ArrayList<Room>> = generateRooms()

    public fun rooms(): Observable<ArrayList<Room>> = apiService.rooms()
            .retry(3)
            .compose(applySchedulers())


    public fun smilesCheck(): Observable<String> = apiService.smilesCheck()
            .retry(3)
            .compose(applySchedulers())

    private fun generateRooms(): Observable<ArrayList<Room>> {
        val rooms: ArrayList<Room> = ArrayList()
        for (i in 1 .. 10) {
            val room = Room()
            room.name = "Room #" + i
            room.description = "The most interesting room ever"
            room.usersCount = i
            room.usersMax = 10
            rooms.add(room)
        }
        return Observable.just(rooms)
    }

    private fun <E> applySchedulers(): ObservableTransformer<E, E> {
        return ObservableTransformer { observable ->
            observable
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())

        }
    }
}