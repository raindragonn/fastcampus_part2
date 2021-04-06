package com.raindragon.chapter7_audiorecorder

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.raindragon.chapter7_audiorecorder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        // 퍼미션 권한 요청 코드
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201
    }

    private lateinit var binding: ActivityMainBinding
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private val recordingFilePath: String by lazy {
        "${externalCacheDir?.absolutePath}/recording.3gp"
    }

    // 필요한 퍼미션 어레이
    private val requirePermissions = arrayOf(Manifest.permission.RECORD_AUDIO)

    private var state = State.BEFORE_RECORDING  // 재생 상태 아래 커스텀 setter 의 경우 초깃값에서 불리진 않는다
        set(value) {
            field = value
            binding.btnReset.isEnabled = (value == State.AFTER_RECORDING ||
                    value == State.ON_PLAYING)
            binding.recordBtn.updateIconWithState(value)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestAudioPermission()

        initViews()
        initVariables()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val audioRecordPermissionGranted =
            requestCode == REQUEST_RECORD_AUDIO_PERMISSION && grantResults.contains(PackageManager.PERMISSION_DENIED)
                .not()

        if (audioRecordPermissionGranted.not()) {
            finish()
        }
    }

    private fun initViews() {
        binding.recordBtn.updateIconWithState(state)
        binding.recordBtn.setOnClickListener {
            when (state) {
                State.BEFORE_RECORDING -> startRecording()
                State.ON_RECORDING -> stopRecording()
                State.AFTER_RECORDING -> startPlaying()
                State.ON_PLAYING -> stopPlaying()
            }
        }

        binding.btnReset.setOnClickListener {
            stopPlaying()
            binding.viewSoundVisual.clearVisualization()
            binding.tvRecordTime.clearCountTime()
            state = State.BEFORE_RECORDING

        }

        binding.viewSoundVisual.onRequestCurrentAmplitude = {
            recorder?.maxAmplitude ?: 0
        }
    }

    private fun initVariables() {
        state = State.BEFORE_RECORDING
    }

    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(recordingFilePath)
            prepare()
        }

        recorder?.start()
        binding.viewSoundVisual.startVisualizing(false)
        binding.tvRecordTime.startCountUp()
        state = State.ON_RECORDING
    }

    private fun stopRecording() {
        recorder?.run {
            stop()
            release()
        }
        recorder = null
        binding.viewSoundVisual.stopVisualizing()
        binding.tvRecordTime.stopCountUp()
        state = State.AFTER_RECORDING
    }

    private fun startPlaying() {
        player = MediaPlayer()
            .apply {
                setDataSource(recordingFilePath)
                prepare()
            }
        player?.setOnCompletionListener {
            stopPlaying()
            state = State.AFTER_RECORDING
        }
        player?.start()
        binding.viewSoundVisual.startVisualizing(true)
        binding.tvRecordTime.startCountUp()
        state = State.ON_PLAYING
    }

    private fun stopPlaying() {
        player?.release()
        player = null
        binding.viewSoundVisual.stopVisualizing()
        binding.tvRecordTime.stopCountUp()
        state = State.AFTER_RECORDING
    }

    private fun requestAudioPermission() {
        requestPermissions(
            requirePermissions,
            REQUEST_RECORD_AUDIO_PERMISSION
        )
    }
}

/*
* custom drawing
* Canvas 는 무엇을 그릴지 (내용)
* Paint 는 어떻게 그릴지  (방법)
* 주의점
* onDraw() 내부에서 Paint 객체를 생성해서는 안된다.
* onDraw() 자체가 굉장히 빈번히 호출 되기 때문에 비용이 큰 작업을 하면 UI 성능을 저하 시킬수있기 때문이다.
*
* 실제로 그려야할 영역을 파악해야 한다.(안드로이드의 다양한 디스플레이 크기에 따른 파편화로 인하여)
*
*/