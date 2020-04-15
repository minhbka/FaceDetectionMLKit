package com.minhbka.facedetectionmlkit.detector

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.minhbka.facedetectionmlkit.graphic.BoxFacesGraphic
private const val TAG = "SimpleFaceDetector"
class SimpleFaceDetector : FaceDetector {
    private val options =  FirebaseVisionFaceDetectorOptions.Builder()
        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
        .build()

    private val detector : FirebaseVisionFaceDetector =
        FirebaseVision.getInstance().getVisionFaceDetector(options)
    override fun process(bitmap: Bitmap, callback: (Bitmap) -> Unit) {
        val fireImage = FirebaseVisionImage.fromBitmap(bitmap)
        detector.detectInImage(fireImage)
            .addOnSuccessListener { faces->
                val bitmapWithFace = BoxFacesGraphic.draw(bitmap,faces)
                callback(bitmapWithFace)

            }
            .addOnFailureListener { e->Log.e(TAG, "$e") }
    }
}