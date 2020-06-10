package com.axles.smartfitness.ui.profile.edit

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.axles.smartfitness.R
import kotlinx.android.synthetic.main.personal_info_view.view.*

class ProfileInfoItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttrs) {
    private var imageResource: Drawable? = null
    private var descriptionText: String? = null
    private var titleText: String? = null

    lateinit var onSelect: () -> Unit

    init {
        inflate(context, R.layout.personal_info_view, this)
        attrs?.let { readAttributes(it) }
        initView()
    }

    private fun readAttributes(attrs: AttributeSet) {
        context.obtainStyledAttributes(attrs, R.styleable.ProfileInfoItem)
            .apply {
                getDrawable(R.styleable.ProfileInfoItem_icon)?.let {
                    imageResource = it
                }
                getString(R.styleable.ProfileInfoItem_description)?.let {
                    descriptionText = it
                }
                getString(R.styleable.ProfileInfoItem_title)?.let {
                    titleText = it
                }
            }
            .recycle()
    }

    fun setValue(text: String) {
        itemValue.text = text
    }

    private fun initView() {
        itemIcon.setImageDrawable(imageResource)
        itemTitle.text = titleText
        itemDescription.text = descriptionText

        valueContainer.setOnClickListener { onSelect() }
    }

}