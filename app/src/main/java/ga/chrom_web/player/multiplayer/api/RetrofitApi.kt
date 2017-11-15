package ga.chrom_web.player.multiplayer.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitApi {

    companion object {
        private const val BASE_URL = "http://patyplay.ga"
        private const val API_PREFIX = "/api"
        private const val PORT = "8080"
        const val API_URL = BASE_URL + API_PREFIX
    }

    private var retrofit: Retrofit

    constructor() {

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
        client.addInterceptor(logging)

        retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(buildGsonConverter())
                .build()
    }

    public fun apiService(): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    private fun buildGsonConverter(): GsonConverterFactory {
        return GsonConverterFactory.create(GsonBuilder().create())
    }
}