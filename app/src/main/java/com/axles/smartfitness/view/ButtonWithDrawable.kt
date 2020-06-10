package com.axles.smartfitness.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.axles.smartfitness.R
import kotlinx.android.synthetic.main.custom_buttom_with_drawable.view.*

class ButtonWithDrawable @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttrs) {
    var imageResource: Drawable? = null
        set(value) {
            field = value
            imageView?.setImageDrawable(value)
        }
    var titleText: String? = null
        set(value) {
            field = value
            buttonText.text = value
        }
    private var buttonBackground: Drawable? = null

    init {
        inflate(context, R.layout.custom_buttom_with_drawable, this)
        attrs?.let { readAttributes(it) }
        initView()
    }

    private fun readAttributes(attrs: AttributeSet) {
        context.obtainStyledAttributes(attrs, R.styleable.ButtonWithDrawable)
            .apply {
                getDrawable(R.styleable.ButtonWithDrawable_icon)?.let {
                    imageResource = it
                }
                getDrawable(R.styleable.ButtonWithDrawable_buttonBackground)?.let {
                    buttonBackground = it
                }
                getString(R.styleable.ButtonWithDrawable_title)?.let {
                    titleText = it
                }
            }
            .recycle()
    }

    private fun initView() {
        imageView.setImageDrawable(imageResource)
        buttonFrame.background = buttonBackground
        buttonText.text = titleText
    }

}