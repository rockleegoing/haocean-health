package com.ruoyi.code.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.ruoyi.app.R
import com.ruoyi.code.Frame

/**
 * @how_to_user
 * val dialog = BaseDialogFragment()
 * dialog.show(parentFragmentManager, "")
 */
class BaseDialogFragment(private val dialog: Dialog) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        return dialog
    }

}
