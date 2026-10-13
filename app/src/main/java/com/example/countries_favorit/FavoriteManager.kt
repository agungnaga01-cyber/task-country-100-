package com.example.countries_favorit

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object FavoriteManager {

    private const val PREF_NAME = "favorite_preferences"
    private const val KEY_FAVORITES = "favorites"

    private val gson = Gson()

    fun getFavorites(context: Context): MutableList<Country> {

        val preferences = context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

        val json = preferences.getString(KEY_FAVORITES, null)

        if (json.isNullOrEmpty()) {
            return mutableListOf()
        }

        val type = object : TypeToken<MutableList<Country>>() {}.type

        return gson.fromJson(json, type)
    }

    fun isFavorite(
        context: Context,
        country: Country
    ): Boolean {

        return getFavorites(context).any {
            it.iso3 == country.iso3
        }
    }

    fun addFavorite(
        context: Context,
        country: Country
    ) {

        val favorites = getFavorites(context)

        if (!favorites.any { it.iso3 == country.iso3 }) {
            favorites.add(country)
            saveFavorites(context, favorites)
        }
    }

    fun removeFavorite(
        context: Context,
        country: Country
    ) {

        val favorites = getFavorites(context)

        favorites.removeAll {
            it.iso3 == country.iso3
        }

        saveFavorites(context, favorites)
    }

    private fun saveFavorites(
        context: Context,
        favorites: List<Country>
    ) {

        val preferences = context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

        val json = gson.toJson(favorites)

        preferences.edit()
            .putString(KEY_FAVORITES, json)
            .apply()
    }
}