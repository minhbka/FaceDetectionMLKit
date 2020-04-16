package com.minhbka.facedetectionmlkit.video.frame.graphic

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.graphics.withSave
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour
import com.minhbka.facedetectionmlkit.video.GraphicOverlayView

class FaceContoursGraphic(
    overlayView: GraphicOverlayView,
    private val firebaseVisionFace: FirebaseVisionFace?
) : GraphicOverlayView.Graphic(overlayView){

    companion object{
        private const val BOX_STROKE_WIDTH = 5.0f
    }

    private val whitePaint = Paint().apply {
        color = Color.WHITE
    }

    private val boxPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = BOX_STROKE_WIDTH
    }

    override fun draw(canvas: Canvas) {
        val face = firebaseVisionFace ?: return
        // Draws a circle at the position of the detected face, with the face's track id below.
        // An offset is used on the Y axis in order to draw the circle, face id and happiness level in the top area
        // of the face's bounding box
        val x = translateX(face.boundingBox.centerX().toFloat())
        val y = translateY(face.boundingBox.centerY().toFloat())

        canvas.withSave {
            // Draws a bounding box around the face tilting with the head
            val xOffset = scaleX(face.boundingBox.width() / 2.0f)
            val yOffset = scaleY(face.boundingBox.height() / 2.0f)
            val left = x - xOffset
            val top = y - yOffset
            val right = x + xOffset
            val bottom = y + yOffset
            canvas.rotate(face.headEulerAngleZ, x, y)
            canvas.drawRect(left, top, right, bottom, boxPaint)
        }

        drawContourPosition(canvas, face, FirebaseVisionFaceContour.ALL_POINTS)
    }

    private fun drawContourPosition(canvas: Canvas, face: FirebaseVisionFace, contourID: Int) {
        val contour: FirebaseVisionFaceContour = face.getContour(contourID)
        contour.points.forEach { point ->
            canvas.drawCircle(
                translateX(point.x),
                translateY(point.y),
                2f, whitePaint
            )
        }
    }
}