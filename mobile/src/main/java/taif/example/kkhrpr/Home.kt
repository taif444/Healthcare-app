package taif.example.kkhrpr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import taif.example.kkhrpr.databinding.FragmentHomeBinding
import kotlin.random.Random

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Home : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val rootView = binding.root

        val textViewUsername = binding.textView
        val textViewPrediction = binding.prediction

        val heartRate = Random.nextInt(10, 21)
        sendToPythonServer(heartRate)

        // Initialize Firebase components
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

        // Get the current user's username from Firebase Realtime Database
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val userId = user.uid
            val userRef = databaseReference.child("users").child(userId)
            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val username = dataSnapshot.child("username").value.toString()
                    textViewUsername.text = "Welcome, $username!"
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle database error
                }
            })
        }

        return rootView
    }

    private fun sendToPythonServer(heartRate: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()
            val json = """{"heart_rate": $heartRate}"""
            val requestBody = json.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("http://localhost:5500/predict")
                .post(requestBody)
                .build()

            try {
                val response = client.newCall(request).execute()
                val responseData = response.body?.string()
                // Handle response data
                withContext(Dispatchers.Main) {
                    // Update UI if needed
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
