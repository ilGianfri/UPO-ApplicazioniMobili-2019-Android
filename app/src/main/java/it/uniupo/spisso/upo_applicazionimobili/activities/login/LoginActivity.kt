package it.uniupo.spisso.upo_applicazionimobili.activities.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import it.uniupo.spisso.upo_applicazionimobili.R
import kotlinx.android.synthetic.main.activity_login.*
import it.uniupo.spisso.upo_applicazionimobili.activities.*

class LoginActivity : AppCompatActivity()
{
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?)
    {
        //Tells the activity to use DayNight theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        //Set activity title and enable back button
        val actionBar = supportActionBar
        actionBar?.title = getString(R.string.login_title)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean
    {
        this?.onBackPressed()
        return super.onSupportNavigateUp()
    }

    fun loginClick(v: View?)
    {
        if (usernameBox.text.toString() == "" && passwordBox.text.toString() == "")
        {
            Toast.makeText(baseContext, getString(R.string.credentials_empty),
                Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(usernameBox.text.toString(), passwordBox.text.toString())
            .addOnSuccessListener (this)
            {
                    // Sign in success
                    val user = auth.currentUser
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
            }.addOnFailureListener { exception -> Toast.makeText(baseContext, exception.localizedMessage,
                Toast.LENGTH_SHORT).show() }
    }

    fun signupClick(v: View?)
    {
        val intent = Intent(this, CreateAccountActivity::class.java)
        startActivity(intent)
        finish()
    }
}
