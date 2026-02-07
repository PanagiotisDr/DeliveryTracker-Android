package com.deliverytracker.app.presentation.util

import android.content.Context
import com.deliverytracker.app.R
import com.deliverytracker.app.domain.model.Shift

/**
 * Extension function για localized μορφοποίηση ωρών εργασίας.
 * Αντικαθιστά το deprecated Shift.formattedWorkTime (hardcoded ελληνικά).
 * 
 * @param context Android Context για πρόσβαση σε string resources
 * @return Μορφοποιημένο string π.χ. "8h 30m" (EN) ή "8ω 30λ" (EL)
 */
fun Shift.formattedWorkTime(context: Context): String {
    val hoursSuffix = context.getString(R.string.shift_hours_suffix)
    val minutesSuffix = context.getString(R.string.shift_minutes_suffix)
    return context.getString(
        R.string.work_time_format,
        workedHours,
        hoursSuffix,
        workedMinutes,
        minutesSuffix
    )
}
