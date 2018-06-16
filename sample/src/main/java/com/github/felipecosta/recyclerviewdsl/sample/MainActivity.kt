package com.github.felipecosta.recyclerviewdsl.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.felipecosta.recyclerviewdsl.R
import com.github.felipecosta.recyclerviewdsl.sample.strings.StringsFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        supportFragmentManager.beginTransaction()
                .replace(R.id.main_content, StringsFragment.newInstance())
                .commit()
    }
}
