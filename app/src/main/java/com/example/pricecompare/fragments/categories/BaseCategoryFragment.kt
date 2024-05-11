package com.example.pricecompare.fragments.categories

import androidx.fragment.app.Fragment
import com.example.pricecompare.R

//open class BaseCategoryFragment: Fragment(R.layout.fragment_base_category) {
//}
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class BaseCategoryFragment : Fragment() {
    private var categoryName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryName = it.getString(ARG_CATEGORY_NAME)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_base_category, container, false)
    }

    companion object {
        private const val ARG_CATEGORY_NAME = "categoryName"

        fun newInstance(categoryName: String): BaseCategoryFragment {
            val fragment = BaseCategoryFragment()
            val args = Bundle()
            args.putString(ARG_CATEGORY_NAME, categoryName)
            fragment.arguments = args
            return fragment
        }
    }
}
