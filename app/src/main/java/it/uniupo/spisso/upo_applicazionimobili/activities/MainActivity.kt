package it.uniupo.spisso.upo_applicazionimobili.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.uniupo.spisso.upo_applicazionimobili.R
import it.uniupo.spisso.upo_applicazionimobili.fragments.*
import it.uniupo.spisso.upo_applicazionimobili.activities.login.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionBar = supportActionBar
        actionBar?.hide()

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val firstFragment = SearchFragment()
        openFragment(firstFragment)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {
        when (it.itemId)
        {

            R.id.action_menu_home -> {
                val homeFragment = SearchFragment()
                openFragment(homeFragment)
                return@OnNavigationItemSelectedListener true
            }

            R.id.action_menu_saved -> {
                val savedFragment = SavedFragment()
                openFragment(savedFragment)
                return@OnNavigationItemSelectedListener true
            }

            R.id.action_menu_add -> {
                val publishFragment = PublishFragment()
                openFragment(publishFragment)
                return@OnNavigationItemSelectedListener true
            }

            R.id.action_menu_messages -> {
                val messagesFragment = MessagesFragment()
                openFragment(messagesFragment)
                return@OnNavigationItemSelectedListener true
            }

            R.id.action_menu_profile ->
            {
                val auth = FirebaseAuth.getInstance()
                //If the user is logged-in already, navigate to its profile page
                if (auth.currentUser != null)
                {
                    val profileFragment = ProfileFragment()
                    openFragment(profileFragment)
                    return@OnNavigationItemSelectedListener true
                }
                else //Otherwise show login/signup
                {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
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
