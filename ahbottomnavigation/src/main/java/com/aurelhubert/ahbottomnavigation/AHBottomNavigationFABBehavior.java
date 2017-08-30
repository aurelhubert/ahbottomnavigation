package com.aurelhubert.ahbottomnavigation;

import android.animation.Animator;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

/**
 *
 */
public class AHBottomNavigationFABBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {

    private int navigationBarHeight = 0;
    private long lastSnackbarUpdate = 0;
    private Interpolator mInterpolator = new FastOutSlowInInterpolator();
    private ViewPropertyAnimatorCompat mAnimator = null;

    public AHBottomNavigationFABBehavior(int navigationBarHeight) {
        this.navigationBarHeight = navigationBarHeight;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        if (dependency != null && dependency instanceof Snackbar.SnackbarLayout) {
            return true;
        } else if (dependency != null && dependency instanceof AHBottomNavigation) {
            return true;
        }
        return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        View depView = dependency;
        for (View view : parent.getDependencies(child)) {
            if (view instanceof Snackbar.SnackbarLayout) {
                depView = view;
                break;
            }
        }
        ensureOrCancelAnimator(child);
        updateFloatingActionButton(child, depView);
        return super.onDependentViewChanged(parent, child, dependency);
    }

    @Override
    public void onDependentViewRemoved(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        lastSnackbarUpdate = System.currentTimeMillis();
        ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
        int fabDefaultBottomMargin = p.bottomMargin;
        float targetY = dependency.getY() - fabDefaultBottomMargin;
        ensureOrCancelAnimator(child);
        mAnimator.y(targetY)
                .setDuration(255)
                .setInterpolator(mInterpolator);


        super.onDependentViewRemoved(parent, child, dependency);
    }

    private void ensureOrCancelAnimator(FloatingActionButton child) {
        if (mAnimator == null) {
            mAnimator = ViewCompat.animate(child);
            mAnimator.setDuration(255)
                    .setInterpolator(mInterpolator);
        } else {
            mAnimator.cancel();
        }
    }

    /**
     * Update floating action button bottom margin
     */
    private void updateFloatingActionButton(FloatingActionButton child, View dependency) {
        if (child != null && dependency != null && dependency instanceof Snackbar.SnackbarLayout) {
            Log.d("Behavior", "dependency snackbar: " + dependency.getY());
            lastSnackbarUpdate = System.currentTimeMillis();
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
            int fabDefaultBottomMargin = p.bottomMargin;
            child.setY(dependency.getY() - fabDefaultBottomMargin);
        } else if (child != null && dependency != null && dependency instanceof AHBottomNavigation) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
            int fabDefaultBottomMargin = p.bottomMargin;
            child.setY(dependency.getY() - fabDefaultBottomMargin);
        }
    }

}