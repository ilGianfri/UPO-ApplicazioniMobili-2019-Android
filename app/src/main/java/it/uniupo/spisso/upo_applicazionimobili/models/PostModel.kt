package it.uniupo.spisso.upo_applicazionimobili.models

data class PostModel(val id: String)
{
    lateinit var title: String
    lateinit var description: String
    lateinit var postedOn: String
    lateinit var coordinates : ArrayList<Double>
    lateinit var userId : String
    lateinit var imageUri : String
}