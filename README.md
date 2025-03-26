# SwitchButtonSettings

This application provides settings for controlling the SwitchButton functionality.  It interacts with SpringLauncherCore and SpringLauncherApp to enable and configure the Detox feature.

## Functionality

*   Provides a user interface within the system settings to configure the action to trigger when the switch button is flipped.
*   Declares a Broadcast Receiver that will receive broadcast when flipping the switch button.
*   Communicates with SpringLauncherCore to start or stop to the detox feature.

### Switch button broadcast

When the switch button is flipped, it should send a broadcast that will be handled by this app:

*   Action: com.fairphone.action.SWITCH_BUTTON_STATE_CHANGED
*	Extra key: com.fairphone.extra.SWITCH_STATUS
*	Extra value: UP | DOWN


## Integration

This application is pre-loaded into `/system/priv-app/SwitchButtonSettings` and requires privileged permissions.  The required permissions are defined in `com.fairphone.settings.switchbutton.xml` and must be included in the system image build.  The `default-permissions-fairphone-system.xml-changes.patch` should be applied to grant these permissions.  The settings UI is integrated into the main Settings app using the `settings-app-changes.patch`.

This app IS A PRIVILEGED APP THAT NEEDS TO BE SIGNED WITH PLATFORM KEYS.

There is a Android.bp file included, to facilitate the integration of the app.