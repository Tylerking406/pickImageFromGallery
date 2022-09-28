package com.example.pickimagefromgallery

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.OutputStream
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var button: Button
    private lateinit var imgV : ImageView

    var pickedPhoto: Uri? = null
    var pickedBitmap: Bitmap?= null
    val REQUEST_CODE = 111

    val dictBitmap = mutableMapOf("default" to 1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.button)
        imgV = findViewById(R.id.imageView)

        val images = intArrayOf(R.drawable.slide01, R.drawable.slide02, R.drawable.slide03,
            R.drawable.slide04, R.drawable.slide05, R.drawable.slide06, R.drawable.slide07,
            R.drawable.slide08, R.drawable.slide09, R.drawable.slide10, R.drawable.slide11,
            R.drawable.slide12, R.drawable.slide13, R.drawable.slide14, R.drawable.slide15,
            R.drawable.slide16, R.drawable.slide17, R.drawable.slide18, R.drawable.slide19,
            R.drawable.slide20, R.drawable.slide21, R.drawable.slide22, R.drawable.slide23,
            R.drawable.slide24, R.drawable.slide25, R.drawable.slide26)
        for(i in 0..25){
            val bitmap = BitmapFactory.decodeResource(resources,images[i])
           // val bit = img.setImageBitmap(bitmap)
            //saveImage(bitmap)
        }


        button.isEnabled = false

        //requst permission if not given
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),111 )
        }
        else
            button.isEnabled = true



        button.setOnClickListener {
           // val bitmap = BitmapFactory.decodeResource(resources,R.drawable.slide01)
            // val bit = img.setImageBitmap(bitmap)
            // saveImage(bitmap)
            openGallery()
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            button.isEnabled = true
    }


    private  fun saveImage(bitmap: Bitmap){
        val outputStream: OutputStream
        try {
            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q){
                val resolver = contentResolver
                val contentValues =ContentValues()
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,"Image_"+".jpg")
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"Image/jpg")
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH,Environment.DIRECTORY_PICTURES+ File.separator+"TestFolder")
                val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues)
                outputStream = resolver.openOutputStream(Objects.requireNonNull(imageUri)!!)!!
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream)
                Objects.requireNonNull<OutputStream>(outputStream)
                Toast.makeText(this,"Image Saved",Toast.LENGTH_SHORT).show()

            }
        }catch (e : Exception){
            Toast.makeText(this,"Image not Saved",Toast.LENGTH_SHORT).show()

        }
    }



    //fun to access gallery
    private fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,REQUEST_CODE)
    }

    // manipulate taken image


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE){
            //imgV.setImageURI(data?.data)
            pickedPhoto = data?.data
            if (Build.VERSION.SDK_INT >=  28) {
                var source = ImageDecoder.createSource(this.contentResolver, pickedPhoto!!)
                pickedBitmap = ImageDecoder.decodeBitmap(source)
                imgV.setImageBitmap(pickedBitmap)
                saveImage(pickedBitmap!!)
            } else {
                pickedBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, pickedPhoto)
                imgV.setImageBitmap(pickedBitmap)
                saveImage(pickedBitmap!!)
            }


        }
    }
}