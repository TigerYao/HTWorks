package com.huatu.test.drawimpl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;


import com.huatu.test.DisplayUtil;
import com.huatu.test.LogUtils;
import com.huatu.test.R;
import com.huatu.test.bean.UnderLine;
import com.huatu.test.custom.DensityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cjx on 2019\6\26 0026.
 */

public class CusNumCheckDetailTag extends CusCheckDetailTag {

    private float mSelectCircleRadius;//选中圆的半径
/*    private Paint mPathPaint;

    protected float mHollowCircleStroke;//空心圆粗细
    private Path mPath = new Path();
    private float mCircleXOffset=0;  //圆的左偏移*/

    private float mlayerYOffset=0;
    protected List<Data> mLineListInfos=new ArrayList<>();

    public CusNumCheckDetailTag(CusAlignText cCusAlignText) {
       super(cCusAlignText);
        mSelectCircleRadius= DensityUtils.dp2px(mContext,8);
        mlayerYOffset=DensityUtils.dp2px(mContext, 2f);
    /*    mPathPaint = new Paint();

        mPathPaint.setStyle(Paint.Style.STROKE);
        mPathPaint.setAntiAlias(true);
        mHollowCircleStroke=DensityUtils.dp2px(mContext, 1f);
        mPathPaint.setStrokeWidth(mHollowCircleStroke);
        mSelectCircleRadius=DensityUtils.dp2px(mContext,8);
        mCircleXOffset=DensityUtils.dp2px(mContext, 0.3f);*/
    }

    @Override
    protected void drawLine(int type, Canvas canvas, ArrayList<UnderLine> singleLines) {
        mLineListInfos.clear();
        if (localPaint != null) {
            localPaint.setColor(ContextCompat.getColor(getContext(), R.color.common_style_text_color));
        }
        if (singleLines != null && singleLines.size() > 0) {
            for (int i = 0; i < singleLines.size(); ) {
                int start = 0;
                int end = 0;
                UnderLine lineData = singleLines.get(i);
                if (lineData != null) {
//                        Log.d("UnderLine", lineData.toString());
                    start = lineData.start;
                    end = lineData.end;
                    List<Data> varAry = getDrawData(1, canvas, start, end, lineData.score,lineData.seq);
                    mLineListInfos.addAll(varAry);
                }
                i++;
            }
        }
    }

    private void drawLineBg( Canvas canvas,  Data data,int color) {
        getPaint().setAlpha(85);
        getPaint().setColor(color);
        if (canvas != null) {
            if (data != null) {
                canvas.drawRect(new RectF(data.xstart, data.ystart - DisplayUtil.dp2px(15), data.xend, data.yend + DisplayUtil.dp2px(4)), getPaint());
            }
        }
        getPaint().setAlpha(255);
    }

     @Override
    protected void drawCus_dHeiLine(Canvas canvas, Data data,boolean islineEnd){

        if (canvas != null) {
            if (data != null) {
                if(data.seq>0){
                   // ContextCompat.getColor(getContext(), R.color.essay_height_light_color)
                    drawLineBg(canvas,data,0xFFFFF600);
                    return;
                }

                localPaint.setColor(Color.parseColor("#FF6D73"));
                localPaint.setStyle(Paint.Style.STROKE);
//                Log.d("dSingleBg", data.toString());
                localPaint.setStrokeWidth(4);
                int i4 = (int) data.xstart;                                                         // 开始处 x坐标
                int i5 = (int) data.xend;                                                           // 结束处 x坐标
                int i6 = (int) (i5 - i4);                                                                   // 划线总宽度
                if (i6 != 0) {
                    localPath.reset();

                    float layerOffset=data.seq*mlayerYOffset;
                    LogUtils.d("layerOffset",data.seq+"");
                    localPath.moveTo(i4, data.ystart + DisplayUtil.dp2px(yOffset)+layerOffset);              // 路径挪到开始 y + 偏移量 的位置
                    int i7 = ((i6) / wspace);                                                       // 总宽度/固定值（不知道是什么） 固定值可能是波浪线的最小循环长度 一个波长 i7 一共有几个整数波长
                    int sm = i6 % wspace;                                                           // 总宽度/固定值 的余数      sm 是整数波长之后的余数
//                    if (i7 == 0) {                                                                  // 如果需要划线的长度不够一个波长，就让它等于 1
//                        i7 = 1;
//                    }


                    for (int i9 = 0; i9 < i7 + 1; i9++) {
                        // quadTo 用于绘制圆滑曲线，即贝塞尔曲线。mPath.quadTo(x1, y1, x2, y2) (x1,y1) 为控制点，(x2,y2)为结束点。
                        localPath.rQuadTo(dx, yheight, dx2, 0.0F);                             // 往下凹的半波长
//                        if (i9 == (i7 - 1)) {
//                            if (data.score == null || i7 < 3 || sm > 15) {                          // 最后一个半波长
//                                localPath.rQuadTo(dx, -yheight, dx2, 0.0F);
//                            }
//                        } else {


                        if(islineEnd){
                            if(i9<i7){  //最后的一半不绘制
                                localPath.rQuadTo(dx, -yheight, dx2, 0.0F);                        // 往上凸的半波长
                            }
                        }else {
                            localPath.rQuadTo(dx, -yheight, dx2, 0.0F);
                        }

//                        }
                    }
//                    if (data.score == null && i7 > 1) {
//                        if (sm > 30) {
//                            localPath.rQuadTo(dx, yheight, dx2, 0.0F);
//                        }
//                    }
                    if (!localPath.isEmpty() && localPaint != null) {
                        canvas.drawPath(localPath, localPaint);
                    }

                    // 画个白的背景，遮挡 分数后的波浪线 或者 遮挡多余的线  移动位置到CusNumAlignText
                  /*  localPaint.setColor(ContextCompat.getColor(getContext(), R.color.white));
                    localPaint.setStyle(Paint.Style.FILL);
                    float left;
                    float top = data.ystart + DisplayUtil.dp2px(yOffset) - yheight;
                    float right = data.xend + wspace;
                    float bottom = data.ystart + DisplayUtil.dp2px(yOffset) + yheight;
                    if (data.score != null) {
                        left = data.xend - DisplayUtil.dp2px(5);
                    } else {
                        left = data.xend;
                    }
                    RectF rectF = new RectF(left, top, right, bottom);
                    canvas.drawRect(rectF, localPaint);
                    localPaint.setColor(ContextCompat.getColor(getContext(), R.color.common_style_text_color));

                    Typeface oldType = localPaint.getTypeface();
                    float oldSize = localPaint.getTextSize();
                    // 字体
                    localPaint.setTypeface(fromAsset);
                    localPaint.setTextSize(DisplayUtil.dp2px(8));

                    localPaint.setStrokeWidth(1);

                    if (data.score != null) {
                     *//*   mPathPaint.setStyle(Paint.Style.FILL);
                        mPathPaint.setColor(Color.WHITE);
                        float cx=left-mCircleXOffset;
                        float cy=data.ystart+mSelectCircleRadius/2+ mHollowCircleStroke-2*mCircleXOffset;
                        canvas.drawCircle(cx, cy, mSelectCircleRadius, mPathPaint);
                        //初始化Path
                        mPath.reset();
                        mPathPaint.setColor(0xFFFF6D73);
                        mPathPaint.setStyle(Paint.Style.STROKE);
                        //Path.Direction.CW顺时针绘制圆 Path.Direction.CCW逆时针绘制圆
                        mPath.addCircle(cx, cy, mSelectCircleRadius - mHollowCircleStroke, Path.Direction.CW);
                        canvas.drawPath(mPath, mPathPaint);*//*


                        // 最后的画分数
//                        canvas.drawText(data.score, i5 - DisplayUtil.dp2px(xOffset), data.ystart + DisplayUtil.dp2px(yOffsetz), localPaint);
                        canvas.drawText(data.score, mSelectCircleRadius+left + 1, data.ystart + DisplayUtil.dp2px(yOffsetz), localPaint);


                    }

                    localPaint.setStyle(Paint.Style.STROKE);
                    localPaint.setTypeface(oldType);
                    localPaint.setTextSize(oldSize);

                    localPaint.setStrokeWidth(2);*/
                }
            }
        }
    }
}
