package com.gribanskij.scanmaster.cameraX


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Rational
import android.util.Size
import android.view.Surface.*
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.gribanskij.scanmaster.databinding.ActivityxLiveBarcodeBinding
import com.gribanskij.scanmaster.settings.SettingsActivity
import com.gribanskij.scanmaster.R

class LiveBarcodeScanningActivityX : AppCompatActivity(), View.OnClickListener {

    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private var needUpdateGraphicOverlayImageSourceInfo = true


    private val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private val targetResolution = Size(1280,720)

    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null

    private lateinit var model: CamViewModel
    private lateinit var binding: ActivityxLiveBarcodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityxLiveBarcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!Utils.allPermissionsGranted(this)) {
            Utils.requestRuntimePermissions(this)
        }

        binding.closeButton.setOnClickListener(this)
        binding.flashButton.setOnClickListener(this)
        binding.settingsButton.setOnClickListener(this)


        model = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[CamViewModel::class.java]
        model.cameraProviderLiveData.observe(this) { provider: ProcessCameraProvider? ->
            cameraProvider = provider
            bindAllCameraUseCases()

        }

        model.barsResults.observe(this) {

            binding.graphicOverlay.clear()

            it.forEach { barcode ->
                binding.graphicOverlay.add(BarcodeGraphic(binding.graphicOverlay, barcode))
            }
            binding.graphicOverlay.invalidate()

            if (isNeedSendResult() && it.isNotEmpty()) {
                val result = it.first()
                val backResult = makeResultIntent(result.first.rawValue ?: "?")
                sendScanResult(backResult)
            }
        }
    }

    private fun bindAllCameraUseCases() {
        cameraProvider?.let {
            bindPreviewUseCase(it)
            bindAnalysisUseCase(it)
            //bindGrope(it)
        }
    }


    @SuppressLint("UnsafeOptInUsageError")
    private fun bindGrope(cameraProvider: ProcessCameraProvider){
        cameraProvider.unbindAll()

        val viewPort =  ViewPort.Builder(Rational(4, 3), ROTATION_270).build()

        previewUseCase = Preview.Builder()
            .setTargetResolution(targetResolution )
            .build()
            .also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

        val useCaseGroup = UseCaseGroup.Builder()
            .addUseCase(previewUseCase!!)
            .setViewPort(viewPort)
            .build()
        cameraProvider.bindToLifecycle(this, cameraSelector, useCaseGroup)


    }

    private fun bindPreviewUseCase(cameraProvider: ProcessCameraProvider) {
        previewUseCase?.let {
            cameraProvider.unbind(it)
        }
        previewUseCase = Preview.Builder()
            .setTargetResolution(targetResolution )
            .build()
            .also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }
        cameraProvider.bindToLifecycle(this, cameraSelector, previewUseCase)
    }

    private fun bindAnalysisUseCase(cameraProvider: ProcessCameraProvider) {
        analysisUseCase.let {
            cameraProvider.unbind(it)
        }

        analysisUseCase = ImageAnalysis.Builder().apply {
            setTargetResolution(targetResolution )
            setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        }.build()

        needUpdateGraphicOverlayImageSourceInfo = true

        analysisUseCase?.setAnalyzer(
            ContextCompat.getMainExecutor(this),
            ImageAnalysis.Analyzer { imageProxy: ImageProxy ->

                if(needUpdateGraphicOverlayImageSourceInfo) {

                    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                    if (rotationDegrees == 0 || rotationDegrees == 180) {
                        binding.graphicOverlay.setImageSourceInfo(imageProxy.width, imageProxy.height, false)
                    } else {
                        binding.graphicOverlay.setImageSourceInfo(imageProxy.height, imageProxy.width, false)
                    }
                    needUpdateGraphicOverlayImageSourceInfo = false
                }

                model.processImage(imageProxy)
            }
        )
        camera = cameraProvider.bindToLifecycle(this, cameraSelector, analysisUseCase)
    }

    override fun onDestroy() {
        super.onDestroy()
        camera = null
    }

    override fun onClick(view: View) {
        when (view.id) {

            R.id.close_button -> onBackPressed()
            R.id.flash_button -> {
                binding.flashButton.let {
                    if (it.isSelected) {
                        it.isSelected = false

                        camera?.cameraControl?.enableTorch(false)

                    } else {
                        it.isSelected = true
                        camera?.cameraControl?.enableTorch(true)
                    }
                }
            }
            R.id.settings_button -> {
                binding.settingsButton.isEnabled = false
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
    }


    private fun isNeedSendResult():Boolean {
        return intent.action == Intents.Scan.ACTION

    }

    private fun makeResultIntent(rawBarCode: String): Intent {
        return Intent(intent.action).apply {
            putExtra(Intents.Scan.RESULT, rawBarCode)
        }
    }

    private fun sendScanResult(resultIntent: Intent) {
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    companion object {
        private const val TAG = "LiveBarcodeActivity"
    }
}
