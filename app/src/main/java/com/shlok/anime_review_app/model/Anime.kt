package com.shlok.anime_review_app.model


data class Anime(
    val mal_id: Int,
    val title_english: String,
    val title_japanese: String,
    val image_url: String,
    val score: Double,
    val episodes: Int,
    val embed_url : String,
    val synopsis: String,
    val genres: List<Genre>,
    val youtube_id: String,


)

data class Genre(
    val mal_id: Int,
    val type: String,
    val name: String,
    val url: String
)
