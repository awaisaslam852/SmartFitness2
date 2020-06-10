package com.axles.smartfitness.ui.profile

import com.axles.smartfitness.engine.user.User
import com.axles.smartfitness.engine.user.User.*
import java.util.*

data class ProfileUI(
    var birthDay: Date,
    var gender: Gender,
    var height: Double,
    var id: Int,
    var imageUrl: String,
    var level: FitnessLevel,
    var username: String,
    var usersInBlackLists: List<Any>,
    var weight: Double
)

fun User.toUI() =
    ProfileUI(
        birthDay,
        gender,
        height,
        id,
        imageUrl,
        level,
        username,
        usersInBlackLists,
        weight
    )

