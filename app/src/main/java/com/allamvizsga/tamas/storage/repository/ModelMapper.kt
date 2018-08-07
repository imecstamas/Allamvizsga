package com.allamvizsga.tamas.storage.repository

import com.allamvizsga.tamas.model.*
import com.google.firebase.database.DataSnapshot


class WalkDTO(val title: String, val description: String, val imageUrl: String)

/**
 * Creates a [Walk] from a [DataSnapshot]
 */
fun mapToWalk(dataSnapShot: DataSnapshot): Walk {
    val resultMap: Map<*, *> = dataSnapShot.value as Map<*, *>
    return Walk(
            dataSnapShot.key,
            resultMap["title"] as String,
            resultMap["description"] as String,
            resultMap["imageUrl"] as String
    )
}

/**
 * Creates a [Station] from a [DataSnapshot]
 */
fun mapToStation(dataSnapShot: DataSnapshot): Station {
    val resultMap: Map<*, *> = dataSnapShot.value as Map<*, *>
    val coordinate = resultMap["coordinate"] as Map<*, *>
    val question = resultMap["question"] as Map<*, *>
    val answerList = mutableListOf<Answer>()
    val answers = question["answers"] as List<Map<*, *>>
    answers.forEach { answerMap ->
        answerList.add(Answer(answerMap["text"] as String, answerMap["correct"] as Boolean))
    }
    return Station(
            dataSnapShot.key,
            resultMap["title"] as String,
            Coordinate(coordinate["latitude"] as Double, coordinate["longitude"] as Double),
            resultMap["description"] as String,
            resultMap["imageUrl"] as String,
            resultMap["audioUrl"] as String,
            Question(question["text"] as String, answerList)
    )
}

/**
 * Creates a [WalkDTO] from a [Walk]
 */
fun mapEntityToDto(walk: Walk) = WalkDTO(walk.title, walk.description, walk.imageUrl)
