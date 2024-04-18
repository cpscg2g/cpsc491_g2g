import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.got2go.R
import android.widget.TextView
import android.widget.Button
import android.widget.PopupWindow
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.RatingBar



class LocationReviewsFragment : Fragment() {

    private lateinit var rootView: View
    private lateinit var reviewTextView: TextView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_location_review, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reviewTextView = rootView.findViewById(R.id.reviewTextView)

        val seeMoreButton: Button = rootView.findViewById(R.id.seeMoreButton)
        seeMoreButton.setOnClickListener {
            showPopup(it)
        }

    }

    private fun showPopup(view: View, ratingCategories: Map<String, Float>) {
        val inflater = requireContext().getSystemService(LayoutInflater::class.java)
        val popupView = inflater.inflate(R.layout.fragment_more_categories, null)

        val popupLayout: LinearLayout = popupView.findViewById(R.id.popupLayout)

        //create and add views for each rating category, fix this later
        for ((category, rating) in ratingCategories) {
            val categoryView = inflater.inflate(R.layout.fragment_location_review, null)
            //val categoryNameTextView = categoryView.findViewById<TextView>(R.id.categoryRatingName)
            //val categoryRatingBar = categoryView.findViewById<RatingBar>(R.id.categoryRatingBar)

            //categoryNameTextView.text = category
            //categoryRatingBar.rating = rating

            popupLayout.addView(categoryView)
        }

        // Create the popup window
        val width = ViewGroup.LayoutParams.WRAP_CONTENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        val focusable = true // Let taps outside the popup dismiss it
        val popupWindow = PopupWindow(popupView, width, height, focusable)

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
    }

}
