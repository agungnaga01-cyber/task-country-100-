package com.example.countries_favorit

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil3.ImageLoader
import coil3.load

class CountryAdapter(
    private var countries: List<Country>,
    private val imageLoader: ImageLoader,
    private val isFavoritesScreen: Boolean = false,
    private val onCountryClick: (Country) -> Unit
) : RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {

    class CountryViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val imgFlag: ImageView =
            itemView.findViewById(R.id.imgFlag)

        val tvCountryName: TextView =
            itemView.findViewById(R.id.tvCountryName)

        val tvFavorite: TextView =
            itemView.findViewById(R.id.tvFavorite)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CountryViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_country, parent, false)

        return CountryViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CountryViewHolder,
        position: Int
    ) {
        val country = countries[position]
        val context = holder.itemView.context

        holder.tvCountryName.text = country.name

        holder.imgFlag.load(
            country.flag,
            imageLoader
        )

        // Tampilkan ikon sesuai status favorit
        fun updateFavoriteIcon() {
            val isFavorite =
                FavoriteManager.isFavorite(context, country)

            holder.tvFavorite.text = if (isFavorite) {
                "♥"
            } else {
                "♡"
            }

            holder.tvFavorite.setTextColor(
                Color.parseColor(
                    if (isFavorite) "#E94B70" else "#5840A8"
                )
            )
        }

        updateFavoriteIcon()

        // Klik hati untuk tambah atau hapus favorit
        holder.tvFavorite.setOnClickListener {
            val wasFavorite =
                FavoriteManager.isFavorite(context, country)

            if (wasFavorite) {
                FavoriteManager.removeFavorite(context, country)
            } else {
                FavoriteManager.addFavorite(context, country)
            }

            if (isFavoritesScreen) {

                // Hapus item dari daftar Favorit secara langsung
                val latestFavorites =
                    FavoriteManager.getFavorites(context)

                updateData(latestFavorites)

                // Perbarui pesan ketika daftar Favorit kosong
                val emptyView =
                    holder.itemView.rootView
                        .findViewById<View>(R.id.tvEmptyFavorites)

                emptyView?.visibility = if (latestFavorites.isEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            } else {
                // Perbarui ikon di Home tanpa memuat ulang seluruh daftar
                updateFavoriteIcon()
            }
        }

        // Klik bagian lain pada kartu untuk membuka popup informasi
        holder.itemView.setOnClickListener {
            onCountryClick(country)
        }
    }

    override fun getItemCount(): Int {
        return countries.size
    }

    fun updateData(newCountries: List<Country>) {
        countries = newCountries
        notifyDataSetChanged()
    }
}
