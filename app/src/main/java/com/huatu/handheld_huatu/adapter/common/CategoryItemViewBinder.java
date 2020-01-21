/*
 * Copyright 2016 drakeet. https://github.com/drakeet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huatu.handheld_huatu.adapter.common;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.listener.EventConstant;
import com.huatu.handheld_huatu.utils.AnimUtils;
import com.me.drakeet.multitype.ItemViewBinder;


public class CategoryItemViewBinder extends ItemViewBinder<Category, CategoryItemViewBinder.ViewHolder> {

    @Override
    protected @NonNull
    ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.course_study_item_category, parent, false), this);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull Category category) {
        holder.title.setText(category.title);
        holder.mOpenImgView.setVisibility(category.canExpland ? View.VISIBLE : View.GONE);
        holder.mOpenImgView.setRotation(category.isExplanded ? 180f : 0f);
        holder.mCategory=category;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private @NonNull  final TextView title;
        private ImageView mOpenImgView;

        private Category mCategory;

        ViewHolder(@NonNull View itemView, @NonNull final CategoryItemViewBinder binder) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            mOpenImgView = itemView.findViewById(R.id.right_open_img);
            mOpenImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {

                        boolean oldIsExpland=mCategory.isExplanded;
                        mCategory.isExplanded=!oldIsExpland;
                        binder.getAdapter().performViewClick(v, position, mCategory.type*(oldIsExpland? 2:1));
                        if(oldIsExpland)
                            AnimUtils.showCloseRotation(v);
                        else
                             AnimUtils.showOpenRotation(v);
                        // binder.getAdapter().notifyItemRemoved(position);
                    }
                }
            });
        }
    }
}
