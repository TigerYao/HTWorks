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

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * FragmentPagerAdapter的使用时的信息及参数的封装实体
 *
 * author : Soulwolf Create by 2015/6/12 14:02
 * email  : ToakerQin@gmail.com.
 */
public class FragmentPagerParams {

    public Class<? extends Fragment> mFragmentClass;

    public Bundle mParams;

    public FragmentPagerParams(Class<? extends Fragment> mFragmentClass, Bundle mParams) {
        this.mFragmentClass = mFragmentClass;
        this.mParams = mParams;
    }

    public FragmentPagerParams(Class<? extends Fragment> mFragmentClass) {
        this.mFragmentClass = mFragmentClass;
    }

    public FragmentPagerParams() {
    }
}
