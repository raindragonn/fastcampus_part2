package com.raindragon.chapter3_secret_diary

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.raindragon.chapter3_secret_diary.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var isPasswordChangeMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewInit()
    }

    private fun viewInit() {
        binding.np1.minValue = 0
        binding.np1.maxValue = 9

        binding.np2.minValue = 0
        binding.np2.maxValue = 9

        binding.np3.minValue = 0
        binding.np3.maxValue = 9

        binding.btnOpen.setOnClickListener {
            onBtnOpen()
        }
        binding.btnChangePassword.setOnClickListener {
            onBtnPasswordChange()
        }
    }

    private fun onBtnPasswordChange() {
        val passwordPreference = getSharedPreferences("password", Context.MODE_PRIVATE)
        val passwordFromUser = "${binding.np1.value}${binding.np2.value}${binding.np3.value}"

        if (isPasswordChangeMode) {
//            preference commit = 동기적
//            preference apply = 비동기적
            passwordPreference.edit(true) {
                putString("password", passwordFromUser)
            }
            isPasswordChangeMode = false
            Toast.makeText(this, "패스워드가 변경되었습니다.", Toast.LENGTH_SHORT).show()
            binding.btnChangePassword.setBackgroundColor(Color.BLACK)

        } else {
            if (passwordPreference.getString("password", "000").equals(passwordFromUser)) {

                isPasswordChangeMode = true
                Toast.makeText(this, "변경할 패스워드를 입력해주세요.", Toast.LENGTH_SHORT).show()
                binding.btnChangePassword.setBackgroundColor(Color.RED)

            } else {
                showErrorDialog()
            }
        }
    }

    private fun showErrorDialog() {
        AlertDialog.Builder(this)
            .setTitle("실패")
            .setMessage("로그인 실패 했습니다.")
            .setPositiveButton("확인") { _, _ -> }
            .show()
    }

    private fun onBtnOpen() {
        if (isPasswordChangeMode) {
            Toast.makeText(this, "비밀번호 변경 중입니다", Toast.LENGTH_SHORT).show()
            return
        }

        val passwordPreference = getSharedPreferences("password", Context.MODE_PRIVATE)
        val passwordFromUser = "${binding.np1.value}${binding.np2.value}${binding.np3.value}"

        if (passwordPreference.getString("password", "000").equals(passwordFromUser)) {
            startActivity(Intent(this, TodoActivity::class.java))
        } else {
            showErrorDialog()
        }

    }
}








