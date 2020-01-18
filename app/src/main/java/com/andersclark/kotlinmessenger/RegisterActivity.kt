package com.andersclark.kotlinmessenger

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.andersclark.kotlinmessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_registration.*
import java.util.*

private const val TAG = "RegisterActivity"

class RegisterActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        supportActionBar?.title = "Sign up"

        registration_registerbutton.setOnClickListener {
            performRegister()
        }
        registration_alreadyhaveaccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        registration_addphoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    private var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            Log.d(TAG, "Photo was selected at ${selectedPhotoUri.toString()}")
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            registration_addphoto_circleimageview.setImageBitmap(bitmap)
            registration_addphoto.alpha = 0f
           // val bitmapDrawable = BitmapDrawable(bitmap)
            //registration_addphoto.setBackgroundDrawable(bitmapDrawable)
        }
    }


    private fun performRegister() {
        val email = registration_email.text.toString()
        val password = registration_password.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter a viable email and password", Toast.LENGTH_SHORT)
                .show()
            return
        }
        Log.d(TAG, "Email is: ${email}")
        Log.d(TAG, "Password is: ${password}")
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                // else if successful
                Log.d(
                    TAG,
                    "SUCCESSFULLY CREATED USER with UID: ${it.result!!.user?.uid}"
                )
                uploadImageToFirebaseStorage()
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }


    private fun uploadImageToFirebaseStorage() {
        Log.d(TAG, "trying to upload image to Firebase...")
            if (selectedPhotoUri == null) {
                Log.d(TAG, "Photo URI is null")
                return
            }
        val filename = UUID.randomUUID().toString()

        // TODO: rename to something useful:
        val ref = FirebaseStorage.getInstance().reference.child("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    Log.d(TAG, "File Location $it")
                }
           saveUserToFireBaseDatabase(it.toString())
           }
           .addOnFailureListener{
               Log.d(TAG, "Uploading imaged failed: ${it.message}")
           }
    }


    private fun saveUserToFireBaseDatabase(profileImageUrl: String) {
        Toast.makeText(this, "Registering user...", Toast.LENGTH_SHORT)
            .show()
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(
            uid,
            registration_username.text.toString(),
            profileImageUrl
        )
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "User ${user.username} saved to Firebase")
                Toast.makeText(this, "Logged in as ${registration_username.text}", Toast.LENGTH_SHORT)

                val intent = Intent(this, LatestMessageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
    }
}


