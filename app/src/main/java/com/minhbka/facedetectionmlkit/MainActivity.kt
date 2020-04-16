package com.minhbka.facedetectionmlkit

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.minhbka.facedetectionmlkit.detector.FaceDetector
import com.minhbka.facedetectionmlkit.detector.SimpleFaceDetector
import com.minhbka.facedetectionmlkit.detector.SmileyFaceDetector
import com.tbruyelle.rxpermissions2.RxPermissions
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.result.BitmapPhoto
import io.fotoapparat.selector.*
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {

    private lateinit var rxPermissions: RxPermissions
    private lateinit var disposable: Disposable
    private lateinit var foto:Fotoapparat
    private var isUsingFrontCamera = true
    private lateinit var detection : FaceDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rxPermissions = RxPermissions(this)
        foto = Fotoapparat(
            context = this,
            view = cameraView,
            scaleType = ScaleType.CenterCrop,
            lensPosition = front(),
            cameraConfiguration = cameraConfiguration)
        requestPermissions()
        detection = SmileyFaceDetector()
        //detection = SimpleFaceDetector(resources)
        resultsGroup.visibility = View.GONE
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
                        detection.process(bitmap, ::onProcessed)
                    }
                }
        }
        changeCameraButton.setOnClickListener {
            val camera = if (isUsingFrontCamera) back() else front()
            foto.switchTo(camera, cameraConfiguration)
            isUsingFrontCamera = !isUsingFrontCamera
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
        if (isUsingFrontCamera) matrix.preScale(1f, -1f)
        matrix.postRotate(rotationCompensation)
        return Bitmap.createBitmap(source, 0,0, source.width, source.height, matrix, true)

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