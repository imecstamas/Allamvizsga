package com.allamvizsga.tamas.model

data class Station(
        val id: String? = null,
        val title: String,
        val coordinate: Coordinate,
        val description: String,
        val imageUrl: String,
        val audioUrl: String
)