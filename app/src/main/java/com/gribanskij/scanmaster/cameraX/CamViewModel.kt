package com.gribanskij.scanmaster.cameraX

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.barhopper.RecognitionOptions.DATA_MATRIX
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

    private var isStopProcess = false

    private val regGtin = ("(?<=01)\\d{14}(?=21)").toRegex()
    private val regSn = ("(?<=21)\\w{13}").toRegex()

    private val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient()
    /*
    private val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_DATA_MATRIX)
            .build()
    )
     */

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


        if (isStopProcess){
            imageProxy.close()
            return
        }

        viewModelScope.launch(Dispatchers.Default) {
            val inputImage = InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)
            val res = barcodeScanner.process(inputImage)
            while (!res.isComplete) delay(1)

            val unknownError = "UNKNOWN"


            val bbb = res.result.map { bar->
                val valueType = bar.valueType
                // See API reference for complete list of supported types
                when (valueType) {
                    Barcode.TYPE_WIFI -> {
                        val ssid = bar.wifi?.ssid?:"?"
                        val password = bar.wifi?.password?:"?"
                        val type = bar.wifi?.encryptionType?:"?"
                        val info = "${ssid}/${password}/${type}"
                        Pair(bar,info)
                    }
                    Barcode.TYPE_URL -> {
                        val title = bar.url?.title?:"?"
                        val url = bar.url?.url?:"?"
                        val info = "${title}/${url}"
                        Pair(bar,info)
                    }
                    /*
                    Barcode.DA -> {
                        val raw = bar.rawValue?:"0"
                        val info = "GTIN:${regGtin.find(raw)?.groupValues?.first()?:"?"}  S/N:${regSn.find(raw)?.groupValues?.first()?:"?"}"
                        Pair(bar,info)
                    }
                     */
                    Barcode.TYPE_EMAIL -> {
                        val email = bar!!.email
                        Pair(bar,bar.displayValue?:unknownError)
                    }
                    Barcode.TYPE_UNKNOWN -> {
                        val text = bar!!.displayValue
                        Pair(bar,bar.displayValue?:unknownError)
                    }
                    Barcode.TYPE_CALENDAR_EVENT -> {
                        val event = bar!!.calendarEvent
                        Pair(bar,bar.displayValue?:unknownError)
                    }
                    Barcode.TYPE_CONTACT_INFO -> {
                        val contact = bar.contactInfo
                        Pair(bar,bar.displayValue?:unknownError)
                    }
                    Barcode.TYPE_GEO -> {
                        val geo = bar!!.geoPoint
                        Pair(bar,bar.displayValue?:unknownError)
                    }
                    Barcode.TYPE_DRIVER_LICENSE -> {
                        val idCard = bar!!.driverLicense
                        Pair(bar,bar.displayValue?:unknownError)
                    }
                    Barcode.TYPE_ISBN -> {
                        val isbn = bar!!.displayValue
                        Pair(bar,bar.displayValue?:unknownError)
                    }
                    Barcode.TYPE_PHONE -> {
                        val phone = bar!!.phone
                        Pair(bar,bar.displayValue?:unknownError)
                    }
                    Barcode.TYPE_PRODUCT -> {
                        val product = bar!!.displayValue
                        Pair(bar,bar.displayValue?:unknownError)
                    }
                    Barcode.TYPE_TEXT -> {
                        val raw = bar.rawValue?:"0"
                        val info = if (regGtin.find(raw)?.groupValues?.isNotEmpty() == true) "GTIN:${regGtin.find(raw)?.groupValues?.first()?:"?"}  S/N:${regSn.find(raw)?.groupValues?.first()?:"?"}"
                        else bar.displayValue?:unknownError
                        Pair(bar,info)
                    }
                    Barcode.TYPE_SMS -> {
                        val sms = bar!!.sms
                        Pair(bar,bar.displayValue?:unknownError)
                    }
                    else -> {
                        Pair(bar,"UNKNOWN")
                    }
                }
            }

            barsResults.postValue(bbb)
            imageProxy.close()
            if (bbb.isNotEmpty())isStopProcess = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        barcodeScanner.close()
    }
}