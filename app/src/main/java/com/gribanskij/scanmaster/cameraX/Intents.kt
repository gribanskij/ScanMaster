package com.gribanskij.scanmaster.cameraX

 class Intents private constructor(){
     object Scan {
         /**
          * Send this intent to open the Barcodes app in scanning mode, find a barcode, and return
          * the results.
          */
         const val ACTION = "com.google.zxing.client.android.SCAN"
         /**
          * By default, sending this will decode all barcodes that we understand. However it
          * may be useful to limit scanning to certain formats. Use
          * {@link android.content.Intent#putExtra(String, String)} with one of the values below.
          *
          * Setting this is effectively shorthand for setting explicit formats with {@link #FORMATS}.
          * It is overridden by that setting.
          */
         const val MODE = "SCAN_MODE"
         /**
          * Decode only 1D barcodes.
          */
         const val ONE_D_MODE = "ONE_D_MODE"

         /**
          * Decode only QR codes.
          */
         const val QR_CODE_MODE = "QR_CODE_MODE"

         /**
          * Decode only Data Matrix codes.
          */
         const val DATA_MATRIX_MODE = "DATA_MATRIX_MODE"

         /**
          * Decode only Aztec.
          */
         const val AZTEC_MODE = "AZTEC_MODE"

         /**
          * Decode only PDF417.
          */
         const val PDF417_MODE = "PDF417_MODE"


         const val RESULT = "SCAN_RESULT"

     }
}