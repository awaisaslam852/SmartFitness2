package com.axles.smartfitness.ui.login

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.axles.smartfitness.R
import com.axles.smartfitness.extensions.logD
import com.axles.smartfitness.provider.RetrofitProvider

class InstagramAuthenticationDialog(val tokenReceivedListener: (token: String?)->Unit) : DialogFragment(){

    lateinit var redirectUrl : String
    lateinit var requestUrl : String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            buildDialog(it)

        } ?: throw IllegalStateException("Activity cannot be null")
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun buildDialog(activity: Activity) : AlertDialog {
        val builder = AlertDialog.Builder(activity)
        val view = View.inflate(activity, R.layout.instagram_authentication_dialog, null)

        redirectUrl = getString(R.string.instagram_redirect_url).also { logD("redirect $it") }
        requestUrl = "${getString(R.string.instagram_base_url)}$INSTAGRAM_OAUTH${getString(R.string.instagram_app_id)}$REDIRECT_URI$redirectUrl$RESPONSE_TYPE".also { logD("request url $it") }

        val webView = view.findViewById<WebView>(R.id.instagram_web_view)
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(requestUrl)
        webView.webViewClient = object : WebViewClient(){
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                logD("should override url loading url:$url")
                if (url?.startsWith(redirectUrl) == true){
                    if (url.contains(ACCESS_CODE)){
                        callForToken(url.substringAfterLast("code=").dropLast(2))
                    }

                    this@InstagramAuthenticationDialog.dismiss()
                    return true
                }

                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                logD("onPageFinished $url")
                if (url?.contains(ACCESS_CODE) == true){
                    val uri = Uri.parse(url)
                    val codeFragment = uri.encodedFragment
                    logD("accesstoken fragment $codeFragment")
                    val code = codeFragment?.substring(codeFragment.lastIndexOf("=") + 1)
                    logD("accesstoken $code")

//                    tokenReceivedListener.invoke(accessToken)
                }
            }
        }

        builder.setView(view)
        return builder.create()
    }

    private fun callForToken(code: String?) {
        logD("call $code")
        tokenReceivedListener.invoke(code)
    }

    companion object {
        private const val INSTAGRAM_OAUTH = "oauth/authorize/?client_id="
        private const val REDIRECT_URI = "&redirect_uri="
        private const val RESPONSE_TYPE = "&response_type=code&display=touch&scope=user_profile" //user_profile//token = code
        private const val ACCESS_CODE = "code"//"code"
    }

}