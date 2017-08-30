## Changelog

### Newest version: 2.1.0

* Update libraries versions
* Add enable/disable tab state (with custom color)
* Add new xml attributes (`colored`, `accentColor`, `inactiveColor`, `disableColor`, `coloredActive`, `coloredInactive`)
* Add param `notificationAnimationDuration`
* Update getDrawable method with `AppCompatResources.getDrawable(context, drawableRes);`
If you use drawable selector and target API < 21, don't forget to add this:
`AppCompatDelegate.setCompatVectorFromResourcesEnabled(true); `

### 2.0.6

* Fix selected item background for API >= 21
* Fix `isHidden()` method
* Update design support library version

### 2.0.5

* Add `setTitleTextSizeInSp(float activeSize, float inactiveSize)`
* Update selected item background for API >= 21
* Allow for the disabling of sound effects

### 2.0.4

* Fixed the icon alpha update for API >= 25

### 2.0.3

* Added method `getViewAtPosition(int position)`

### 2.0.2

* Fix a bug when titles are always shown 

### 2.0.1

* Fix a crash with `setCurrentItem(int position, boolean useCallback)`

### 2.0.0

* **BREAKING!** 3 states for titles: `SHOW_WHEN_ACTIVE`, `ALWAYS_SHOW` & `ALWAYS_HIDE` (PR #140)
* Color under the navigation bar (PR #166)
* Fix CoordinatorLayout with FloatingActionButton: use `manageFloatingActionButtonBehavior`

### 1.5.1

* Fixed ripple effect bug (API 21+)

### 1.5.0

* Added AHNotification class to manage easily the style of each notification (PR #156) (**old method still works**)
* Added `setForceTitlesHide(boolean forceTitlesHide)` to force the titles to be hidden (when 3 or less items are displayed)
* Updated `buildToolsVersion` to version `24.0.2`
* Updated `'com.android.support:design:24.2.1'`

### 1.4.0

* Added `isHidden()` method.
* Added `setDefaultBackgroundResource(@DrawableRes int defaultBackgroundResource)`
* Added optional selected item background (PR #132)
* Displayed classic items for less than 3 items (PR #152)

### 1.3.3

* Added a setup method with colors for `AHBottomNavigationAdapter`

### 1.3.2

* Added a new class `AHBottomNavigationAdapter` to inflate menu from resources.
* Updated example to show how to implement `AHBottomNavigationAdapter`.

### 1.3.1

* Added `setColoredModeColors(@ColorInt int colorActive, @ColorInt int colorInactive)` to set the item color for the colored mode.
* Added `OnNavigationPositionListener` to follow the Y translation changes of the bottom navigation.
* Improved vector support.

### 1.3.0

* **BREAKING!** Updated listener, now return a boolean => `boolean onTabSelected(int position, boolean wasSelected);`
* Improved notification management for small items
* Added notification elevation
* Managed complex drawable (selector with states)
* Added constructor `public AHBottomNavigationItem(String title, Drawable drawable)`

### 1.2.3

* Added `setUseElevation(boolean useElevation, float elevation)`
* Fixed a bug with `behaviorTranslationEnabled` & `restoreBottomNavigation`
* Improved translation behavior when the Scroll View is not long enough.

### 1.2.2

* Fixed bug when switching between normal and colored mode

### 1.2.1

* Fixed method typo `setNotificationMarginLef` => `setNotificationMarginLeft`
* Avoid multiple call for showing/hiding AHBottomNavigation

### 1.2.0

* Updated Notification: now accept String (empty String to remove the notification)
* Deprecated integer for Notification
* Removed deprecated methods & interface for `AHBottomNavigationListener`
* Fixed touch ripples when the bottom navigation is colored
* Cleaned colors.xml to avoid conflicts
* Removed constructor AHBottomNavigationItem()
* Added `setTitleTextSize(float activeSize, float inactiveSize)`
* Added `setNotificationMarginLeft(int activeMargin, int inactiveMargin)`

### 1.1.8

* Added `hideBottomNavigation(boolean withAnimation)`
* Added `restoreBottomNavigation(boolean withAnimation)`

### 1.1.7

* Added `public AHBottomNavigationItem getItem(int position)` to get a specific item
* Added `public void refresh()` to force a UI refresh

### 1.1.6

* Improved `hideBottomNavigation()` and `restoreBottomNavigation()`
* Added `setTitleTypeface`
* Changed method name `setNotificationBackgroundColorResource` by `setNotificationTypeface`
* Started working on `onSaveInstanceState` and `onRestoreInstanceState` (currentItem & notifications for now)

### 1.1.5

* Added hideBottomNavigation()
* Added CURRENT_ITEM_NONE to unselect all items
* Improved Notifications (animation, size)

### 1.1.4

* Updated lib dependencies

### 1.1.3

* Fixed Snackbar when setBehaviorTranslationEnabled(false)

### 1.1.2

* Fixed animations on pre Kit Kat
* Added an example with Vector Drawable

### 1.1.1

* Fixed layout rendering with fragments

### 1.1.0

* Compatible with Snackbar
* Compatible with Floating Action Button

### 1.0.5

* Snackbar is now compatible

### 1.0.4

* Added: setCurrentItem(int position, boolean useCallback)
* Added: setUseElevation(boolean useElevation)
* Added: restoreBottomNavigation()

### 1.0.3

* Fixed setForceTint()

### 1.0.2

* Fixed crash when setForceTitlesDisplay(true)
* Improved UI

### 1.0.1

* Bug fixes
* Notifications
* Minimum SDK version: 14

### Before

* AHBottomNavigation was under development.
