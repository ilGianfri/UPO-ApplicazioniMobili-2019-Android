package it.uniupo.spisso.upo_applicazionimobili.fragments


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment

import it.uniupo.spisso.upo_applicazionimobili.R
import com.google.firebase.firestore.FirebaseFirestore


/**
 * A simple [Fragment] subclass.
 */
class PublishFragment : Fragment()
{
    val db = FirebaseFirestore.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_publish, container, false)
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
    }

    fun cancelButtonClick(v: View?)
    {
        activity?.onBackPressed()
    }
}
