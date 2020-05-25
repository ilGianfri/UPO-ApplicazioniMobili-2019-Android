package it.uniupo.spisso.upo_applicazionimobili.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.uniupo.spisso.upo_applicazionimobili.R
import it.uniupo.spisso.upo_applicazionimobili.models.BaseMessage

class MessagesAdapter(private val uid: String, private var messages: MutableList<BaseMessage>)  : RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>()
{
    companion object
    {
        private const val SENT = 0
        private const val RECEIVED = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = when (viewType) {
            SENT -> {
                LayoutInflater.from(parent.context).inflate(R.layout.message_sent, parent, false)
            }
            else -> {
                LayoutInflater.from(parent.context).inflate(R.layout.message_received, parent, false)
            }
        }
        return MessageViewHolder(view)
    }

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int)
    {
        holder.bind(messages[position])
    }

    override fun getItemViewType(position: Int): Int
    {
        return if (messages[position].senderId.contentEquals(uid))
        {
            SENT
        } else
        {
            RECEIVED
        }
    }

    fun updateMessages(messages: List<BaseMessage>)
    {
        this.messages = messages.toMutableList()
        notifyDataSetChanged()
    }

    fun appendMessage(message: BaseMessage)
    {
        this.messages.add(message)
        notifyItemInserted(this.messages.size - 1)
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        private val messageText: TextView = itemView.findViewById(R.id.message_text)

        fun bind(message: BaseMessage)
        {
            messageText.text = message.message
        }
    }
}