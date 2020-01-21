package com.huatu.handheld_huatu.business.other;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.fragment.AbsSettingFragment;
import com.huatu.handheld_huatu.helper.UIJumpHelper;
import com.huatu.handheld_huatu.ui.TBSFileView;
import com.huatu.handheld_huatu.utils.ArgConstant;

import java.io.File;

import butterknife.BindView;

/**
 * Created by Administrator on 2019\2\18 0018.
 */

public class PdfViewFragment extends AbsSettingFragment {

    TBSFileView mPdfView;

    String mFilePath;

    public static void lanuch(Context context, String filePath) {
        Bundle arg = new Bundle();
        arg.putString(ArgConstant.LOCAL_PATH, filePath);
        UIJumpHelper.jumpFragment(context, PdfViewFragment.class, arg);
    }

    @Override
    protected void parserParams(Bundle args) {
        mFilePath = args.getString(ArgConstant.LOCAL_PATH);
    }

    @Override
    protected int getContentView() {
        return R.layout.pdf_read_layout;
    }


    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        super.layoutInit(inflater, savedInstanceSate);
        setHasOptionsMenu(true);
        setHomeAsUpEnabled(true);
        setTitle("详情");
    }

    @Override
    public void requestData() {
        super.requestData();
        mPdfView = this.findViewById(R.id.pdfView);
        mPdfView.displayFile(new File(mFilePath));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPdfView.onStopDisplay();
    }
}
