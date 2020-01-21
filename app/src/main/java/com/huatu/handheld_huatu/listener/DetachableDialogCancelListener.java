package com.huatu.handheld_huatu.listener;

import android.content.DialogInterface;

/**
 * Created by Administrator on 2019\9\17 0017.
 * 解决Android5.0以下Dialog引起的内存泄漏
 * https://www.cnblogs.com/endure/p/7664320.html
 */

public final class DetachableDialogCancelListener implements DialogInterface.OnCancelListener {
    public static DetachableDialogCancelListener wrap(DialogInterface.OnCancelListener delegate) {
        return new DetachableDialogCancelListener(delegate);
    }

    private DialogInterface.OnCancelListener delegateOrNull;

    private DetachableDialogCancelListener(DialogInterface.OnCancelListener delegate) {
        this.delegateOrNull = delegate;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (delegateOrNull != null) {
            delegateOrNull.onCancel(dialog);
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