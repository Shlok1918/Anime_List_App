package com.shlok.anime_review_app.view.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.shlok.anime_review_app.R
import com.shlok.anime_review_app.model.Anime
import com.shlok.anime_review_app.model.Repository
import com.squareup.picasso.Picasso

class DetailsActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_ANIME_ID = "EXTRA_ANIME_ID"

        fun newIntent(context: Context, animeId: Int): Intent {
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra(EXTRA_ANIME_ID, animeId)
            return intent
        }
    }

    private lateinit var youtubeWebView: WebView
    private lateinit var animePoster: ImageView
    private lateinit var animeTitle: TextView
    private lateinit var animeSynopsis: TextView
    private lateinit var animeGenres: TextView
    private lateinit var animeMainCast: TextView
    private lateinit var animeEpisodes: TextView
    private lateinit var animeRating: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        youtubeWebView = findViewById(R.id.youtubeWebView)
        animePoster = findViewById(R.id.animePoster)
        animeTitle = findViewById(R.id.animeTitle)
        animeSynopsis = findViewById(R.id.animeSynopsis)
        animeGenres = findViewById(R.id.animeGenres)
        animeMainCast = findViewById(R.id.animeMainCast)
        animeEpisodes = findViewById(R.id.animeEpisodes)
        animeRating = findViewById(R.id.animeRating)

        val animeId = intent.getIntExtra(EXTRA_ANIME_ID, -1)
        if (animeId == -1) {
            showError("Invalid Anime ID")
            return
        }

        fetchAnimeDetails(animeId)
    }

    private fun fetchAnimeDetails(animeId: Int) {
        val repository = Repository(this)
        repository.fetchAnimeDetails(
            animeId,
            onSuccess = { anime -> updateUI(anime) },
            onError = { showError(it) }
        )
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(anime: Anime) {
        Log.d("DetailsActivity", "Anime details: $anime")
        Log.d("DetailsActivity", "Anime embed URL: ${anime.embed_url}")

        animeTitle.text = anime.title_english
        animeSynopsis.text = anime.synopsis.ifEmpty { "Synopsis not available" }
        animeGenres.text = "Genres: ${anime.genres.joinToString(", ") { it.name }}"
        animeEpisodes.text = "Episodes: ${anime.episodes.takeIf { it > 0 } ?: "Unknown"}"
        animeRating.text = "Rating: ${anime.score.takeIf { it > 0 } ?: "N/A"}"

        animeMainCast.text = "Main Cast: Data not available in current API"

        if (anime.embed_url == "null") {
            setupPoster(anime.image_url)
        } else {
            setupYouTubeIFrame(anime.embed_url)
        }
    }


    private fun setupPoster(imageUrl: String) {
        Log.d("DetailsActivity", "Setting up poster with image URL: $imageUrl")
        youtubeWebView.visibility = View.GONE
        animePoster.visibility = View.VISIBLE
        Picasso.get().load(imageUrl).into(animePoster)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupYouTubeIFrame(embed_url: String) {
        animePoster.visibility = View.GONE
        youtubeWebView.visibility = View.VISIBLE

        youtubeWebView.settings.javaScriptEnabled = true
        youtubeWebView.settings.pluginState = WebSettings.PluginState.ON
        youtubeWebView.webViewClient = WebViewClient()

        val embedHtml = """
            <html>
                <body style="margin:0;">
                    <iframe 
                        width="100%" 
                        height="100%" 
                        src= $embed_url;
                        frameborder="5" 
                        allow="autoplay; encrypted-media" 
                        allowfullscreen>
                    </iframe>
                </body>
            </html>
        """
        youtubeWebView.loadData(embedHtml, "text/html", "utf-8")
        Log.d("DetailsActivity", "Loaded YouTube video ID: $embed_url")
    }

    private fun showError(message: String) {
        animeTitle.text = "Error"
        animeSynopsis.text = message
    }
}
