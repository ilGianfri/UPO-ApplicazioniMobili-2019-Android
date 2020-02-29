package it.uniupo.spisso.upo_applicazionimobili.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
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
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View
//    {
//        var view: View
//
//        if (convertView == null)
//        {
//            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//            view = inflater.inflate(R.layout.postitem_layout, null, true)
//
//            val imageView = view.findViewById<ImageView>(R.id.post_image)
//            val titleView = view.findViewById<TextView>(R.id.post_title)
////            var locationView = view.findViewById<TextView>(R.id.post_location)
//
////            var priceView = toReturnView.findViewById<TextView>(R.id.post_price)
//
//            var postedOnView = view.findViewById<TextView>(R.id.post_published_on)
//            //var postedByView = toReturnView.findViewById<TextView>(R.id.post_postedby)
//
//            doAsync {
//                val url = URL(posts[position].imageUri)
//                val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
//                uiThread {
//                    imageView.setImageBitmap(bmp)
//                }
//            }
//
//            titleView.text =  posts[position].title
//            //locationView.text = posts[position].locationName
//            //priceView.text = posts[position].price.toString() + "€"
//
//            //postedByView.text = context.getString(R.string.posted_by_text) + " " + posts[position].userSelectedDisplayName
//
//            val parser = SimpleDateFormat("yyyyMMdd_HH:mm:ss")
//            val formatter = SimpleDateFormat("dd/MM")
//            postedOnView.text = formatter.format(parser.parse(posts[position].postedOn))
//        }
//        else
//            view = convertView
//
//        return view
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainPostAdapter.PostViewHolder
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