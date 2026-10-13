package com.example.countries_favorit

import android.content.Context
import coil3.ImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import okhttp3.OkHttpClient

object AppImageLoader {

    fun create(context: Context): ImageLoader {

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->

                val request = chain.request()
                    .newBuilder()
                    .header(
                        "User-Agent",
                        "CountriesExplorer/1.0 (student project)"
                    )
                    .build()

                chain.proceed(request)
            }
            .build()

        return ImageLoader.Builder(context)
            .components {
                add(
                    OkHttpNetworkFetcherFactory(
                        callFactory = okHttpClient
                    )
                )
            }
            .build()
    }
}