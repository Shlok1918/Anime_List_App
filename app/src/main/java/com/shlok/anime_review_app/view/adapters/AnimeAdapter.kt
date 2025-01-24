package com.shlok.anime_review_app.view.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shlok.anime_review_app.R
import com.shlok.anime_review_app.model.Anime
import com.squareup.picasso.Picasso

class AnimeAdapter(
    private val animeList: List<Anime>,
    private val onClick: (Anime) -> Unit
) : RecyclerView.Adapter<AnimeAdapter.AnimeViewHolder>() {

    inner class AnimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val animeTitle: TextView = itemView.findViewById(R.id.animeTitle)
        private val animeScore: TextView = itemView.findViewById(R.id.animeScore)
        private val animeEpisodes: TextView = itemView.findViewById(R.id.animeEpisodes)
        private val animeImage: ImageView = itemView.findViewById(R.id.animeImage)

        @SuppressLint("SetTextI18n")
        fun bind(anime: Anime) {
            animeTitle.text = anime.title_english
            animeScore.text = "Rating: ${anime.score.takeIf { it > 0 } ?: "N/A"}"
            animeEpisodes.text = "Episodes: ${anime.episodes.takeIf { it > 0 } ?: "Unknown"}"
            Picasso.get().load(anime.image_url).into(animeImage)

            itemView.setOnClickListener { onClick(anime) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_anime, parent, false)
        return AnimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        holder.bind(animeList[position])
    }

    override fun getItemCount() = animeList.size
}

