package com.axles.smartfitness.view

import android.content.Context
import com.axles.smartfitness.engine.cardio.*

object PickerValueFactory {

    fun createFirstTimeValues(): List<String> {
        return List(60) {
            it
        }.map {
            if (it < 10) {
                "0$it"
            } else it.toString()
        }
    }

    fun createSecondTimeValues(): List<String> {
        return List(6) {
            it * 10
        }.map {
            if (it == 0) {
                "00"
            } else {
                it.toString()
            }
        }
    }

    fun createFirstDistanceValues(): List<String> {
        return List(100) {
            it
        }.map {
            it.toString()
        }
    }

    fun createSecondDistanceValues(): List<String> {
        return List(10) {
            it * 10
        }.map {
            if (it == 0) {
                "00"
            } else {
                it.toString()
            }
        }
    }

    fun createAlternativeFirstDistanceValues(): List<String> {
        return List(31) {
            it
        }.map {
            it.toString()
        }
    }

    fun createAlternativeSecondDistanceValues(): List<String> {
        return List(100) {
            it
        }.map {
            String.format("%02d", it)
        }
    }

    fun createFirstSpeedValues(): List<String> {
        return List(26) {
            it
        }.map {
            it.toString()
        }
    }

    fun createSecondSpeedValues(): List<String> {
        return List(10) {
            it
        }.map {
            it.toString()
        }
    }

    fun createFirstInclineValues(): List<String> {
        return List(21) {
            it
        }.map {
            it.toString()
        }
    }

    fun createSecondInclineValues(): List<String> {
        return List(2) {
            it * 5
        }.map {
            it.toString()
        }
    }

    fun createResistanceValues(): List<String> {
        return List(25) {
            it + 1
        }.map {
            it.toString()
        }
    }

    fun createRPMFirstValues(): List<String> {
        return List(100) {
            (it * 2) + 10
        }.map {
            it.toString()
        }
    }

    fun createRPMSecondValues(): List<String> {
        return List(101) {
            (it * 2) + 20
        }.map {
            it.toString()
        }
    }

    fun createSPMFirst(): List<String> {
        return List(106) {
            (it * 2) + 10
        }.map {
            it.toString()
        }
    }

    fun createSPMSecond(): List<String> {
        return List(106) {
            (it * 2) + 20
        }.map {
            it.toString()
        }
    }

    fun createRowingMachineIntensity(context: Context): List<String> {
        return listOf(
            context.getString(RowingMachineActivity.Intensity.MEDIUM.intensityRes),
            context.getString(RowingMachineActivity.Intensity.EASY.intensityRes),
            context.getString(RowingMachineActivity.Intensity.HARD.intensityRes),
            context.getString(RowingMachineActivity.Intensity.RACE_PACE.intensityRes),
            context.getString(RowingMachineActivity.Intensity.WARM_UP.intensityRes),
            context.getString(RowingMachineActivity.Intensity.COOL_DOWN.intensityRes),
            context.getString(RowingMachineActivity.Intensity.REST.intensityRes)
        )
    }

    fun createRowingMachinePaceFirst(): List<String> {
        return List(10) {
            it
        }.map {
            "0$it"
        }
    }

    fun createRowingMachinePaceSecond(): List<String> {
        return List(60) {
            it
        }.map {
            if (it < 10) {
                "0$it"
            } else {
                it.toString()
            }
        }
    }

    fun createRowingMachineSpmFirst() = List(91) {
        it
    }.map {
        it.toString()
    }

    fun createRowingMachineSpmSecond() = List(81) {
        it
    }.map {
        it.toString()
    }

    fun createStepsValues() = List(1000) {
        it
    }.map {
        it.toString()
    }

    fun createStepsSpmFirst() = List(91) {
        it
    }.map {
        it.toString()
    }

    fun createStepsSpmSecond() = List(91) {
        it
    }.map {
        it.toString()
    }

    fun createStyle(context: Context) = listOf(
        context.getString(SwimmingActivity.Style.FRONT_CRAWL.styleRes),
        context.getString(SwimmingActivity.Style.BREASTSTROKE.styleRes),
        context.getString(SwimmingActivity.Style.BACKSTROKE.styleRes),
        context.getString(SwimmingActivity.Style.BUTTERFLY_STROKE.styleRes)
    )

    fun createSwimmingIntensity(context: Context) = listOf(
        context.getString(SwimmingActivity.Intensity.MAIN_SET.swimmingIntensityRes),
        context.getString(SwimmingActivity.Intensity.WARM_UP.swimmingIntensityRes),
        context.getString(SwimmingActivity.Intensity.COOL_DOWN.swimmingIntensityRes),
        context.getString(SwimmingActivity.Intensity.REST.swimmingIntensityRes)
    )

    fun createExercise(context: Context) = listOf(
        context.getString(SwimmingActivity.ExerciseType.NORMAL.swimmingExerciseRes),
        context.getString(SwimmingActivity.ExerciseType.SPRINT.swimmingExerciseRes),
        context.getString(SwimmingActivity.ExerciseType.NO_LEGS.swimmingExerciseRes),
        context.getString(SwimmingActivity.ExerciseType.NO_HANDS.swimmingExerciseRes),
        context.getString(SwimmingActivity.ExerciseType.BREATH_HOLD.swimmingExerciseRes)
    )

    fun createMachineNumbers() = List(101) {
        if (it == 0) {
            "--"
        } else {
            (it + 1).toString()
        }
    }

}