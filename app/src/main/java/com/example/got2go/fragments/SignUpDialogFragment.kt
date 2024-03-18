package com.example.got2go.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.got2go.R

class SignUpDialogFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_signup, container, false)
        val btnSignUp = view.findViewById<Button>(R.id.btnSignUp)

        btnSignUp.setOnClickListener {
            Log.i(TAG, "Sign-up button tapped")
            //add sign-up logic here
        }

        return view
    }

    companion object {
        const val TAG = "SignUpDialogFragment"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }
}
