package com.aurelhubert.ahbottomnavigation;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;

/**
 *
 */
public class AHBottomNavigationFABBehavior<T extends View> extends CoordinatorLayout.Behavior<T> {

	private int navigationBarHeight = 0;
	private long lastSnackbarUpdate = 0;
	private float factor = 1.0f;

	public AHBottomNavigationFABBehavior(int navigationBarHeight, float factor) {
		this.navigationBarHeight = navigationBarHeight;
		this.factor = (factor == 0.0f) ? 1.0f : factor;
	}

	@Override
	public boolean layoutDependsOn(CoordinatorLayout parent, T child, View dependency) {
		if (dependency != null && dependency instanceof Snackbar.SnackbarLayout) {
			return true;
		} else if (dependency != null && dependency instanceof AHBottomNavigation) {
			return true;
		}
		return super.layoutDependsOn(parent, child, dependency);
	}

	@Override
	public boolean onDependentViewChanged(CoordinatorLayout parent, T child, View dependency) {
		updateFloatingActionButton(child, dependency);
		return super.onDependentViewChanged(parent, child, dependency);
	}

	/**
	 * Update floating action button bottom margin
	 */
	private void updateFloatingActionButton(T child, View dependency) {
		if(child == null || dependency == null) {
			return;
		}
		if (dependency instanceof Snackbar.SnackbarLayout) {
			lastSnackbarUpdate = System.currentTimeMillis();
		} else if (dependency instanceof AHBottomNavigation) {
			// Hack to avoid moving the FAB when the AHBottomNavigation is moving (showing or hiding animation)
			if (System.currentTimeMillis() - lastSnackbarUpdate < 30) {
				return;
			}
		}

		ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
		int fabDefaultBottomMargin = p.bottomMargin;
		child.setY((dependency.getY()/factor) - fabDefaultBottomMargin);
	}

}