package it.uniupo.spisso.upo_applicazionimobili.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import it.uniupo.spisso.upo_applicazionimobili.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?)
    {
        //Tells the activity to use DayNight theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //to change title of activity
        val actionBar = supportActionBar
        actionBar?.title = getString(R.string.login_title)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
