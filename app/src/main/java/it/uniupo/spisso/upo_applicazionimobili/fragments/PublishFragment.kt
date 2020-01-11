package it.uniupo.spisso.upo_applicazionimobili.fragments


import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment

import it.uniupo.spisso.upo_applicazionimobili.R
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


/**
 * Fragment used to handle the creation of a new ad
 */
class PublishFragment : Fragment()
{
    private val db = FirebaseFirestore.getInstance()
    private var categoriesList : ArrayList<String> = ArrayList()
    private val PICK_IMAGE_REQUEST = 71

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

        return view
    }

    private fun loadCategories(locale: String, categoriesLoaded : CategoriesCallback)
    {
        val categories = arrayListOf<String>()

        val dbName : String = if (locale == "it")
            "categories_it"
        else
            "categories_en"

        Toast.makeText(activity?.baseContext, dbName,
            Toast.LENGTH_SHORT).show()

        db.collection(dbName).get().addOnCompleteListener { task ->
            if (task.isSuccessful)
            {
                //Placeholder to not have a category already selected on first navigation to page
                categories.add(getString(R.string.upload_select_category))
                for (item in task.result!!.documents)
                {
                    categories.add(item.id)
                }
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

//        val categories = db.collection("categories_en").get().addOnSuccessListener {
//                result ->
//            result.documents.toTypedArray()
//
//            val adapter = ArrayAdapter(activity!!.applicationContext, android.R.layout.simple_spinner_item)
//        }

        Locale.getDefault()?.language //en/it
    }

    fun cancelButtonClick(v: View?)
    {
        activity?.onBackPressed()
    }

    fun uploadImageClick(v: View?)
    {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, getString(R.string.publish_select_upload_image)), PICK_IMAGE_REQUEST)
    }
}

interface CategoriesCallback {
    fun onCallback(value: ArrayList<String>)
}