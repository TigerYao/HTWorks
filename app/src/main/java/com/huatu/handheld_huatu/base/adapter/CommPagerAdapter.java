/**
 * <pre>
 * Copyright (C) 2015  校导网(武汉)科技有限责任公司 Inc！
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
 * </pre>
 */
package com.huatu.handheld_huatu.base.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.huatu.handheld_huatu.base.fragment.AbsFragment;


/**
 * author : Soulwolf Create by 2015/9/22 11:02
 * email  : ToakerQin@gmail.com.
 * https://blog.csdn.net/qq_29951983/article/details/80622176
 * ViewPager复用
 */
public class CommPagerAdapter extends FragmentPagerAdapter {

    private AbsFragment[] mFragments;
    private String []  mTitles;

    public CommPagerAdapter(FragmentManager fm, String[] mTitles, AbsFragment... fragments){
        super(fm);
        this.mFragments = fragments;
        this.mTitles = mTitles;
    }

    @Override
    public int getCount() {
        return mFragments != null ? mFragments.length : 0;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
