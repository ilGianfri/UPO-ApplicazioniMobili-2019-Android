package it.uniupo.spisso.upo_applicazionimobili.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.uniupo.spisso.upo_applicazionimobili.R
import it.uniupo.spisso.upo_applicazionimobili.models.ConversationModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL


class ChatsListAdapter(private val context: Context, private val conversations: ArrayList<ConversationModel>) : RecyclerView.Adapter<ChatsListAdapter.ChatsViewHolder>()
{
    var onItemClick: ((ConversationModel) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.postitem_layout, parent, false)
        return ChatsViewHolder(view)
    }

    override fun getItemCount(): Int
    {
        return conversations.size
    }

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int)
    {
        holder.bind(conversations[position])
    }

    inner class ChatsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        private val imageView = itemView.findViewById<ImageView>(R.id.post_image)
        private val titleView = itemView.findViewById<TextView>(R.id.post_title)

        init
        {
            itemView.setOnClickListener {
                onItemClick?.invoke(conversations[adapterPosition])
            }
        }

        fun bind(conversation: ConversationModel)
        {
            doAsync {
                val url = URL(conversation.imageUri)
                val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                uiThread { imageView.setImageBitmap(bmp) }
            }

            titleView.text =  conversation.title
        }
    }
}