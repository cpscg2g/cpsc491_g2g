package com.example.got2go.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.got2go.R
import android.app.AlertDialog
import android.widget.ImageButton
import android.widget.EditText
import android.widget.LinearLayout


class HomeFragment : Fragment() {
    //TODO: update fragment w/g2g stuff

    //to store the names of categories that have been added
    private val addedCategories: MutableSet<String> = mutableSetOf()


    private lateinit var quantityEditText: EditText

    //add more categories counter
    private lateinit var categoryContainer: LinearLayout
    private var categoryCount = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //inflate the layout
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val overallRatingBar = view.findViewById<RatingBar>(R.id.overallRatingBar)

        //number of stars
        overallRatingBar.numStars = 5

        //step size
        overallRatingBar.stepSize = 1f

        //rating
        overallRatingBar.rating = 0f

        //width and height
        val layoutParams = overallRatingBar.layoutParams
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        //layoutParams.bottomMargin = 20
        overallRatingBar.layoutParams = layoutParams

        quantityEditText = view.findViewById(R.id.quantityEditText)
        categoryContainer = view.findViewById(R.id.categoryContainer)

        //quantityEditText.setOnClickListener {
        //    showNumberPickerDialog()
        //}

        view.findViewById<Button>(R.id.addMore).setOnClickListener {
            showCategorySelectionDialog()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var btnScan: Button = view.findViewById(R.id.btnScan)
        val overallRatingBar: RatingBar = view.findViewById(R.id.overallRatingBar)
        var tvWelcome: TextView = view.findViewById(R.id.tvWelcome)
        tvWelcome.text = "Write a Review for Location"
        btnScan.setOnClickListener(View.OnClickListener { Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show() })

        //quantityEditText.setOnClickListener {
        //    showNumberPickerDialog()
        //}
    }

    private fun addCategory(categoryName: String) {
        if (!addedCategories.contains(categoryName)) {
            val inflater = LayoutInflater.from(requireContext())
            val newCategoryView = inflater.inflate(R.layout.category_item_layout, categoryContainer, false)

            val categoryNameTextView = newCategoryView.findViewById<TextView>(R.id.accessibilityRating)
            categoryNameTextView.text = categoryName

            val categoryRatingBar = newCategoryView.findViewById<RatingBar>(R.id.accessibilityRatingBar)
            // Customize the RatingBar as needed
            categoryRatingBar.numStars = 5
            categoryRatingBar.stepSize = 1f
            categoryRatingBar.rating = 0f
            categoryRatingBar.scaleX = 0.5f
            categoryRatingBar.scaleY = 0.5f

            // Get reference to delete button
            val deleteButton = newCategoryView.findViewById<ImageButton>(R.id.deleteButton)
            deleteButton.setOnClickListener {
                // Remove the category from the view and the set of added categories
                categoryContainer.removeView(newCategoryView)
                addedCategories.remove(categoryName)
            }

            val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            )
            //params.gravity = Gravity.CENTER_VERTICAL
            //deleteButton.layoutParams = params

            categoryContainer.addView(newCategoryView)

            // Add the category name to the set of added categories
            addedCategories.add(categoryName)
        } else {
            // Category already added, show a message or handle it accordingly
            Toast.makeText(requireContext(), "Category '$categoryName' already added", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showCategorySelectionDialog() {
        val categoryOptions = arrayOf("Accessibility", "Cleanliness", "Location", "Space", "Stalls") // Example category options
        val dialog = AlertDialog.Builder(requireContext())
                .setTitle("Select Category")
                .setItems(categoryOptions) { _, which ->
                    val selectedCategory = categoryOptions[which]
                    addCategory(selectedCategory)
                }
                .setNegativeButton("Cancel", null)
                .create()

        dialog.show()
    }


    /* private fun showNumberPickerDialog() {
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
    } */


}
