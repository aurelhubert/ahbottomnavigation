
# AHBottomNavigation
Library to implement the Bottom Navigation component from Material Design guidelines (minSdkVersion=14).

## Demo
<img src="https://raw.githubusercontent.com/aurelhubert/ahbottomnavigation/master/demo1.gif" width="208" height="368" /> <img src="https://raw.githubusercontent.com/aurelhubert/ahbottomnavigation/master/demo2.gif" width="208" height="368" /> <img src="https://raw.githubusercontent.com/aurelhubert/ahbottomnavigation/master/demo3.gif" width="208" height="368" /> <img src="https://raw.githubusercontent.com/aurelhubert/ahbottomnavigation/master/demo4.gif" width="208" height="368" />

## What's new (1.5.1) - [Changelog](https://github.com/aurelhubert/ahbottomnavigation/blob/master/CHANGELOG.md)
* Fixed ripple effect bug (API 21+)

## Features
* Follow the bottom navigation guidelines (https://www.google.com/design/spec/components/bottom-navigation.html)
* Add 3 to 5 items (with title, icon & color)
* Choose your style: Classic or colored navigation
* Add a OnTabSelectedListener to detect tab selection
* Support icon font color with "setForceTint(true)"
* Manage notififcations for each item

## How to?

### Gradle
```groovy
dependencies {
    compile 'com.aurelhubert:ahbottomnavigation:1.5.1'
}
```
### XML
```xml
<com.aurelhubert.ahbottomnavigation.AHBottomNavigation
        android:id="@+id/bottom_navigation"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
```
OR
```xml
<android.support.design.widget.CoordinatorLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    ...

    <com.aurelhubert.ahbottomnavigation.AHBottomNavigation
        android:id="@+id/bottom_navigation"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom" />

</android.support.design.widget.CoordinatorLayout>
```

### Activity/Fragment
```java
AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

// Create items
AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.tab_1, R.drawable.ic_maps_place, R.color.color_tab_1);
AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.tab_2, R.drawable.ic_maps_local_bar, R.color.color_tab_2);
AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.tab_3, R.drawable.ic_maps_local_restaurant, R.color.color_tab_3);

// Add items
bottomNavigation.addItem(item1);
bottomNavigation.addItem(item2);
bottomNavigation.addItem(item3);

// Set background color
bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));

// Disable the translation inside the CoordinatorLayout
bottomNavigation.setBehaviorTranslationEnabled(false);

// Change colors
bottomNavigation.setAccentColor(Color.parseColor("#F63D2B"));
bottomNavigation.setInactiveColor(Color.parseColor("#747474"));

// Force to tint the drawable (useful for font with icon for example)
bottomNavigation.setForceTint(true);

// Force the titles to be displayed (against Material Design guidelines!)
bottomNavigation.setForceTitlesDisplay(true);
// Or force the titles to be hidden (against Material Design guidelines, too!)
bottomNavigation.setForceTitlesHide(true);

// Use colored navigation with circle reveal effect
bottomNavigation.setColored(true);

// Set current item programmatically
bottomNavigation.setCurrentItem(1);

// Customize notification (title, background, typeface)
bottomNavigation.setNotificationBackgroundColor(Color.parseColor("#F63D2B"));

// Add or remove notification for each item
bottomNavigation.setNotification("1", 3);
// OR
AHNotification notification = new AHNotification.Builder()
    .setText("1")
    .setBackgroundColor(ContextCompat.getColor(DemoActivity.this, R.color.color_notification_back))
    .setTextColor(ContextCompat.getColor(DemoActivity.this, R.color.color_notification_text))
     .build();
bottomNavigation.setNotification(notification, 1);

// Set listeners
bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
	@Override
	public boolean onTabSelected(int position, boolean wasSelected) {
		// Do something cool here...
        return true;
	}
});
bottomNavigation.setOnNavigationPositionListener(new AHBottomNavigation.OnNavigationPositionListener() {
	@Override public void onPositionChange(int y) {
		// Manage the new y position
	}
});
```

### With XML menu
```java
int[] tabColors = getApplicationContext().getResources().getIntArray(R.array.tab_colors);
AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.bottom_navigation_menu_3);
navigationAdapter.setupWithBottomNavigation(bottomNavigation, tabColors);
```

## TODO

* Add color under the navigation bar.
* Manage tablet

## Contributions

Feel free to create issues / pull requests.

## License

```
AHBottomNavigation library for Android
Copyright (c) 2016 Aurelien Hubert (http://github.com/aurelhubert).

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
