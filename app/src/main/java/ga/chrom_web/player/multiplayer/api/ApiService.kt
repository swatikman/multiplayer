package ga.chrom_web.player.multiplayer.api

import ga.chrom_web.player.multiplayer.data.Room
import io.reactivex.Observable
import retrofit2.http.GET
import java.util.ArrayList

interface ApiService {

    @GET(RetrofitApi.API_URL + "/rooms")
    fun rooms(): Observable<ArrayList<Room>>

    @GET(RetrofitApi.API_URL + "/check")
    fun smilesCheck(): Observable<String>

}