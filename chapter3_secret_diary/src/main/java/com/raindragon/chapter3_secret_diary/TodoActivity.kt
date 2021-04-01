package com.raindragon.chapter3_secret_diary

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener
import com.raindragon.chapter3_secret_diary.databinding.ActivityTodoBinding

class TodoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTodoBinding
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val detailPreferences = getSharedPreferences("diary", Context.MODE_PRIVATE)

        val runnable = Runnable {
            getSharedPreferences("diary", Context.MODE_PRIVATE).edit {
                putString("detail", binding.etMemo.text.toString())
            }
        }

        binding.apply {
            etMemo.setText(detailPreferences.getString("detail", ""))
            etMemo.addTextChangedListener {
                // 이전에 실행중인 콜백 runnable 객체를 지운다
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, 500)
            }
        }
    }
}