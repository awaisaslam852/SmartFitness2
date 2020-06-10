package com.axles.smartfitness.ui.profile

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout
import com.axles.smartfitness.R
import kotlinx.android.synthetic.main.profile_custom_selectable.view.*

class SelectableThing @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
) : FrameLayout(context, attrs, defStyleAttrs) {
    private var imageResource: Drawable? = null
    private var imageResourceSelected: Drawable? = null
    private var descriptionText: String? = null

    private val frameDrawable by lazy { context.getDrawable(R.drawable.ic_section_frame_profile) }
    private val frameDrawableSelected by lazy { context.getDrawable(R.drawable.ic_section_frame_profile_selected) }

    var isChosen = false
        set(value) {
            field = value
            changeState()
        }
    var doAfterClick = {} // callback that will be called after click on this view

    init {
        inflate(context, R.layout.profile_custom_selectable, this)
        attrs?.let { readAttributes(it) }
        initView()
    }

    private fun readAttributes(attrs: AttributeSet) {
        context.obtainStyledAttributes(attrs, R.styleable.SelectableThing)
            .apply {
                getDrawable(R.styleable.SelectableThing_icon)?.let {
                    imageResource = it
                }
                getDrawable(R.styleable.SelectableThing_iconSelected)?.let {
                    imageResourceSelected = it
                }
                getString(R.styleable.SelectableThing_description)?.let {
                    descriptionText = it
                }
            }
            .recycle()
    }

    private fun initView() {
        content.setImageDrawable(imageResource)
        description.text = descriptionText

        setOnClickListener {
            doAfterClick()
        }
    }

    private fun changeState() {
        content.setImageDrawable(if (isChosen) imageResourceSelected else imageResource)
        frame.setImageDrawable(if (isChosen) frameDrawableSelected else frameDrawable)
    }

    fun resetView() {
        isChosen = false
    }
}