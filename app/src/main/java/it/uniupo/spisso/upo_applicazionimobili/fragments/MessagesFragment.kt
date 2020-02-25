package it.uniupo.spisso.upo_applicazionimobili.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import it.uniupo.spisso.upo_applicazionimobili.R
import it.uniupo.spisso.upo_applicazionimobili.adapters.ChatsListAdapter
import it.uniupo.spisso.upo_applicazionimobili.models.BaseMessage
import it.uniupo.spisso.upo_applicazionimobili.models.ConversationModel

/**
 * Displays conversations for the current user
 */
class MessagesFragment : Fragment()
{
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var messagesList : ListView? = null
    private var conversations : ArrayList<ConversationModel> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        var view : View? = inflater.inflate(R.layout.fragment_messages, container, false)

        messagesList = view?.findViewById(R.id.messages_list)

        getChatsList(object : ConversationsCallBack
        {
            override fun onCallback(value: ArrayList<ConversationModel>)
            {
                try
                {
                    conversations = value
                    val conversationsAdapter = ChatsListAdapter(requireContext(), conversations)
                    messagesList?.adapter = conversationsAdapter
                }
                catch (e: Exception){}
            }
        })

        messagesList?.setOnItemClickListener  { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as ConversationModel
            val bundle = Bundle()
            bundle.putString("chatId", selectedItem.id)
            bundle.putStringArrayList("users", selectedItem.users)

            val detailsView = ChatView()
            detailsView.arguments = bundle
            val transaction = fragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            transaction?.replace(R.id.container, detailsView)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        return view
    }

    private fun getChatsList(messagesLoaded : ConversationsCallBack)
    {
        db.collection("chats").whereArrayContains("users", auth.uid.toString()).get().addOnCompleteListener { task ->
            if (task.isSuccessful)
            {
                //No results = no chats
                if (task.result!!.isEmpty)
                    return@addOnCompleteListener

                val conversations = arrayListOf<ConversationModel>()
                for (item in task.result!!.documents)
                {
                    var conversation = ConversationModel(item.id)
                    conversation.title = item.getString("itemTitle") as String
                    conversation.imageUri = item.getString("itemImage") as String
                    conversation.users = item.get("users") as ArrayList<String>
                    conversations.add(conversation)
                }

                messagesLoaded?.onCallback(conversations)
            }
        }.addOnFailureListener {  exception -> Toast.makeText(activity?.baseContext, exception.localizedMessage, Toast.LENGTH_SHORT).show() }
    }
}

/**
 * Callback used to know when all messages have been loaded
 */
interface ConversationsCallBack
{
    fun onCallback(value: ArrayList<ConversationModel>)
}