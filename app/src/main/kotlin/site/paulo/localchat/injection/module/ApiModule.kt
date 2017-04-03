package site.paulo.localchat.injection.module

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import site.paulo.localchat.data.remote.ChatGeoService
import site.paulo.localchat.data.remote.RibotsService
import javax.inject.Singleton

@Module
class ApiModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient()
    }

    @Provides
    @Singleton
    fun provideRibotsService(okHttpClient: OkHttpClient, gson: Gson): RibotsService {
        return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://api.ribot.io/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(RibotsService::class.java)
    }

    @Provides
    @Singleton
    fun provideChatGeoService(): ChatGeoService {
        val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .baseUrl("https://chatgeo.herokuapp.com/user/")
                //.baseUrl("http://localhost:1337/user/")
                .build()

        System.out.println("ChatGeoService base URL: " + retrofit.baseUrl());

        return retrofit.create(ChatGeoService::class.java)
    }

}
