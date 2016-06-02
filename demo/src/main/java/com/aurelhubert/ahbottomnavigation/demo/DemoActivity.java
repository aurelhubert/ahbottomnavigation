package com.aurelhubert.ahbottomnavigation.demo;

import android.animation.Animator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;

import java.util.ArrayList;

public class DemoActivity extends AppCompatActivity {

	private DemoFragment currentFragment;
	private DemoViewPagerAdapter adapter;
	private ArrayList<AHBottomNavigationItem> bottomNavigationItems = new ArrayList<>();

	// UI
	private AHBottomNavigationViewPager viewPager;
	private AHBottomNavigation bottomNavigation;
	private FloatingActionButton floatingActionButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		initUI();
	}

	/**
	 * Init UI
	 */
	private void initUI() {

		bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
		viewPager = (AHBottomNavigationViewPager) findViewById(R.id.view_pager);
		floatingActionButton = (FloatingActionButton) findViewById(R.id.floating_action_button);

		AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.tab_1, R.drawable.ic_apps_black_24dp, R.color.color_tab_1);
		AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.tab_2, R.drawable.ic_maps_local_bar, R.color.color_tab_2);
		AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.tab_3, R.drawable.ic_maps_local_restaurant, R.color.color_tab_3);

		bottomNavigationItems.add(item1);
		bottomNavigationItems.add(item2);
		bottomNavigationItems.add(item3);

		bottomNavigation.addItems(bottomNavigationItems);
		bottomNavigation.setAccentColor(Color.parseColor("#F63D2B"));
		bottomNavigation.setInactiveColor(Color.parseColor("#747474"));

		bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
			@Override
			public boolean onTabSelected(int position, boolean wasSelected) {

				currentFragment = adapter.getCurrentFragment();
				if (wasSelected) {
					currentFragment.refresh();
					return true;
				}
				viewPager.setCurrentItem(position, false);
				currentFragment.willBeDisplayed();

				if (position == 1) {
					bottomNavigation.setNotification("", 1);

					floatingActionButton.setVisibility(View.VISIBLE);
					floatingActionButton.setAlpha(0f);
					floatingActionButton.setScaleX(0f);
					floatingActionButton.setScaleY(0f);
					floatingActionButton.animate()
							.alpha(1)
							.scaleX(1)
							.scaleY(1)
							.setDuration(300)
							.setInterpolator(new OvershootInterpolator())
							.setListener(new Animator.AnimatorListener() {
								@Override
								public void onAnimationStart(Animator animation) {

								}

								@Override
								public void onAnimationEnd(Animator animation) {
									floatingActionButton.animate()
											.setInterpolator(new LinearOutSlowInInterpolator())
											.start();
								}

								@Override
								public void onAnimationCancel(Animator animation) {

								}

								@Override
								public void onAnimationRepeat(Animator animation) {

								}
							})
							.start();

				} else {
					if (floatingActionButton.getVisibility() == View.VISIBLE) {
						floatingActionButton.animate()
								.alpha(0)
								.scaleX(0)
								.scaleY(0)
								.setDuration(300)
								.setInterpolator(new LinearOutSlowInInterpolator())
								.setListener(new Animator.AnimatorListener() {
									@Override
									public void onAnimationStart(Animator animation) {

									}

									@Override
									public void onAnimationEnd(Animator animation) {
										floatingActionButton.setVisibility(View.GONE);
									}

									@Override
									public void onAnimationCancel(Animator animation) {
										floatingActionButton.setVisibility(View.GONE);
									}

									@Override
									public void onAnimationRepeat(Animator animation) {

									}
								})
								.start();
					}
				}
				return true;
			}
		});

		viewPager.setOffscreenPageLimit(4);
		adapter = new DemoViewPagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(adapter);

		currentFragment = adapter.getCurrentFragment();

		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				bottomNavigation.setNotification("16", 1);
				Snackbar.make(bottomNavigation, "Snackbar with bottom navigation",
						Snackbar.LENGTH_SHORT).show();
			}
		}, 3000);
	}

	/**
	 * Update the bottom navigation colored param
	 */
	public void updateBottomNavigationColor(boolean isColored) {
		bottomNavigation.setColored(isColored);
	}

	/**
	 * Return if the bottom navigation is colored
	 */
	public boolean isBottomNavigationColored() {
		return bottomNavigation.isColored();
	}

	/**
	 * Add or remove items of the bottom navigation
	 */
	public void updateBottomNavigationItems(boolean addItems) {

		AHBottomNavigationItem item4 = new AHBottomNavigationItem(getString(R.string.tab_4),
				ContextCompat.getDrawable(this, R.drawable.ic_maps_local_bar),
				ContextCompat.getColor(this, R.color.color_tab_4));
		AHBottomNavigationItem item5 = new AHBottomNavigationItem(getString(R.string.tab_5),
				ContextCompat.getDrawable(this, R.drawable.ic_maps_place),
				ContextCompat.getColor(this, R.color.color_tab_5));

		if (addItems) {
			bottomNavigation.addItem(item4);
			bottomNavigation.addItem(item5);
			bottomNavigation.setNotification("1", 3);
		} else {
			bottomNavigation.removeAllItems();
			bottomNavigation.addItems(bottomNavigationItems);
		}
	}

	/**
	 * Show or hide the bottom navigation with animation
	 */
	public void showOrHideBottomNavigation(boolean show) {
		if (show) {
			bottomNavigation.restoreBottomNavigation(true);
		} else {
			bottomNavigation.hideBottomNavigation(true);
		}
	}

	/**
	 * Return the number of items in the bottom navigation
	 */
	public int getBottomNavigationNbItems() {
		return bottomNavigation.getItemsCount();
	}

}
