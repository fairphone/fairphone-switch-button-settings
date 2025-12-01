/*
 * Copyright (C) 2025 FairPhone B.V.
 *
 * SPDX-FileCopyrightText: 2025. FairPhone B.V.
 *
 * SPDX-License-Identifier: EUPL-1.2
 */

package com.fairphone.settings.switchbutton.util

import android.app.NotificationManager
import android.app.UiModeManager
import android.app.role.RoleManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.net.ConnectivityManager
import android.os.PowerManager
import android.os.UserHandle
import android.provider.Settings
import android.util.Log

fun Context.cameraManager() = getSystemService(Context.CAMERA_SERVICE) as CameraManager

fun Context.powerManager() = getSystemService(Context.POWER_SERVICE) as PowerManager

fun Context.notificationManager() =
    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

fun Context.uiModeManager() = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager

fun Context.connectivityManager() =
    getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

fun Context.roleManager() = getSystemService(Context.ROLE_SERVICE) as RoleManager

fun Context.isFairphoneMomentsAvailable(): Boolean =
    isPackageAvailable(Constants.FAIRPHONE_MOMENTS_PACKAGE_NAME)

fun Context.isPackageAvailable(packageName: String): Boolean = try {
    packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA)
    true
} catch (e: PackageManager.NameNotFoundException) {
    false
}

/**
 * Start the fairphone moments settings activity.
 */
fun Context.startFairphoneMomentsSettings() = try {
    val intent = Intent(Constants.ACTION_SPRING_LAUNCHER_SETTINGS).apply {
        `package` = Constants.FAIRPHONE_MOMENTS_PACKAGE_NAME
        component = ComponentName(
            Constants.FAIRPHONE_MOMENTS_PACKAGE_NAME,
            Constants.FAIRPHONE_MOMENTS_SETTINGS_ACTIVITY,
        )
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    startActivity(intent)
} catch (e: Exception) {
    Log.e("SwitchButtonSetting", "Error starting spring launcher settings", e)
}

/**
 * @return true if user device setup is complete, false otherwise
 */
fun Context.isUserSetupComplete(): Boolean {
    return Settings.Secure.getIntForUser(
        contentResolver,
        Settings.Secure.USER_SETUP_COMPLETE, 0, UserHandle.USER_CURRENT
    ) != 0
}
