package com.huatu.handheld_huatu.business.essay.bhelper.textselect;

/**
 * Created by Administrator on 2019\7\19 0019.
 */

public class SelectableMultTextV2Helper extends SelectableMultTextHelper {

    public SelectableMultTextV2Helper(Builder builder) {
        super(builder);
    }

    @Override
    public void destroy(boolean fullDestory){
        super.destroy(false);
    }
}
