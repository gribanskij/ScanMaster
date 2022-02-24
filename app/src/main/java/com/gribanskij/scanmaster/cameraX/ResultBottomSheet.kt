package com.gribanskij.scanmaster.cameraX

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gribanskij.scanmaster.R

class ResultBottomSheet: BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.result_bottom_sheet, container, false)

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}