package com.echoeyecodes.newsapp.utils

import android.content.Context
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.echoeyecodes.newsapp.R

class CustomClickableSpan(private val value: String, private val context:Context) : ClickableSpan() {
    override fun onClick(widget: View) {
        widget as TextView
        AndroidUtilities.log(value)
    }

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.isUnderlineText = true
        ds.color = ResourcesCompat.getColor(context.resources, R.color.teal_200, null)
    }

}