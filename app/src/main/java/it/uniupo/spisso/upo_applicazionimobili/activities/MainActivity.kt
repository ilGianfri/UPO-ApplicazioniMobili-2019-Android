package it.uniupo.spisso.upo_applicazionimobili.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import it.uniupo.spisso.upo_applicazionimobili.R
import it.uniupo.spisso.upo_applicazionimobili.activities.login.LoginActivity
import it.uniupo.spisso.upo_applicazionimobili.fragments.*
import it.uniupo.spisso.upo_applicazionimobili.models.BaseMessage

class MainActivity : AppCompatActivity()
{
    private var bottomNavigation: BottomNavigationView? = null
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onStart()
    {
        super.onStart()
        registerReceiver(broadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onStop()
    {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context, intent: Intent)
        {
            val notConnected = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)
            if (notConnected)
            {
                val intent = Intent(context, NoConnectivityActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (FirebaseAuth.getInstance().currentUser == null)
        {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        val actionBar = supportActionBar
        actionBar?.hide()

        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation?.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val firstFragment = SearchFragment()
        openFragment(firstFragment)
    }

    /**
     * Handle bottom navigation buttons
     */
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {
        when (it.itemId)
        {
            //Home button
            R.id.action_menu_home -> {
                val homeFragment = SearchFragment()
                openFragment(homeFragment)
                return@OnNavigationItemSelectedListener true
            }

            //Saved posts

//            R.id.action_menu_saved -> {
//                val savedFragment = SavedFragment()
//                openFragment(savedFragment)
//                return@OnNavigationItemSelectedListener true
//            }

            //Publish page

//            R.id.action_menu_add -> {
//                val auth = FirebaseAuth.getInstance()
//                if (auth.currentUser != null) {
//                    val publishFragment = PublishFragment()
//                    val transaction = supportFragmentManager.beginTransaction()
//                    transaction.replace(R.id.container, publishFragment)
//                    transaction.addToBackStack(null)
//                    transaction.commit()
//                    return@OnNavigationItemSelectedListener true
//                }
//                else
//                {
//                    Toast.makeText(baseContext, getString(R.string.login_required_post),
//                        Toast.LENGTH_SHORT).show()
//                }
//            }

            //Messages
            R.id.action_menu_messages -> {
                val messagesFragment = MessagesFragment()
                openFragment(messagesFragment)
                bottomNavigation?.removeBadge(R.id.action_menu_messages)
                return@OnNavigationItemSelectedListener true
            }

            //Profile
            R.id.action_menu_profile ->
            {
                val profileFragment = ProfileFragment()
                openFragment(profileFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun openFragment(fragment: Fragment)
    {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        //transaction.addToBackStack(null)
        transaction.commit()
    }
}
