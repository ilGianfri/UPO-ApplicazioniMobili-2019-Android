package it.uniupo.spisso.upo_applicazionimobili.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.spisso.upo_applicazionimobili.R
import it.uniupo.spisso.upo_applicazionimobili.models.PostModel


/**
 * Displays all the items on a map
 */
class MapViewFragment : Fragment()
{
    private lateinit var mapV : MapView
    private lateinit var googleMap : GoogleMap
    private val db = FirebaseFirestore.getInstance()
    private val LOCATION_REQUEST_CODE = 101

    /**
     * Requests to the system to get access to a specific permission
     */
    private fun requestPermission(permissionType: String, requestCode: Int)
    {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(permissionType), requestCode)
    }

    /**
     * Handle result from the permission dialog
     */
    override fun onRequestPermissionsResult(requestCode: Int,  permissions: Array<String>, grantResults: IntArray)
    {
        when (requestCode)
        {
            LOCATION_REQUEST_CODE ->
            {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(requireContext(), getString(R.string.missing_location), Toast.LENGTH_LONG).show()
                else
                {
                    val permission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)

                    if (permission == PackageManager.PERMISSION_GRANTED)
                    {
                        googleMap?.isMyLocationEnabled = true
                        showPositionOnMap()
                    }
                    else
                        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_REQUEST_CODE)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map_view, container, false)

        mapV = view.findViewById(R.id.mapView)
        mapV.onCreate(savedInstanceState)
        mapV.onResume()

        //Requests location permission if missing
        requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_REQUEST_CODE)

        try {
            MapsInitializer.initialize(activity?.applicationContext)
        } catch (e: Exception) {
        }


        mapV.getMapAsync { mMap ->
            googleMap = mMap
            // To show a move to my location button
            googleMap?.isMyLocationEnabled = false

            populateMap()

            showPositionOnMap()
        }

        return view
    }

    /**
     * Shows the current user location on the map (handles missing permission)
     */
    private fun showPositionOnMap()
    {
        val permissionCheck = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            googleMap?.isMyLocationEnabled = true

            val fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                // Got last known location and zooms to it
                if (location != null) {
                    val currentLocation = LatLng(location.latitude, location.longitude)
                    val cameraPosition =
                        CameraPosition.Builder().target(currentLocation).zoom(12f).build()
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                }
            }
        }
    }

    /**
     * Populates the map with the available items
     */
    private fun populateMap()
    {
        db.collection(getString(R.string.items_db_name)).get().addOnCompleteListener { task ->
            if (task.isSuccessful)
            {
                for (item in task.result!!.documents)
                {
                    val model = PostModel(item.id)
                    model.title = item.get("Title") as String
                    model.description = item.get("Description") as String
                    model.coordinates = item.get("Coordinates") as ArrayList<Double>

                    //Adds the marker to the map
                    val itemLocation = LatLng(model.coordinates[0], model.coordinates[1])
                    googleMap.addMarker(MarkerOptions().position(itemLocation).title(model.title).snippet(model.description))
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
