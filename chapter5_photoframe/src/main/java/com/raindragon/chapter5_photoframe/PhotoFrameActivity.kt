package com.raindragon.chapter5_photoframe

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.raindragon.chapter5_photoframe.databinding.ActivityPhotoFrameBinding
import java.util.*
import kotlin.concurrent.timer

class PhotoFrameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotoFrameBinding
    private val photoList = mutableListOf<Uri>()
    private var timer: Timer? = null
    private var currentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoFrameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getPhotoListFromIntent()
    }

    private fun getPhotoListFromIntent() {
        val size = intent.getIntExtra(MainActivity.EXTRA_PHOTO_LIST_SIZE, 0)

        for (i in 0..size) {
            intent.getStringExtra("EXTRA_PHOTO$i")?.let {
                photoList.add(Uri.parse(it))
            }
        }
    }

    private fun startTimer() {
        // MainThread 가 아님
        timer = timer(period = 5 * 1000) {
            runOnUiThread {
                val current = currentPosition
                val next = if (photoList.size <= currentPosition + 1) 0 else current + 1

                binding.ivPhotoBack.setImageURI(photoList[current])

                binding.ivPhotoFront.alpha = 0f
                binding.ivPhotoFront.setImageURI(photoList[next])
                binding.ivPhotoFront.animate()
                    .alpha(1.0f)
                    .setDuration(1000)
                    .start()

                currentPosition = next
            }
        }
    }

    override fun onStop() {
        super.onStop()
        timer?.cancel()
    }

    override fun onStart() {
        super.onStart()
        startTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}