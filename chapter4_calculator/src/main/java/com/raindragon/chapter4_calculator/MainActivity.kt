package com.raindragon.chapter4_calculator

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.room.Room
import com.raindragon.chapter4_calculator.databinding.ActivityMainBinding
import com.raindragon.chapter4_calculator.model.History

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var isOperator = false
    private var hasOprator = false

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "historyDB"
        ).build()
    }

    fun onBtnClicked(view: View) {
        when (view.id) {
            binding.btn0.id -> numberBtnClicked("0")
            binding.btn1.id -> numberBtnClicked("1")
            binding.btn2.id -> numberBtnClicked("2")
            binding.btn3.id -> numberBtnClicked("3")
            binding.btn4.id -> numberBtnClicked("4")
            binding.btn5.id -> numberBtnClicked("5")
            binding.btn6.id -> numberBtnClicked("6")
            binding.btn7.id -> numberBtnClicked("7")
            binding.btn8.id -> numberBtnClicked("8")
            binding.btn9.id -> numberBtnClicked("9")
            binding.btnPlus.id -> operatorClicked("+")
            binding.btnMinus.id -> operatorClicked("-")
            binding.btnMulti.id -> operatorClicked("*")
            binding.btnModulo.id -> operatorClicked("%")
            binding.btnDivider.id -> operatorClicked("/")
        }
    }

    private fun operatorClicked(operator: String) {
        if (binding.tvExpression.text.isEmpty())
            return

        when {
            isOperator -> {
                val text = binding.tvExpression.text.toString()
                binding.tvExpression.text = text.dropLast(1) + operator
            }

            hasOprator -> {
                Toast.makeText(this, "???????????? ??? ?????? ????????? ??? ???????????????.", Toast.LENGTH_SHORT).show()
                return
            }

            else -> {
                binding.tvExpression.append(" $operator")
            }
        }

        val ssb = SpannableStringBuilder(binding.tvExpression.text)
        ssb.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.green)),
            binding.tvExpression.text.length - 1,
            binding.tvExpression.text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvExpression.text = ssb
        isOperator = true
        hasOprator = true
    }


    private fun numberBtnClicked(number: String) {
        if (isOperator) {
            binding.tvExpression.append(" ")
        }

        isOperator = false

        val expressionText = binding.tvExpression.text.split(" ")

        if (expressionText.isNotEmpty() && expressionText.last().length > 15) {
            Toast.makeText(this, "15?????? ????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show()
            return
        } else if (expressionText.last().isEmpty() && number == "0") {
            Toast.makeText(this, "0??? ?????? ?????? ??? ??? ????????????.", Toast.LENGTH_SHORT).show()
            return
        }

        binding.tvExpression.append(number)

        binding.tvResult.text = calculateExpression()
    }

    fun onBtnCleared(view: View) {
        binding.tvExpression.text = ""
        binding.tvResult.text = ""
        isOperator = false
        hasOprator = false
    }

    fun onBtnResulted(view: View) {
        val expressionTexts = binding.tvExpression.text.split(" ")

        if (binding.tvExpression.text.isEmpty() || expressionTexts.size == 1) {
            return
        }

        if (expressionTexts.size != 3 && hasOprator) {
            Toast.makeText(this, "?????? ???????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show()
            return
        }

        if (expressionTexts[0].isNumber().not() || expressionTexts[2].isNumber().not()) {
            Toast.makeText(this, "????????? ??????????????????.", Toast.LENGTH_SHORT).show()
            return
        }

        val expressionText = binding.tvExpression.text.toString()
        val resultText = calculateExpression()


        // TODO ????????? ???????????? ??????
        Thread {
            db.historyDao().insert(
                History(
                    null,
                    expressionText,
                    resultText
                )
            )
        }.start()

        binding.tvResult.text = ""
        binding.tvExpression.text = resultText

        isOperator = false
        hasOprator = false

    }

    private fun calculateExpression(): String {
        val expressionTexts = binding.tvExpression.text.split(" ")
        if (hasOprator.not() || expressionTexts.size != 3) {
            return ""
        } else if (expressionTexts[0].isNumber().not() || expressionTexts[2].isNumber().not()) {
            return ""
        }

        val exp1 = expressionTexts[0].toBigInteger()
        val exp2 = expressionTexts[2].toBigInteger()
        val operator = expressionTexts[1]
        return when (operator) {
            "+" -> (exp1 + exp2).toString()
            "-" -> (exp1 - exp2).toString()
            "*" -> (exp1 * exp2).toString()
            "/" -> (exp1 / exp2).toString()
            "%" -> (exp1 % exp2).toString()
            else -> ""
        }
    }

    fun onBtnHistory(view: View) {
        binding.clHistory.isVisible = true
        binding.llHistory.removeAllViews()

        // TODO ???????????? ?????? ?????? ????????????
        Thread {
            //Collection ?????? ????????? reversed
            db.historyDao().getAll().reversed().forEach {
                runOnUiThread {
                    val historyView = LayoutInflater.from(this)
                        .inflate(R.layout.history_row, null, false)

                    val historyView2 = layoutInflater.inflate(R.layout.history_row, null, false)

                    historyView2.findViewById<TextView>(R.id.tv_expression).text = it.expression
                    historyView2.findViewById<TextView>(R.id.tv_result).text = "= ${it.result}"

                    binding.llHistory.addView(historyView2)
                }
            }
        }.start()

        // TODO ?????? ?????? ?????? ????????????
    }

    fun onBtnHistoryClose(view: View) {
        binding.clHistory.isVisible = false
    }

    fun onBtnHistoryClear(view: View) {
        binding.llHistory.removeAllViews()
        Thread {
            db.historyDao().deleteAll()
        }.start()
        // TODO ???????????? ?????? ?????? ??????
        // TODO ????????? ?????? ????????????
    }

}