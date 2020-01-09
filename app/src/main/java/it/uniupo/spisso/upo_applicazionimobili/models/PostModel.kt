package it.uniupo.spisso.upo_applicazionimobili.models

import java.util.*

data class PostModel(val id: String)
{
    lateinit var title: String
    lateinit var description: String
    lateinit var availabilityStart: Date
    lateinit var availabilityEnd: Date
    var category: Int = 0
    lateinit var expiresOn: Date
    val locationLat: Double = 0.0
    var locationLong: Double = 0.0
    lateinit var postedOn: Date
    var price: Double = 0.0
}