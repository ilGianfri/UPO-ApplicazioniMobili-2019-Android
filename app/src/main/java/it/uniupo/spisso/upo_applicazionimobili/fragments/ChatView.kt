package it.uniupo.spisso.upo_applicazionimobili.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import it.uniupo.spisso.upo_applicazionimobili.R
import it.uniupo.spisso.upo_applicazionimobili.adapters.MessagesAdapter
import it.uniupo.spisso.upo_applicazionimobili.models.BaseMessage
import java.text.SimpleDateFormat
import java.util.*


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

        view.findViewById<EditText>(R.id.enter_message).setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND)
            {
                sendMessage()
                return@OnEditorActionListener true
            }
            false
        })

        view.findViewById<TextInputLayout>(R.id.message_layout).setEndIconOnClickListener{ sendMessage() }

        return view
    }

    private fun fetchMessages() {
        val collection = db.collection("chats").document(chatID).collection("messages")
            .orderBy("dateTime", Query.Direction.ASCENDING)

        collection.addSnapshotListener { snapshots, e ->
            if (snapshots != null && e == null)
            {
                for (dc in snapshots!!.documentChanges)
                {
                    when (dc.type)
                    {
                        DocumentChange.Type.ADDED ->
                        {
                            val textMessage = BaseMessage(dc.document.id)
                            textMessage.message = dc.document.getString("message") as String
                            textMessage.senderId = dc.document.getString("senderId") as String
                            textMessage.receiverId = dc.document.getString("receiverId") as String

                            messagesAdapter.appendMessage(textMessage)
                        }
                    }
                }
            }
        }
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
            "dateTime" to SimpleDateFormat("yyyyMMdd_HH:mm:ss").format(Date())
        )

        db.collection("chats").document(chatID).collection("messages").document(textMessage.id)
            .set(data as Map<String, Any>)
            .addOnSuccessListener {

                //messagesAdapter.appendMessage(textMessage)
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