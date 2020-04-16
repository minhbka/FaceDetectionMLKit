package com.minhbka.facedetectionmlkit.detector

import android.graphics.Bitmap
import com.minhbka.facedetectionmlkit.CameraDirection

interface FaceDetector {
    fun process(
        bitmap: Bitmap,
        callback: (Bitmap) -> Unit,
        cameraDirection: CameraDirection)
}