package com.example.got2go.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.got2go.R

class AddRestroomDialogFragment: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            builder.setView(inflater.inflate(R.layout.fragment_dialog_add_restroom, null))
                .setPositiveButton(R.string.add_restroom_submit) { dialog, id ->
                    // Send the positive button event back to the host activity
//                    mListener.onDi
                }
                .setNegativeButton(R.string.add_restroom_cancel) { dialog, id ->
                    // Send the negative button event back to the host activity
                    dialog.dismiss()
                    // mListener.onDialogNegativeClick(AddRestroomDialogFragment.this);
                }
            return builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}