package it.uniupo.spisso.upo_applicazionimobili.fragments


import android.location.Location
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.spisso.upo_applicazionimobili.R
import it.uniupo.spisso.upo_applicazionimobili.adapters.MainPostAdapter
import it.uniupo.spisso.upo_applicazionimobili.models.PostModel
import kotlinx.android.synthetic.main.fragment_search.*


/**
 * Fragment that contains the homepage of the app
 */
class SearchFragment : Fragment()
{
    private val db = FirebaseFirestore.getInstance()
    private var postsList : RecyclerView? = null
    private var posts : ArrayList<PostModel> = ArrayList()
    private lateinit var dbName : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        dbName = getString(R.string.items_db_name)
        // Inflate the layout for this fragment

        var view : View? = inflater.inflate(R.layout.fragment_search, container, false)

        postsList = view?.findViewById(R.id.posts_list)
        val layoutMgr = LinearLayoutManager(requireContext())
        layoutMgr.stackFromEnd = true
        postsList?.layoutManager = layoutMgr

        search()

        var distanceStrings = arrayOf(getString(R.string.any_distance), "10 km", "20 km", "50 km")
        val spinnerAdapter = ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, distanceStrings)
        view?.findViewById<Spinner>(R.id.distance)?.adapter = spinnerAdapter

        //Handle distance changes
        val distanceSpinner = view?.findViewById<Spinner>(R.id.distance)

        distanceSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
            {
                search(searchBox?.text.toString(), position)
            }

        }

        //Handle floating button click (new post)
        view?.findViewById<FloatingActionButton>(R.id.publishButton)?.setOnClickListener{
            val publishFragment = PublishFragment()
            val transaction = activity!!.supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.animator.enter_from_bottom, android.R.animator.fade_out)
            transaction.replace(R.id.container, publishFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        //Handle map button click
        view?.findViewById<Button>(R.id.mapview_btn)?.setOnClickListener{
            val fragment = MapViewFragment()
            val fragmentManager = activity!!.supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.container, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        //Handle search button click
        val search = view?.findViewById<TextInputLayout>(R.id.searchBoxLayout)

        search?.setEndIconOnClickListener{
            search(searchBox.text.toString(),distanceSpinner!!.selectedItemPosition)
        }

        val searchB = view?.findViewById<TextInputEditText>(R.id.searchBox)
        searchB?.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP)
                {
                    search(searchBox.text.toString(),distanceSpinner!!.selectedItemPosition)
                    return@OnKeyListener true
                }
                false
            })

        return view
    }

    /**
     * Performs a search
     */
    private fun search(text : String = "", searchRadious : Int = 0)
    {
        populatePostsList(text, searchRadious)
    }

    /**
     * Loads all the posts from the db
     */
    private fun populatePostsList(text : String, searchRadius : Int)
    {
        this.posts.clear()

        var currentLocation : Location? = null
        val posts = arrayListOf<PostModel>()

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            // Got last known location
            if (location != null)
                currentLocation = location
        }

        db.collection(dbName).whereArrayContains("Keywords", text.toLowerCase()).get().addOnCompleteListener { task ->
            if (task.isSuccessful)
            {
                loop@ for (item in task.result!!.documents)
                {
                    val model = PostModel(item.id)
                    model.title = item.get("Title") as String
                    model.description = item.get("Description") as String
                    model.postedOn = item.get("PostedOn") as String
                    model.userSelectedDisplayName = item.get("UserSelectedDisplayName") as String
                    model.imageUri = item.get("ImageUri") as String
                    model.coordinates = item.get("Coordinates") as ArrayList<Double>

                    //If 0 = any distance
                    if (searchRadius != 0 && currentLocation != null)
                    {
                        val itemLoc = Location("")
                        itemLoc.latitude = model.coordinates[0]
                        itemLoc.longitude = model.coordinates[1]

                        when (searchRadius)
                        {
                            1 -> //10km
                            {
                                if (currentLocation!!.distanceTo(itemLoc) > 10000)
                                    continue@loop
                            }
                            2 -> //20km
                            {
                                if (currentLocation!!.distanceTo(itemLoc) > 20000)
                                    continue@loop
                            }
                            3 -> //50km
                            {
                                if (currentLocation!!.distanceTo(itemLoc) > 50000)
                                    continue@loop
                            }
                        }
                    }

                    posts.add(model)
                }

                val postsAdapter = MainPostAdapter(requireContext(), posts)
                postsList?.adapter = postsAdapter

                postsAdapter?.onItemClick = { post ->
                    val selectedItem = post as PostModel
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