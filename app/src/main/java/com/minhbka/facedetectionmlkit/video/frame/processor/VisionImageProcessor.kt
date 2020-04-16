package com.minhbka.facedetectionmlkit.video.frame.processor

import android.graphics.Bitmap
import com.google.firebase.ml.common.FirebaseMLException
import com.minhbka.facedetectionmlkit.common.FrameMetadata
import com.minhbka.facedetectionmlkit.video.GraphicOverlayView
import java.nio.ByteBuffer

interface VisionImageProcessor {
    /** Processes the images with the underlying machine learning models.  */
    @Throws(FirebaseMLException::class)
    fun process(data: ByteBuffer, frameMetadata: FrameMetadata, graphicOverlayView: GraphicOverlayView)

    /** Processes the bitmap images.  */
    fun process(bitmap: Bitmap, graphicOverlayView: GraphicOverlayView)

    /** Stops the underlying machine learning model and release resources.  */
    fun stop()
}