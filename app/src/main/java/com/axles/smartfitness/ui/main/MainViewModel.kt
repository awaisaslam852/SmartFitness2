package com.axles.smartfitness.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axles.smartfitness.livedata.ResponseData
import com.axles.smartfitness.engine.network.session.SessionManagerFactory
import com.axles.smartfitness.ui.profile.ProfileUI
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainViewModel : ViewModel() {
    val profile: MutableLiveData<ResponseData<ProfileUI>> = MutableLiveData()
    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }

    private val disposable by lazy { CompositeDisposable() }

    private val sessionManager by lazy {
        SessionManagerFactory.sessionManager
    }

    fun logout() {
        sessionManager.clearAccessToken()
//        ApiProvider.usersApi.logout()
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                {},
//                {}
//            )
//            .addTo(disposable)
    }


    fun fetchUser() {
//        ApiProvider.usersApi
//            .fetchUser()
//            .map { it.toUI() }
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(
//                { profile.setValue(ResponseData.SuccessResponse(it)) },
//                { profile.value = ResponseData.ErrorResponse(Throwable("Error occurred")) }
//            )
//            .addTo(disposable)
    }
}
