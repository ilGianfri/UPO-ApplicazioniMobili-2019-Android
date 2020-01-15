package it.uniupo.spisso.upo_applicazionimobili.models

import java.util.*

data class PostModel(val id: String)
{
    lateinit var title: String
    lateinit var description: String
    var category: Int = 0
    lateinit var postedOn: String
    //var price: Long = 0
    lateinit var locationName: String
    lateinit var userSelectedDisplayName : String
    lateinit var imageUri : String
}