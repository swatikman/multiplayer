package ga.chrom_web.player.multiplayer.api

import ga.chrom_web.player.multiplayer.data.Response
import ga.chrom_web.player.multiplayer.data.Room
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*


class ApiManager {

    private var apiService: ApiService

    constructor(apiService: ApiService) {
        this.apiService = apiService;
    }

    fun rooms(): Observable<Response<ArrayList<Room>>> = apiService.rooms()
            .retry(3)
            .flatMap { obsRooms ->
                val response = Response.successResponse(obsRooms)
                return@flatMap Observable.just(response)
            }
            .onErrorReturn { rooms ->
                return@onErrorReturn Response.errorResponse()
            }
            .compose(applySchedulers())


    fun smilesCheck(): Observable<String> = apiService.smilesCheck()
            .retry(3)
            .compose(applySchedulers())

    private fun generateRooms(): Observable<ArrayList<Room>> {
        val rooms: ArrayList<Room> = ArrayList()
        for (i in 1..10) {
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