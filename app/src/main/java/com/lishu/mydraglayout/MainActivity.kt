package com.lishu.mydraglayout

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lv_left.setAdapter(ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Cheeses.sCheeseStrings))

        lv_main.setAdapter(object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Cheeses.NAMES) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val textView = super.getView(position, convertView, parent) as TextView
                textView.setTextColor(Color.BLACK)
                return textView
            }
        })

    }
}
