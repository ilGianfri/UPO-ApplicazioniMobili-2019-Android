package it.uniupo.spisso.upo_applicazionimobili.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import it.uniupo.spisso.upo_applicazionimobili.R
import it.uniupo.spisso.upo_applicazionimobili.models.ConversationModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL


class ChatsListAdapter(private val context: Context, private val conversations: ArrayList<ConversationModel>) : BaseAdapter()
{
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View
    {
        var view: View

        if (convertView == null)
        {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.chat_list_item_layout, null, true)

            val imageView = view.findViewById<ImageView>(R.id.post_image)
            val titleView = view.findViewById<TextView>(R.id.post_title)

            doAsync {
                val url = URL(conversations[position].imageUri)
                val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                uiThread {
                    imageView.setImageBitmap(bmp)
                }
            }

            titleView.text =  conversations[position].title

//            val parser = SimpleDateFormat("yyyyMMdd_HHmmss")
//            val formatter = SimpleDateFormat("dd/MM")
//            postedOnView.text = formatter.format(parser.parse(posts[position].postedOn))
        }
        else
            view = convertView

        return view
    }

    override fun getItem(position: Int): Any
    {
        return conversations[position]
    }

    override fun getItemId(position: Int): Long
    {
        return 0
    }

    override fun getCount(): Int
    {
        return conversations.size
    }

}