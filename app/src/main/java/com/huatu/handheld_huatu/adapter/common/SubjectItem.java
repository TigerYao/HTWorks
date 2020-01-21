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

/**

 */
public class SubjectItem  implements Comparable<SubjectItem>{

  public final int id;
  public boolean isSelected;
  public String title;
  public int  type;

  public SubjectItem(int type,String title,int keyId) {
    this.id = keyId;
    this.title=title;
    this.type=type;


  }
  @Override
  public int compareTo(SubjectItem obj){

      int curCode=this.id+10*type;
      int objcode=obj.id+10*type;
      if(objcode<curCode) {
        return -1;
      }

      if(objcode>curCode){
        return 1;
      }
      if(objcode==curCode){
           return this.title.compareTo(obj.title);
      }

      return 0;
  }

}
