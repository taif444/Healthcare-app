package taif.example.kkhrpr

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import taif.example.kkhrpr.databinding.FragmentProfiletBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Profilet : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentProfiletBinding
    private var imageUri: Uri? = null
    private lateinit var storageReference: StorageReference
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfiletBinding.inflate(inflater, container, false)
        val rootView = binding.root

        val editName = binding.editName
        val editEmail = binding.editEmail
        val editUsername = binding.editUsername
        val editPassword = binding.editPassword
        val saveButton = binding.saveButton
        val firebaseImage = binding.firebaseimage

        val reference = FirebaseDatabase.getInstance().getReference("users")

        val intent = activity?.intent
        var nameUser = intent?.getStringExtra("name") ?: ""
        var emailUser = intent?.getStringExtra("email") ?: ""
        var usernameUser = intent?.getStringExtra("username") ?: ""
        var passwordUser = intent?.getStringExtra("password") ?: ""

        editName.setText(nameUser)
        editEmail.setText(emailUser)
        editUsername.setText(usernameUser)
        editPassword.setText(passwordUser)

        saveButton.setOnClickListener {
            if (isNameChanged(nameUser, editName.text.toString(), reference, usernameUser)) {
                nameUser = editName.text.toString()
            }
            if (isEmailChanged(emailUser, editEmail.text.toString(), reference, usernameUser)) {
                emailUser = editEmail.text.toString()
            }
            if (isPasswordChanged(passwordUser, editPassword.text.toString(), reference, usernameUser)) {
                passwordUser = editPassword.text.toString()
            }
            if (!isNameChanged(nameUser, editName.text.toString(), reference, usernameUser) &&
                !isEmailChanged(emailUser, editEmail.text.toString(), reference, usernameUser) &&
                !isPasswordChanged(passwordUser, editPassword.text.toString(), reference, usernameUser)
            ) {
                Toast.makeText(activity, "Changes Saved", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Saved", Toast.LENGTH_SHORT).show()
            }
        }

        binding.uploadImg.setOnClickListener {

            selectImage()
        }

        return rootView
    }

    private fun isNameChanged(
        oldName: String,
        newName: String,
        reference: DatabaseReference,
        username: String
    ): Boolean {
        if (oldName != newName) {
            reference.child(username).child("name").setValue(newName)
            return true
        }
        return false
    }

    private fun isEmailChanged(
        oldEmail: String,
        newEmail: String,
        reference: DatabaseReference,
        username: String
    ): Boolean {
        if (oldEmail != newEmail) {
            reference.child(username).child("email").setValue(newEmail)
            return true
        }
        return false
    }

    private fun isPasswordChanged(
        oldPassword: String,
        newPassword: String,
        reference: DatabaseReference,
        username: String
    ): Boolean {
        if (oldPassword != newPassword) {
            reference.child(username).child("password").setValue(newPassword)
            return true
        }
        return false
    }
    private fun setImageView(imageUri: Uri?) {
        binding.firebaseimage.setImageURI(imageUri)
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("ProfileFragment", "onActivityResult called")
        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            binding.firebaseimage.setImageURI(imageUri)
            uploadImage()
        }
    }

    private fun uploadImage() {
        progressDialog = ProgressDialog(activity)
        progressDialog.setTitle("Uploading File....")
        progressDialog.show()

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA)
        val fileName = formatter.format(Date())
        storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")

        storageReference.putFile(imageUri!!)
            .addOnSuccessListener {
                binding.firebaseimage.setImageURI(null)
                Toast.makeText(activity, "Successfully Uploaded", Toast.LENGTH_SHORT).show()
                if (progressDialog.isShowing) progressDialog.dismiss()
            }
            .addOnFailureListener {
                if (progressDialog.isShowing) progressDialog.dismiss()
                Toast.makeText(activity, "Failed to Upload", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Profilet().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
