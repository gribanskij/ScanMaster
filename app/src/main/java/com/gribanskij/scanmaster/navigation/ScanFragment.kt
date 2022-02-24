package com.gribanskij.scanmaster.navigation

import android.content.Intent
import android.os.Bundle
import android.util.Size
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gribanskij.scanmaster.R
import com.gribanskij.scanmaster.cameraX.BarcodeGraphic
import com.gribanskij.scanmaster.cameraX.CamViewModel
import com.gribanskij.scanmaster.cameraX.Intents
import com.gribanskij.scanmaster.databinding.ScanFragmentBinding
import com.gribanskij.scanmaster.settings.SettingsActivity

class ScanFragment:Fragment (R.layout.scan_fragment) {

    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private var needUpdateGraphicOverlayImageSourceInfo = true


    private val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private val targetResolution = Size(1280,720)

    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null

    private lateinit var model: CamViewModel


    private var _binding: ScanFragmentBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProvider(this)[CamViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = ScanFragmentBinding.bind(view)

        model.barsResults.observe(viewLifecycleOwner) {
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

        model.cameraProviderLiveData.observe(viewLifecycleOwner) { provider: ProcessCameraProvider? ->
            cameraProvider = provider
            bindAllCameraUseCases()

        }

        binding.closeButton.setOnClickListener {
            onClick(it)
        }
        binding.flashButton.setOnClickListener{
            onClick(it)
        }
        binding.settingsButton.setOnClickListener{
            onClick(it)
        }


    }


    private fun bindAllCameraUseCases() {
        cameraProvider?.let {
            bindPreviewUseCase(it)
            bindAnalysisUseCase(it)
        }
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
            ContextCompat.getMainExecutor(requireContext()),
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

    private fun onClick(view: View) {
        when (view.id) {

            //R.id.close_button -> onBackPressed()
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
                startActivity(Intent(requireActivity(), SettingsActivity::class.java))
            }
        }
    }


    private fun isNeedSendResult():Boolean {
        return requireActivity().intent.action == Intents.Scan.ACTION

    }

    private fun makeResultIntent(rawBarCode: String): Intent {
        return Intent(requireActivity().intent.action).apply {
            putExtra(Intents.Scan.RESULT, rawBarCode)
        }
    }

    private fun sendScanResult(resultIntent: Intent) {
        requireActivity().setResult(AppCompatActivity.RESULT_OK, resultIntent)
        requireActivity().finish()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        camera=null
    }
}