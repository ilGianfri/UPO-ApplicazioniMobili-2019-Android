package it.uniupo.spisso.upo_applicazionimobili.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.uniupo.spisso.upo_applicazionimobili.R
import it.uniupo.spisso.upo_applicazionimobili.fragments.*
import androidx.fragment.app.Fragment

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
                val firstFragment = SearchFragment()
                openFragment(firstFragment)
                return@OnNavigationItemSelectedListener true
            }

            R.id.action_menu_saved -> {
                val secondFragment = SavedFragment()
                openFragment(secondFragment)
                return@OnNavigationItemSelectedListener true
            }

            R.id.action_menu_add -> {
                val thirdFragment = PublishFragment()
                openFragment(thirdFragment)
                return@OnNavigationItemSelectedListener true
            }

            R.id.action_menu_messages -> {
                val thirdFragment = MessagesFragment()
                openFragment(thirdFragment)
                return@OnNavigationItemSelectedListener true
            }

            R.id.action_menu_profile -> {
                val thirdFragment = ProfileFragment()
                openFragment(thirdFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun openFragment(fragment: Fragment)
    {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}
