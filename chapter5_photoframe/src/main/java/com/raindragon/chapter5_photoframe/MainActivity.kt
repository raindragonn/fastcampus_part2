package com.raindragon.chapter5_photoframe

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.raindragon.chapter5_photoframe.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_PERMISSION_CODE = 1000
        const val REQUEST_CONTENT_CODE = 2000
        const val EXTRA_PHOTO = "EXTRA_PHOTO"
        const val EXTRA_PHOTO_LIST_SIZE = "EXTRA_PHOTO_LIST_SIZE"
    }

    val TAG : String = MainActivity::class.java.simpleName

    private lateinit var binding: ActivityMainBinding

    private val imageUriList: MutableList<Uri> = mutableListOf()
    private val imageViewList: List<ImageView> by lazy {
        mutableListOf(
            binding.iv1,
            binding.iv2,
            binding.iv3,
            binding.iv4,
            binding.iv5,
            binding.iv6
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewInit()
    }

    private fun viewInit() {
        binding.btnPhotoAdd.setOnClickListener {
            onAddPhotoBtn()
        }
        binding.btnPhotoFrame.setOnClickListener {
            onStartPhotoFrameBtn()
        }
    }

    private fun onAddPhotoBtn() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                // TODO 권한이 잘 부여 되었을 때 갤러리에서 사진을 선택하는 기능
                navigationPhotos()
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                // TODO 교육용 팝업 확인 후 권한 팝업을 띄우는 기능
                showPermissionContextPopup()
            }
            else -> {
                requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_PERMISSION_CODE
                )
            }
        }
    }

    private fun onStartPhotoFrameBtn() {
        startActivity(Intent(this, PhotoFrameActivity::class.java).apply {
            imageUriList.forEachIndexed { index, uri ->
                putExtra("EXTRA_PHOTO$index", uri.toString())
                Log.d(TAG, "onStartPhotoFrameBtn: EXTRA_PHOTO$index")
            }
            putExtra(EXTRA_PHOTO_LIST_SIZE, imageUriList.size)
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    navigationPhotos()
                } else
                    Toast.makeText(this, "권한을 거부 하셨습니다.", Toast.LENGTH_SHORT).show()
            }
            else -> {

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK)
            return

        when (requestCode) {
            REQUEST_CONTENT_CODE -> {
                data?.data?.let {
                    if (imageUriList.size == 6) {
                        Toast.makeText(this, "이미 사진이 꽉 찼습니다", Toast.LENGTH_SHORT).show()
                        return
                    }

                    imageUriList.add(it)
                    imageViewList[imageUriList.size - 1].setImageURI(it)

                } ?: run {
                    Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }


            }
            else -> {
                Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigationPhotos() {
        Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }.run {
            startActivityForResult(this, REQUEST_CONTENT_CODE)
        }
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("앱에서 사진을 불러오기위해 권한이 필요합니다.")
            .setPositiveButton("확인") { _, _ ->
                requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    1000
                )
            }
            .setNegativeButton("취소") { _, _ ->

            }
            .create()
            .show()
    }


}