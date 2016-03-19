package com.aurelhubert.ahbottomnavigation;

/**
 * Created by Michele on 18/03/2016.
 *
 * This is HideOnScroll utility made for AHBottomNavigation.
 *
 * Copyright 2016 Michele Lacorte
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.animation.LinearInterpolator;

public final class HideOnScroll {

    public static class ShowHideBottomBarOnScrollingListener extends RecyclerView.OnScrollListener {

        private AHBottomNavigation bottomBar;
        private Toolbar toolbar;
        private State state;

        public ShowHideBottomBarOnScrollingListener(AHBottomNavigation bottomBar, Toolbar toolbar) {
            this.bottomBar = bottomBar;
            this.toolbar = toolbar;
            this.state = new State();
        }


        /**
         * Bottom bar animation show
         */
        private void bottomBarAnimateShow() {
            bottomBar.animate()
                    .translationY(0)
                    .setInterpolator(new LinearInterpolator())
                    .setDuration(50);
        }

        /**
         * Bottom bar animation hide
         */
        private void bottomBarAnimateHide() {
            bottomBar.animate()
                    .translationY(bottomBar.getHeight())
                    .setInterpolator(new LinearInterpolator())
                    .setDuration(50);
        }


        /**
         * Bottom bar animation show
         */
        private void toolbarAnimateShow() {
            toolbar.animate()
                    .translationY(0)
                    .setInterpolator(new LinearInterpolator())
                    .setDuration(50);
        }

        /**
         * Bottom bar animation hide
         */
        private void toolbarAnimateHide() {
            toolbar.animate()
                    .translationY(-toolbar.getHeight())
                    .setInterpolator(new LinearInterpolator())
                    .setDuration(50);
        }

        @Override
        public final void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (state.scrollingOffset > 0) {
                    bottomBarAnimateHide();
                    toolbarAnimateHide();
                } else if (state.scrollingOffset < 0) {
                    bottomBarAnimateShow();
                    toolbarAnimateShow();
                }
            }
        }

        @Override
        public final void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            state.scrollingOffset = dy;
            bottomBar.animate().cancel();
            if (dy > 0) {
                bottomBarAnimateHide();
                toolbarAnimateHide();
            } else if (dy < 0) {
                bottomBarAnimateShow();
                toolbarAnimateShow();
            }
        }

        /**
         * Saving and restoring current state.
         */
        public static final class State implements Parcelable {
            public static Creator<State> CREATOR = new Creator<State>() {
                public State createFromParcel(Parcel parcel) {
                    return new State(parcel);
                }

                public State[] newArray(int size) {
                    return new State[size];
                }
            };
            // Scroll UP/DOWN offset
            private int scrollingOffset;

            State() {
            }

            State(Parcel parcel) {
                this.scrollingOffset = parcel.readInt();
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel parcel, int flags) {
                parcel.writeInt(scrollingOffset);
            }
        }
    }
}
