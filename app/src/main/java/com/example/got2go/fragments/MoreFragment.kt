package com.example.got2go.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.got2go.R

class MoreFragment : Fragment() {

    private lateinit var btnSettings: Button
    private lateinit var btnNotifications: Button
    private lateinit var btnPreferences: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_more, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        btnSettings.setOnClickListener {
            //handle click for Settings button
        }

        btnNotifications.setOnClickListener {
            //handle click for Notifications button
        }

        btnPreferences.setOnClickListener {
            //handle click for Preferences button
        }
    }
}