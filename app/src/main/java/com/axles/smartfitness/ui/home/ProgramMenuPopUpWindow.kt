package com.axles.smartfitness.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.TextView
import com.axles.smartfitness.R
import com.axles.smartfitness.extensions.dpToPx
import com.axles.smartfitness.extensions.logD

class ProgramMenuPopUpWindow(
    val parent: View,
    val onEdit: () -> Unit,
    val onShare: () -> Unit,
    val onDelete: () -> Unit,
    val onDismiss: () -> Unit
) {

    private lateinit var popupWindow: PopupWindow

    fun showPopupMenu() {
        val popupViewInflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = popupViewInflater.inflate(R.layout.program_menu_popup, null)
//            LayoutInflater.from(view.context).inflate(R.layout.program_menu_popup, null, false)
//        popupWindow = PopupWindow(popupView, 400, 400, true)
        popupWindow = PopupWindow(popupView, parent.measuredWidth, 120.dpToPx(), true)

        popupWindow.showAsDropDown(parent, 0, (-120).dpToPx())//(-164).pxToDp(), (-100).pxToDp())
        popupWindow.setOnDismissListener { onDismiss.invoke() }
//        popupWindow.showAtLocation()
//        popupWindow.isFocusable = true
        setListeners(popupView)

    }

    private fun setListeners(popupView: View) {
        setEditListeners(popupView)
        setShareListeners(popupView)
        setDeleteListeners(popupView)
        setClosePopupListeners(popupView)
    }

    private fun setClosePopupListeners(popupView: View) {
        popupView.setOnTouchListener { v, event ->
            popupWindow.dismiss()
            return@setOnTouchListener true
        }

        popupView.findViewById<ImageButton>(R.id.imageButtonCloseMenu).setOnClickListener {
            popupWindow.dismiss()
        }
    }

    private fun setDeleteListeners(popupView: View) {
        val textViewDelete = popupView.findViewById<TextView>(R.id.textViewDeleteMenu)
        textViewDelete.setOnClickListener(::onDeleteClicked)
        val imageButtonDelete = popupView.findViewById<ImageButton>(R.id.imageButtonDeleteMenu)
        imageButtonDelete.setOnClickListener(::onDeleteClicked)
    }

    private fun setShareListeners(popupView: View) {
        val textViewShare = popupView.findViewById<TextView>(R.id.textViewShareMenu)
        textViewShare.setOnClickListener(::onShareClicked)
        val imageButtonShare = popupView.findViewById<ImageButton>(R.id.imageButtonShareMenu)
        imageButtonShare.setOnClickListener(::onShareClicked)
    }

    private fun setEditListeners(popupView: View) {
        val textViewEdit = popupView.findViewById<TextView>(R.id.textViewEditMenu)
        textViewEdit.setOnClickListener(::onEditClicked)
        val imageButtonEdit = popupView.findViewById<ImageButton>(R.id.imageButtonEditMenu)
        imageButtonEdit.setOnClickListener(::onEditClicked)
    }

    private fun onEditClicked(view: View) {
        onEdit.invoke()
        popupWindow.dismiss()
    }

    private fun onShareClicked(view: View) {
        onShare.invoke()
        popupWindow.dismiss()
    }

    private fun onDeleteClicked(view: View) {
        logD("program menu delete clicked")
        onDelete.invoke()
        popupWindow.dismiss()
    }
}
