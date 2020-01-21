package com.huatu.popup;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huatu.utils.DensityUtils;
import com.huatu.utils.StringUtils;


/**
 * Created by Terry
 * Date : 2016/6/28 0028.
 * Email: terry@xiaodao360.com
 */
public class QuickListAction extends PopupWindows implements View.OnClickListener {


    onItemViewClickListener mOnItemViewClickListener;
    public interface onItemViewClickListener{
         void onItemViewClick(int pos, View view);
    }

    private ViewGroup mRoot;

    private LayoutInflater mInflater;
    private Context mContext;

    public final static int ANIM_GROW_FROM_LEFT = 1;
    public final static int ANIM_GROW_FROM_RIGHT = 2;
    public final static int ANIM_GROW_FROM_CENTER = 3;
    public final static int ANIM_REFLECT = 4;
    public final static int ANIM_AUTO = 5;

    private int curx=-1, cury=-1;
    private int animStyle;
    //private ViewGroup mTrack;
    //private LinearLayout scroller;
    private ViewGroup scroller;
    private int mDistance=0;
    private boolean mIsForceBottom=false;

    public ViewGroup getRootView() {  return mRoot;   }

    public void setDistance(int adjustDistance){
        mDistance=adjustDistance;
    }

    public void setForceOnBottom(){
        mIsForceBottom=true;
    }
    /**
     * Constructor

     */
    public QuickListAction(Context context,int LayoutID,int ScrollerID) {
        super(context);

        mInflater 	= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDistance= DensityUtils.dp2px(context, 8);
        // root = (ViewGroup)inflater.Inflate(Resource.Layout.Popmenu, null);
        mRoot = (ViewGroup)mInflater.inflate(LayoutID, null);


        // mArrowDown = (ImageView)root.FindViewById(Resource.Id.arrow_down);
        // mArrowUp = (ImageView)root.FindViewById(Resource.Id.arrow_up);

        setContentView(mRoot);


        // dataListView = (ListView)root.FindViewById(Resource.Id.BaseInfoannounce_list);
        scroller = (ViewGroup) mRoot.findViewById(ScrollerID);
        animStyle = ANIM_AUTO;
    }



    public void setContentView(View root) {
        mRootView = root;
        mWindow.setContentView(root);
    }

    public void setAnimStyle(int animStyle) {
        this.animStyle = animStyle;
    }

    public void Reshow(View curAnchor)
    {
        //if (curx != -1 && cury != -1)
        //    Window.ShowAtLocation(curAnchor, GravityFlags.NoGravity, curx, cury);
        //else
        show(curAnchor,false);
    }

    public void show(View anchor) {
         show(anchor, true);
    }

    public void setOnViewItemClickListener(onItemViewClickListener   itemViewClickListener ){
        mOnItemViewClickListener=itemViewClickListener;

        int pos=0;
        for (int i=0;i<scroller.getChildCount();i++){
            if(scroller.getChildAt(i) instanceof TextView){
                 scroller.getChildAt(i).setTag(pos);

                ((TextView)scroller.getChildAt(i)).setOnClickListener(this);
                pos++;
            }
        }

    }

    @Override
    public  void onClick(View v){
       if(v.getTag()!=null){
            if(null!=mOnItemViewClickListener)
                mOnItemViewClickListener.onItemViewClick(StringUtils.parseInt(v.getTag().toString()),v);
        }
    }

    /**
     * Show popup window. Popup is automatically positioned, on top or bottom of anchor view.
     *
     */
    private void show(View currentAnchor,boolean needPre)
    {
        if(!ViewCompat.isAttachedToWindow(currentAnchor)){
            return;
        }
        if (needPre) preShow();

        int xPos, yPos;

        int[] location = new int[2];

        currentAnchor.getLocationOnScreen(location);

        Rect anchorRect = new Rect(location[0], location[1], location[0] + currentAnchor.getWidth(), location[1]
                + currentAnchor.getHeight());

        //createActionList();

        currentAnchor.getGlobalVisibleRect(anchorRect);

        mRootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mRootView.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        int rootWidth 		= mRootView.getMeasuredWidth();
        int rootHeight 		= mRootView.getMeasuredHeight();
        int screenWidth 	= mWindowManager.getDefaultDisplay().getWidth();

        int screenHeight 	= mWindowManager.getDefaultDisplay().getHeight();

        //automatically get X coord of popup (top left)
        boolean isShowCenter=false;
        if ((anchorRect.left + rootWidth) > screenWidth)
        {
            xPos = anchorRect.left - (rootWidth - currentAnchor.getWidth())-mDistance;
        }
        else
        {
            if (currentAnchor.getWidth() > rootWidth)
            {
                isShowCenter=true;
                xPos = anchorRect.centerX() - (rootWidth / 2);
            }
            else
            {
                xPos = anchorRect.left-mDistance;
            }
        }

        int dyTop = anchorRect.top;
        int dyBottom = screenHeight - anchorRect.bottom;

        boolean onTop = (dyTop > dyBottom) ? true : false;

        if ((!mIsForceBottom)&&onTop)
        {
            if (rootHeight > dyTop)
            {
                yPos = 15;
                ViewGroup.LayoutParams l = scroller.getLayoutParams();//.getLayoutParams();
                l.height = dyTop - currentAnchor.getHeight();
            }
            else
            {
                yPos = anchorRect.top - rootHeight;
            }
        }
        else
        {
            yPos = anchorRect.bottom;

            if (rootHeight > dyBottom)
            {
                ViewGroup.LayoutParams l = scroller.getLayoutParams();
                l.height = dyBottom;
            }
        }

        // showArrow(((onTop) ? Resource.Id.arrow_down : Resource.Id.arrow_up), anchorRect.CenterX() - xPos);

        // SetAnimationStyle(screenWidth, anchorRect.CenterX(), onTop);
        // setAnimationStyle(screenWidth, anchorRect.CenterX(), onTop);

        mWindow.setAnimationStyle(animStyle);
        curx=mIsForceBottom ?  xPos+rootWidth/2:xPos;
        cury = yPos;
       /* if(isShowCenter){
            mWindow.showAsDropDown(currentAnchor,curx,yPos);

        }else*/ {
            mWindow.showAtLocation(currentAnchor, Gravity.NO_GRAVITY, curx, yPos);//xPos
        }

    }


    public void popupAtPoint(View currentAnchor, int pointX, int anchorWidth, boolean needPre) {
        if(!ViewCompat.isAttachedToWindow(currentAnchor)){
            return;
        }
        if (needPre) preShow();
        int xPos, yPos = 0;

        int[] location = new int[2];

        currentAnchor.getLocationOnScreen(location);
        location[0] =location[0]+ pointX;

        Rect anchorRect = new Rect(location[0], location[1], location[0] + anchorWidth, location[1]
                + currentAnchor.getHeight());

        mRootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mRootView.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        int rootWidth = mRootView.getMeasuredWidth();
        int rootHeight = mRootView.getMeasuredHeight();

        int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
        int screenHeight = mWindowManager.getDefaultDisplay().getHeight();

        xPos =location[0];// pointX;
        int dyTop = anchorRect.top;
       // int dyBottom = screenHeight - anchorRect.bottom;

        boolean onTop = true;
        if (onTop) {
            if (rootHeight > dyTop) {
                yPos = 15;
                ViewGroup.LayoutParams l = scroller.getLayoutParams();//.getLayoutParams();
                l.height = dyTop - currentAnchor.getHeight();
            } else {
                yPos = anchorRect.top - rootHeight;
            }
        }

        mWindow.setAnimationStyle(animStyle);
        curx =  xPos-rootWidth/2;
        cury = yPos;
        mWindow.showAtLocation(currentAnchor, Gravity.NO_GRAVITY, curx, yPos);//xPos
    }

}
