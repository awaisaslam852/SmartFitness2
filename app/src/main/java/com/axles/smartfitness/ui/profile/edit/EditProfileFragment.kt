package com.axles.smartfitness.ui.profile.edit

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.user.User
import com.axles.smartfitness.extensions.format
import com.axles.smartfitness.extensions.formatTo
import com.axles.smartfitness.extensions.observeWrapper
import com.axles.smartfitness.ui.base.BaseFragment
import com.axles.smartfitness.ui.profile.ProfileUI
import kotlinx.android.synthetic.main.profile_edit_fragment.*
import kotlinx.android.synthetic.main.toolbar_profile_edit.*
import java.util.*

class EditProfileFragment : BaseFragment(R.layout.profile_edit_fragment), ParentInteractor {
    lateinit var viewModel: EditProfileViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EditProfileViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        viewModel.profileLD.observeWrapper(this, ::initWith)
    }

    private fun initWith(profile: ProfileUI) {
        genderItem.setValue(profile.gender.name)
        heightItem.setValue(profile.height.format(2))
        weightItem.setValue(profile.weight.format(2))
        workoutLevelItem.setValue(profile.level.name)
        birthdayItem.setValue(profile.birthDay.formatTo())
    }

    private fun initView() {
        imageButtonBurger.setOnClickListener { navController.popBackStack() }
        birthdayItem.onSelect = {
            val calendar = viewModel.profile?.birthDay?.let {
                Calendar.getInstance().apply {
                    time = it
                }
            } ?: Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                provideOnDateChangeListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
                .apply {
                    setOnShowListener {
                        updateDate(
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        )
                    }
                }
                .show()
        }
        weightItem.onSelect = {
            openPopUpFragment(
                WeightDialog(
                    viewModel.profile?.weight,
                    {
                        val weight = getString(R.string.placeholder_kg, it.toString())
                        weightItem.setValue(weight)
                    },
                    this
                ),
                R.id.root
            )
        }
        heightItem.onSelect = {
            openPopUpFragment(
                HeightDialog(
                    viewModel.profile?.height,
                    {
                        val height = getString(R.string.placeholder_cm, it.toString())
                        heightItem.setValue(height)
                    },
                    this
                ),
                R.id.root
            )
        }
        genderItem.onSelect = {
            GenderDialog(viewModel.profile?.gender ?: User.Gender.Male)
            { genderItem.setValue(it.name) }
                .show(childFragmentManager, null)
        }
        workoutLevelItem.onSelect = {
            FitnessLevelDialog(
                viewModel.profile?.level ?: User.FitnessLevel.Beginner
            ) { workoutLevelItem.setValue(it.name) }
                .show(childFragmentManager, null)
        }

    }

    override fun closeCurrent() {
        childFragmentManager.popBackStack()
    }

    private val provideOnDateChangeListener by lazy {
        DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val calendar = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, day)
            }
            birthdayItem.setValue(calendar.time.formatTo())
        }
    }
}