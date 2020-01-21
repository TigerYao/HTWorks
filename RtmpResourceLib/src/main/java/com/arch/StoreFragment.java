package com.arch;

import android.arch.lifecycle.ViewModelStore;
import android.arch.lifecycle.ViewModelStoreOwner;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * Created by Administrator on 2020\1\8 0008.
 */

public class StoreFragment extends Fragment implements ViewModelStoreOwner {

    ViewModelStore mViewModelStore;

    /**
     * Returns the {@link ViewModelStore} associated with this Fragment
     *
     * @return a {@code ViewModelStore}
     * @throws IllegalStateException if called before the Fragment is attached i.e., before
     * onAttach().
     */
    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        if (getContext() == null) {
            throw new IllegalStateException("Can't access ViewModels from detached fragment");
        }
        if (mViewModelStore == null) {
            mViewModelStore = new ViewModelStore();
        }
        return mViewModelStore;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FragmentActivity activity = getActivity();
        boolean isChangingConfigurations = activity != null && activity.isChangingConfigurations();
        if (mViewModelStore != null && !isChangingConfigurations) {
            mViewModelStore.clear();
        }
    }
}
