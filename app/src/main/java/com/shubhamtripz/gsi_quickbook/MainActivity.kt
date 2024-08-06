package com.shubhamtripz.gsi_quickbook

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class MainActivity : AppCompatActivity() {

    private lateinit var editTextManagerId: EditText
    private lateinit var editTextName: EditText
    private lateinit var editTextCity: EditText
    private lateinit var editTextPrice: EditText
    private lateinit var editTextOpenTime: EditText
    private lateinit var editTextCloseTime: EditText
    private lateinit var buttonSelectImages: Button
    private lateinit var buttonSubmit: Button

    private val storage = Firebase.storage
    private val firestore = FirebaseFirestore.getInstance()

    private val imageUris = mutableListOf<Uri>()

    // For selecting multiple images
    private val getContent = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        imageUris.clear()
        imageUris.addAll(uris)
        Toast.makeText(this, "${uris.size} images selected", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextManagerId = findViewById(R.id.editTextManagerId)
        editTextName = findViewById(R.id.editTextName)
        editTextCity = findViewById(R.id.editTextCity)
        editTextPrice = findViewById(R.id.editTextPrice)
        editTextOpenTime = findViewById(R.id.editTextOpenTime)
        editTextCloseTime = findViewById(R.id.editTextCloseTime)
        buttonSelectImages = findViewById(R.id.buttonSelectImages)
        buttonSubmit = findViewById(R.id.buttonSubmit)

        buttonSelectImages.setOnClickListener {
            selectImages()
        }

        buttonSubmit.setOnClickListener {
            val managerId = editTextManagerId.text.toString()
            val name = editTextName.text.toString()
            val city = editTextCity.text.toString()
            val price = editTextPrice.text.toString().toIntOrNull() ?: 0
            val openTime = editTextOpenTime.text.toString()
            val closeTime = editTextCloseTime.text.toString()

            // Check if all fields are filled
            if (managerId.isNotBlank() && name.isNotBlank() && city.isNotBlank() && openTime.isNotBlank() && closeTime.isNotBlank()) {
                uploadImagesAndSubmitData(managerId, name, city, price, openTime, closeTime)
            } else {
                // Show a toast if any field is empty
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to upload images to Firebase Storage and submit data to Firestore
    private fun uploadImagesAndSubmitData(managerId: String, name: String, city: String, price: Int, openTime: String, closeTime: String) {
        val imageUrls = mutableListOf<String>()
        var uploadCount = 0

        for (uri in imageUris) {
            val storageRef = storage.reference.child("images/${uri.lastPathSegment}")
            val uploadTask = storageRef.putFile(uri)

            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                storageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result.toString()
                    imageUrls.add(downloadUri)
                    uploadCount++

                    // Check if all images have been uploaded
                    if (uploadCount == imageUris.size) {
                        // Submit data to Firestore
                        val data = hashMapOf(
                            "manager_id" to managerId,
                            "name" to name,
                            "city" to city,
                            "price" to price,
                            "open_time" to openTime,
                            "close_time" to closeTime,
                            "pictures" to imageUrls
                        )

                        firestore.collection("places")
                            .add(data)
                            .addOnSuccessListener { documentReference ->
                                // Document added successfully
                                Toast.makeText(this, "Data added successfully", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                // Error adding document
                                Toast.makeText(this, "Error adding data: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    // Handle unsuccessful upload
                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Function to launch image picker
    private fun selectImages() {
        getContent.launch("image/*")
    }
}
