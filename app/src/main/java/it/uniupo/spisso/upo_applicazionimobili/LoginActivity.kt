package it.uniupo.spisso.upo_applicazionimobili

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?)
    {
        //Tells the activity to use DayNight theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //to change title of activity
        val actionBar = supportActionBar
        actionBar!!.title = getString(R.string.login_title)
        actionBar.setDisplayHomeAsUpEnabled(true)
    }
}
