package com.aurelhubert.ahbottomnavigation;

import android.animation.Animator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigationBehavior.OnYTranslationListener;

import java.util.ArrayList;
import java.util.List;

/**
 * AHBottomNavigationLayout
 * Material Design guidelines : https://www.google.com/design/spec/components/bottom-navigation.html
 */
public class AHBottomNavigation extends FrameLayout implements
		OnYTranslationListener {

	// Constant
	public static final int CURRENT_ITEM_NONE = -1;
	public static final int UPDATE_ALL_NOTIFICATIONS = -1;

	// Static
	private static String TAG = "AHBottomNavigation";
	private static final int MIN_ITEMS = 3;
	private static final int MAX_ITEMS = 5;

	// Listener
	private OnTabSelectedListener tabSelectedListener;
	private AHBottomNavigationBehavior.OnYTranslationListener yTranslationListener;

	// Variables
	private Context context;
	private Resources resources;
	private ArrayList<AHBottomNavigationItem> items = new ArrayList<>();
	private ArrayList<View> views = new ArrayList<>();
	private AHBottomNavigationBehavior<AHBottomNavigation> bottomNavigationBehavior;
	private View backgroundColorView;
	private Animator circleRevealAnim;
	private boolean colored = false;
	private String[] notifications = {"", "", "", "", ""};
	private boolean isBehaviorTranslationSet = false;
	private int currentItem = 0;
	private int currentColor = 0;
	private boolean behaviorTranslationEnabled = true;
	private boolean needHideBottomNavigation = false;
	private boolean hideBottomNavigationWithAnimation = false;

	// Variables (Styles)
	private Typeface titleTypeface;
	private int defaultBackgroundColor = Color.WHITE;
	//private int accentColor = Color.WHITE;
	//private int inactiveColor = Color.WHITE;
	private @ColorInt int itemActiveColor, itemInactiveColor;
	private @ColorInt int titleColorActive, titleColorInactive;
	private @ColorInt int coloredTitleColorActive, coloredTitleColorInactive;
	private float titleActiveTextSize, titleInactiveTextSize;
	private int bottomNavigationHeight;
	private float selectedItemWidth, notSelectedItemWidth;
	private boolean forceTint = false;
	private boolean forceTitlesDisplay = false;

	// Notifications
	private @ColorInt int notificationTextColor;
	private @ColorInt int notificationBackgroundColor;
	private Drawable notificationBackgroundDrawable;
	private Typeface notificationTypeface;
	private int notificationActiveMarginLeft, notificationInactiveMarginLeft;

	/**
	 * Constructors
	 */
	public AHBottomNavigation(Context context) {
		super(context);
		init(context);
	}

	public AHBottomNavigation(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public AHBottomNavigation(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		createItems();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (!isBehaviorTranslationSet) {
			//The translation behavior has to be set up after the super.onMeasure has been called.
			setBehaviorTranslationEnabled(behaviorTranslationEnabled);
			isBehaviorTranslationSet = true;
		}
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		bundle.putParcelable("superState", super.onSaveInstanceState());
		bundle.putInt("current_item", currentItem);
		bundle.putStringArray("notifications", notifications);
		return bundle;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;
			currentItem = bundle.getInt("current_item");
			notifications = bundle.getStringArray("notifications");
			state = bundle.getParcelable("superState");
		}
		super.onRestoreInstanceState(state);
	}

	/////////////
	// PRIVATE //
	/////////////

	/**
	 * Init
	 *
	 * @param context
	 */
	private void init(Context context) {
		this.context = context;
		resources = this.context.getResources();

		notificationTextColor = ContextCompat.getColor(context, android.R.color.white);
		bottomNavigationHeight = (int) resources.getDimension(R.dimen.bottom_navigation_height);

		// Item colors
		titleColorActive = ContextCompat.getColor(context, R.color.colorBottomNavigationAccent);
		titleColorInactive = ContextCompat.getColor(context, R.color.colorBottomNavigationInactive);
		// Colors for colored bottom navigation
		coloredTitleColorActive = ContextCompat.getColor(context, R.color.colorBottomNavigationActiveColored);
		coloredTitleColorInactive = ContextCompat.getColor(context, R.color.colorBottomNavigationInactiveColored);

		itemActiveColor = titleColorActive;
		itemInactiveColor = titleColorInactive;

		// Notifications
		notificationActiveMarginLeft = (int) resources.getDimension(R.dimen.bottom_navigation_notification_margin_left_active);
		notificationInactiveMarginLeft = (int) resources.getDimension(R.dimen.bottom_navigation_notification_margin_left);

		ViewCompat.setElevation(this, resources.getDimension(R.dimen.bottom_navigation_elevation));
		setClipToPadding(false);

		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, bottomNavigationHeight);
		setLayoutParams(params);
	}

	/**
	 * Create the items in the bottom navigation
	 */
	private void createItems() {
		if (items.size() < MIN_ITEMS) {
			Log.w(TAG, "The items list should have at least 3 items");
		} else if (items.size() > MAX_ITEMS) {
			Log.w(TAG, "The items list should not have more than 5 items");
		}

		int layoutHeight = (int) resources.getDimension(R.dimen.bottom_navigation_height);

		removeAllViews();
		views.clear();
		backgroundColorView = new View(context);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			LayoutParams backgroundLayoutParams = new LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT, layoutHeight);
			addView(backgroundColorView, backgroundLayoutParams);
		}

		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		linearLayout.setGravity(Gravity.CENTER);

		LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, layoutHeight);
		addView(linearLayout, layoutParams);

		if (items.size() == MIN_ITEMS || forceTitlesDisplay) {
			createClassicItems(linearLayout);
		} else {
			createSmallItems(linearLayout);
		}

		// Force a request layout after all the items have been created
		post(new Runnable() {
			@Override
			public void run() {
				requestLayout();
			}
		});
	}

	/**
	 * Create classic items (only 3 items in the bottom navigation)
	 *
	 * @param linearLayout The layout where the items are added
	 */
	private void createClassicItems(LinearLayout linearLayout) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		float height = resources.getDimension(R.dimen.bottom_navigation_height);
		float minWidth = resources.getDimension(R.dimen.bottom_navigation_min_width);
		float maxWidth = resources.getDimension(R.dimen.bottom_navigation_max_width);

		if (forceTitlesDisplay && items.size() > MIN_ITEMS) {
			minWidth = resources.getDimension(R.dimen.bottom_navigation_small_inactive_min_width);
			maxWidth = resources.getDimension(R.dimen.bottom_navigation_small_inactive_max_width);
		}

		int layoutWidth = getWidth();
		if (layoutWidth == 0 || items.size() == 0) {
			return;
		}

		float itemWidth = layoutWidth / items.size();
		if (itemWidth < minWidth) {
			itemWidth = minWidth;
		} else if (itemWidth > maxWidth) {
			itemWidth = maxWidth;
		}

		float activeSize = resources.getDimension(R.dimen.bottom_navigation_text_size_active);
		float inactiveSize = resources.getDimension(R.dimen.bottom_navigation_text_size_inactive);
		int activePaddingTop = (int) resources.getDimension(R.dimen.bottom_navigation_margin_top_active);

		if (titleActiveTextSize != 0 && titleInactiveTextSize != 0) {
			activeSize = titleActiveTextSize;
			inactiveSize = titleInactiveTextSize;
		} else if (forceTitlesDisplay && items.size() > MIN_ITEMS) {
			activeSize = resources.getDimension(R.dimen.bottom_navigation_text_size_forced_active);
			inactiveSize = resources.getDimension(R.dimen.bottom_navigation_text_size_forced_inactive);
		}

		for (int i = 0; i < items.size(); i++) {

			final int itemIndex = i;
			AHBottomNavigationItem item = items.get(itemIndex);

			View view = inflater.inflate(R.layout.bottom_navigation_item, this, false);
			FrameLayout container = (FrameLayout) view.findViewById(R.id.bottom_navigation_container);
			ImageView icon = (ImageView) view.findViewById(R.id.bottom_navigation_item_icon);
			TextView title = (TextView) view.findViewById(R.id.bottom_navigation_item_title);
			TextView notification = (TextView) view.findViewById(R.id.bottom_navigation_notification);

			icon.setImageDrawable(item.getDrawable(context));
			title.setText(item.getTitle(context));

			if (titleTypeface != null) {
				title.setTypeface(titleTypeface);
			}

			if (forceTitlesDisplay && items.size() > MIN_ITEMS) {
				container.setPadding(0, container.getPaddingTop(), 0, container.getPaddingBottom());
			}

			if (i == currentItem) {
				// Update margins (icon & notification)
				if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
					ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) icon.getLayoutParams();
					p.setMargins(p.leftMargin, activePaddingTop, p.rightMargin, p.bottomMargin);

					ViewGroup.MarginLayoutParams paramsNotification = (ViewGroup.MarginLayoutParams)
							notification.getLayoutParams();
					paramsNotification.setMargins(notificationActiveMarginLeft, paramsNotification.topMargin,
							paramsNotification.rightMargin, paramsNotification.bottomMargin);

					view.requestLayout();
				}
			} else {
				ViewGroup.MarginLayoutParams paramsNotification = (ViewGroup.MarginLayoutParams)
						notification.getLayoutParams();
				paramsNotification.setMargins(notificationInactiveMarginLeft, paramsNotification.topMargin,
						paramsNotification.rightMargin, paramsNotification.bottomMargin);
			}

			if (colored) {
				if (i == currentItem) {
					setBackgroundColor(item.getColor(context));
					currentColor = item.getColor(context);
				}
			} else {
				setBackgroundColor(defaultBackgroundColor);
			}

			icon.setImageDrawable(AHHelper.getTintDrawable(items.get(i).getDrawable(context),
					currentItem == i ? itemActiveColor : itemInactiveColor, forceTint));
			title.setTextColor(currentItem == i ? itemActiveColor :itemInactiveColor);
			title.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentItem == i ? activeSize : inactiveSize);
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					updateItems(itemIndex, true);
				}
			});

			LayoutParams params = new LayoutParams((int) itemWidth, (int) height);
			linearLayout.addView(view, params);
			views.add(view);
		}

		updateNotifications(true, UPDATE_ALL_NOTIFICATIONS);
	}

	/**
	 * Create small items (more than 3 items in the bottom navigation)
	 *
	 * @param linearLayout The layout where the items are added
	 */
	private void createSmallItems(LinearLayout linearLayout) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		float height = resources.getDimension(R.dimen.bottom_navigation_height);
		float minWidth = resources.getDimension(R.dimen.bottom_navigation_small_inactive_min_width);
		float maxWidth = resources.getDimension(R.dimen.bottom_navigation_small_inactive_max_width);

		int layoutWidth = getWidth();
		if (layoutWidth == 0 || items.size() == 0) {
			return;
		}

		float itemWidth = layoutWidth / items.size();

		if (itemWidth < minWidth) {
			itemWidth = minWidth;
		} else if (itemWidth > maxWidth) {
			itemWidth = maxWidth;
		}

		int activeMarginTop = (int) resources.getDimension(R.dimen.bottom_navigation_small_margin_top_active);
		float difference = resources.getDimension(R.dimen.bottom_navigation_small_selected_width_difference);

		selectedItemWidth = itemWidth + items.size() * difference;
		itemWidth -= difference;
		notSelectedItemWidth = itemWidth;


		for (int i = 0; i < items.size(); i++) {

			final int itemIndex = i;
			AHBottomNavigationItem item = items.get(itemIndex);

			View view = inflater.inflate(R.layout.bottom_navigation_small_item, this, false);
			ImageView icon = (ImageView) view.findViewById(R.id.bottom_navigation_small_item_icon);
			TextView title = (TextView) view.findViewById(R.id.bottom_navigation_small_item_title);
			TextView notification = (TextView) view.findViewById(R.id.bottom_navigation_notification);
			icon.setImageDrawable(item.getDrawable(context));
			title.setText(item.getTitle(context));

			if (titleActiveTextSize != 0) {
				title.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleActiveTextSize);
			}

			if (titleTypeface != null) {
				title.setTypeface(titleTypeface);
			}

			if (i == currentItem) {
				// Update margins (icon & notification)
				if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
					ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) icon.getLayoutParams();
					p.setMargins(p.leftMargin, activeMarginTop, p.rightMargin, p.bottomMargin);

					ViewGroup.MarginLayoutParams paramsNotification = (ViewGroup.MarginLayoutParams)
							notification.getLayoutParams();
					paramsNotification.setMargins(notificationActiveMarginLeft, paramsNotification.topMargin,
							paramsNotification.rightMargin, paramsNotification.bottomMargin);

					view.requestLayout();
				}
			} else {
				ViewGroup.MarginLayoutParams paramsNotification = (ViewGroup.MarginLayoutParams)
						notification.getLayoutParams();
				paramsNotification.setMargins(notificationInactiveMarginLeft, paramsNotification.topMargin,
						paramsNotification.rightMargin, paramsNotification.bottomMargin);
			}

			if (colored) {
				if (i == currentItem) {
					setBackgroundColor(item.getColor(context));
					currentColor = item.getColor(context);
				}
			} else {
				setBackgroundColor(defaultBackgroundColor);
			}

			icon.setImageDrawable(AHHelper.getTintDrawable(items.get(i).getDrawable(context),
					currentItem == i ? itemActiveColor : itemInactiveColor, forceTint));
			title.setTextColor(currentItem == i ? itemActiveColor : itemInactiveColor);
			title.setAlpha(currentItem == i ? 1 : 0);
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					updateSmallItems(itemIndex, true);
				}
			});

			LayoutParams params = new LayoutParams(i == currentItem ? (int) selectedItemWidth :
					(int) itemWidth, (int) height);
			linearLayout.addView(view, params);
			views.add(view);
		}

		updateNotifications(true, UPDATE_ALL_NOTIFICATIONS);
	}


	/**
	 * Update Items UI
	 *
	 * @param itemIndex   int: Selected item position
	 * @param useCallback boolean: Use or not the callback
	 */
	private void updateItems(final int itemIndex, boolean useCallback) {

		if (currentItem == itemIndex) {
			if (tabSelectedListener != null && useCallback) {
				tabSelectedListener.onTabSelected(itemIndex, true);
			}
			return;
		}

		if (tabSelectedListener != null) {
			boolean selectionAllowed = tabSelectedListener.onTabSelected(itemIndex, false);
			if (!selectionAllowed) return;
		}


		int activeMarginTop = (int) resources.getDimension(R.dimen.bottom_navigation_margin_top_active);
		int inactiveMarginTop = (int) resources.getDimension(R.dimen.bottom_navigation_margin_top_inactive);
		float activeSize = resources.getDimension(R.dimen.bottom_navigation_text_size_active);
		float inactiveSize = resources.getDimension(R.dimen.bottom_navigation_text_size_inactive);

		if (titleActiveTextSize != 0 && titleInactiveTextSize != 0) {
			activeSize = titleActiveTextSize;
			inactiveSize = titleInactiveTextSize;
		} else if (forceTitlesDisplay && items.size() > MIN_ITEMS) {
			activeSize = resources.getDimension(R.dimen.bottom_navigation_text_size_forced_active);
			inactiveSize = resources.getDimension(R.dimen.bottom_navigation_text_size_forced_inactive);
		}

		for (int i = 0; i < views.size(); i++) {

			if (i == itemIndex) {

				final TextView title = (TextView) views.get(itemIndex).findViewById(R.id.bottom_navigation_item_title);
				final ImageView icon = (ImageView) views.get(itemIndex).findViewById(R.id.bottom_navigation_item_icon);
				final TextView notification = (TextView) views.get(itemIndex).findViewById(R.id.bottom_navigation_notification);

				AHHelper.updateTopMargin(icon, inactiveMarginTop, activeMarginTop);
				AHHelper.updateLeftMargin(notification, notificationInactiveMarginLeft, notificationActiveMarginLeft);
				AHHelper.updateTextColor(title, itemInactiveColor, itemActiveColor);
				AHHelper.updateTextSize(title, inactiveSize, activeSize);
				AHHelper.updateDrawableColor(context, items.get(itemIndex).getDrawable(context), icon,
						itemInactiveColor, itemActiveColor, forceTint);

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && colored) {

					int finalRadius = Math.max(getWidth(), getHeight());
					int cx = (int) views.get(itemIndex).getX() + views.get(itemIndex).getWidth() / 2;
					int cy = views.get(itemIndex).getHeight() / 2;

					if (circleRevealAnim != null && circleRevealAnim.isRunning()) {
						circleRevealAnim.cancel();
						setBackgroundColor(items.get(itemIndex).getColor(context));
						backgroundColorView.setBackgroundColor(Color.TRANSPARENT);
					}

					circleRevealAnim = ViewAnimationUtils.createCircularReveal(backgroundColorView, cx, cy, 0, finalRadius);
					circleRevealAnim.setStartDelay(5);
					circleRevealAnim.addListener(new Animator.AnimatorListener() {
						@Override
						public void onAnimationStart(Animator animation) {
							backgroundColorView.setBackgroundColor(items.get(itemIndex).getColor(context));
						}

						@Override
						public void onAnimationEnd(Animator animation) {
							setBackgroundColor(items.get(itemIndex).getColor(context));
							backgroundColorView.setBackgroundColor(Color.TRANSPARENT);
						}

						@Override
						public void onAnimationCancel(Animator animation) {
						}

						@Override
						public void onAnimationRepeat(Animator animation) {
						}
					});
					circleRevealAnim.start();
				} else if (colored) {
					AHHelper.updateViewBackgroundColor(this, currentColor,
							items.get(itemIndex).getColor(context));
				} else {
					setBackgroundColor(defaultBackgroundColor);
					backgroundColorView.setBackgroundColor(Color.TRANSPARENT);
				}

			} else if (i == currentItem) {

				final TextView title = (TextView) views.get(currentItem).findViewById(R.id.bottom_navigation_item_title);
				final ImageView icon = (ImageView) views.get(currentItem).findViewById(R.id.bottom_navigation_item_icon);
				final TextView notification = (TextView) views.get(currentItem).findViewById(R.id.bottom_navigation_notification);

				AHHelper.updateTopMargin(icon, activeMarginTop, inactiveMarginTop);
				AHHelper.updateLeftMargin(notification, notificationActiveMarginLeft, notificationInactiveMarginLeft);
				AHHelper.updateTextColor(title, itemActiveColor, itemInactiveColor);
				AHHelper.updateTextSize(title, activeSize, inactiveSize);
				AHHelper.updateDrawableColor(context, items.get(currentItem).getDrawable(context), icon,
						itemActiveColor, itemInactiveColor, forceTint);
			}
		}

		currentItem = itemIndex;
		if (currentItem > 0 && currentItem < items.size()) {
			currentColor = items.get(currentItem).getColor(context);
		} else if (currentItem == CURRENT_ITEM_NONE) {
			setBackgroundColor(defaultBackgroundColor);
			backgroundColorView.setBackgroundColor(Color.TRANSPARENT);
		}

		if (tabSelectedListener != null && useCallback) {
			tabSelectedListener.onTabSelected(itemIndex, false);
		}
	}

	/**
	 * Update Small items UI
	 *
	 * @param itemIndex   int: Selected item position
	 * @param useCallback boolean: Use or not the callback
	 */
	private void updateSmallItems(final int itemIndex, boolean useCallback) {

		if (currentItem == itemIndex) {
			if (tabSelectedListener != null && useCallback) {
				tabSelectedListener.onTabSelected(itemIndex, true);
			}
			return;
		}

		if (tabSelectedListener != null) {
			boolean selectionAllowed = tabSelectedListener.onTabSelected(itemIndex, false);
			if (!selectionAllowed) return;
		}

		int activeMarginTop = (int) resources.getDimension(R.dimen.bottom_navigation_small_margin_top_active);
		int inactiveMargin = (int) resources.getDimension(R.dimen.bottom_navigation_small_margin_top);

		for (int i = 0; i < views.size(); i++) {

			if (i == itemIndex) {

				final FrameLayout container = (FrameLayout) views.get(itemIndex).findViewById(R.id.bottom_navigation_small_container);
				final TextView title = (TextView) views.get(itemIndex).findViewById(R.id.bottom_navigation_small_item_title);
				final ImageView icon = (ImageView) views.get(itemIndex).findViewById(R.id.bottom_navigation_small_item_icon);
				final TextView notification = (TextView) views.get(itemIndex).findViewById(R.id.bottom_navigation_notification);

				AHHelper.updateTopMargin(icon, inactiveMargin, activeMarginTop);
				AHHelper.updateLeftMargin(notification, notificationInactiveMarginLeft, notificationActiveMarginLeft);
				AHHelper.updateTextColor(title, itemInactiveColor, itemActiveColor);
				AHHelper.updateAlpha(title, 0, 1);
				AHHelper.updateWidth(container, notSelectedItemWidth, selectedItemWidth);
				AHHelper.updateDrawableColor(context, items.get(itemIndex).getDrawable(context), icon,
						itemInactiveColor, itemActiveColor, forceTint);

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && colored) {
					int finalRadius = Math.max(getWidth(), getHeight());
					int cx = (int) views.get(itemIndex).getX() + views.get(itemIndex).getWidth() / 2;
					int cy = views.get(itemIndex).getHeight() / 2;

					if (circleRevealAnim != null && circleRevealAnim.isRunning()) {
						circleRevealAnim.cancel();
						setBackgroundColor(items.get(itemIndex).getColor(context));
						backgroundColorView.setBackgroundColor(Color.TRANSPARENT);
					}

					circleRevealAnim = ViewAnimationUtils.createCircularReveal(backgroundColorView, cx, cy, 0, finalRadius);
					circleRevealAnim.setStartDelay(5);
					circleRevealAnim.addListener(new Animator.AnimatorListener() {
						@Override
						public void onAnimationStart(Animator animation) {
							backgroundColorView.setBackgroundColor(items.get(itemIndex).getColor(context));
						}

						@Override
						public void onAnimationEnd(Animator animation) {
							setBackgroundColor(items.get(itemIndex).getColor(context));
							backgroundColorView.setBackgroundColor(Color.TRANSPARENT);
						}

						@Override
						public void onAnimationCancel(Animator animation) {
						}

						@Override
						public void onAnimationRepeat(Animator animation) {
						}
					});
					circleRevealAnim.start();
				} else if (colored) {
					AHHelper.updateViewBackgroundColor(this, currentColor,
							items.get(itemIndex).getColor(context));
				} else {
					setBackgroundColor(defaultBackgroundColor);
					backgroundColorView.setBackgroundColor(Color.TRANSPARENT);
				}

			} else if (i == currentItem) {

				final View container = views.get(currentItem).findViewById(R.id.bottom_navigation_small_container);
				final TextView title = (TextView) views.get(currentItem).findViewById(R.id.bottom_navigation_small_item_title);
				final ImageView icon = (ImageView) views.get(currentItem).findViewById(R.id.bottom_navigation_small_item_icon);
				final TextView notification = (TextView) views.get(currentItem).findViewById(R.id.bottom_navigation_notification);

				AHHelper.updateTopMargin(icon, activeMarginTop, inactiveMargin);
				AHHelper.updateLeftMargin(notification, notificationActiveMarginLeft, notificationInactiveMarginLeft);
				AHHelper.updateTextColor(title, itemActiveColor, itemInactiveColor);
				AHHelper.updateAlpha(title, 1, 0);
				AHHelper.updateWidth(container, selectedItemWidth, notSelectedItemWidth);
				AHHelper.updateDrawableColor(context, items.get(currentItem).getDrawable(context), icon,
						itemActiveColor, itemInactiveColor, forceTint);

			}
		}

		currentItem = itemIndex;
		if (currentItem > 0 && currentItem < items.size()) {
			currentColor = items.get(currentItem).getColor(context);
		} else if (currentItem == CURRENT_ITEM_NONE) {
			setBackgroundColor(defaultBackgroundColor);
			backgroundColorView.setBackgroundColor(Color.TRANSPARENT);
		}

		if (tabSelectedListener != null && useCallback) {
			tabSelectedListener.onTabSelected(itemIndex, false);
		}
	}

	/**
	 * Update notifications
	 */
	private void updateNotifications(boolean updateStyle, int itemPosition) {

		for (int i = 0; i < views.size(); i++) {

			if (itemPosition != UPDATE_ALL_NOTIFICATIONS && itemPosition != i) {
				continue;
			}

			TextView notification = (TextView) views.get(i).findViewById(R.id.bottom_navigation_notification);

			String currentValue = notification.getText().toString();
			boolean animate = !currentValue.equals(String.valueOf(notifications[i]));

			if (updateStyle) {
				notification.setTextColor(notificationTextColor);
				if (notificationTypeface != null) {
					notification.setTypeface(notificationTypeface);
				} else {
					notification.setTypeface(null, Typeface.BOLD);
				}

				if (notificationBackgroundDrawable != null) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
						Drawable drawable = notificationBackgroundDrawable.getConstantState().newDrawable();
						notification.setBackground(drawable);
					} else {
						notification.setBackgroundDrawable(notificationBackgroundDrawable);
					}

				} else if (notificationBackgroundColor != 0) {
					Drawable defautlDrawable = ContextCompat.getDrawable(context, R.drawable.notification_background);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
						notification.setBackground(AHHelper.getTintDrawable(defautlDrawable,
								notificationBackgroundColor, forceTint));
					} else {
						notification.setBackgroundDrawable(AHHelper.getTintDrawable(defautlDrawable,
								notificationBackgroundColor, forceTint));
					}
				}
			}

			if (notifications[i].length() == 0 && notification.getText().length() > 0) {
				notification.setText("");
				if (animate) {
					notification.animate()
							.scaleX(0)
							.scaleY(0)
							.alpha(0)
							.setInterpolator(new AccelerateInterpolator())
							.setDuration(150)
							.start();
				}
			} else if (notifications[i].length() > 0) {
				notification.setText(String.valueOf(notifications[i]));
				if (animate) {
					notification.setScaleX(0);
					notification.setScaleY(0);
					notification.animate()
							.scaleX(1)
							.scaleY(1)
							.alpha(1)
							.setInterpolator(new OvershootInterpolator())
							.setDuration(150)
							.start();
				}
			}
		}
	}


	////////////
	// PUBLIC //
	////////////

	/**
	 * Add an item
	 */
	public void addItem(AHBottomNavigationItem item) {
		if (this.items.size() > MAX_ITEMS) {
			Log.w(TAG, "The items list should not have more than 5 items");
		}
		items.add(item);
		createItems();
	}

	/**
	 * Add all items
	 */
	public void addItems(List<AHBottomNavigationItem> items) {
		if (items.size() > MAX_ITEMS || (this.items.size() + items.size()) > MAX_ITEMS) {
			Log.w(TAG, "The items list should not have more than 5 items");
		}
		this.items.addAll(items);
		createItems();
	}

	/**
	 * Remove an item at the given index
	 */
	public void removeItemAtIndex(int index) {
		if (index < items.size()) {
			this.items.remove(index);
			createItems();
		}
	}

	/**
	 * Remove all items
	 */
	public void removeAllItems() {
		this.items.clear();
		createItems();
	}

	/**
	 * Refresh the AHBottomView
	 */
	public void refresh() {
		createItems();
	}

	/**
	 * Return the number of items
	 *
	 * @return int
	 */
	public int getItemsCount() {
		return items.size();
	}

	/**
	 * Return if the Bottom Navigation is colored
	 */
	public boolean isColored() {
		return colored;
	}

	/**
	 * Set if the Bottom Navigation is colored
	 */
	public void setColored(boolean colored) {
		this.colored = colored;
		this.itemActiveColor = colored ? coloredTitleColorActive : titleColorActive;
		this.itemInactiveColor = colored ? coloredTitleColorInactive : titleColorInactive;
		createItems();
	}

	/**
	 * Return the bottom navigation background color
	 *
	 * @return The bottom navigation background color
	 */
	public int getDefaultBackgroundColor() {
		return defaultBackgroundColor;
	}

	/**
	 * Set the bottom navigation background color
	 *
	 * @param defaultBackgroundColor The bottom navigation background color
	 */
	public void setDefaultBackgroundColor(@ColorInt int defaultBackgroundColor) {
		this.defaultBackgroundColor = defaultBackgroundColor;
		createItems();
	}

	/**
	 * Get the accent color (used when the view contains 3 items)
	 *
	 * @return The default accent color
	 */
	public int getAccentColor() {
		return itemActiveColor;
	}

	/**
	 * Set the accent color (used when the view contains 3 items)
	 *
	 * @param accentColor The new accent color
	 */
	public void setAccentColor(int accentColor) {
		this.titleColorActive = accentColor;
		this.itemActiveColor = accentColor;
		createItems();
	}

	/**
	 * Get the inactive color (used when the view contains 3 items)
	 *
	 * @return The inactive color
	 */
	public int getInactiveColor() {
		return itemInactiveColor;
	}

	/**
	 * Set the inactive color (used when the view contains 3 items)
	 *
	 * @param inactiveColor The inactive color
	 */
	public void setInactiveColor(int inactiveColor) {
		this.titleColorInactive = inactiveColor;
		this.itemInactiveColor = inactiveColor;
		createItems();
	}

	/**
	 * Set notification typeface
	 *
	 * @param typeface Typeface
	 */
	public void setTitleTypeface(Typeface typeface) {
		this.titleTypeface = typeface;
		createItems();
	}

	/**
	 * Set title text size
	 *
	 * @param activeSize
	 * @param inactiveSize
	 */
	public void setTitleTextSize(float activeSize, float inactiveSize) {
		this.titleActiveTextSize = activeSize;
		this.titleInactiveTextSize = inactiveSize;
		createItems();
	}

	/**
	 * Get item at the given index
	 *
	 * @param position int: item position
	 * @return The item at the given position
	 */
	public AHBottomNavigationItem getItem(int position) {
		if (position < 0 || position > items.size() - 1) {
			Log.w(TAG, "The position is out of bounds of the items (" + items.size() + " elements)");
		}
		return items.get(position);
	}

	/**
	 * Get the current item
	 *
	 * @return The current item position
	 */
	public int getCurrentItem() {
		return currentItem;
	}

	/**
	 * Set the current item
	 *
	 * @param position int: position
	 */
	public void setCurrentItem(int position) {
		setCurrentItem(position, true);
	}

	/**
	 * Set the current item
	 *
	 * @param position    int: item position
	 * @param useCallback boolean: use or not the callback
	 */
	public void setCurrentItem(int position, boolean useCallback) {
		if (position >= items.size()) {
			Log.w(TAG, "The position is out of bounds of the items (" + items.size() + " elements)");
			return;
		}

		if (items.size() == MIN_ITEMS || forceTitlesDisplay) {
			updateItems(position, useCallback);
		} else {
			updateSmallItems(position, useCallback);
		}
	}

	/**
	 * Return if the behavior translation is enabled
	 *
	 * @return a boolean value
	 */
	public boolean isBehaviorTranslationEnabled() {
		return behaviorTranslationEnabled;
	}

	/**
	 * Set the behavior translation value
	 *
	 * @param behaviorTranslationEnabled boolean for the state
	 */
	public void setBehaviorTranslationEnabled(boolean behaviorTranslationEnabled) {
		this.behaviorTranslationEnabled = behaviorTranslationEnabled;
		if (getParent() instanceof CoordinatorLayout) {
			ViewGroup.LayoutParams params = getLayoutParams();
			if (bottomNavigationBehavior == null) {
				bottomNavigationBehavior = new AHBottomNavigationBehavior<>(behaviorTranslationEnabled);
			} else {
				bottomNavigationBehavior.setBehaviorTranslationEnabled(behaviorTranslationEnabled);
			}

			bottomNavigationBehavior.setYTranslationListener(this);
			((CoordinatorLayout.LayoutParams) params).setBehavior(bottomNavigationBehavior);
			if (needHideBottomNavigation) {
				needHideBottomNavigation = false;
				bottomNavigationBehavior.hideView(this, bottomNavigationHeight, hideBottomNavigationWithAnimation);
			}
		}
	}

	/**
	 * Hide Bottom Navigation with animation
	 */
	public void hideBottomNavigation() {
		hideBottomNavigation(true);
	}

	/**
	 * Hide Bottom Navigation with or without animation
	 *
	 * @param withAnimation Boolean
	 */
	public void hideBottomNavigation(boolean withAnimation) {
		if (bottomNavigationBehavior != null) {
			bottomNavigationBehavior.hideView(this, bottomNavigationHeight, withAnimation);
		} else if (getParent() instanceof CoordinatorLayout) {
			needHideBottomNavigation = true;
			hideBottomNavigationWithAnimation = withAnimation;
		} else {
			// Hide bottom navigation
			ViewCompat.animate(this)
					.translationY(bottomNavigationHeight)
					.setInterpolator(new LinearOutSlowInInterpolator())
					.setDuration(withAnimation ? 300 : 0)
					.start();
		}
	}

	/**
	 * Restore Bottom Navigation with animation
	 */
	public void restoreBottomNavigation() {
		restoreBottomNavigation(true);
	}

	/**
	 * Restore Bottom Navigation with or without animation
	 *
	 * @param withAnimation Boolean
	 */
	public void restoreBottomNavigation(boolean withAnimation) {
		if (bottomNavigationBehavior != null) {
			bottomNavigationBehavior.resetOffset(this, withAnimation);
		} else {
			// Show bottom navigation
			ViewCompat.animate(this)
					.translationY(0)
					.setInterpolator(new LinearOutSlowInInterpolator())
					.setDuration(withAnimation ? 300 : 0)
					.start();
		}
	}

	/**
	 * Return if the tint should be forced (with setColorFilter)
	 *
	 * @return Boolean
	 */
	public boolean isForceTint() {
		return forceTint;
	}

	/**
	 * Set the force tint value
	 * If forceTint = true, the tint is made with drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
	 *
	 * @param forceTint Boolean
	 */
	public void setForceTint(boolean forceTint) {
		this.forceTint = forceTint;
		createItems();
	}

	/**
	 * Return if we force the titles to be displayed
	 *
	 * @return Boolean
	 */
	public boolean isForceTitlesDisplay() {
		return forceTitlesDisplay;
	}

	/**
	 * Force the titles to be displayed (or used the classic behavior)
	 * Note: Against Material Design guidelines
	 *
	 * @param forceTitlesDisplay Boolean
	 */
	public void setForceTitlesDisplay(boolean forceTitlesDisplay) {
		this.forceTitlesDisplay = forceTitlesDisplay;
		createItems();
	}

	/**
	 * Set AHOnTabSelectedListener
	 */
	public void setOnTabSelectedListener(OnTabSelectedListener tabSelectedListener) {
		this.tabSelectedListener = tabSelectedListener;
	}

	/**
	 * Remove AHOnTabSelectedListener
	 */
	public void removeOnTabSelectedListener() {
		this.tabSelectedListener = null;
	}

	/**
	 * Set AHOnYTranslationListener
	 */
	public void setOnYTranslationListner(OnYTranslationListener yTranslationListener) {
		this.yTranslationListener = yTranslationListener;
	}

	/**
	 * Remove AHOnYTranslationListener()
	 */
	public void removeOnYTranslationListener() {
		this.yTranslationListener = null;
	}

	/**
	 * Set the notification number
	 *
	 * @param nbNotification int
	 * @param itemPosition   int
	 */
	@Deprecated
	public void setNotification(int nbNotification, int itemPosition) {
		if (itemPosition < 0 || itemPosition > items.size() - 1) {
			Log.w(TAG, "The position is out of bounds of the items (" + items.size() + " elements)");
			return;
		}
		notifications[itemPosition] = nbNotification == 0 ? "" : String.valueOf(nbNotification);
		updateNotifications(false, itemPosition);
	}

	/**
	 * Set Notification content
	 *
	 * @param title String
	 * @param itemPosition int
	 */
	public void setNotification(String title, int itemPosition) {
		notifications[itemPosition] = title;
		updateNotifications(false, itemPosition);
	}

	/**
	 * Set notification text color
	 *
	 * @param textColor int
	 */
	public void setNotificationTextColor(@ColorInt int textColor) {
		this.notificationTextColor = textColor;
		updateNotifications(true, UPDATE_ALL_NOTIFICATIONS);
	}

	/**
	 * Set notification text color
	 *
	 * @param textColor int
	 */
	public void setNotificationTextColorResource(@ColorRes int textColor) {
		this.notificationTextColor = ContextCompat.getColor(context, textColor);
		updateNotifications(true, UPDATE_ALL_NOTIFICATIONS);
	}

	/**
	 * Set notification background resource
	 *
	 * @param drawable Drawable
	 */
	public void setNotificationBackground(Drawable drawable) {
		this.notificationBackgroundDrawable = drawable;
		updateNotifications(true, UPDATE_ALL_NOTIFICATIONS);
	}

	/**
	 * Set notification background color
	 *
	 * @param color int
	 */
	public void setNotificationBackgroundColor(@ColorInt int color) {
		this.notificationBackgroundColor = color;
		updateNotifications(true, UPDATE_ALL_NOTIFICATIONS);
	}

	/**
	 * Set notification background color
	 *
	 * @param color int
	 */
	public void setNotificationBackgroundColorResource(@ColorRes int color) {
		this.notificationBackgroundColor = ContextCompat.getColor(context, color);
		updateNotifications(true, UPDATE_ALL_NOTIFICATIONS);
	}

	/**
	 * Set notification typeface
	 *
	 * @param typeface Typeface
	 */
	public void setNotificationTypeface(Typeface typeface) {
		this.notificationTypeface = typeface;
		updateNotifications(true, UPDATE_ALL_NOTIFICATIONS);
	}

	/**
	 * Set the notification margin left
	 * @param activeMargin
	 * @param inactiveMargin
	 */
	public void setNotificationMarginLeft(int activeMargin, int inactiveMargin) {
		this.notificationActiveMarginLeft = activeMargin;
		this.notificationInactiveMarginLeft = inactiveMargin;
		createItems();
	}

	/**
	 * Activate or not the elevation
	 *
	 * @param useElevation boolean
	 */
	public void setUseElevation(boolean useElevation) {
		ViewCompat.setElevation(this, useElevation ?
				resources.getDimension(R.dimen.bottom_navigation_elevation) : 0);
		setClipToPadding(false);
	}

	/**
	 * Activate or not the elevation, and set the value
	 * @param useElevation boolean
	 * @param elevation float
	 */
	public void setUseElevation(boolean useElevation, float elevation) {
		ViewCompat.setElevation(this, useElevation ? elevation : 0);
		setClipToPadding(false);
	}


	@Override public void onYTranslation(int y) {
		if (yTranslationListener != null) {
			yTranslationListener.onYTranslation(y);
		}
	}


	////////////////
	// INTERFACES //
	////////////////

	/**
	 *
	 */
	public interface OnTabSelectedListener {
		boolean onTabSelected(int position, boolean wasSelected);
	}
}