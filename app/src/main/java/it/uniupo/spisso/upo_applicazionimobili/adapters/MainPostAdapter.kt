package it.uniupo.spisso.upo_applicazionimobili.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import it.uniupo.spisso.upo_applicazionimobili.R
import it.uniupo.spisso.upo_applicazionimobili.models.PostModel

class MainPostAdapter(private val context: Context, private val posts: ArrayList<PostModel>) : BaseAdapter()
{
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View
    {
        var toreturnView: View

        if (convertView == null)
        {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            toreturnView = inflater.inflate(R.layout.postitem_layout, null, true)
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