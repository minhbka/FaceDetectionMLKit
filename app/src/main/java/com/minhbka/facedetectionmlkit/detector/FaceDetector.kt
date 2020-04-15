package com.minhbka.facedetectionmlkit.detector

import android.graphics.Bitmap

interface FaceDetector {
    fun process(bitmap: Bitmap, callback: (Bitmap) -> Unit)
}