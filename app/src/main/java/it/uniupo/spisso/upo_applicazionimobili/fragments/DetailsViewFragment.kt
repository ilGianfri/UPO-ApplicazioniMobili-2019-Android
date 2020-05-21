package it.uniupo.spisso.upo_applicazionimobili.fragments

import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.location.LocationServices
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import it.uniupo.spisso.upo_applicazionimobili.R
import it.uniupo.spisso.upo_applicazionimobili.models.ConversationModel
import it.uniupo.spisso.upo_applicazionimobili.models.PostModel
import kotlinx.android.synthetic.main.fragment_details_view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

/**
 * Displays the details of a selected item
 */
class DetailsViewFragment : Fragment()
{
    private var itemID: String? = null
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var dbName : String
    private lateinit var currentItem : PostModel

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        itemID = arguments?.getString("postId")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_details_view, container, false)
        dbName = getString(R.string.items_db_name)

        getItemDetails(itemID.toString())

        view.findViewById<MaterialButton>(R.id.delete_post).setOnClickListener {
            if (currentItem.userId == auth.currentUser?.uid.toString())
            {
                db.collection("available_items").document(currentItem.id).delete().addOnSuccessListener { activity?.onBackPressed() }
            }
        }

        view.findViewById<MaterialButton>(R.id.messageUserBtn).setOnClickListener {
            if (auth.currentUser?.uid.toString() == currentItem.userId)
            {
                Toast.makeText(requireContext(), R.string.chat_yourself_no, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val data = hashMapOf(
                "itemId" to currentItem.id,
                "itemImage" to currentItem.imageUri,
                "itemTitle" to currentItem.title,
                "users" to arrayListOf<String>(auth.currentUser?.uid.toString(), currentItem.userId)
            )

            val id = UUID.randomUUID().toString()

            //TODO
            db.collection("chats").whereEqualTo("itemId", currentItem.id).get().addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    //No results
                    if (task.result!!.isEmpty)
                        return@addOnCompleteListener


                }
            }

            db.collection("chats").document(id)
                .set(data as Map<String, Any>)
                .addOnSuccessListener {
                    val bundle = Bundle()
                    bundle.putString("chatId", id)
                    bundle.putStringArrayList("users", arrayListOf<String>(auth.currentUser?.uid.toString(), currentItem.userId))

                    val detailsView = ChatView()
                    detailsView.arguments = bundle
                    val transaction = fragmentManager?.beginTransaction()
                    transaction?.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    transaction?.replace(R.id.container, detailsView)
                    transaction?.addToBackStack(null)
                    transaction?.commit()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_SHORT)
                        .show()
                }
        }

        // Inflate the layout for this fragment
        return view
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
//                currentItem.userSelectedDisplayName = item.get("UserSelectedDisplayName") as String
                currentItem.imageUri = item.get("ImageUri") as String
                currentItem.coordinates = item.get("Coordinates") as ArrayList<Double>
                currentItem.userId = item.get("UserId") as String

                doAsync {
                    val url = URL(currentItem.imageUri)
                    val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    uiThread {
                        post_image.setImageBitmap(bmp)
                        post_title.text = currentItem.title
                        post_description.text = currentItem.description
                    }
                }

                var deletebtn = view?.findViewById<MaterialButton>(R.id.delete_post)
                if (auth.currentUser?.uid.toString() != currentItem.userId)
                    deletebtn?.visibility = View.INVISIBLE

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
