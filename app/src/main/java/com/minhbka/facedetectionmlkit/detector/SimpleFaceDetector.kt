package com.minhbka.facedetectionmlkit.detector

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.minhbka.facedetectionmlkit.CameraDirection
import com.minhbka.facedetectionmlkit.graphic.BlurGraphic
import com.minhbka.facedetectionmlkit.graphic.BoxFacesGraphic
import com.minhbka.facedetectionmlkit.graphic.TrollGraphic

private const val TAG = "SimpleFaceDetector"
class SimpleFaceDetector(private val context: Context) : FaceDetector {
    private val options =  FirebaseVisionFaceDetectorOptions.Builder()
        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
        .build()

    private val detector : FirebaseVisionFaceDetector =
        FirebaseVision.getInstance().getVisionFaceDetector(options)
    override fun process(bitmap: Bitmap, callback: (Bitmap) -> Unit, cameraDirection: CameraDirection) {
        val fireImage = FirebaseVisionImage.fromBitmap(bitmap)
        detector.detectInImage(fireImage)
            .addOnSuccessListener { faces->
                //val bitmapWithFaces = BoxFacesGraphic.draw(bitmap,faces)
                //val bitmapWithFaces = TrollGraphic(resources).draw(bitmap, faces)
                val bitmapWithFaces = BlurGraphic(context, cameraDirection ).draw(bitmap, faces)
                callback(bitmapWithFaces)

            }
            .addOnFailureListener { e->Log.e(TAG, "$e") }
    }
}