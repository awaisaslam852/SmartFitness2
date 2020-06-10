package com.axles.smartfitness.engine

import android.Manifest
import android.R
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.os.Build
import android.os.Environment
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DimenRes
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import com.axles.smartfitness.app.SmartFitnessApplication
import com.axles.smartfitness.ui.dialogs.LoadingDialog
import com.axles.smartfitness.ui.dialogs.OkDialog
import java.io.*
import java.util.*

class Helper {
	companion object {
		var SDK_SUPPORTED = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH

		const val REQUEST_CAMERA_PERMISSION = 1

		fun showErrorAlert(context: Context?, sMsg: String?) {
			AlertDialog.Builder(context)
				.setTitle("Error")
				.setMessage(sMsg)
				.setPositiveButton(
					"OK"
				) { dialog, which -> }.show()
		}

		fun showMessageAlert(context: Context?, sTitle: String?, sMsg: String?) {
			AlertDialog.Builder(context)
				.setTitle(sTitle)
				.setMessage(sMsg)
				.setPositiveButton(
					"OK"
				) { dialog, which -> }.show()
		}

		fun showToast(context: Context, messageID: Int) {
			showToast(context, context.resources.getString(messageID))
		}

		fun showToast(context: Context?, message: String?) {
			showCustomToast(context!!, message, Toast.LENGTH_SHORT)
		}

		fun showErrorToast(context: Context, messageID: Int) {
			showErrorToast(context, context.resources.getString(messageID))
		}

		fun showErrorToast(context: Context?, message: String?) {
			showCustomToast(context!!, message, Toast.LENGTH_LONG)
		}

		private fun showCustomToast(context: Context, message: String?, length: Int) {
			val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
			val textView = (toast.view as ViewGroup).getChildAt(0)
			if (textView is TextView) { textView.textSize = 17f }
			toast.show()
		}

		fun showErrorAlert(fragmentManager: FragmentManager, @StringRes messageId: Int) {
			showAlert(fragmentManager, string(messageId))
		}

		fun showErrorAlert(fragmentManager: FragmentManager, message: String?) {
			showAlert(fragmentManager, message)
		}

		private fun showAlert(fragmentManager: FragmentManager, message: String?) {
			if (message == null) return
			OkDialog(message).show(fragmentManager, "ErrorAlert")
		}

		private var loadingDialog: LoadingDialog? = null
		fun showLoading(fragmentManager: FragmentManager) {
			hideLoading()
			loadingDialog = LoadingDialog()
			loadingDialog?.show(fragmentManager, "Loading")
		}

		fun hideLoading() {
			loadingDialog?.dismiss()
			loadingDialog = null
		}

		fun hideSoftKeyboard(context: Context) {
			if ((context as Activity).currentFocus != null) {
				val inputMethodManager =
					context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
				inputMethodManager.hideSoftInputFromWindow(
					context.currentFocus!!.windowToken, 0
				)
			}
		}

		fun showSoftKeyboard(context: Context, view: View) {
			val inputMethodManager = (context as Activity).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
			view.requestFocus()
			inputMethodManager.showSoftInput(view, 0)
		}

		private fun isImmersiveAvailable(context: Context): Boolean {
			return Build.VERSION.SDK_INT >= 19
		}

		// This snippet hides the system bars.
		fun hideSystemUI(context: Context) {
			if (!isImmersiveAvailable(context)) return

			// Set the IMMERSIVE flag.
			// Set the content to appear under the system bars so that the content
			// doesn't resize when the system bars hide and show.
			(context as Activity).window.decorView.systemUiVisibility =
				(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
						or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
						or View.SYSTEM_UI_FLAG_IMMERSIVE)
		}

		// This snippet shows the system bars. It does this by removing all the flags
		// except for the ones that make the content appear under the system bars.
		fun showSystemUI(context: Context) {
			if (!isImmersiveAvailable(context)) return
			(context as Activity).window.decorView.systemUiVisibility =
				(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
		}

		fun hideSystemUIPermanently(context: Context) {
			hideSystemUI(context)
			(context as Activity).window.decorView
				.setOnSystemUiVisibilityChangeListener { hideSystemUI(context) }
		}

		fun isDataConnectionAvailable(): Boolean {
			val context: Context = SmartFitnessApplication.instance
			val connectivityManager =
				context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
			val info = connectivityManager.activeNetworkInfo ?: return false
			return connectivityManager.activeNetworkInfo.isConnected
		}

		fun setLanguage(localeString: String?) {
			val locale = Locale(localeString)
			Locale.setDefault(locale)
			val config = Configuration()
			config.locale = locale
			val context: Context = SmartFitnessApplication.instance
			context.resources
				.updateConfiguration(config, context.resources.displayMetrics)
		}

		fun requestStoragePermission(activity: Activity?) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(
					activity!!,
					Manifest.permission.READ_EXTERNAL_STORAGE
				)
			) {
				// Provide an additional rationale to the user if the permission was not granted
				// and the user would benefit from additional context for the use of the permission.
				// For example if the user has previously denied the permission.
				AlertDialog.Builder(activity)
					.setTitle("Permission Request")
					.setMessage("Requesting Permission to Access Internet")
					.setCancelable(false)
					.setPositiveButton(
						R.string.yes
					) { dialog, which -> //re-request
						ActivityCompat.requestPermissions(
							activity,
							arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
							0
						)
					}
					.show()
			} else {
				// READ_PHONE_STATE permission has not been granted yet. Request it directly.
				ActivityCompat.requestPermissions(
					activity,
					arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
					0
				)
			}
		}

		fun checkAndRequestCameraPermission(activity: Activity?): Boolean {
			if (ActivityCompat.checkSelfPermission(
					activity!!,
					Manifest.permission.CAMERA
				) != PackageManager.PERMISSION_GRANTED
			) {
				ActivityCompat.requestPermissions(
					activity,
					arrayOf(
						Manifest.permission.CAMERA,
						Manifest.permission.READ_EXTERNAL_STORAGE,
						Manifest.permission.WRITE_EXTERNAL_STORAGE
					),
					REQUEST_CAMERA_PERMISSION
				)
				return false
			}
			return true
		}

		fun writeByteToFile(bytes: ByteArray?, filePath: String?) {
			try {
				val file = File(filePath)
				if (file.exists()) {
					file.delete()
				}
				file.createNewFile()
				val bos =
					BufferedOutputStream(FileOutputStream(file))
				bos.write(bytes)
				bos.flush()
				bos.close()
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}

		/*
		fun getUniqueIMEIId(): String? {
			val context: Context = SmartFitnessApplication.instance
			try {
				val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
				if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
					return ""
				}
				val imei = telephonyManager.getDeviceId()
				return if (imei != null && !imei.isEmpty()) {
					imei
				} else {
					Build.SERIAL
				}
			} catch (e: Exception) {
				e.printStackTrace()
			}
			return "not_found"
		}

		 */

		fun randomColor(): Int {
			val red = (Math.random() * 1000 % 256).toInt()
			val green = (Math.random() * 1000 % 256).toInt()
			val blue = (Math.random() * 1000 % 256).toInt()
			return Color.rgb(red, green, blue)
		}

		fun randomIndex(scale: Int): Int {
			return (Math.random() * 1000).toInt() % scale
		}

		fun saveTemporaryImage(bitmap: Bitmap): String {
			val bytes = ByteArrayOutputStream()
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
			val wallpaperDirectory = File((Environment.getExternalStorageDirectory()).toString() + "/bitmaps")
			// have the object build the directory structure, if needed.
			Log.d("fee",wallpaperDirectory.toString())
			if (!wallpaperDirectory.exists()) {
				wallpaperDirectory.mkdirs()
			}

			try {
				Log.d("heel",wallpaperDirectory.toString())
				val f = File(wallpaperDirectory, ((Calendar.getInstance().getTimeInMillis()).toString() + ".jpg"))
				f.createNewFile()
				val fo = FileOutputStream(f)
				fo.write(bytes.toByteArray())
				MediaScannerConnection.scanFile(SmartFitnessApplication.instance, arrayOf(f.getPath()), arrayOf("image/jpeg"), null)
				fo.close()
				Log.d("TAG", "File Saved::--->" + f.getAbsolutePath())

				return f.getAbsolutePath()
			} catch (e1: IOException) {
				e1.printStackTrace()
			}

			return ""
		}

		fun color(resourceId: Int): Int {
			return SmartFitnessApplication.instance.resources.getColor(resourceId)
		}

		fun string(resourceId: Int): String {
			return SmartFitnessApplication.instance.resources.getString(resourceId)
		}

		fun dimension(@DimenRes dimenRes: Int): Float {
			return SmartFitnessApplication.instance.resources.getDimension(dimenRes)
		}

		fun convertDPtoPixel(dp: Float): Float {
			val resources = SmartFitnessApplication.instance.resources
			return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
		}

		fun convertSPtoPixel(sp: Float): Float {
			val resources = SmartFitnessApplication.instance.resources
			return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics)
		}

		fun packageName(): String {
			return SmartFitnessApplication.instance.packageName
		}

		fun googlePlayStoreLink(): String {
			return "https://play.google.com/store/apps/details?id=" + packageName()
		}

		fun deviceModel(): String {
			return android.os.Build.MODEL
		}

		fun versionNumber(): String {
			try {
				val pInfo = SmartFitnessApplication.instance.getPackageManager().getPackageInfo(
					packageName(), 0)
				return pInfo.versionName
			} catch (e: PackageManager.NameNotFoundException) {
				e.printStackTrace()
			}
			return "1.0.0"
		}

		fun getSharedPreferences(key: String, default: String): String {
			val sharedPreferences = SmartFitnessApplication.instance.getSharedPreferences(SmartFitnessApplication::class.java.simpleName, 0)
			return sharedPreferences.getString(key, default) ?: default
		}

		fun putSharedPreferences(key: String, value: String) {
			val sharedPreferences = SmartFitnessApplication.instance.getSharedPreferences(SmartFitnessApplication::class.java.simpleName, 0)
			sharedPreferences.edit().putString(key, value).apply()
		}
	}
}