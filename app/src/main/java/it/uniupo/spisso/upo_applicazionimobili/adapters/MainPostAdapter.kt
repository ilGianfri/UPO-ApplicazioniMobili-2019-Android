package it.uniupo.spisso.upo_applicazionimobili.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import it.uniupo.spisso.upo_applicazionimobili.R
import it.uniupo.spisso.upo_applicazionimobili.models.PostModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL
import java.text.SimpleDateFormat
import java.util.concurrent.Executors


class MainPostAdapter(private val context: Context, private val posts: ArrayList<PostModel>) : BaseAdapter()
{
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View
    {
        var toreturnView: View

        if (convertView == null)
        {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            toreturnView = inflater.inflate(R.layout.postitem_layout, null, true)

            val imageView = toreturnView.findViewById<ImageView>(R.id.post_image)
            val titleView = toreturnView.findViewById<TextView>(R.id.post_title)
            var locationView = toreturnView.findViewById<TextView>(R.id.post_location)

//            var priceView = toreturnView.findViewById<TextView>(R.id.post_price)

            var postedOnView = toreturnView.findViewById<TextView>(R.id.post_published_on)
            //var postedByView = toreturnView.findViewById<TextView>(R.id.post_postedby)

            doAsync {
                val url = URL(posts[position].imageUri)
                val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                uiThread {
                    imageView.setImageBitmap(bmp)
                }
            }

            titleView.text =  posts[position].title
            locationView.text = posts[position].locationName
//            priceView.text = posts[position].price.toString() + "€"

            //postedByView.text = context.getString(R.string.posted_by_text) + " " + posts[position].userSelectedDisplayName

            val parser = SimpleDateFormat("yyyyMMdd_HHmmss")
            val formatter = SimpleDateFormat("dd/MM")
            postedOnView.text = formatter.format(parser.parse(posts[position].postedOn))
        }
        else
            toreturnView = convertView

        return toreturnView
    }

    override fun getItem(position: Int): Any
    {
        return posts[position]
    }

    override fun getItemId(position: Int): Long
    {
        return 0
    }

    override fun getCount(): Int
    {
        return posts.size
    }

}