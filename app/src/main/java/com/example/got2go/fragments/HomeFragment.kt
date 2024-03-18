package com.example.got2go.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.got2go.R


class HomeFragment : Fragment() {
    //TODO: update fragment w/g2g stuff

    private lateinit var quantityEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        quantityEditText = view.findViewById(R.id.quantityEditText)

        quantityEditText.setOnClickListener {
            showNumberPickerDialog()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var btnScan: Button = view.findViewById(R.id.btnScan)
        //var overall_rate: RatingBar = view.findViewById(R.id.overallRating)
        var tvWelcome: TextView = view.findViewById(R.id.tvWelcome)
        tvWelcome.text = "Write a Review for Location"
        btnScan.setOnClickListener(View.OnClickListener {
            Toast.makeText(
                context,
                "Clicked",
                Toast.LENGTH_SHORT
            ).show()
        })
        quantityEditText.setOnClickListener {
            showNumberPickerDialog()
        }
    }

    private fun showNumberPickerDialog() {
        val numberPicker = NumberPicker(requireContext())
        numberPicker.minValue = 0
        numberPicker.maxValue = 10

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Select Quantity")
            .setView(numberPicker)
            .setPositiveButton("OK") { _, _ ->
                val selectedQuantity = numberPicker.value
                quantityEditText.setText(selectedQuantity.toString())
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }


}
