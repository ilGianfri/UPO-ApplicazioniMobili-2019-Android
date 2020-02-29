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
import it.uniupo.spisso.upo_applicazionimobili.models.PostModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL
import java.text.SimpleDateFormat


class MainPostAdapter(private val context: Context, private val posts: ArrayList<PostModel>) : RecyclerView.Adapter<MainPostAdapter.PostViewHolder>()
{
    var onItemClick: ((PostModel) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.postitem_layout, parent, false)
        return PostViewHolder(view)
    }

    override fun getItemCount(): Int
    {
        return posts.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int)
    {
        holder.bind(posts[position])
    }

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        private val imageView = itemView.findViewById<ImageView>(R.id.post_image)
        private val titleView = itemView.findViewById<TextView>(R.id.post_title)
        private var postedOnView = itemView.findViewById<TextView>(R.id.post_published_on)

        init
        {
            itemView.setOnClickListener {
                onItemClick?.invoke(posts[adapterPosition])
            }
        }

        fun bind(post: PostModel)
        {
            doAsync {
                val url = URL(post.imageUri)
                val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                uiThread { imageView.setImageBitmap(bmp) }
            }

            titleView.text = post.title
            val parser = SimpleDateFormat("yyyyMMdd_HH:mm:ss")
            val formatter = SimpleDateFormat("dd/MM")
            postedOnView.text = formatter.format(parser.parse(post.postedOn))
        }
    }
}