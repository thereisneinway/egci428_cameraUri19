package com.example.egci428_camerauri19

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import java.io.File
import android.Manifest
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import java.util.Locale

class MainActivity : AppCompatActivity() {
    lateinit var photoImageView: ImageView
    lateinit var photoButton : Button
    lateinit var textView : TextView

    private var uriKey: Uri? = null
    private lateinit var  outputDirectory: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        photoButton = findViewById(R.id.photoBtn)
        photoImageView = findViewById(R.id.imageView)
        textView = findViewById(R.id.textView)
    }

    fun takePhoto(view: View){
        requestCameraPermission.launch(Manifest.permission.CAMERA)
    }

    private val requestCameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            isSuccess : Boolean ->
        if(isSuccess){
            Log.d("Take Picture", "Permission granted")
            takePicture()
        } else {
            Toast.makeText(applicationContext, "Camera has no permission", Toast.LENGTH_SHORT).show()
        }
    }

    private val captureImage = registerForActivityResult(ActivityResultContracts.TakePicture()){
        if(it){
            uriKey.let{
                    uri ->
                if(uri!=null){
                    uriKey = uri
                    photoImageView.setImageURI(uriKey)
                    Log.d("Capturte Image",uriKey.toString())
                    textView.text = uriKey.toString()
                }
            }
        }
    }

    private fun takePicture(){
        uriKey = getTempFileUri()
        captureImage.launch(uriKey)
    }
    private fun getTempFileUri(): Uri {
        outputDirectory = getOutputDirectory(this)
        val tmpFile = File.createTempFile(
            SimpleDateFormat(FILENAME, Locale.ENGLISH).format(System.currentTimeMillis()), PHOTO_EXTENSION, outputDirectory).apply {
            createNewFile()
            deleteOnExit()
        }
        return FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
    }
    companion object {
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpeg"

        private fun getOutputDirectory(context: Context): File {
            val appContext = context.applicationContext
            val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
                File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
            }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else appContext.filesDir
        }
    }
}








