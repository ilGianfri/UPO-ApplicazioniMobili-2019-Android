package it.uniupo.spisso.upo_applicazionimobili.fragments


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task

import it.uniupo.spisso.upo_applicazionimobili.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_publish.*
import java.io.IOException
import java.util.*


/**
 * Fragment used to handle the creation of a new ad
 */
class PublishFragment : Fragment()
{
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var categoriesList : ArrayList<String> = ArrayList()
    private val PICK_IMAGE_REQUEST = 71
    private val PERMISSION_CODE = 1001;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_publish, container, false)


        val categoriesSpinner = view.findViewById<Spinner>(R.id.categories)

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

        return view
    }

    private fun loadCategories(locale: String, categoriesLoaded : CategoriesCallback)
    {
        val categories = arrayListOf<String>()

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

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        Locale.getDefault().language //en/it
    }

    private fun cancelButtonClick(v: View?)
    {
        activity?.onBackPressed()
    }

    /**
     * Launched "gallery" activity to let the user select an image
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
                val path = data?.data
                if (path != null)
                {
                    val bitmap = MediaStore.Images.Media.getBitmap(activity!!.contentResolver, path)
                    //TODO Show preview here
                    uploadImage(path)

                }
            }
            catch (e: IOException) {

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
     */
    private fun uploadImage(filePath: Uri)
    {
        if (filePath == null)
            return

        var storageRef = storage.reference
        val imgId = UUID.randomUUID().toString()
        val fileRef = storageRef.child(imgId)
        val pathRef = storageRef.child("uploaded_images/${imgId}")

        val uploadTask = storageRef?.putFile(filePath!!)

        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful)
            {
                task.exception?.let {
                    throw it
                }
            }
            pathRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful)
            {
                val downloadUri = task.result
            }
            else
            {
                // Handle failures
                // ...
            }
        }
    }
}

interface CategoriesCallback {
    fun onCallback(value: ArrayList<String>)
}