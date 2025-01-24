package com.shlok.anime_review_app.model

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class Repository(private val context: Context) {
    private val baseUrl = "https://api.jikan.moe/v4"

    fun fetchAnimeList(onSuccess: (List<Anime>) -> Unit, onError: (String) -> Unit) {
        val requestQueue = Volley.newRequestQueue(context)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, "$baseUrl/top/anime", null,
            { response ->
                try {
                    val data = response.getJSONArray("data")
                    val animeList = (0 until data.length()).map { i ->
                        val animeJson = data.getJSONObject(i)
                        val embedUrl = extractEmbedUrl(animeJson)
                        val imageUrl = animeJson.getJSONObject("images").getJSONObject("jpg").getString("image_url")


                        Anime(
                            mal_id = animeJson.getInt("mal_id"),
                            title_english = animeJson.getString("title_english"),
                            title_japanese = animeJson.getString("title_japanese"),
                            image_url = imageUrl,
                            score = animeJson.getDouble("score"),
                            episodes = animeJson.optInt("episodes", 0),
                            embed_url = embedUrl,
                            synopsis = animeJson.optString("synopsis", ""),
                            genres = parseGenres(animeJson.getJSONArray("genres")),
                            youtube_id = animeJson.optString("youtube_id", ""),
                        )


                    }
                    onSuccess(animeList)
                } catch (e: JSONException) {
                    onError("Parsing error")
                }
            },
            { error ->
                onError(error.message ?: "Network error")
            }
        )
        requestQueue.add(jsonObjectRequest)
    }

    private fun extractEmbedUrl(animeJson: JSONObject): String {
        return try {
            val embedUrl = animeJson.getJSONObject("trailer").optString("embed_url", "")
            if (embedUrl.isEmpty()) {
            }
            embedUrl
        } catch (e: JSONException) {
            ""
        }
    }

    fun fetchAnimeDetails(
        animeId: Int,
        onSuccess: (Anime) -> Unit,
        onError: (String) -> Unit
    ) {
        val requestQueue = Volley.newRequestQueue(context)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, "$baseUrl/anime/$animeId", null,
            { response ->
                try {
                    val data = response.getJSONObject("data")
                    val embedUrl = data.getJSONObject("trailer").optString("embed_url", "")

                    val anime = Anime(
                        mal_id = data.getInt("mal_id"),
                        title_english = data.optString("title_english", "N/A"),
                        title_japanese = data.optString("title_english", ""),
                        image_url = data.getJSONObject("images").getJSONObject("jpg").getString("image_url"),
                        score = data.optDouble("score", 0.0),
                        episodes = data.optInt("episodes", 0),
                        embed_url = embedUrl,
                        synopsis = data.optString("synopsis", "N/A"),
                        genres = parseGenres(data.getJSONArray("genres")),
                        youtube_id = data.optString("youtube_id", "N/A"),
                    )
                    onSuccess(anime)
                    Log.d("DetailsActivity", "Embed URL: ${anime.embed_url}, Image URL: ${anime.image_url}")
                } catch (e: JSONException) {
                    onError("Parsing error")
                }
            },
            { error ->
                onError(error.message ?: "Network error")
            }
        )
        requestQueue.add(jsonObjectRequest)
    }

    private fun parseGenres(genresArray: JSONArray): List<Genre> {
        return (0 until genresArray.length()).map { i ->
            val genreJson = genresArray.getJSONObject(i)
            Genre(
                mal_id = genreJson.getInt("mal_id"),
                type = genreJson.getString("type"),
                name = genreJson.getString("name"),
                url = genreJson.getString("url")
            )
        }
    }
}
