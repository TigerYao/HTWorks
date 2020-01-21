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
package com.huatu.handheld_huatu.helper.retrofit;



/**
 * Retrofit回调的Fragment页面的实现接口
 *
 * author : Soulwolf Create by 2015/9/12 10:49
 * email  : ToakerQin@gmail.com.
 */
public interface KindRetrofitCallBack<RESPONSE> {

    void onSubscriberStart();

    void onSuccess(RESPONSE response)  ;

    void onError(String error, int type);


    boolean isFragmentFinished();
}
