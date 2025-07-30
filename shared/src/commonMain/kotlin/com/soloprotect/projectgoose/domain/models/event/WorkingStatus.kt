package com.myprotect.projectx.domain.models.event

import com.myprotect.projectx.common.LocaleKeys

enum class WorkingStatus(val defaultName: String, val id: Int, val hasExpectToWork: Boolean) {
    AVAILABLE("Available", 1, false),
    FURLOUGH_LEAVE("Furlough Leave", 12, true),
    MATERNITY("Maternity", 11, true),
    OFF_SHIFT("Off Shift", 3, true),
    ON_ANNUAL_LEAVE("On Annual Leave", 6, true),
    ON_BREAK("On a Break", 4, false),
    ON_LEAVE("On Leave", 5, true),
    ON_SHIFT("On Shift", 2, false),
    ON_SICK_LEAVE("On Sick Leave", 13, true),
    PATERNITY("Paternity", 10, true),
    SELF_ISOLATING_UNABLE_TO_WORK("Self Isolating - Unable to Work", 9, true),
    SELF_ISOLATING_WORKING_FROM_HOME("Self Isolating - Working From Home", 8, true),
    WORKING_FROM_HOME("Working From Home", 7, false);
}

fun WorkingStatus.displayName(): String {
    return defaultName
}

fun WorkingStatus.localeKey(): String {
    return when (this) {
        WorkingStatus.AVAILABLE -> LocaleKeys.USERSTATUS_AVAILABLE
        WorkingStatus.FURLOUGH_LEAVE -> LocaleKeys.USERSTATUS_FURLOUGH_LEAVE
        WorkingStatus.MATERNITY -> LocaleKeys.USERSTATUS_MATERNITY
        WorkingStatus.OFF_SHIFT -> LocaleKeys.USERSTATUS_OFF_SHIFT
        WorkingStatus.ON_ANNUAL_LEAVE -> LocaleKeys.USERSTATUS_ON_ANNUAL_LEAVE
        WorkingStatus.ON_BREAK -> LocaleKeys.USERSTATUS_ON_BREAK
        WorkingStatus.ON_LEAVE -> LocaleKeys.ON_LEAVE
        WorkingStatus.ON_SHIFT -> LocaleKeys.USERSTATUS_ON_SHIFT
        WorkingStatus.ON_SICK_LEAVE -> LocaleKeys.USERSTATUS_ON_LEAVE
        WorkingStatus.PATERNITY -> LocaleKeys.USERSTATUS_PATERNITY
        WorkingStatus.SELF_ISOLATING_UNABLE_TO_WORK -> LocaleKeys.USERSTATUS_SELF_ISOLATING_UNABLE_TO_WORK
        WorkingStatus.SELF_ISOLATING_WORKING_FROM_HOME -> LocaleKeys.USERSTATUS_SELF_ISOLATING_WORKING_FROM_HOME
        WorkingStatus.WORKING_FROM_HOME -> LocaleKeys.USERSTATUS_WORKING_FROM_HOME
    }
}
