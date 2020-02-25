package it.uniupo.spisso.upo_applicazionimobili.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

import it.uniupo.spisso.upo_applicazionimobili.R
import it.uniupo.spisso.upo_applicazionimobili.adapters.MessagesAdapter
import it.uniupo.spisso.upo_applicazionimobili.models.BaseMessage
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatView : Fragment()
{
    private lateinit var messagesAdapter: MessagesAdapter
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var messagesList: RecyclerView
    private lateinit var chatID : String //chat document ID
    private lateinit var receiverId : String

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        chatID = arguments?.getString("chatId").toString()
        val users = arguments?.getStringArrayList("users")
        users?.remove(auth.currentUser?.uid.toString())
        receiverId = users!!.first()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        var view =  inflater.inflate(R.layout.fragment_chat_view, container, false)

        messagesList = view.findViewById(R.id.messages)
        val layoutMgr = LinearLayoutManager(requireContext())
        layoutMgr.stackFromEnd = true
        messagesList.layoutManager = layoutMgr

        messagesAdapter = MessagesAdapter(auth.currentUser?.uid.toString(), mutableListOf())
        messagesList.adapter = messagesAdapter

        fetchMessages();

        val sendBtn = view.findViewById<Button>(R.id.send_message)
        sendBtn.setOnClickListener { sendMessage() }

        return view
    }

    private fun fetchMessages()
    {
        db.collection("chats").document(chatID).collection("messages").orderBy("dateTime", Query.Direction.ASCENDING).get().addOnCompleteListener { task ->
            if (task.isSuccessful)
            {
                //No results = no chats
                if (task.result!!.isEmpty)
                    return@addOnCompleteListener

                val messages = arrayListOf<BaseMessage>()
                for (item in task.result!!.documents)
                {
                    val textMessage = BaseMessage(item.id)
                    textMessage.message = item.getString("message") as String
                    textMessage.senderId = item.getString("senderId") as String
                    textMessage.receiverId = item.getString("receiverId") as String

                    messagesAdapter.appendMessage(textMessage)
                }
            }
        }.addOnFailureListener {  exception -> Toast.makeText(activity?.baseContext, exception.localizedMessage, Toast.LENGTH_SHORT).show() }
    }

    private fun sendMessage()
    {
        //Create a new message with a random identifier
        val textMessage = BaseMessage(UUID.randomUUID().toString())
        textMessage.message = view?.findViewById<EditText>(R.id.enter_message)?.text.toString()
        textMessage.senderId = auth.currentUser?.uid.toString()
        textMessage.receiverId = receiverId

        val data = hashMapOf(
            "message" to textMessage.message,
            "senderId" to textMessage.senderId,
            "receiverId" to textMessage.receiverId,
            "dateTime" to SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        )

        db.collection("chats").document(chatID).collection("messages").document(textMessage.id)
            .set(data as Map<String, Any>)
            .addOnSuccessListener {

                messagesAdapter.appendMessage(textMessage)
                scrollToBottom()
                view?.findViewById<EditText>(R.id.enter_message)?.text?.clear()
            }
            .addOnFailureListener { exception -> Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_SHORT).show() }
    }

    private fun scrollToBottom()
    {
        messagesList.scrollToPosition(messagesAdapter.itemCount - 1)
    }
}