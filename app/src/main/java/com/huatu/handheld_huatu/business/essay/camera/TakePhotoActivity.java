package com.huatu.handheld_huatu.business.essay.camera;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baijiayun.glide.Glide;
import com.baijiayun.glide.load.DecodeFormat;
import com.baijiayun.glide.load.engine.DiskCacheStrategy;
import com.baijiayun.glide.request.RequestOptions;
import com.baijiayun.glide.request.transition.Transition;
import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.essay.bean.UpLoadEssayData;
import com.huatu.handheld_huatu.business.essay.camera.cropper.CropImageView;
import com.huatu.handheld_huatu.business.essay.camera.views.CameraPreview;
import com.huatu.handheld_huatu.business.essay.camera.views.FocusView;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.FileUtil;
import com.huatu.handheld_huatu.utils.LogUtils;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.utils.SpUtils;
import com.huatu.handheld_huatu.utils.ToastUtils;
import com.huatu.handheld_huatu.view.CustomDialog;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ht on 2017/12/14.
 */

public class TakePhotoActivity extends Activity implements CameraPreview.OnCameraStatusListener, SensorEventListener {

    private CompositeSubscription compositeSubscription;
    private CustomDialog mDailyDialog;
    private static final String TAG = "TakePhotoActivity";
    public static final Uri IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    public static final String PATH = Environment.getExternalStorageDirectory().toString() + "/ShenLunPiGai/";

    CameraPreview mCameraPreview;
    CropImageView mCropImageView;
    RelativeLayout mTakePhotoLayout;
    LinearLayout mCropperLayout;

    private String mData;
    private static final int TIME_OUT = 30 * 1000; // 超时时间
    private static final String CHARSET = "utf-8"; // 设置编码
    //    private String uploadURL = "http://39.106.163.36:9999/upload";
    private String uploadURL = "http://hw.htexam.com/upload";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置横屏
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // 设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_take_phote);

        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);

        // Initialize components of the app
        mCropImageView = (CropImageView) findViewById(R.id.CropImageView);
        mCameraPreview = (CameraPreview) findViewById(R.id.cameraPreview);
        FocusView focusView = (FocusView) findViewById(R.id.view_focus);
        mTakePhotoLayout = (RelativeLayout) findViewById(R.id.take_photo_layout);
        mCropperLayout = (LinearLayout) findViewById(R.id.cropper_layout);

        mCameraPreview.setFocusView(focusView);
        mCameraPreview.setOnCameraStatusListener(this);
        mCropImageView.setGuidelines(2);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }


    boolean isRotated = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (!isRotated) {
            TextView hint_tv = (TextView) findViewById(R.id.hint);
//            ObjectAnimator animator = ObjectAnimator.ofFloat(hint_tv, "rotation", 0f, 0f);
//            animator.setStartDelay(800);
//            animator.setDuration(1000);
//            animator.setInterpolator(new LinearInterpolator());
//            animator.start();
            View view = findViewById(R.id.crop_hint);
//            AnimatorSet animSet = new AnimatorSet();
//            ObjectAnimator animator1 = ObjectAnimator.ofFloat(view, "rotation", 0f, 0f);
//            ObjectAnimator moveIn = ObjectAnimator.ofFloat(view, "translationX", 0f, -50f);
//            animSet.play(animator1).before(moveIn);
//            animSet.setDuration(10);
//            animSet.start();
            isRotated = true;
        }
        mSensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        LogUtils.e(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

    public void takePhoto(View view) {
        if (mCameraPreview != null) {
            mCameraPreview.takePicture();
        }
    }

    public void close(View view) {
        this.onBackPressed();
    }

    /**
     * 关闭截图界面
     *
     * @param view
     */
    public void closeCropper(View view) {
        showTakePhotoLayout();
    }

    /**
     * 开始截图，并保存图片
     *
     * @param view
     */
    public void startCropper(View view) {
        if(!mCropImageView.hasBitmap()) {
            return;
        }
        //获取截图
        CropperImage cropperImage = mCropImageView.getCroppedImage();
        LogUtils.e(TAG, cropperImage.getX() + "," + cropperImage.getY());
        LogUtils.e(TAG, cropperImage.getWidth() + "," + cropperImage.getHeight());
//        Bitmap bitmap = Utils.rotate(cropperImage.getBitmap(), 0);
        final Bitmap photo = cropperImage.getBitmap();
        // 系统时间
        long dateTaken = System.currentTimeMillis();
////        // 图像名称    ("yyyy-MM-dd  HH:mm:ss");
        String filename = dateTaken + ".jpg";
        //String filename = DateFormat.format("yyyyMMddkk.mm.ss", dateTaken).toString() + ".jpg";
        Uri uri = insertImage(getContentResolver(), filename, dateTaken, PATH, filename, photo, null);
//        cropperImage.getBitmap().recycle();
//        cropperImage.setBitmap(null);
        File file = null;
        try {
            file = new File(PATH, filename);
            if (!FileUtil.isFileExist(PATH + filename)) {
                ToastUtils.showEssayToast("裁剪失败了,请重新拍照试试");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (SpUtils.getUpdatePhotoAnswerType() == 1) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
            showLoadingDialog();

            final File finalFile = file;
            ServiceProvider.sendEssayPicture(compositeSubscription, body, 0, new NetResponse() {
                @Override
                public void onSuccess(BaseResponseModel model) {
                    super.onSuccess(model);
                    dismissLoadingDialog();
                    if (model != null && model.data != null) {
                        UpLoadEssayData upLoadData = (UpLoadEssayData) model.data;
                        if (upLoadData != null) {
                            ToastUtils.showEssayToast("上传成功");
                            mData = upLoadData.content;
                            Intent mIntent = new Intent();
                            mIntent.putExtra("mPhotoData", mData);
                            // 设置结果，并进行传送
                            setResult(100861, mIntent);
                            finalFile.delete();
                            TakePhotoActivity.this.finish();
                        }
                    } else {
                        ToastUtils.showEssayToast("上传失败");
                    }
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    dismissLoadingDialog();
                    ToastUtils.showEssayToast("上传失败,请稍后再试");
                }
            });
        } else {
            showLoadingDialog();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    upLoadBitmap(photo);

                }
            }).start();
        }
    }

    private void upLoadBitmap(Bitmap bitmap) {
        String result = null;
        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型
        try {
            URL url = new URL(uploadURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("Charset", CHARSET); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            if (bitmap != null) {
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                long dateTaken = System.currentTimeMillis();
                // 图像名称
//                String filename = DateFormat.format("yyyy-MM-dd_kk.mm.ss", dateTaken)
//                .toString() + ".jpg";
                String filename = "android_" + SpUtils.getUid() + "_" + dateTaken + ".jpg";
                LogUtils.d(TAG, "updata file name is : " + filename);
                sb.append("Content-Disposition: form-data; name=\"fileToUpload\"; filename=\""
                        + filename + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] datas = baos.toByteArray();
                dos.write(datas, 0, datas.length);
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                int res = conn.getResponseCode();
                LogUtils.e(TAG, "response code:" + res);
                // if(res==200)
                // {
                LogUtils.e(TAG, "request success");
                InputStream input = conn.getInputStream();
                StringBuffer sb1 = new StringBuffer();
                int ss;
                while ((ss = input.read()) != -1) {
                    sb1.append((char) ss);
                }
                result = sb1.toString();
                LogUtils.v(TAG, "result : " + result);
                String value = new String(result.getBytes("iso8859-1"), "UTF-8");
                LogUtils.d(TAG, "value : " + value);
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("FILENAME", filename);
                msg.setData(bundle);
                msg.what = 121;
                msg.obj = value;
                myHandler.sendMessage(msg);
                conn.disconnect();
            }
        } catch (MalformedURLException e) {
            Message msg = new Message();
            msg.what = 123;
            myHandler.sendMessage(msg);
            e.printStackTrace();

        } catch (IOException e) {
            Message msg = new Message();
            msg.what = 124;
            myHandler.sendMessage(msg);
            e.printStackTrace();
        }
    }

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 121:
                    dismissLoadingDialog();
                    String outCome = (String) msg.obj;
                    String mFileName = (String) msg.getData().get("FILENAME");
                    Intent mIntent = new Intent();
                    mIntent.putExtra("FILENAME", mFileName);
                    mIntent.putExtra("mPhotoData", outCome);
                    setResult(100861, mIntent);
                    finish();
                    break;
                case 123:
                    dismissLoadingDialog();
                    ToastUtils.showEssayToast("解析识别失败");
                    break;
                case 124:
                    dismissLoadingDialog();
                    ToastUtils.showEssayToast("io识别失败");
                    break;
            }
            super.handleMessage(msg);
        }
    };



    private final RequestOptions mUploadlocalOption =
            new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).format(DecodeFormat.PREFER_RGB_565)
                    .skipMemoryCache(true);

    /**
     * 拍照成功后回调
     * 存储图片并显示截图界面
     *
     * @param data
     */
    @Override
    public void onCameraStopped(byte[] data) {
        LogUtils.i("TAG", "==onCameraStopped==");
        Glide.with(this).asBitmap().load(data).apply(mUploadlocalOption).into(new SimpleViewTargetV2<Bitmap>(mCropImageView) {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                mCropImageView.setImageBitmap(resource);
            }
        });
        showCropperLayout();
    }

    /**
     * 存储图像并将信息添加入媒体数据库
     */
    private Uri insertImage(ContentResolver cr, String name, long dateTaken, String directory, String filename, Bitmap source, byte[] jpegData) {
        OutputStream outputStream = null;
        String filePath = directory + filename;
        try {
            File dir = new File(directory);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(directory, filename);
            if (file.createNewFile()) {
                outputStream = new FileOutputStream(file);
                if (source != null) {
                    source.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                } else {
                    outputStream.write(jpegData);
                }
            }
        } catch (FileNotFoundException e) {
            LogUtils.e(TAG, e.getMessage());
            return null;
        } catch (IOException e) {
            LogUtils.e(TAG, e.getMessage());
            return null;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Throwable t) {
                }
            }
        }
        ContentValues values = new ContentValues(7);
        values.put(MediaStore.Images.Media.TITLE, name);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
        values.put(MediaStore.Images.Media.DATE_TAKEN, dateTaken);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATA, filePath);
        return cr.insert(IMAGE_URI, values);
    }

    private void showTakePhotoLayout() {
        mTakePhotoLayout.setVisibility(View.VISIBLE);
        mCropperLayout.setVisibility(View.GONE);
        mCameraPreview.resume();   //继续启动摄像头
    }

    private void showCropperLayout() {
        mTakePhotoLayout.setVisibility(View.GONE);
        mCropperLayout.setVisibility(View.VISIBLE);
    }


    private float mLastX = 0;
    private float mLastY = 0;
    private float mLastZ = 0;
    private boolean mInitialized = false;
    private SensorManager mSensorManager;
    private Sensor mAccel;

    @Override
    public void onSensorChanged(SensorEvent event) {

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        if (!mInitialized) {
            mLastX = x;
            mLastY = y;
            mLastZ = z;
            mInitialized = true;
        }
        float deltaX = Math.abs(mLastX - x);
        float deltaY = Math.abs(mLastY - y);
        float deltaZ = Math.abs(mLastZ - z);

        if (deltaX > 0.8 || deltaY > 0.8 || deltaZ > 0.8) {
            mCameraPreview.setFocus();
        }
        mLastX = x;
        mLastY = y;
        mLastZ = z;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (null != mCameraPreview) {
            mCameraPreview.setOnCameraStatusListener(null);
            mCameraPreview.stop();
            mCameraPreview.release();
        }


        RxUtils.unsubscribeIfNotNull(compositeSubscription);
    }

    private void showLoadingDialog() {
        if (mDailyDialog == null) {
            mDailyDialog = new CustomDialog(this, R.layout.dialog_type2);
            TextView tv = (TextView) mDailyDialog.mContentView.findViewById(R.id.tv_notify_message);
            tv.setText("上传中");
        }
        mDailyDialog.show();

    }

    public void dismissLoadingDialog() {
        try {
            if (mDailyDialog != null) {
                mDailyDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("AlertDialog  Exception:", e.getMessage() + "");
        }

    }

}
