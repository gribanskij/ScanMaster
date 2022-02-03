package com.gribanskij.scanmaster.cameraX

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutionException

class CamViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "camViewModel"

    private val regGtin = ("(?<=01)\\d{14}(?=21)").toRegex()
    private val regSn = ("(?<=21)\\w{13}").toRegex()

    private val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_DATA_MATRIX)
            .build()
    )

    val cameraProviderLiveData = MutableLiveData<ProcessCameraProvider?>().apply {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val cameraProviderFuture = ProcessCameraProvider.getInstance(getApplication()).get()
                postValue(cameraProviderFuture)
            } catch (e: ExecutionException) {
                // Handle any errors (including cancellation) here.
                Log.e(TAG, "Unhandled exception", e)
            } catch (e: InterruptedException) {
                Log.e(TAG, "Unhandled exception", e)
            }
        }
    }


    val barsResults = MutableLiveData<List<Pair<Barcode,String>>>()


    @SuppressLint("UnsafeOptInUsageError")
    fun processImage(imageProxy: ImageProxy) {

        viewModelScope.launch(Dispatchers.Default) {
            val inputImage = InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)
            val res = barcodeScanner.process(inputImage)
            while (!res.isComplete) delay(1)
            val bars = res.result.map {
                val raw = it.rawValue?:"0"
                val format = "GTIN:${regGtin.find(raw)?.groupValues?.first()?:"?"}  S/N:${regSn.find(raw)?.groupValues?.first()?:"?"}"
                Pair(it,format)
            }
            barsResults.postValue(bars)
            imageProxy.close()
        }
    }

    override fun onCleared() {
        super.onCleared()
        barcodeScanner.close()
    }
}