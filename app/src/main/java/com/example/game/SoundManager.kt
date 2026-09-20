package com.example.game

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

object SoundManager {
    private val scope = CoroutineScope(Dispatchers.Default)

    // Synthesize and play custom synth frequency sweep
    private fun playSynthSound(
        startFreq: Float,
        endFreq: Float,
        durationMs: Int,
        waveType: String = "SINE"
    ) {
        scope.launch {
            try {
                val sampleRate = 22050
                val minBufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                // Short array is 16-bit (2 bytes) per element. So minSamples is minBufferSize / 2.
                val minSamples = if (minBufferSize > 0) minBufferSize / 2 else 1024
                val calculatedSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val numSamples = maxOf(calculatedSamples, minSamples)
                val buffer = ShortArray(numSamples)

                val activeSamples = minOf(calculatedSamples, numSamples)

                for (i in 0 until numSamples) {
                    if (i >= activeSamples) {
                        // Pad extra buffer space with silence
                        buffer[i] = 0
                        continue
                    }
                    val t = i.toDouble() / sampleRate
                    val progress = i.toDouble() / activeSamples
                    val currentFreq = startFreq + (endFreq - startFreq) * progress
                    
                    // Simple envelope to fade out near the end
                    val envelope = if (progress > 0.8) {
                        (1.0 - progress) / 0.2
                    } else if (progress < 0.1) {
                        progress / 0.1
                    } else {
                        1.0
                    }

                    val angle = 2.0 * Math.PI * currentFreq * t
                    val sampleVal = when (waveType) {
                        "SQUARE" -> if (sin(angle) >= 0) 1.0 else -1.0
                        "TRIANGLE" -> {
                            val x = (angle / (2.0 * Math.PI)) % 1.0
                            if (x < 0.5) 4.0 * x - 1.0 else 3.0 - 4.0 * x
                        }
                        else -> sin(angle) // SINE
                    }

                    // Scaled amplitude
                    val amplitude = 8000.0 * envelope
                    buffer[i] = (sampleVal * amplitude).toInt().toShort()
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()
                
                // Allow sound to play fully before releasing resources
                val delayTime = maxOf(durationMs.toLong(), (numSamples * 1000L) / sampleRate)
                kotlinx.coroutines.delay(delayTime + 50L)
                audioTrack.stop()
                audioTrack.release()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    // Sound 1: Stretch / Pull sound (A sliding pleasant chirp)
    fun playStretchSound() {
        playSynthSound(
            startFreq = 400f,
            endFreq = 800f,
            durationMs = 90,
            waveType = "SINE"
        )
    }

    // Sound 2: Hit Obstacle / Wall sound (Low buzzer bump sound)
    fun playHitObstacleSound() {
        playSynthSound(
            startFreq = 220f,
            endFreq = 120f,
            durationMs = 150,
            waveType = "TRIANGLE"
        )
    }

    // Sound 3: Level Complete Sound (An ascending happy melody arpeggio)
    fun playLevelCompleteSound() {
        scope.launch {
            // Arpeggio notes: C5 (523Hz), E5 (659Hz), G5 (784Hz), C6 (1046Hz)
            val notes = listOf(523.25f, 659.25f, 784.00f, 1046.50f)
            for (freq in notes) {
                playSynthSound(
                    startFreq = freq,
                    endFreq = freq,
                    durationMs = 120,
                    waveType = "SINE"
                )
                kotlinx.coroutines.delay(100L)
            }
        }
    }
}
