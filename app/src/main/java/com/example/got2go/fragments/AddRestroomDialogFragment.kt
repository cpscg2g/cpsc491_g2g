package com.example.got2go.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.got2go.R
import com.example.got2go.data.Address
import com.example.got2go.data.Coordinates
import com.example.got2go.map.MapViewModel

class AddRestroomDialogFragment: DialogFragment() {
    private val mapViewModel: MapViewModel by viewModels { MapViewModel.Factory }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dialog_add_restroom, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            val builder = AlertDialog.Builder(requireContext())
            val inflater = requireActivity().layoutInflater

            builder.setView(inflater.inflate(R.layout.fragment_dialog_add_restroom, null)).create()
            val submitButton = view.findViewById<ImageButton>(R.id.addRestroomSubmit)
            submitButton.setOnClickListener {
                    val name = view.findViewById<EditText>(R.id.restroomName).text.toString()
                    val streetAddress = view.findViewById<EditText>(R.id.restroomAddress).text.toString()
                    val status = view.findViewById<EditText>(R.id.restroomStatus).text.toString()
//                    val coordinates = mapViewModel.getCoordinates()
                    val coordinates = Coordinates(0.0, 0.0)
                    if (name.isEmpty() || streetAddress.isEmpty() || status.isEmpty()) {
                        Toast.makeText(context, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    val address = Address(streetAddress, "Fullerton", "CA", "92871", "USA")
                    mapViewModel.addRestroom(name, address, coordinates, status)
                    Toast.makeText(context, "Restroom added", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                val cancelButton = view.findViewById<ImageButton>(R.id.addRestroomCancel)
                cancelButton.setOnClickListener{
                    dismiss()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
    companion object {
        const val TAG = "AddRestroomDialogFragment"
    }
}