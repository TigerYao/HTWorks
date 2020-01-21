package com.huatu.handheld_huatu.business.essay.camera;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.baijiayun.glide.request.Request;
import com.baijiayun.glide.request.target.SimpleTarget;


/**
 * Created by Administrator on 2019\4\28 0028.
 */

public abstract class SimpleViewTargetV2<Z> extends SimpleTarget<Z> {

    View mImageView;
    @Nullable private static Integer tagId = null;
    public SimpleViewTargetV2(View imageView){
        super();
        mImageView=imageView;
    }


    @Override
    public void setRequest(@Nullable Request request) {
        setTag(request);
    }

    /**
     * Returns any stored request using {@link View#getTag()}.
     *
     * <p> For Glide to function correctly, Glide must be the only thing that calls {@link
     * View#setTag(Object)}. If the tag is cleared or put to another object type, Glide will not be
     * able to retrieve and cancel previous loads which will not only prevent Glide from reusing
     * resource, but will also result in incorrect images being loaded and lots of flashing of images
     * in lists. As a result, this will throw an {@link IllegalArgumentException} if {@link
     * View#getTag()}} returns a non null object that is not an {link
     * com.bumptech.glide.request.Request}. </p>
     */
    @Override
    @Nullable
    public Request getRequest() {
        Object tag = getTag();
        Request request = null;
        if (tag != null) {
            if (tag instanceof Request) {
                request = (Request) tag;
            } else {
                throw new IllegalArgumentException(
                        "You must not call setTag() on a view Glide is targeting");
            }
        }
        return request;
    }



    private void setTag(@Nullable Object tag) {
        if (tagId == null) {
           // isTagUsedAtLeastOnce = true;
            mImageView.setTag(tag);
        } else {
            mImageView.setTag(tagId, tag);
        }
    }

    @Nullable
    private Object getTag() {
        if (tagId == null) {
            return mImageView.getTag();
        } else {
            return mImageView.getTag(tagId);
        }
    }
}
