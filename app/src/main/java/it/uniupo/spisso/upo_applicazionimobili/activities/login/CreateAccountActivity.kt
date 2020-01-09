package it.uniupo.spisso.upo_applicazionimobili.activities.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import it.uniupo.spisso.upo_applicazionimobili.R
import it.uniupo.spisso.upo_applicazionimobili.activities.MainActivity
import kotlinx.android.synthetic.main.activity_create_account.*

class CreateAccountActivity : AppCompatActivity()
{
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        auth = FirebaseAuth.getInstance()
    }

    fun signupClick(v: View?)
    {
        if (passwordBox.text.toString().length < 6)
        {
            Toast.makeText(baseContext, R.string.unsecure_password_error,
                Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(usernameBox.text.toString(), passwordBox.text.toString())
            .addOnSuccessListener (this) {
                    // Sign in success
                    val user = auth.currentUser

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
            }.addOnFailureListener { exception -> Toast.makeText(baseContext, exception.localizedMessage,
                Toast.LENGTH_SHORT).show() }
    }
}
