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

    //change this later to correlate with database
    private val ratingCategories: Map<String, Float> = mapOf(
            "Accessibility" to 4f,
            "Location" to 3f,
            "Overall" to 4f,
            "Space" to 3f,
            "Stalls" to 2f,
    )

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
            // Pass ratingCategories to the showPopup function
            showPopup(view, ratingCategories)
        }
    }

    private fun showPopup(view: View, ratingCategories: Map<String, Float>) {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.fragment_more_categories, null)

        val popupLayout: LinearLayout = popupView.findViewById(R.id.popupLayout)

        //creating views for each rating category
        for ((category, rating) in ratingCategories) {
            val categoryView = inflater.inflate(R.layout.category_layout, null)
            val categoryNameTextView = categoryView.findViewById<TextView>(R.id.categoryRatingName)
            val categoryRatingBar = categoryView.findViewById<RatingBar>(R.id.categoryRatingBar)

            categoryNameTextView.text = category
            categoryRatingBar.rating = rating

            popupLayout.addView(categoryView)
        }

        //makes the popup window
        val popupWindow = PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        )

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
    }
}
