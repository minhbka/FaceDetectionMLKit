package com.minhbka.facedetectionmlkit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.selector.back
import io.fotoapparat.selector.front
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var rxPermissions: RxPermissions
    private lateinit var disposable: Disposable
    private lateinit var foto:Fotoapparat
    private var isUsingFrontCamera = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rxPermissions = RxPermissions(this)
        foto = Fotoapparat(context = this, view = cameraView, scaleType = ScaleType.CenterCrop, lensPosition = front())
        requestPermissions()
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
                .whenAvailable {
                    TODO("Took photo")
                }
        }
        changeCameraButton.setOnClickListener {
            val camera = if (isUsingFrontCamera) back() else front()
            foto.switchTo(camera, CameraConfiguration.default())
            isUsingFrontCamera = !isUsingFrontCamera
        }
    }

    private fun stopCamera() {
        foto.stop()
    }

    private fun requestPermissions() {
        disposable = rxPermissions.request(android.Manifest.permission.CAMERA).subscribe{ granted->

            if(!granted) finish()
        }
    }

    val RxPermissions.hasCameraPermission get() = isGranted(android.Manifest.permission.CAMERA)
}
