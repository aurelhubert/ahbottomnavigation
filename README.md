# AHBottomNavigation
Library to implement the Bottom Navigation component from Material Design guidelines (minSdkVersion=16).

# Demo
<img src="https://raw.githubusercontent.com/aurelhubert/ahbottomnavigation/master/demo1.gif" width="278" height="492" /> <img src="https://raw.githubusercontent.com/aurelhubert/ahbottomnavigation/master/demo2.gif" width="278" height="492" /> <img src="https://raw.githubusercontent.com/aurelhubert/ahbottomnavigation/master/demo3.gif" width="278" height="492" />

# Features

* Follow the bottom navigation guidelines (https://www.google.com/design/spec/components/bottom-navigation.html)
* Add 3 to 5 items (with title, icon & color)
* Choose your style: Classic or colored navigation
* Add a AHBottomNavigationListener to detect tab selection

# How to?

## Gradle
```groovy
dependencies {
    compile 'com.aurelhubert:ahbottomnavigation:0.1.1'
}
```
## XML
```xml
<com.aurelhubert.ahbottomnavigation.AHBottomNavigation
        android:id="@+id/bottom_navigation"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
```

## Activity/Fragment
```java
AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

// Create items
AHBottomNavigationItem item1 = new AHBottomNavigationItem("Label One", R.drawable.ic_maps_place, Color.parseColor("#455C65"));
AHBottomNavigationItem item2 = new AHBottomNavigationItem("Label Two", R.drawable.ic_maps_local_bar, Color.parseColor("#00886A"));
AHBottomNavigationItem item3 = new AHBottomNavigationItem("Label Three", R.drawable.ic_maps_local_restaurant, Color.parseColor("#8B6B62"));

// Add items
bottomNavigation.addItem(item1);
bottomNavigation.addItem(item2);
bottomNavigation.addItem(item3);

// Set background color
bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));

// Change colors
bottomNavigation.setAccentColor(Color.parseColor("#F63D2B"));
bottomNavigation.setInactiveColor(Color.parseColor("#747474"));

// Use colored navigation with circle reveal effect
bottomNavigation.setColored(true);

// Set listener
bottomNavigation.setAHBottomNavigationListener(new AHBottomNavigation.AHBottomNavigationListener() {
	@Override
	public void onTabSelected(int position) {
		// Do something cool here...
	}
});
```

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
