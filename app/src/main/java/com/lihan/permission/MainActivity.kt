package com.lihan.permission

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.lihan.permission.databinding.ActivityMainBinding

//pdf https://www.section.io/engineering-education/picking-pdf-and-image-from-phone-storage/
class MainActivity : AppCompatActivity() {
    companion object{
        private const val PICK_FILE_REQUEST_CODE  = 101
    }
    private lateinit var binding : ActivityMainBinding

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("TAG", "registerForActivityResult : get Permission ")
            } else {
                Log.d("TAG", "registerForActivityResult : Oops ")
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        }

   val content = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
           result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val resultIntent = result.data?.data?.path
            Toast.makeText(this@MainActivity, "${resultIntent}", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            getImageButton.setOnClickListener {
                //check permission
                getImage()
            }
        }

    }




    @RequiresApi(Build.VERSION_CODES.M)
    private fun getImage() {
        checkPermission()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkPermission(){
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                toGetFiles()
            }
            shouldShowRequestPermissionRationale(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {

            showInContextUI()
        }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }


    }

    private fun toGetFiles() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        content.launch(intent)
    }

    private fun showInContextUI() {
        AlertDialog.Builder(this).apply {
            setTitle("Warning")
            setMessage("Need You Accept Permission")
            setPositiveButton("OK") { p0, p1 ->
                requestPermissionLauncher.launch(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
            setNegativeButton("Cancel"){p0,p1 ->
                Log.d("TAG", "showInContextUI: Ok fine ! ")
            }
        }.show()
    }


}