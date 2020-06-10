package com.axles.smartfitness.extensions

import android.media.Image
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.program_received_list_item.view.*

fun View.makeInvisible(invisible: Boolean = true) {
    this.visibility = if (invisible) View.INVISIBLE else View.VISIBLE
}

fun View.makeVisible(){
    this.visibility = View.VISIBLE
}

fun View.makeGone(){
    this.visibility = View.GONE
}

fun ImageView.load(imageUrl: String) {
    Glide.with(context)
        .load(imageUrl)
        .into(this)
}

fun View.makeVisibleOrGone(visible: Boolean) {
    if (visible) this.visibility = View.VISIBLE else this.visibility = View.GONE
}

fun View.isVisible(): Boolean {
    return this.visibility == View.VISIBLE
}