package com.geekbrains.tests.view.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.geekbrains.tests.R
import com.geekbrains.tests.presenter.details.DetailsPresenter
import com.geekbrains.tests.presenter.details.PresenterDetailsContract
import kotlinx.android.synthetic.main.fragment_test_layout.*
import java.util.*

class DetailsFragment : Fragment(R.layout.fragment_test_layout), ViewDetailsContract {

    private val presenter: PresenterDetailsContract = DetailsPresenter()

    companion object {

        private const val FRAGMENT_TOTAL_COUNT_EXTRA = "FRAGMENT_TOTAL_COUNT_EXTRA"

        @JvmStatic
        fun newInstance(bundle: Bundle): Fragment {
            val fragment = DetailsFragment()

            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.onAttach(this)
    }

    override fun onPause() {
        super.onPause()
        presenter.onDetach()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUI()
    }

    private fun setUI() {
        arguments?.let {
            val count = it.getInt(FRAGMENT_TOTAL_COUNT_EXTRA, 0)
            presenter.setCounter(count)
            setCountText(count)
        }

        decrementButton.setOnClickListener { presenter.onDecrement() }
        incrementButton.setOnClickListener { presenter.onIncrement() }
    }

    override fun setCount(count: Int) {
        setCountText(count)
    }

    private fun setCountText(count: Int) {
        totalCountTextView.text =
            String.format(Locale.getDefault(), getString(R.string.results_count), count)
    }
}