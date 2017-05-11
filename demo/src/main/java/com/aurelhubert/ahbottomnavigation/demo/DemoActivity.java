package com.aurelhubert.ahbottomnavigation.demo;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;

import java.util.ArrayList;

public class DemoActivity extends AppCompatActivity {

	private DemoFragment currentFragment;
	private DemoViewPagerAdapter adapter;
	private AHBottomNavigationAdapter navigationAdapter;
	private ArrayList<AHBottomNavigationItem> bottomNavigationItems = new ArrayList<>();
	private boolean useMenuResource = true;
	private int[] tabColors;
	private Handler handler = new Handler();

	// UI
	private AHBottomNavigationViewPager viewPager;
	private AHBottomNavigation bottomNavigation;
	private FloatingActionButton floatingActionButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean enabledTranslucentNavigation = getSharedPreferences("shared", Context.MODE_PRIVATE)
				.getBoolean("translucentNavigation", false);
		setTheme(enabledTranslucentNavigation ? R.style.AppTheme_TranslucentNavigation : R.style.AppTheme);
		setContentView(R.layout.activity_home);
		initUI();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeCallbacksAndMessages(null);
	}

	/**
	 * Init UI
	 */
	private void initUI() {

		bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
		viewPager = (AHBottomNavigationViewPager) findViewById(R.id.view_pager);
		floatingActionButton = (FloatingActionButton) findViewById(R.id.floating_action_button);

		if (useMenuResource) {
			tabColors = getApplicationContext().getResources().getIntArray(R.array.tab_colors);
			navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.bottom_navigation_menu_3);
			navigationAdapter.setupWithBottomNavigation(bottomNavigation, tabColors);
		} else {
			AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.tab_1, R.drawable.ic_apps_black_24dp, R.color.color_tab_1);
			AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.tab_2, R.drawable.ic_maps_local_bar, R.color.color_tab_2);
			AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.tab_3, R.drawable.ic_maps_local_restaurant, R.color.color_tab_3);

			bottomNavigationItems.add(item1);
			bottomNavigationItems.add(item2);
			bottomNavigationItems.add(item3);

			bottomNavigation.addItems(bottomNavigationItems);
		}

		bottomNavigation.manageFloatingActionButtonBehavior(floatingActionButton);
		bottomNavigation.setTranslucentNavigationEnabled(true);

		bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
			@Override
			public boolean onTabSelected(int position, boolean wasSelected) {

				if (currentFragment == null) {
					currentFragment = adapter.getCurrentFragment();
				}

				if (wasSelected) {
					currentFragment.refresh();
					return true;
				}

				if (currentFragment != null) {
					currentFragment.willBeHidden();
				}

				viewPager.setCurrentItem(position, false);
				currentFragment = adapter.getCurrentFragment();
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

		/*
		bottomNavigation.setOnNavigationPositionListener(new AHBottomNavigation.OnNavigationPositionListener() {
			@Override public void onPositionChange(int y) {
				Log.d("DemoActivity", "BottomNavigation Position: " + y);
			}
		});
		*/

		viewPager.setOffscreenPageLimit(4);
		adapter = new DemoViewPagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(adapter);

		currentFragment = adapter.getCurrentFragment();

		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// Setting custom colors for notification
				AHNotification notification = new AHNotification.Builder()
						.setText(":)")
						.setBackgroundColor(ContextCompat.getColor(DemoActivity.this, R.color.color_notification_back))
						.setTextColor(ContextCompat.getColor(DemoActivity.this, R.color.color_notification_text))
						.build();
				bottomNavigation.setNotification(notification, 1);
				Snackbar.make(bottomNavigation, "Snackbar with bottom navigation",
						Snackbar.LENGTH_SHORT).show();

			}
		}, 3000);

		//bottomNavigation.setDefaultBackgroundResource(R.drawable.bottom_navigation_background);
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

		if (useMenuResource) {
			if (addItems) {
				navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.bottom_navigation_menu_5);
				navigationAdapter.setupWithBottomNavigation(bottomNavigation, tabColors);
				bottomNavigation.setNotification("1", 3);
			} else {
				navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.bottom_navigation_menu_3);
				navigationAdapter.setupWithBottomNavigation(bottomNavigation, tabColors);
			}

		} else {
			if (addItems) {
				AHBottomNavigationItem item4 = new AHBottomNavigationItem(getString(R.string.tab_4),
						ContextCompat.getDrawable(this, R.drawable.ic_maps_local_bar),
						ContextCompat.getColor(this, R.color.color_tab_4));
				AHBottomNavigationItem item5 = new AHBottomNavigationItem(getString(R.string.tab_5),
						ContextCompat.getDrawable(this, R.drawable.ic_maps_place),
						ContextCompat.getColor(this, R.color.color_tab_5));

				bottomNavigation.addItem(item4);
				bottomNavigation.addItem(item5);
				bottomNavigation.setNotification("1", 3);
			} else {
				bottomNavigation.removeAllItems();
				bottomNavigation.addItems(bottomNavigationItems);
			}
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
	 * Show or hide selected item background
	 */
	public void updateSelectedBackgroundVisibility(boolean isVisible) {
		bottomNavigation.setSelectedBackgroundVisible(isVisible);
	}

	/**
	 * Show or hide selected item background
	 */
	public void setForceTitleHide(boolean forceTitleHide) {
		AHBottomNavigation.TitleState state = forceTitleHide ? AHBottomNavigation.TitleState.ALWAYS_HIDE : AHBottomNavigation.TitleState.ALWAYS_SHOW;
		bottomNavigation.setTitleState(state);
	}

	/**
	 * Show BorderLine
	 * default setBottomLine()
	 */
	public void setNavigationBorderLine(@ColorInt int iColor, int iRadius, Rect rShapePading) {
		bottomNavigation.setNavigationBorderLine(iColor,iRadius,rShapePading);
	}

	/**
	 * Reload activity
	 */
	public void reload() {
		startActivity(new Intent(this, DemoActivity.class));
		finish();
	}

	/**
	 * Return the number of items in the bottom navigation
	 */
	public int getBottomNavigationNbItems() {
		return bottomNavigation.getItemsCount();
	}

}
