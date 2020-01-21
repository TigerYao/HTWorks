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

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.ui.SelectTextView;
import com.me.drakeet.multitype.ItemViewBinder;

import java.util.Set;



/**
 * @author cjx
 */
public class SubjectItemViewBinder extends ItemViewBinder<SubjectItem, SubjectItemViewBinder.ViewHolder> {

  private final @NonNull  Set<String> selectedSet;
  private final int NorColor= Color.parseColor("#ff4a4a4a");

  public SubjectItemViewBinder(@NonNull Set<String> selectedSet) {
     this.selectedSet = selectedSet;

  }


  @Override
  protected @NonNull ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
    View root = inflater.inflate(R.layout.course_study_item_subject, parent, false);
    return new ViewHolder(root);
  }


  @Override
  protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull SubjectItem square) {
    holder.square = square;
    holder.squareView.setHasBackground(true);
    holder.squareView.setText(square.title);
    holder.squareView.setSelected(square.isSelected);
    holder.squareView.setTextColor(square.isSelected ?Color.WHITE:NorColor);

  }


  public @NonNull  Set<String> getSelectedSet() {
    return selectedSet;
  }


  public class ViewHolder extends RecyclerView.ViewHolder {

    private SelectTextView squareView;
    private SubjectItem square;


    ViewHolder(final View itemView) {
      super(itemView);
      squareView = itemView.findViewById(R.id.title_txt);
      squareView.setHasBackground(true);
      itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          squareView.setSelected(square.isSelected = !square.isSelected);
          squareView.setTextColor(square.isSelected ?Color.WHITE:NorColor);
         // squareView.invalidate();
          if (square.isSelected) {
            selectedSet.add(square.type+"_"+square.id+"_"+square.title);
          } else {
            selectedSet.remove(square.type+"_"+square.id+"_"+square.title);
          }
        }
      });
    }
  }
}
