package com.shubhamtripz.gsi_quickbook

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("android:text")
fun setLongText(view: TextView, value: Long) {
    view.text = value.toString()
}
