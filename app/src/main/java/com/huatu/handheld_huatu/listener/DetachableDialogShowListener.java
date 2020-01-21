package com.huatu.handheld_huatu.listener;

import android.content.DialogInterface;

/**
 * Created by Administrator on 2019\9\17 0017.
 */

public final class DetachableDialogShowListener implements DialogInterface.OnShowListener {
    public static DetachableDialogShowListener wrap(DialogInterface.OnShowListener delegate) {
        return new DetachableDialogShowListener(delegate);
    }

    private DialogInterface.OnShowListener delegateOrNull;

    private DetachableDialogShowListener(DialogInterface.OnShowListener delegate) {
        this.delegateOrNull = delegate;
    }

    @Override
    public void onShow(DialogInterface dialog) {
        if (delegateOrNull != null) {
            delegateOrNull.onShow(dialog);
            // delegateOrNull = null;
        }
    }

    public void clear() {
        delegateOrNull = null;
    }

   /* public void clearOnDetach(Dialog dialog)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            dialog.getWindow()
                    .getDecorView()
                    .getViewTreeObserver()
                    .addOnWindowAttachListener(new ViewTreeObserver.OnWindowAttachListener()
                    {
                        @Override
                        public void onWindowAttached()
                        {

                        }

                        @Override
                        public void onWindowDetached()
                        {
                            if (delegateOrNull != null)
                            {
                                delegateOrNull.onCancel(dialog);
                                delegateOrNull = null;
                            }
                        }
                    });
        }
    }*/
}