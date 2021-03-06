package com.minhbka.facedetectionmlkit.photo

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.minhbka.facedetectionmlkit.common.CameraDirection
import com.minhbka.facedetectionmlkit.R
import com.minhbka.facedetectionmlkit.common.Effect
import com.minhbka.facedetectionmlkit.photo.detector.FaceDetector
import com.minhbka.facedetectionmlkit.photo.detector.SimpleFaceDetector
import com.minhbka.facedetectionmlkit.photo.detector.SmileyFaceDetector
import com.tbruyelle.rxpermissions2.RxPermissions
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.result.BitmapPhoto
import io.fotoapparat.selector.*
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var rxPermissions: RxPermissions
    private lateinit var disposable: Disposable
    private lateinit var foto:Fotoapparat
    //private var isUsingFrontCamera = true
    private var cameraDirection = CameraDirection.FRONT


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rxPermissions = RxPermissions(this)
        foto = Fotoapparat(
            context = this,
            view = cameraView,
            scaleType = ScaleType.CenterCrop,
            lensPosition = front(),
            cameraConfiguration = cameraConfiguration
        )
        requestPermissions()
        //detection = SmileyFaceDetector()
        //detection = SimpleFaceDetector(this)
        resultsGroup.visibility = View.GONE
    }

    private val detection : FaceDetector by lazy {
        val effect = intent?.extras?.getString(EFFECT_ARG)?.let {
            Effect.valueOf(it)
        } ?: Effect.BOX
        when (effect) {
            Effect.BLUR,
            Effect.TROLL,
            Effect.BOX -> SimpleFaceDetector(
                this,
                effect
            )
            Effect.OUTLINE -> SmileyFaceDetector()
        }
    }
    override fun onResume() {
        super.onResume()
        if (rxPermissions.hasCameraPermission) startCamera()
    }

    override fun onPause() {
        super.onPause()
        if (rxPermissions.hasCameraPermission) stopCamera()
    }
    private fun startCamera() {
        foto.start()
        shootButton.setOnClickListener {
            foto.takePicture()
                .toBitmap()
                .transform { it.rotate() }
                .whenAvailable {photo->
                    photo?.let {bitmap->
                        detection.process(bitmap, ::onProcessed, cameraDirection)
                    }
                }
        }
        changeCameraButton.setOnClickListener {
            val camera = if (cameraDirection == CameraDirection.FRONT) back() else front()
            foto.switchTo(camera, cameraConfiguration)
            cameraDirection = when(cameraDirection){
                CameraDirection.BACK -> CameraDirection.FRONT
                CameraDirection.FRONT -> CameraDirection.BACK
            }
            //isUsingFrontCamera = !isUsingFrontCamera

        }

        tryAgainButton.setOnClickListener {
            resultsGroup.visibility = View.GONE
        }
    }

    private fun stopCamera() {
        foto.stop()
    }

    private fun onProcessed(bitmapWithFace: Bitmap){
        Log.d(TAG, "onProcessed")
        resultView.setImageBitmap(bitmapWithFace)
        resultsGroup.visibility = View.VISIBLE
    }
    private fun requestPermissions() {
        disposable = rxPermissions.request(android.Manifest.permission.CAMERA).subscribe{ granted->

            if(!granted) finish()
        }
    }

    private val RxPermissions.hasCameraPermission get() = isGranted(android.Manifest.permission.CAMERA)

    private fun BitmapPhoto.rotate(): Bitmap {

        val rotationCompensation = -rotationDegrees.toFloat()
        val source = bitmap
        val matrix = Matrix()
        if (cameraDirection == CameraDirection.FRONT) matrix.preScale(1f, -1f)
        matrix.postRotate(rotationCompensation)
        return Bitmap.createBitmap(source, 0,0, source.width, source.height, matrix, true)

    }

    companion object {
        private const val TAG = "MainActivity"
        private const val EFFECT_ARG = "EFFECT_PHOTO"
        fun start(context: Context, effect: Effect) =
            context.startActivity(Intent(context, MainActivity::class.java).apply {
                putExtra(EFFECT_ARG, effect.name)
            })
    }
}



private val cameraConfiguration = CameraConfiguration(
    pictureResolution = {
        filter {
            it.width in 500..800
        }.minBy { it.area }

    },
    previewResolution = highestResolution(),
    previewFpsRange = highestFps(),
    focusMode = firstAvailable(continuousFocusPicture(), autoFocus(), fixed()),
    flashMode = firstAvailable(
        //autoRedEye(),
        //autoFlash(),
        //torch(),
        off()),
    antiBandingMode = firstAvailable(auto(), hz50(), hz60(), none()),
    jpegQuality = manualJpegQuality(90)
)