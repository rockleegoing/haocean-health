package com.ruoyi.code.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.ruoyi.app.R
import com.ruoyi.code.Frame

/**
 * @how_to_user
 *  val loadingDialog = LoadingDialog()
 *   loadingDialog.show(parentFragmentManager, "")
 */
class LoadingDialog(private val text: String = Frame.getString(R.string.bubble_loading_title)) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        return BubbleDialog(requireContext(), title = text)
    }

}
