package com.geekbrains.tests.view.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.geekbrains.tests.R

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        openFragment()
    }

    private fun openFragment() {
        val count = intent.getIntExtra(TOTAL_COUNT_EXTRA, 0)
        val bundle = Bundle()
        bundle.putInt(FRAGMENT_TOTAL_COUNT_EXTRA, count)

        supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.detailsFragmentContainer, DetailsFragment.newInstance(bundle))
            .commit()
    }

    companion object {

        const val TOTAL_COUNT_EXTRA = "TOTAL_COUNT_EXTRA"
        const val FRAGMENT_TOTAL_COUNT_EXTRA = "FRAGMENT_TOTAL_COUNT_EXTRA"

        fun getIntent(context: Context, totalCount: Int): Intent {
            return Intent(context, DetailsActivity::class.java).apply {
                putExtra(TOTAL_COUNT_EXTRA, totalCount)
            }
        }
    }
}
