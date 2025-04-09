/*
 * Copyright (c) 2025. Fairphone B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fairphone.settings.switchbutton.util

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.NotificationManager
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.UserHandle
import android.util.Log
import androidx.core.content.ContextCompat
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

object LauncherUtils {
    /**
     * Enable detox mode. This process consists in the following steps:
     *
     *  - Set detox launcher as default launcher
     *  - Show detox mode enabled hint or start detox launcher
     *  - Enable DND
     *  - (disabled) Set detox lockscreen wallpaper
     */
    @SuppressLint("MissingPermission")
    suspend fun enableDetoxMode(context: Context) {
        Log.d(Constants.LOG_TAG, "enabling detox mode")

        // Set Spring Launcher as default launcher
        val homeAppSetSuccess =
            setDefaultHomeAppAsync(context, Constants.SPRING_LAUNCHER_PACKAGE_NAME)

        if (homeAppSetSuccess) {
            // Show switch enabled hint
            if (shouldShowOverlayAnimation(context)) {
                Log.d(Constants.LOG_TAG, "showing overlay animation")
                showDetoxEnabledOverlayHint(context)
            } else {
                startLauncherIntent(context, Constants.SPRING_LAUNCHER_PACKAGE_NAME)
            }

            // Enable DND
            context
                .notificationManager()
                .setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)

            // TODO: Change lockscreen wallpaper
            // Set lockscreen wallpaper
            //LockscreenUtils.setDetoxLockscreenWallpaper(context)
            LockscreenWallpaperWorker.enqueueWallpaperWork(context, isDetoxEnabled = true)
        } else {
            //TODO: Handle launcher switch error
        }
    }

    /**
     * Disable detox mode. This process consists in the following steps:
     *
     *  - Set stock launcher as default launcher (probably need to refine these to support 3rd party launchers)
     *  - Show detox mode disabled hint or start launcher(HOME) Intent
     *  - Disable DND
     *  - (disabled) Restore lockscreen wallpaper
     */
    @SuppressLint("MissingPermission")
    suspend fun disableDetoxMode(context: Context) {
        Log.d(Constants.LOG_TAG, "disabling detox mode")
        // Set Search Launcher as default launcher
        val homeAppSetSuccess =
            setDefaultHomeAppAsync(context, Constants.STOCK_LAUNCHER_PACKAGE_NAME)

        if (homeAppSetSuccess) {
            // Show switch disabled hint
            if (shouldShowOverlayAnimation(context)) {
                Log.d(Constants.LOG_TAG, "showing overlay animation")
                showDetoxDisabledOverlayHint(context)
            } else {
                startLauncherIntent(context, Constants.STOCK_LAUNCHER_PACKAGE_NAME)
            }

            // Disable DND
            context
                .notificationManager()
                .setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)

            // TODO: Revert lockscreen wallpaper
            LockscreenWallpaperWorker.enqueueWallpaperWork(context, isDetoxEnabled = false)
        } else {
            //TODO: Handle launcher switch error
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun setDefaultHomeAppAsync(context: Context, packageName: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            Log.d(Constants.LOG_TAG, "setting default home app: $packageName")

            try {
                val roleManager = context.getSystemService(Context.ROLE_SERVICE) as RoleManager
                val foregroundUser = ActivityManager.getCurrentUser()
                roleManager.addRoleHolderAsUser(
                    RoleManager.ROLE_HOME,
                    packageName,
                    0,
                    UserHandle.of(foregroundUser),
                    ContextCompat.getMainExecutor(context),
                ) { success: Boolean ->
                    if (success) {
                        Log.d(Constants.LOG_TAG, "$packageName is now the default HOME app")
                    } else {
                        Log.e(Constants.LOG_TAG, "Error setting $packageName as default HOME app")
                    }
                    continuation.resume(success)
                }
            } catch (e: Exception) {
                Log.e(Constants.LOG_TAG, "Error setting $packageName as default home app", e)
                continuation.resume(false)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun showDetoxEnabledOverlayHint(context: Context) {
        val intent = Intent(Constants.ACTION_SHOW_SWITCH_BUTTON_HINT).apply {
            putExtra(
                Constants.EXTRA_SWITCH_BUTTON_STATE,
                Constants.EXTRA_SWITCH_BUTTON_STATE_ENABLED
            )
            putExtra(
                Constants.EXTRA_SHOW_OVERLAY,
                true
            )
            setPackage(Constants.SPRING_LAUNCHER_PACKAGE_NAME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivityAsUser(intent, UserHandle.SYSTEM)
    }

    @SuppressLint("MissingPermission")
    private fun showDetoxDisabledOverlayHint(context: Context) {
        val intent = Intent(Constants.ACTION_SHOW_SWITCH_BUTTON_HINT).apply {
            putExtra(
                Constants.EXTRA_SWITCH_BUTTON_STATE,
                Constants.EXTRA_SWITCH_BUTTON_STATE_DISABLED
            )
            putExtra(
                Constants.EXTRA_SHOW_OVERLAY,
                true
            )
            setPackage(Constants.SPRING_LAUNCHER_PACKAGE_NAME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        context.startActivityAsUser(intent, UserHandle.SYSTEM)
    }

    /**
     * Detox Launcher should display an overlay animation when user is interacting with any app,
     * or what is the same: it should NOT display an overlay animation when user is
     * interacting with stock launcher or spring launcher
     */
    @Suppress("DEPRECATION")
    private fun shouldShowOverlayAnimation(context: Context): Boolean {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        try {
            // Get the foreground task
            val runningTasks = am.getRunningTasks(1)
            runningTasks?.getOrNull(0)?.topActivity?.let { topActivity ->
                Log.d(Constants.LOG_TAG, "Top activity package: ${topActivity.packageName}")
                Log.d(Constants.LOG_TAG, "Top activity name: ${topActivity.className}")
                return when {
                    topActivity.packageName == Constants.STOCK_LAUNCHER_PACKAGE_NAME -> false
                    topActivity.packageName == Constants.SPRING_LAUNCHER_PACKAGE_NAME
                            && topActivity.className == Constants.SPRING_LAUNCHER_HOME_ACTIVITY -> false

                    else -> true
                }
            } ?: true
        } catch (e: SecurityException) {
            Log.e(Constants.LOG_TAG, "Permission needed for getRunningTasks")
        }
        return true
    }


    private fun startLauncherIntent(context: Context, launcherPackageName: String) {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            setPackage(launcherPackageName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NO_ANIMATION
        }
        context.startActivity(intent)
    }
}
