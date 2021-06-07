package com.nunu.camerax

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.nunu.camerax.databinding.ActivityMainBinding
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private var imageCapture: ImageCapture? = null
    private lateinit var binding: ActivityMainBinding

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        binding.btnMainCapture.setOnClickListener { takePhoto() }
    }

    private fun takePhoto() {
        TODO("Not yet implemented")
    }

    private fun startCamera() {
        TODO("Not yet implemented")
    }

    private fun getOutputDirectory(): File {
        val mediaDirectory = externalMediaDirs.firstOrNull()?.let {
            // 파일을 왜 이렇게 정의할까?
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        // filesDir은 뭐지?
        // mediaDirectory가 exists를 먼저 체크하는거 보면 얘도 유일하게 만들 생각인가 보네
        return if (mediaDirectory != null && mediaDirectory.exists()) mediaDirectory else filesDir
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        // checkSelfPermission에 대해서 정리해야겠다.
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        // Executor.shutDown() 함수는 어떤 역할이지?
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}