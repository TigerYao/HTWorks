package com.arch;

import android.arch.lifecycle.ViewModelStore;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import static com.arch.HolderFragment.holderFragmentFor;

/**
 * Factory methods for {@link ViewModelStore} class.
 */
@SuppressWarnings("WeakerAccess")
public class ViewModelStores {

    private ViewModelStores() {
    }

    /**
     * Returns the {@link ViewModelStore} of the given activity.
     *
     * @param activity an activity whose {@code ViewModelStore} is requested
     * @return a {@code ViewModelStore}
     */
    @MainThread
    public static ViewModelStore of(@NonNull FragmentActivity activity) {
        return holderFragmentFor(activity).getViewModelStore();
    }

    /**
     * Returns the {@link ViewModelStore} of the given fragment.
     *
     * @param fragment a fragment whose {@code ViewModelStore} is requested
     * @return a {@code ViewModelStore}
     */
    @MainThread
    public static ViewModelStore of(@NonNull Fragment fragment) {
        return holderFragmentFor(fragment).getViewModelStore();
    }
}
