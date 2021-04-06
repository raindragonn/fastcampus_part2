package com.raindragon.chapter7_audiorecorder

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

// Created by raindragonn on 2021/04/06.

class SoundVisualizerView(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    companion object {
        //    선의 길이 = length
        //    선의 두깨 = width
        //    선 사이 여백 = space
        private const val LINE_WIDTH = 10F
        private const val LINE_SPACE = 15F

        // 진폭 최대크기
        private const val MAX_AMPLITUDE = Short.MAX_VALUE.toFloat()

        private const val ACTION_INTERVAL = 20L
    }

    // 진폭값
    var onRequestCurrentAmplitude: (() -> Int)? = null

    // anti_alia_flag 계단 현상 방지
    private val amplitudePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        //색상 지정
        color = context.getColor(R.color.purple_500)
        strokeWidth = LINE_WIDTH
        // 라인의 양끝처리
        strokeCap = Paint.Cap.ROUND
    }

    // 그려야 할 영역
    private var drawingWidth: Int = 0

    private var drawingHeight: Int = 0

    // 진폭 리스트
    private var drawingAmplitudes: List<Int> = emptyList()
    private var isReplaying: Boolean = false
    private var replayingPosition: Int = 0

    private val visualizeRepeatAction: Runnable = object : Runnable {
        override fun run() {
            //Amplitude 가져와서 draw 로직
            if (isReplaying.not()) {
                val currentAmplitude = onRequestCurrentAmplitude?.invoke() ?: 0
                drawingAmplitudes = listOf(currentAmplitude) + drawingAmplitudes
            } else {
                replayingPosition++
            }

            // 뷰 갱신 (onDraw() Call)
            invalidate()
            //20L 이유 = 1초에 45~65프레임을 나타내기 위함
            handler?.postDelayed(this, ACTION_INTERVAL)
        }
    }

    // 새로 확인된 영역
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        drawingWidth = w
        drawingHeight = h
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return

        // 뷰의 센터 결정
        val centerY = drawingHeight / 2f
        // 시작 포인트
        var offsetX = drawingWidth.toFloat()

        drawingAmplitudes
            .let { amplitudes ->
                if (isReplaying) {
                    amplitudes.takeLast(replayingPosition)
                } else {
                    amplitudes
                }
            }
            .forEach { amplitude ->
                // 최댓값 대비 퍼센트 * 길이 * 전체 길이 조정
                val lineLength = amplitude / MAX_AMPLITUDE * drawingHeight * 0.8F

                offsetX -= LINE_SPACE

                // 화면을 벗어나는 영역
                if (offsetX < 0) return@forEach

                // 캔버스는 왼쪽 최상단이 0,0
                canvas.drawLine(
                    offsetX,
                    centerY - lineLength / 2F,
                    offsetX,
                    centerY + lineLength / 2F,
                    amplitudePaint
                )
            }
    }

    fun startVisualizing(isReplaying: Boolean) {
        this.isReplaying = isReplaying
        handler?.post(visualizeRepeatAction)
    }

    fun stopVisualizing() {
        replayingPosition = 0
        handler?.removeCallbacks(visualizeRepeatAction)
    }

    fun clearVisualization() {
        drawingAmplitudes = emptyList()
        invalidate()
    }
}