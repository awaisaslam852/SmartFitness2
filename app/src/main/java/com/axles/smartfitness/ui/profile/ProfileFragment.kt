package com.axles.smartfitness.ui.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.axles.smartfitness.R
import com.axles.smartfitness.extensions.format
import com.axles.smartfitness.extensions.formatTo
import com.axles.smartfitness.extensions.load
import com.axles.smartfitness.extensions.observeWrapper
import com.axles.smartfitness.ui.base.BaseFragment
import com.axles.smartfitness.ui.dialogs.OkDialog
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.profile_fragment.*
import kotlinx.android.synthetic.main.toolbar_profile.*

class ProfileFragment : BaseFragment(R.layout.profile_fragment) {
    private lateinit var selectable: List<SelectableThing>
    lateinit var viewModel: ProfileViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeVM()
        selectable = listOf(selectableThing1, selectableThing2, selectableThing3, selectableThing4)
        initView()
    }

    private fun subscribeVM() {
        viewModel.profile.observeWrapper(this,
            ::setupProfile,
            {}
        )
    }

    @SuppressLint("SetTextI18n")
    private fun setupProfile(profile: ProfileUI) {
        profile.apply {
            userImageView.load(imageUrl)
            usernameTextView.text = username
            heightValue.text = getString(R.string.placeholder_cm, height.format(2))
            weightValue.text = getString(R.string.placeholder_kg, weight.format(2))
            birthdayValue.text = birthDay.formatTo()
            genderValue.text = gender.name
            workoutLevelValue.text = level.name
        }
    }

    private fun initView() {
        setupDrawer()
        editImageView.setOnClickListener {
            findNavController().navigate(R.id.actionProfileToEdit)
        }
        initSelectables()
        userImageView.setOnClickListener { openGallery() }
        progressStorySection.setOnClickListener {
            OkDialog(getString(R.string.coming_soon)).show(
                childFragmentManager, null
            )
        }
    }

    private fun initSelectables() {
        selectable.forEach { it.doAfterClick = { chooseSection(it) } }
        selectable.first().isChosen = true
    }

    private fun setupDrawer() {
        imageButtonBurger.setOnClickListener {
            openDrawer()
        }
    }

    private fun chooseSection(selectableThing: SelectableThing) {
        selectable.filter { it != selectableThing }.forEach { it.resetView() }
        selectableThing.isChosen = !selectableThing.isChosen
    }

    private fun openGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE) {
            val imageUri = data?.data ?: return
            Glide.with(context ?: return)
                .load(imageUri)
                .into(userImageView)
            imageUri.path?.let { viewModel.uploadAvatar(path = it) }
        }
    }

    companion object {
        const val PICK_IMAGE = 1234
    }
}