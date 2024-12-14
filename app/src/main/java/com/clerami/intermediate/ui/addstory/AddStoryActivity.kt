package com.clerami.intermediate.ui.addstory

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.clerami.intermediate.databinding.ActivityAddStoryBinding
import com.clerami.intermediate.ui.story.StoryActivity
import java.io.File

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private val addStoryViewModel: AddStoryViewModel by viewModels()
    private var photoUri: Uri? = null
    private val CAMERA_REQUEST_CODE = 1001
    private var cameraUri: Uri? = null


    private val galleryResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            photoUri = result.data?.data
            binding.ivAddPhoto.setImageURI(photoUri)
        }
    }

    private val cameraResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            cameraUri?.let { uri ->
                binding.ivAddPhoto.setImageURI(uri)
                photoUri = uri
            }
        }
    }

    companion object {
        private const val NAVIGATION_DELAY_MS: Long = 500L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addStoryViewModel.uploadResult.observe(this, Observer { result ->
            Log.d("AddStoryActivity", "Upload result: $result")
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show()

            if (result == "Story created successfully") {
                Handler(Looper.getMainLooper()).postDelayed({
                    Log.d("AddStoryActivity", "Navigating to StoryActivity")
                    navigateToStoryActivity()
                }, NAVIGATION_DELAY_MS)
            }
        })

        binding.tambahGambar.setOnClickListener {
            pickImageFromGallery()
        }

        binding.ambilGambar.setOnClickListener {
            openCamera()
        }

        binding.buttonAdd.setOnClickListener {
            val description = binding.edAddDescription.text.toString()
            val lat = 0.0f
            val lon = 0.0f
            addStoryViewModel.uploadStory(this, description, photoUri, lat, lon)
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryResultLauncher.launch(intent)
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        } else {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (cameraIntent.resolveActivity(packageManager) != null) {
                val photoFile = createImageFile()
                cameraUri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", photoFile)
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri)
                cameraIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                cameraResultLauncher.launch(cameraIntent)
            }
        }
    }

    private fun createImageFile(): File {
        val storageDir: File? = externalCacheDir
        return File.createTempFile("camera_photo_", ".jpg", storageDir).apply {
            Log.d("AddStoryActivity", "File created at: $absolutePath")
        }
    }

    private fun navigateToStoryActivity() {
        Log.d("AddStoryActivity", "Launching StoryActivity now!")
        val intent = Intent(this, StoryActivity::class.java)
        startActivity(intent)
        finish()
    }
}
