package it.uniupo.spisso.upo_applicazionimobili.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import it.uniupo.spisso.upo_applicazionimobili.R
import it.uniupo.spisso.upo_applicazionimobili.activities.login.LoginActivity
import it.uniupo.spisso.upo_applicazionimobili.fragments.MessagesFragment
import it.uniupo.spisso.upo_applicazionimobili.fragments.ProfileFragment
import it.uniupo.spisso.upo_applicazionimobili.fragments.SearchFragment


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity()
{
    private var bottomNavigation: BottomNavigationView? = null

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
                val newIntent = Intent(context, NoConnectivityActivity::class.java)
                startActivity(newIntent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Goes to the login page if there's no user signed in
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
        transaction.commit()
    }
}
