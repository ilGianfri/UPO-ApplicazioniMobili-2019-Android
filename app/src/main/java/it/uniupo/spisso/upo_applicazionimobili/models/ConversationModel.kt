package it.uniupo.spisso.upo_applicazionimobili.models

data class ConversationModel(val id: String)
{
    lateinit var imageUri : String
    lateinit var dateTime : String
    lateinit var title : String
    lateinit var latestMessage : String
    lateinit var users : ArrayList<String>
}