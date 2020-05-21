package it.uniupo.spisso.upo_applicazionimobili.activities.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.uniupo.spisso.upo_applicazionimobili.R
import it.uniupo.spisso.upo_applicazionimobili.activities.MainActivity
import kotlinx.android.synthetic.main.activity_create_account.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class CreateAccountActivity : AppCompatActivity()
{
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        auth = FirebaseAuth.getInstance()

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        /**
         * Realtime password check. Disables sign-up button if password is too weak
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
     * Handles back button press
     */
    override fun onSupportNavigateUp(): Boolean
    {
        onBackPressed()
        return true
    }

    /**
     * Handles the sign-up click button
     */
    private fun signupClick()
    {
        if (passwordBox.text.toString().length < 6)
        {
            Toast.makeText(
                baseContext, R.string.unsecure_password_error,
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (nameBox.text.isNullOrEmpty() || cityBox.text.isNullOrEmpty() || addressBox.text.isNullOrEmpty())
        {
            Toast.makeText(
                baseContext, R.string.please_fill_fields,
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        auth.createUserWithEmailAndPassword(
            usernameBox.text.toString(),
            passwordBox.text.toString()
        )
            .addOnSuccessListener(this) {
                // Sign in success
                val user = auth.currentUser

                val data = hashMapOf(
                    "Name" to nameBox.text,
                    "City" to cityBox.text,
                    "Address" to addressBox.text
                )

                db.collection("user_details").document(auth.currentUser?.uid.toString().toString())
                    .set(data as Map<String, Any>)
                    .addOnSuccessListener {

                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_SHORT).show()
                    }

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener { exception ->
                Toast.makeText(
                    baseContext, exception.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
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
    private fun isValidPassword(password: String?): Boolean
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
