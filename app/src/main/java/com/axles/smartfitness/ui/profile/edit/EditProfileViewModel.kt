package com.axles.smartfitness.ui.profile.edit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.axles.smartfitness.livedata.ResponseData
import com.axles.smartfitness.ui.profile.ProfileUI
import io.reactivex.disposables.CompositeDisposable

class EditProfileViewModel : ViewModel() {
    val profileLD: MutableLiveData<ResponseData<ProfileUI>> = MutableLiveData()
    val profile: ProfileUI?
        get() {
            return if (profileLD.value is ResponseData.SuccessResponse) {
                (profileLD.value as ResponseData.SuccessResponse<ProfileUI>).data!!
            } else null
        }

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
//                { profileLD.setValue(ResponseData.SuccessResponse(it)) },
//                { profileLD.value = ResponseData.ErrorResponse(Throwable("Error occurred")) }
//            )
//            .addTo(disposable)
    }
}