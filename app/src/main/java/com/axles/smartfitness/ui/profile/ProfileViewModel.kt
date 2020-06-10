package com.axles.smartfitness.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axles.smartfitness.livedata.ResponseData
import io.reactivex.disposables.CompositeDisposable

class ProfileViewModel : ViewModel() {
    val profile: MutableLiveData<ResponseData<ProfileUI>> = MutableLiveData()
    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }

    private val disposable by lazy { CompositeDisposable() }

    init {
        fetchUser()
    }

    private fun fetchUser() {
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

    fun uploadAvatar(path: String) {

    }
}