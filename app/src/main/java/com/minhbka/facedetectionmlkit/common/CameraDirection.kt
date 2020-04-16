package com.minhbka.facedetectionmlkit.common


enum class CameraDirection(val value: Int) {

    FRONT(1),
    BACK(0);
    companion object {
        private val lookupTable =
            values().associateBy(CameraDirection::value)

        fun parse(cameraId: Int) = lookupTable[cameraId]
    }
}