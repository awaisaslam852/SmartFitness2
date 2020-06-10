package com.axles.smartfitness.ui.program

import androidx.recyclerview.widget.LinearLayoutManager
import com.axles.smartfitness.R
import com.axles.smartfitness.engine.Helper
import com.axles.smartfitness.extensions.makeGone
import com.axles.smartfitness.extensions.makeVisible
import com.axles.smartfitness.ui.base.BaseFragment
import com.axles.smartfitness.ui.dialogs.OkDialog
import kotlinx.android.synthetic.main.program_received_fragment.*
import kotlinx.android.synthetic.main.toolbar_program_received.*

class ProgramReceivedFragment : BaseFragment(R.layout.program_received_fragment) {

	override fun init() {
		imageButtonBurger.setOnClickListener { openDrawer() }

		recyclerViewReceivedPrograms.layoutManager = LinearLayoutManager(requireContext())
		recyclerViewReceivedPrograms.adapter = ProgramReceivedAdapter(childFragmentManager,
			isEmptyListener = { isEmpty ->
				if (isEmpty) {
					textViewNoProgramsReceived.makeVisible()
				} else {
					textViewNoProgramsReceived.makeGone()
				}
			},
			didAccept = ::onDidAccept,
			didReject = ::onDidReject
		)
	}

	override fun update() {
	}

	private fun onDidAccept() {
		val dialog = OkDialog(Helper.string(R.string.program_was_pinned_to_board))
		dialog.show(childFragmentManager, "ProgramReceivedFragment.Accept.OkDialog")
	}

	private fun onDidReject() {

	}
}