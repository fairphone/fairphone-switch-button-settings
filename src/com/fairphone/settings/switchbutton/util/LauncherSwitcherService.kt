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
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.UserHandle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.fairphone.settings.switchbutton.data.model.SwitchState
import com.fairphone.settings.switchbutton.data.prefs.AppPrefs
import kotlin.coroutines.resume
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine

class LauncherSwitcherService(private val appPrefs: AppPrefs) {

    companion object {
        private var instance: LauncherSwitcherService? = null

        fun getInstance(context: Context): LauncherSwitcherService {
            if (instance == null) {
                instance = LauncherSwitcherService(AppPrefs(context))
            }
            return instance!!
        }
    }

    /**
     * Switch to user default preferred launcher.
     *
     * - Get the saved default home app package name.
     * - Set the default home app.
     * - Start switch state change activity in detox launcher to display overlay if needed.
     * - Set lockscreen wallpaper.
     */
    suspend fun switchToUserLauncher(context: Context): Result<Unit> {
        // Switch default launcher
        val shouldShowOverlayAnimation = shouldShowOverlayAnimation(context)
        val homeAppSetSuccess = setDefaultHomeAppAsync(context, appPrefs.getSavedHomeApp(context))
        if (homeAppSetSuccess) {
            // Start switch state change activity in detox launcher to display overlay
            startSwitchStateChangeActivity(
                context = context,
                detoxEnabled = false,
                showOverlay = shouldShowOverlayAnimation
            )
            // Set lockscreen wallpaper
            LockscreenWallpaperSwitcherWorker.enqueueWallpaperWork(
                context = context,
                isDetoxEnabled = false
            )
            return Result.success(Unit)
        } else {
            return Result.failure(Exception("Failed to set default launcher"))
        }
    }

    /**
     * Switch to Fairphone Moments (a.k.a. Spring) Launcher
     *
     * - Saved default home app package name (to switch back later).
     * - Set the default home app.
     * - Start switch state change activity in detox launcher to display overlay if needed.
     * - Set lockscreen wallpaper.
     */
    suspend fun switchToFairphoneMoments(context: Context): Result<Unit> {
        val shouldShowOverlayAnimation = shouldShowOverlayAnimation(context)
        val currentHomeApp = getDefaultHomeAppPackageName(context)
        appPrefs.saveDefaultHomeApp(context, currentHomeApp)

        // Switch default launcher
        val homeAppSetSuccess =
            setDefaultHomeAppAsync(
                context = context,
                packageName = Constants.FAIRPHONE_MOMENTS_PACKAGE_NAME
            )
        if (homeAppSetSuccess) {
            // Start switch state change activity in detox launcher to display overlay
            startSwitchStateChangeActivity(
                context = context,
                detoxEnabled = true,
                showOverlay = shouldShowOverlayAnimation
            )

            // Set lockscreen wallpaper
            LockscreenWallpaperSwitcherWorker.enqueueWallpaperWork(
                context,
                isDetoxEnabled = true
            )
            return Result.success(Unit)
        } else {
            return Result.failure(Exception("Failed to set default launcher"))
        }
    }

    /**
     * Set the default home app..
     */
    @SuppressLint("MissingPermission")
    private suspend fun setDefaultHomeAppAsync(context: Context, packageName: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            Log.d(Constants.LOG_TAG, "setting default home app: $packageName")

            try {
                val userManager =
                    context.getSystemService(Context.USER_SERVICE) as android.os.UserManager
                val profiles = userManager.userProfiles
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

    /**
     * Get the default home app package name.
     */
    private fun getDefaultHomeAppPackageName(context: Context): String {
        val roleManager = context.getSystemService(Context.ROLE_SERVICE) as RoleManager
        return roleManager.getDefaultApplication(RoleManager.ROLE_HOME).run {
            Log.d(Constants.LOG_TAG, "Default home app package name: $this")
            this
        } ?: throw IllegalStateException("Could not get default home app package name")
    }

    /**
     * Start switch state change activity in detox launcher to display overlay.
     */
    @SuppressLint("MissingPermission")
    private fun startSwitchStateChangeActivity(
        context: Context,
        detoxEnabled: Boolean,
        showOverlay: Boolean
    ) {
        val intent = Intent(Constants.ACTION_SHOW_SWITCH_BUTTON_HINT).apply {
            putExtra(
                Constants.EXTRA_SWITCH_BUTTON_STATE,
                if (detoxEnabled) {
                    Constants.EXTRA_SWITCH_BUTTON_STATE_ENABLED
                } else {
                    Constants.EXTRA_SWITCH_BUTTON_STATE_DISABLED
                }
            )
            putExtra(
                Constants.EXTRA_SHOW_OVERLAY,
                showOverlay,
            )
            setPackage(Constants.FAIRPHONE_MOMENTS_PACKAGE_NAME)
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
            val defaultHomeAppPackage = getDefaultHomeAppPackageName(context)
            runningTasks?.getOrNull(0)?.topActivity?.let { topActivity ->
                Log.d(Constants.LOG_TAG, "Top activity package: ${topActivity.packageName}")
                Log.d(Constants.LOG_TAG, "Top activity name: ${topActivity.className}")
                Log.d(Constants.LOG_TAG, "Default home app package: $defaultHomeAppPackage")
                return when {
                    topActivity.packageName == defaultHomeAppPackage -> false
                    topActivity.packageName == Constants.FAIRPHONE_MOMENTS_PACKAGE_NAME
                            && topActivity.className == Constants.FAIRPHONE_MOMENTS_HOME_ACTIVITY -> false

                    else -> true
                }
            } ?: true
        } catch (e: SecurityException) {
            Log.e(Constants.LOG_TAG, "Permission needed for getRunningTasks")
        }
        return true
    }
}
