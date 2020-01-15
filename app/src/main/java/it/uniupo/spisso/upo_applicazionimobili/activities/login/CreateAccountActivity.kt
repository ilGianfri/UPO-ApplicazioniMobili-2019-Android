package it.uniupo.spisso.upo_applicazionimobili.activities.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import it.uniupo.spisso.upo_applicazionimobili.R
import it.uniupo.spisso.upo_applicazionimobili.activities.MainActivity
import kotlinx.android.synthetic.main.activity_create_account.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class CreateAccountActivity : AppCompatActivity()
{
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        auth = FirebaseAuth.getInstance()

        /**
         * Realtime password check. Disables signup button if password is too weak
         */
        passwordBox.addTextChangedListener(object : TextWatcher
        {
            override fun afterTextChanged(s: Editable?)
            {
                val isValid = isValidPassword(s.toString())
                signup_button.isEnabled = isValid
                if (!isValid)
                    passwordBox.error = getString(R.string.password_requirements)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })



        signup_button.setOnClickListener{signupClick()}
    }

    /**
     * Handles the signup click button
     */
    fun signupClick()
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

    /**
     * Returns true is password is complex enough
     *
     * REQUIREMENTS:
     * - Must contain at least a number [0-9]
     * - Must contain at least a lowercase letter
     * - Must contain at least an uppercase letter
     * - Must contain at least a special character
     */
    fun isValidPassword(password: String?): Boolean
    {
        val pattern: Pattern
        val matcher: Matcher
        val passwordPattern =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(passwordPattern)
        matcher = pattern.matcher(password)
        return matcher.matches()
    }
}
