package it.uniupo.spisso.upo_applicazionimobili.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

import it.uniupo.spisso.upo_applicazionimobili.R
import it.uniupo.spisso.upo_applicazionimobili.adapters.MainPostAdapter
import it.uniupo.spisso.upo_applicazionimobili.models.PostModel
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

/**
 * Fragment that contains the homepage of the app
 */
class SearchFragment : Fragment()
{
    private val db = FirebaseFirestore.getInstance()
    private var postsList : ListView? = null
    private var posts : ArrayList<PostModel> = ArrayList()
    private var dbName : String = "available_items"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_search, container, false)

        postsList = view.findViewById(R.id.posts_list)

        populatePostsList(object : PostsCallback {
            override fun onCallback(value: ArrayList<PostModel>)
            {
                try {
                    posts = value
                    val postsAdapter = MainPostAdapter(requireContext(), posts)
                    postsList?.adapter = postsAdapter
                }
                catch (e: Exception){}

            }
        })

        return view
    }

    private fun populatePostsList(postsLoaded : PostsCallback)
    {
        val posts = arrayListOf<PostModel>()

        db.collection(dbName).get().addOnCompleteListener { task ->
            if (task.isSuccessful)
            {
                for (item in task.result!!.documents) {
                    val model = PostModel(item.id)
                    model.title = item.get("Title") as String
                    model.description = item.get("Description") as String
                    model.locationName = item.get("LocationName") as String
                    //model.price = item.get("Price") as Long
                    model.postedOn = item.get("PostedOn") as String
                    model.userSelectedDisplayName = item.get("UserSelectedDisplayName") as String
                    model.imageUri = item.get("ImageUri") as String
                    posts.add(model)
                }
                postsLoaded.onCallback(posts)
            }
        }.addOnFailureListener {  exception ->
            Toast.makeText(activity?.baseContext, exception.localizedMessage,
            Toast.LENGTH_SHORT).show()
        }

    }
}

interface PostsCallback {
    fun onCallback(value: ArrayList<PostModel>)
}
