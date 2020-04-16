package com.minhbka.facedetectionmlkit.photo.detector

import android.graphics.Bitmap
import com.minhbka.facedetectionmlkit.common.CameraDirection

interface FaceDetector {
    fun process(
        bitmap: Bitmap,
        callback: (Bitmap) -> Unit,
        cameraDirection: CameraDirection
    )
}