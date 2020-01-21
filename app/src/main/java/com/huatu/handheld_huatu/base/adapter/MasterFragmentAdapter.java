/**
 * <pre>
 * Copyright (C) 2015  Soulwolf XiaoDaoW3.0
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
 * </pre>
 */
package com.huatu.handheld_huatu.base.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


/**
 *
 * {@link } Fragment 适配器
 *
 * author : Soulwolf Create by 2015/6/12 13:57
 * email  : ToakerQin@gmail.com.
 */
public class MasterFragmentAdapter extends FragmentPagerAdapter {

    FragmentPagerParams[] mFragmentData;

    Fragment[] fragments;
    String [] TITLES;

    Context mContext;

    public MasterFragmentAdapter(Context context, FragmentManager fm, FragmentPagerParams... args) {
        this(context,fm,null,args);
    }

    public MasterFragmentAdapter(Context context, FragmentManager fm, String[] titles, FragmentPagerParams... args) {
        super(fm);
        this.mContext = context;
        this.mFragmentData = args;
        this.TITLES = titles;
        fragments = new Fragment[args.length];
    }

    public Fragment getCurrFragment(int position){
        if(position<fragments.length){
            return fragments[position];
        }
        return null;
    }

    @Override
    public Fragment getItem(int position) {
        if(mFragmentData != null && mFragmentData.length > position){
            if(fragments != null && fragments.length > position && fragments[position] != null)
                return fragments[position];
            FragmentPagerParams entry = mFragmentData[position];
            if(entry.mParams == null){
                fragments[position] = Fragment.instantiate(mContext, entry.mFragmentClass.getName());
                return fragments[position];
            }
            fragments[position] = Fragment.instantiate(mContext, entry.mFragmentClass.getName(),
                    entry.mParams);
            return fragments[position];
        }
        return null;
    }

    @Override
    public int getCount() {
        if(mFragmentData != null){
            return mFragmentData.length;
        }
        return 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(TITLES != null && TITLES.length > position){
            return TITLES[position];
        }
        return super.getPageTitle(position);
    }
}
