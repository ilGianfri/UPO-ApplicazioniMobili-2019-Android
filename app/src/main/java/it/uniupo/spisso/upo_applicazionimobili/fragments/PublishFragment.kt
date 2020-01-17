package it.uniupo.spisso.upo_applicazionimobili.fragments


import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import it.uniupo.spisso.upo_applicazionimobili.R
import kotlinx.android.synthetic.main.fragment_publish.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 * Fragment used to handle the creation of a new ad
 */
class PublishFragment : Fragment()
{
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var categoriesList : ArrayList<String> = ArrayList()
    private val PICK_IMAGE_REQUEST = 71
    private val PERMISSION_CODE = 1001
    private var imagePath: Uri? = null
    private var selectedCategory: Int = 0
    private var locationManager : LocationManager? = null
    private val LOCATION_REQUEST_CODE = 101

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_publish, container, false)

        val categoriesSpinner = view.findViewById<Spinner>(R.id.categories)
        categoriesSpinner.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) { }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategory = position
            }
        }

        loadCategories(Locale.getDefault().language, object : CategoriesCallback
        {
            override fun onCallback(value: ArrayList<String>)
            {
                categoriesList = value

                val spinnerAdapter = ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, categoriesList)
                categoriesSpinner.adapter = spinnerAdapter
            }
        })

        //Handle upload button click and check and request permission
        val buttonChooseImage = view.findViewById<Button>(R.id.btn_choose_image)
        buttonChooseImage.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_DENIED)
                {
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions, PERMISSION_CODE);
                }
                else
                    selectGalleryImage()
            }
            else selectGalleryImage()
        }

        val publishButton = view.findViewById<Button>(R.id.publishButton)
        publishButton.setOnClickListener { view ->
            publishClick()
        }

        cancel?.setOnClickListener{cancelButtonClick()}

        return view
    }

    /**
     * Loads categories for the spinner to display
     */
    private fun loadCategories(locale: String, categoriesLoaded : CategoriesCallback)
    {
        val categories = arrayListOf<String>()

        //Based on locale, loads different categories
        val dbName : String = if (locale == "it")
            "categories_it"
        else
            "categories_en"

        db.collection(dbName).get().addOnCompleteListener { task ->
            if (task.isSuccessful)
            {
                //Placeholder to not have a category already selected on first navigation to page
                categories.add(getString(R.string.upload_select_category))

                for (item in task.result!!.documents)
                    categories.add(item.id)

                categoriesLoaded.onCallback(categories)
            }
        }.addOnFailureListener {  exception ->
            Toast.makeText(activity?.baseContext, exception.localizedMessage,
                Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Handles cancel button click
     */
    private fun cancelButtonClick()
    {
        activity?.onBackPressed()
    }

    /**
     * Handles publish button click
     */
    private fun publishClick()
    {
        uploadImage(imagePath, object : ImageUploadedCallback
        {
            override fun onCallback(value: Uri?)
            {
                //Keeps asking for location access
                while (!checkLocationPermissions())
                    requestLocationPermission()


                var fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
                fusedLocationClient.lastLocation
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if (task.result != null)
                                publishPost(task.result, value)
                        }
                    }
            }
        })
    }

    private fun publishPost(userLocation : Location?, image : Uri?)
    {
        if (userLocation == null || userLocation?.latitude == null || userLocation?.longitude == null)
        {
            Toast.makeText(activity?.baseContext, getString(R.string.missing_location),
                Toast.LENGTH_SHORT).show()
            return
        }

        val data = hashMapOf(
            "UserId" to auth.currentUser?.uid.toString(),
            "Title" to titleBox.text.toString(),
            "Description" to descriptionBox.text.toString(),
            "Category" to categoriesList[selectedCategory],
            "Coordinates" to doubleArrayOf(userLocation!!.latitude, userLocation!!.longitude).toList(),
            //"LocationName" to addressText.text.toString(),
//                    "Price" to priceText.text.toString().toLong(),
            "PostedOn" to SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()),
            "UserSelectedDisplayName" to displayedName.text.toString(),
            "ImageUri" to image.toString()
        )

        db.collection("available_items").document(UUID.randomUUID().toString())
            .set(data as Map<String, Any>)
            .addOnSuccessListener {
                uploadCompleteDialog()
                activity?.onBackPressed()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Launches "gallery" activity to let the user select an image
     */
    private fun selectGalleryImage()
    {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, getString(R.string.publish_select_upload_image)), PICK_IMAGE_REQUEST)
    }

    /**
     * Handles the return from an activity
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST)
        {
            //No image selected
            if(data == null || data.data == null)
                return

            try
            {
                imagePath = data?.data
                if (imagePath != null)
                {
                    val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, imagePath)
                    //TODO Show preview here
                }
            }
            catch (e: IOException)
            {

            }
        }
    }

    /**
     * Requests permission to access storage from the user
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //Permission granteed
                    selectGalleryImage()
                }
                else
                {
                    //User denied the permission, stops upload process
                    Toast.makeText(requireContext(), getString(R.string.storage_permission_needed), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Uploads the selected image and returns its ID
     *
     * Each image has a random ID that is saved to the DB
     */
    private fun uploadImage(filePath: Uri?, imageUploaded : ImageUploadedCallback) : Uri?
    {
        //If no path was given, ends here
        if (filePath == null)
            return null

        btn_choose_image.isEnabled = false

        var imagePath : Uri? = null

        //Generate the path that includes a random ID for the image
        val pathRef = storage.reference.child("uploaded_images/${UUID.randomUUID()}")

        //Upload from given image path
        val uploadTask = pathRef?.putFile(filePath)

        //Handling of the upload task
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful)
            {
                task.exception?.let {
                    throw it
                }
                btn_choose_image.isEnabled = true
            }
            pathRef.downloadUrl
        }.addOnCompleteListener { task ->
            //Task returns the image url
            if (task.isSuccessful)
            {
                imagePath = task.result
                imageUploaded.onCallback(imagePath)
            }
            else {
                btn_choose_image.isEnabled = false
                Toast.makeText(
                    requireContext(),
                    getString(R.string.image_upload_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return imagePath
    }

    /**
     * Shows an animation once a post has been successfully uploaded
     */
    private fun uploadCompleteDialog()
    {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.upload_dialog_layout)
        dialog.show()
        dialog.window?.setLayout(600, 600);

        Handler().postDelayed({ dialog.dismiss() }, 3000)
    }

/***************************************************** HELPER METHODS *************************************************************/

    /**
     * Returns true if the app has access to location
     */
    private fun checkLocationPermissions(): Boolean
    {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            return true

        return false
    }

    /**
     * Shows the dialog to the user to request access to location
     */
    private fun requestLocationPermission()
    {
        ActivityCompat.requestPermissions(
            activity!!,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_REQUEST_CODE
        )
    }
}

/**
 * Interface to implement the callback once categories are loaded
 */
interface CategoriesCallback {
    fun onCallback(value: ArrayList<String>)
}

/**
 * Interface to implement the callback once the image has been uploaded
 */
interface ImageUploadedCallback {
    fun onCallback(value: Uri?)
}