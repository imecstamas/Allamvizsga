package com.allamvizsga.tamas.model

data class Walk(val id: String? = null, val title: String, val description: String, val imageUrl: String, val stations: List<Station>? = null)