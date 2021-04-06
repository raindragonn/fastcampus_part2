package com.raindragon.chapter7_audiorecorder

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton

// Created by raindragonn on 2021/04/06.

// 안드로이드는 버전간의 호환성을 위해 기존 클래스를 랩핑 해서 새로 출시한 기능을 지원하기 위한 것이 Appcompat 이다.

class RecordButton(
    context: Context,
    attrs: AttributeSet
) : AppCompatImageButton(context, attrs) {
    init {
        setBackgroundResource(R.drawable.bg_recorder)
    }

    fun updateIconWithState(state: State) {
        when (state) {
            State.BEFORE_RECORDING -> {
                setImageResource(R.drawable.ic_record)
            }
            State.ON_RECORDING -> {
                setImageResource(R.drawable.ic_stop)
            }
            State.AFTER_RECORDING -> {
                setImageResource(R.drawable.ic_play)
            }
            State.ON_PLAYING -> {
                setImageResource(R.drawable.ic_pause)
            }
        }
    }
}