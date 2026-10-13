package com.example.countries_favorit

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowCompat
import coil3.load
import android.graphics.Color
import android.graphics.drawable.ColorDrawable

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val rootLayout = findViewById<View>(R.id.rootLayout)

        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { view, windowInsets ->
            val systemBars = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            windowInsets
            }

            WindowCompat.getInsetsController(
                window,
                window.decorView
                ).isAppearanceLightStatusBars = true
        // =========================
        // VIEW
        // =========================
        val bottomNavigation =
            findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                R.id.bottomNavigation
            )

        val homeContent = findViewById<View>(R.id.homeContent)
        val favoriteContent = findViewById<View>(R.id.favoriteContent)

        val rvCountries = findViewById<RecyclerView>(R.id.rvCountries)
        val rvFavorites = findViewById<RecyclerView>(R.id.rvFavorites)

        val etSearch = findViewById<EditText>(R.id.etSearch)
        val tvEmptyFavorites = findViewById<View>(R.id.tvEmptyFavorites)

        // =========================
        // DATA
        // =========================
        var allCountries = emptyList<Country>()

        // =========================
        // IMAGE LOADER
        // =========================
        val imageLoader = AppImageLoader.create(this)

        // =========================
        // HOME ADAPTER
        // =========================
        val countryAdapter = CountryAdapter(
            emptyList(),
            imageLoader
        ) { country ->
            showCountryDialog(country, imageLoader)
        }

        rvCountries.layoutManager = LinearLayoutManager(this)
        rvCountries.adapter = countryAdapter

        // =========================
        // FAVORITE ADAPTER
        // =========================
        val favoriteAdapter = CountryAdapter(
            emptyList(),
            imageLoader,
            isFavoritesScreen = true
        ) { country ->
            showCountryDialog(country, imageLoader)
        }

        rvFavorites.layoutManager = LinearLayoutManager(this)
        rvFavorites.adapter = favoriteAdapter

        // =========================
        // BOTTOM NAVIGATION
        // =========================
        bottomNavigation.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_home -> {
                    homeContent.visibility = View.VISIBLE
                    favoriteContent.visibility = View.GONE

                    true
                }

                R.id.nav_favorite -> {
                    homeContent.visibility = View.GONE
                    favoriteContent.visibility = View.VISIBLE

                    loadFavorites(
                        favoriteAdapter,
                        tvEmptyFavorites
                    )

                    true
                }

                else -> false
            }
        }

        // =========================
        // SEARCH
        // =========================
        etSearch.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {

                val searchText = s.toString().trim()

                val filteredCountries = if (searchText.isEmpty()) {

                    allCountries

                } else {

                    allCountries.filter {
                        it.name.contains(
                            searchText,
                            ignoreCase = true
                        )
                    }
                }

                countryAdapter.updateData(filteredCountries)
            }

            override fun afterTextChanged(
                s: Editable?
            ) {
            }
        })

        // =========================
        // AMBIL DATA API
        // =========================
        RetrofitClient.api.getCountries().enqueue(
            object : Callback<ApiResponse> {

                override fun onResponse(
                    call: Call<ApiResponse>,
                    response: Response<ApiResponse>
                ) {

                    if (response.isSuccessful) {

                        allCountries =
                            response.body()?.data ?: emptyList()

                        countryAdapter.updateData(
                            allCountries
                        )
                    }
                }

                override fun onFailure(
                    call: Call<ApiResponse>,
                    t: Throwable
                ) {
                    // Nanti bisa ditambahkan
                    // pesan error
                }
            }
        )
    }

    // =================================
    // MENAMPILKAN DATA FAVORIT
    // =================================
    private fun loadFavorites(
        adapter: CountryAdapter,
        emptyView: View
    ) {

        val favorites =
            FavoriteManager.getFavorites(this)

        adapter.updateData(favorites)

        if (favorites.isEmpty()) {

            emptyView.visibility = View.VISIBLE

        } else {

            emptyView.visibility = View.GONE
        }
    }

    // =================================
    // DIALOG INFORMASI NEGARA
    // =================================

    private fun showCountryDialog(
        country: Country,
        imageLoader: coil3.ImageLoader
    ) {
        val dialogView = layoutInflater.inflate(
            R.layout.dialog_country,
            null
        )

        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val dialogFlag =
            dialogView.findViewById<android.widget.ImageView>(
                R.id.dialogFlag
            )

        val dialogCountryName =
            dialogView.findViewById<android.widget.TextView>(
                R.id.dialogCountryName
            )

        val dialogIso2 =
            dialogView.findViewById<android.widget.TextView>(
                R.id.dialogIso2
            )

        val dialogIso3 =
            dialogView.findViewById<android.widget.TextView>(
                R.id.dialogIso3
            )

        val btnFavoriteAction =
            dialogView.findViewById<com.google.android.material.button.MaterialButton>(
                R.id.btnFavoriteAction
            )

        val btnCloseDialog =
            dialogView.findViewById<com.google.android.material.button.MaterialButton>(
                R.id.btnCloseDialog
            )

        // Isi informasi negara
        dialogCountryName.text = country.name
        dialogIso2.text = country.iso2
        dialogIso3.text = country.iso3

        // Tampilkan bendera dengan Coil
        dialogFlag.load(country.flag, imageLoader)

        // Sesuaikan teks tombol dengan status favorit
        fun updateFavoriteButton() {
            val isFavorite =
                FavoriteManager.isFavorite(this, country)

            btnFavoriteAction.text = if (isFavorite) {
                "♥ Hapus dari Favorit"
            } else {
                "♡ Tambah Favorit"
            }
        }

        updateFavoriteButton()

        // Tambah atau hapus favorit
        btnFavoriteAction.setOnClickListener {
            val isFavorite =
                FavoriteManager.isFavorite(this, country)

            if (isFavorite) {
                FavoriteManager.removeFavorite(this, country)
            } else {
                FavoriteManager.addFavorite(this, country)
            }

            updateFavoriteButton()
        }

        btnCloseDialog.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

        dialog.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
    }

}