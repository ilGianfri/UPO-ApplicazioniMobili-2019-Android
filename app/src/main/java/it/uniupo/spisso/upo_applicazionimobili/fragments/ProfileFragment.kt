package it.uniupo.spisso.upo_applicazionimobili.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import it.uniupo.spisso.upo_applicazionimobili.R
import it.uniupo.spisso.upo_applicazionimobili.adapters.MainPostAdapter
import it.uniupo.spisso.upo_applicazionimobili.models.PostModel
import java.lang.Exception

/**
 * Fragment used to display the ads published by the current user
 */
class ProfileFragment : Fragment()
{
    private val db = FirebaseFirestore.getInstance()
    private var postsList : RecyclerView? = null
    private var posts : ArrayList<PostModel> = ArrayList()
    private lateinit var dbName : String
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        dbName = getString(R.string.items_db_name)
        // Inflate the layout for this fragment
        var view : View? = inflater.inflate(R.layout.fragment_profile, container, false)

        postsList = view?.findViewById(R.id.myposts_list)
        val layoutMgr = LinearLayoutManager(requireContext())
        layoutMgr.stackFromEnd = true
        postsList?.layoutManager = layoutMgr

        populatePostsList()

        return view
    }

    /**
     * Loads all the posts from the db owned by the current user
     */
    private fun populatePostsList()
    {
        val posts = arrayListOf<PostModel>()

        db.collection(dbName).whereEqualTo("UserId", auth.currentUser?.uid.toString()).get().addOnCompleteListener { task ->
            if (task.isSuccessful)
            {
                for (item in task.result!!.documents)
                {
                    val model = PostModel(item.id)
                    model.title = item.get("Title") as String
                    model.description = item.get("Description") as String
                    //model.locationName = item.get("LocationName") as String
                    //model.price = item.get("Price") as Long
                    model.postedOn = item.get("PostedOn") as String
                    model.userSelectedDisplayName = item.get("UserSelectedDisplayName") as String
                    model.userId = item.get("UserId") as String
                    model.imageUri = item.get("ImageUri") as String
                    posts.add(model)
                }

                val postsAdapter = MainPostAdapter(requireContext(), posts)
                postsList?.adapter = postsAdapter

                postsAdapter?.onItemClick = { post ->
                    val selectedItem = post
                    val bundle = Bundle()
                    bundle.putString("postId", selectedItem.id)

                    val detailsView = DetailsViewFragment()
                    detailsView.arguments = bundle
                    val transaction = fragmentManager?.beginTransaction()
                    transaction?.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    transaction?.replace(R.id.container, detailsView)
                    transaction?.addToBackStack(null)
                    transaction?.commit()
                }
            }
        }.addOnFailureListener {  exception ->
            Toast.makeText(activity?.baseContext, exception.localizedMessage,
                Toast.LENGTH_SHORT).show()
        }
    }
}