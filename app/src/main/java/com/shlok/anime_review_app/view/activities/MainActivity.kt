package com.shlok.anime_review_app.view.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shlok.anime_review_app.R
import com.shlok.anime_review_app.model.Repository
import com.shlok.anime_review_app.view.adapters.AnimeAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var animeRecyclerView: RecyclerView
    private val repository by lazy { Repository(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        animeRecyclerView = findViewById(R.id.animeRecyclerView)
        animeRecyclerView.layoutManager = LinearLayoutManager(this)

        repository.fetchAnimeList(
            onSuccess = { animeList ->
                animeRecyclerView.adapter = AnimeAdapter(animeList) { anime ->
                    val intent = DetailsActivity.newIntent(this, anime.mal_id)
                    startActivity(intent)
                }
            },
            onError = { errorMessage ->
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        )
    }
}
