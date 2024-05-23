package taif.example.kkhrpr

//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
//import com.google.firebase.auth.FirebaseAuthUserCollisionException
//import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginRedirectText: TextView
    private lateinit var signUpButton: Button
//    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize views
        nameEditText = findViewById(R.id.signup_name)
        emailEditText = findViewById(R.id.signup_email)
        usernameEditText = findViewById(R.id.signup_username)
        passwordEditText = findViewById(R.id.signup_password)
        loginRedirectText = findViewById(R.id.loginRedirectText)
        signUpButton = findViewById(R.id.signup_button)

        // Initialize Firebase Auth and Database
//        firebaseAuth = FirebaseAuth.getInstance()


        // Set OnClickListener to sign-up button
        signUpButton.setOnClickListener {
            database = FirebaseDatabase.getInstance()
            reference = database.getReference("users")
            // Retrieve text from EditText views
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Perform validation checks
            if (name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if email is valid
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if password is strong enough (optional)
            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val helperClass = Helper(name, email, username, password)
            reference.child(username).setValue(helperClass)

            Toast.makeText(this@SignUp, "You have signed up successfully!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@SignUp, MainActivity::class.java)
            startActivity(intent)

            // Create user with email and password
            /*firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign up success, update UI with the signed-in user's information
                        val user = firebaseAuth.currentUser
                        val helperClass = Helper(name, email, username, password)
                        reference.child(username).setValue(helperClass)
                        Toast.makeText(this, "You have signed up successfully!", Toast.LENGTH_SHORT).show()

                        // You can further process user data or navigate to another screen here
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this, task.exception?.message ?: "Sorry Sign-up failed. Please try again later.", Toast.LENGTH_SHORT).show()
                        when (task.exception) {
                            is FirebaseAuthWeakPasswordException -> {
                                Toast.makeText(this, "Weak password. Please use a stronger password.", Toast.LENGTH_SHORT).show()
                            }
                            is FirebaseAuthInvalidCredentialsException -> {
                                Toast.makeText(this, "Invalid email. Please enter a valid email address.", Toast.LENGTH_SHORT).show()
                            }
                            is FirebaseAuthUserCollisionException -> {
                                Toast.makeText(this, "User with this email already exists.", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Toast.makeText(this, "Sign-up failed. Please try again later.", Toast.LENGTH_SHORT).show()
                            }
                        }*/
//                    }
        }
        loginRedirectText.setOnClickListener {
            val intent = Intent(this, LogIn::class.java)
            startActivity(intent)
        }
    }

}

