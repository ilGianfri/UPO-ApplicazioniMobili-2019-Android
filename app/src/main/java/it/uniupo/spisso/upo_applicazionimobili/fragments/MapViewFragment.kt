package it.uniupo.spisso.upo_applicazionimobili.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.spisso.upo_applicazionimobili.R
import it.uniupo.spisso.upo_applicazionimobili.adapters.MainPostAdapter
import it.uniupo.spisso.upo_applicazionimobili.models.PostModel


/**
 * Displays all the items on a map
 */
class MapViewFragment : Fragment()
{
    private lateinit var mapV : MapView
    private lateinit var googleMap : GoogleMap
    private val db = FirebaseFirestore.getInstance()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val view = inflater.inflate(R.layout.fragment_map_view, container, false)

        mapV = view.findViewById(R.id.mapView)
        mapV.onCreate(savedInstanceState)
        mapV.onResume()

        try
        {
            MapsInitializer.initialize(activity?.applicationContext)
        }
        catch (e : Exception) {}

        mapV.getMapAsync { mMap ->
            googleMap = mMap
            // For showing a move to my location button
            googleMap.setMyLocationEnabled(true)

            populateMap()


            // For dropping a marker at a point on the Map
            //val sydney = LatLng(-34, 151)
            //googleMap.addMarker(MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"))
            // For zooming automatically to the location of the marker
            //val cameraPosition =
            //    CameraPosition.Builder().target(sydney).zoom(12f).build()
            //googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }



        return view
    }

    private fun populateMap()
    {
        //val posts = arrayListOf<PostModel>()

        db.collection(getString(R.string.items_db_name)).get().addOnCompleteListener { task ->
            if (task.isSuccessful)
            {
                for (item in task.result!!.documents)
                {
                    val model = PostModel(item.id)
                    model.title = item.get("Title") as String
                    model.description = item.get("Description") as String
                    model.coordinates = item.get("Coordinates") as ArrayList<Double>

                    val sydney = LatLng(model.coordinates[0], model.coordinates[1])

                    googleMap.addMarker(MarkerOptions().position(sydney).title(model.title).snippet(model.description))
                }
            }
        }.addOnFailureListener {  exception ->
            Toast.makeText(activity?.baseContext, exception.localizedMessage,
                Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume()
    {
        super.onResume()
        mapV.onResume()
    }
}
