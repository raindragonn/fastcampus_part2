package com.raindragon.chapter2_lotto

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.raindragon.chapter2_lotto.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val TAG: String = MainActivity::class.java.simpleName
    private lateinit var binding: ActivityMainBinding

    private var didRun = false
    private val pickNumberSet = hashSetOf<Int>()

    private val tvList: List<TextView> by lazy {
        listOf(
            binding.tv1,
            binding.tv2,
            binding.tv3,
            binding.tv4,
            binding.tv5,
            binding.tv6
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewInit()
    }

    private fun viewInit() {
        binding.np.minValue = 1
        binding.np.maxValue = 45
        binding.btnRun.setOnClickListener {
            val list = getRandomNumber()
            didRun = true

            list.forEachIndexed { index, i ->
                val textView = tvList[index]
                textView.text = i.toString()
                textView.isVisible = true
                colorSet(i, textView)
            }
        }

        binding.btnAdd.setOnClickListener {
            if (didRun) {
                Toast.makeText(this, "초기화 후에 시도해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pickNumberSet.size >= 6) {
                Toast.makeText(this, "번호는 6개까지만 선택할 수 있습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pickNumberSet.contains(binding.np.value)) {
                Toast.makeText(this, "중복된 번호는 선택할 수 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val textView = tvList[pickNumberSet.size]
            textView.isVisible = true
            textView.text = binding.np.value.toString()

            colorSet(binding.np.value, textView)
            pickNumberSet.add(binding.np.value)
        }

        binding.btnReset.setOnClickListener {
            pickNumberSet.clear()
            tvList.forEach {
                it.isVisible = false
                it.text = ""
            }
            didRun = false
        }
    }

    private fun getRandomNumber(): List<Int> = mutableListOf<Int>().run {
        for (i in 1..45) {
            if (pickNumberSet.contains(i)) {
                continue
            }
            add(i)
        }
        shuffle()
        pickNumberSet.toList() + subList(0, 6 - pickNumberSet.size)
    }.sorted()

    private fun colorSet(value: Int, view: TextView) {
        when (value) {
            in 1..10 -> view.background =
                ContextCompat.getDrawable(this, R.drawable.circile_yellow)
            in 11..20 -> view.background =
                ContextCompat.getDrawable(this, R.drawable.circile_blue)
            in 21..30 -> view.background =
                ContextCompat.getDrawable(this, R.drawable.circile_red)
            in 31..40 -> view.background =
                ContextCompat.getDrawable(this, R.drawable.circile_gray)
            else -> ContextCompat.getDrawable(this, R.drawable.circile_green)
        }
    }
}