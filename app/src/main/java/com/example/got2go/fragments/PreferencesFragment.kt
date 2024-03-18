package com.example.got2go.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import com.example.got2go.R

class PreferencesFragment : Fragment() {


    private lateinit var checkboxWheelchairAccessible: CheckBox
    private lateinit var checkboxInclusiveStall: CheckBox
    private lateinit var checkboxNoPasscode: CheckBox
    private lateinit var checkboxHygiene: CheckBox
    private lateinit var checkboxBaby: CheckBox
    private lateinit var checkboxUrinal: CheckBox
    private lateinit var checkboxSingleStall: CheckBox
    private lateinit var checkboxFamily: CheckBox


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_preferences, container, false)


        checkboxWheelchairAccessible = view.findViewById(R.id.checkbox_wheelchair_accessible)
        checkboxInclusiveStall = view.findViewById(R.id.checkbox_inclusive_stall)
        checkboxNoPasscode = view.findViewById(R.id.checkbox_no_passcode)
        checkboxHygiene = view.findViewById(R.id.checkbox_hygiene)
        checkboxBaby = view.findViewById(R.id.checkbox_baby_station)
        checkboxUrinal = view.findViewById(R.id.checkbox_urinal)
        checkboxSingleStall = view.findViewById(R.id.checkbox_single_stall)
        checkboxFamily = view.findViewById(R.id.checkbox_family_restroom)


        //listener for CheckBoxes, add later
        setCheckBoxListeners()

        return view
    }

    private fun setCheckBoxListeners() {

        checkboxWheelchairAccessible.setOnCheckedChangeListener { buttonView, isChecked ->
            //handle checkbox state change, do later
            if (isChecked) {

            } else {
                //unchecked
            }
        }

    }
}
