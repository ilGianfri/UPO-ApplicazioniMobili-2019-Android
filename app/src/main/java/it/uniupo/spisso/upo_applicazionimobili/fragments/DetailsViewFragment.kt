package it.uniupo.spisso.upo_applicazionimobili.fragments

import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore

import it.uniupo.spisso.upo_applicazionimobili.R
import it.uniupo.spisso.upo_applicazionimobili.models.PostModel
import kotlinx.android.synthetic.main.fragment_details_view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL
import kotlin.math.roundToInt

/**
 * Displays the details of a selected item
 */
class DetailsViewFragment : Fragment()
{
    private var itemID: String? = null
    private val db = FirebaseFirestore.getInstance()
    private var dbName : String = "available_items"
    private lateinit var currentItem : PostModel

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        itemID = arguments?.getString("postId")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment

        getItemDetails(itemID.toString())
        return inflater.inflate(R.layout.fragment_details_view, container, false)
    }

    private fun getItemDetails(itemId : String)
    {
        db.collection(dbName).document(itemId).get().addOnCompleteListener { task ->
            if (task.isSuccessful)
            {
                val item = task.result
                currentItem = PostModel(item!!.id)
                currentItem.title = item.get("Title") as String
                currentItem.description = item.get("Description") as String
                currentItem.postedOn = item.get("PostedOn") as String
                currentItem.userSelectedDisplayName = item.get("UserSelectedDisplayName") as String
                currentItem.imageUri = item.get("ImageUri") as String
                currentItem.coordinates = item.get("Coordinates") as ArrayList<Double>

                doAsync {
                    val url = URL(currentItem.imageUri)
                    val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    uiThread {
                        post_image.setImageBitmap(bmp)
                        post_title.text = currentItem.title
                        post_description.text = currentItem.description
                    }
                }

                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    // Got last known location
                    if (location != null)
                    {
                        var currentLocation = location
                        val itemLoc = Location("")
                        itemLoc.latitude = currentItem.coordinates[0]
                        itemLoc.longitude = currentItem.coordinates[1]
                        post_distance.text = getString(R.string.details_distance) + (currentLocation?.distanceTo(itemLoc)/1000).roundToInt() + " km"
                    }
                }
            }
        }.addOnFailureListener {  exception ->
            Toast.makeText(activity?.baseContext, exception.localizedMessage,
                Toast.LENGTH_SHORT).show()
        }
    }
}
