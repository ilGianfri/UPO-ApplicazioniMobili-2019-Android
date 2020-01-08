package it.uniupo.spisso.upo_applicazionimobili.activities.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import it.uniupo.spisso.upo_applicazionimobili.R
import kotlinx.android.synthetic.main.activity_login.*

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

        //to change title of activity
        val actionBar = supportActionBar
        actionBar?.title = getString(R.string.login_title)
        actionBar?.setDisplayHomeAsUpEnabled(true)

    }

    public override fun onStart()
    {
        super.onStart()
        val currentUser = auth.currentUser
        //TODO
        //updateUI(currentUser)
    }

    fun loginClick(v: View?)
    {
        auth.signInWithEmailAndPassword(usernameBox.text.toString(), passwordBox.text.toString())
            .addOnCompleteListener(this)
            { task ->
                if (task.isSuccessful)
                {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
//                    updateUI(user)
                }
                else
                {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, R.string.authentication_failed_message,
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun signupClick(v: View?)
    {
        val intent = Intent(this, CreateAccountActivity::class.java)
        startActivity(intent)
        finish()
    }
}
