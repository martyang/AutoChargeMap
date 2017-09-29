package com.gionee.autochargemap.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.gionee.autochargemap.R;
import com.gionee.autochargemap.bean.DataBean;
import com.gionee.autochargemap.utils.Constants;
import com.gionee.autochargemap.utils.DensityUtils;
import com.gionee.autochargemap.utils.JxlUtil;
import com.gionee.autochargemap.utils.Log;
import com.gionee.autochargemap.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 展示结果
 */
public class ResultActivity
        extends AppCompatActivity
        implements View.OnClickListener, AdapterView.OnItemClickListener
{
    private ViewPager         mPager;
    private LinearLayout      mLLPrint;// 装没有被选择的小圆点的容器
    private RelativeLayout    mRlPoint;// 装所有点的容器
    private View              mPointSelected;// 被选择的小圆点
    private Button            mBtnSelect;
    private List<View>        mData;
    private PopupWindow       mPopupWindow;
    private TextView          mTvEmpty;
    private ListView          mListView;
    private ArrayList<String> mFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //		requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        initView();
        initData();

    }

    private void initView() {
        setContentView(R.layout.activity_result);
        //设置一个返回键
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mPager = (ViewPager) findViewById(R.id.result_viewpager);
        mLLPrint = (LinearLayout) findViewById(R.id.guide_point_container);
        mPointSelected = findViewById(R.id.guide_point_selected);
        mRlPoint = (RelativeLayout) findViewById(R.id.guide_point_bigcontainer);
        mBtnSelect = (Button) findViewById(R.id.result_btn_select);
        mBtnSelect.setOnClickListener(this);
        mTvEmpty = (TextView) findViewById(R.id.result_tv_empty);
        for (int i = 0; i < 3; i++) {
            // 动态的加载点
            View point = new View(this);
            point.setBackgroundResource(R.drawable.point_normal);
            int px = DensityUtils.dp2px(this, 10);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(px, px);
            if (i != 0) {
                params.leftMargin = DensityUtils.dp2px(this, 10);
            }
            mLLPrint.addView(point, params);
        }
    }

    private void initData() {
        mFiles = new ArrayList<>();
        File[] files = new File(Constants.IMAGE_PATH).listFiles();
        for (int i = 0; i < files.length; i++) {
            mFiles.add(files[i].getName());
        }
        if (mFiles.size() == 0) {
            mTvEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void loadingData(String path) {
        mData = new ArrayList<>();
        File   file  = new File(Constants.IMAGE_PATH + File.separator + path);
        File[] files = file.listFiles();
        Log.i("files.length:"+files.length);
        if (files.length < 3) {
            File                f    = new File(file, "数据记录.xls");
            ArrayList<DataBean> list = Utils.log2List(file.getAbsolutePath() + File.separator + "log.txt");
            JxlUtil.writeAppinfoData(f, list);
            showTishi("由于关机或是强退apk的原因,导致结果图没有生成,请到'内部存储器/AutoChargeMap/ChargeImage'目录下查看表格结果");
            return;
        }
        for (int i = 0; i < files.length; i++) {

            if (!files[i].getName()
                         .endsWith(".jpg"))
            {continue;}
            ImageView view = image2Bitmap(files[i].getAbsolutePath());
            view.setScaleType(ImageView.ScaleType.FIT_XY);
            mData.add(view);

        }
        mPager.setAdapter(new GuideAdapter());
        mPager.addOnPageChangeListener(new GuideStateChanged());
        mRlPoint.setVisibility(View.VISIBLE);
    }

    public void showTishi(String s) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("提示");
        dialog.setMessage(s);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setPositiveButton("确定", null);
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        if (view == mBtnSelect) {
            initData();
            if (mFiles.size() == 0) {
                mTvEmpty.setVisibility(View.VISIBLE);//为空
                mRlPoint.setVisibility(View.INVISIBLE);//隐藏小圆点
                mPager.setVisibility(View.INVISIBLE);//隐藏滑动页
            } else {
                mTvEmpty.setVisibility(View.INVISIBLE);
                mPager.setVisibility(View.VISIBLE);
                showPopupWindow(view);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        loadingData(mFiles.get(i));
        mPopupWindow.dismiss();
    }

    /**
     * viewpager的滑动事件
     */
    private class GuideAdapter
            extends PagerAdapter
    {
        @Override
        public int getCount() {
            if (mData != null) {
                return mData.size();
            }
            return 0;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mData.get(position);// 拿到
            container.addView(view);// 添加
            return view;// 返回
        }
    }

    /**
     * pager页面发生改变
     *
     */
    private class GuideStateChanged
            implements OnPageChangeListener
    {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        // 页面滚动
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            int pointplace = DensityUtils.dp2px(ResultActivity.this, 10) * 2;// 两点之间的距离
            int place      = (int) (arg1 * pointplace + pointplace * arg0 + 0.5f);
            LayoutParams params = (LayoutParams) mPointSelected.getLayoutParams();
            params.leftMargin = place;
            mPointSelected.setLayoutParams(params);
        }

        @Override
        // 页面改变
        public void onPageSelected(int arg0) {

        }
    }

    /**
     * 图片转成imageView
     */
    private ImageView image2Bitmap(String path) {
        ImageView             jpgView = new ImageView(this);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bm = BitmapFactory.decodeFile(path, options);
        jpgView.setImageBitmap(bm);
        return jpgView;
    }


    /**
     * 打开popupwindow
     */
    public void showPopupWindow(View parent) {
        // 构建一个popupwindow的布局
        View popupView = ResultActivity.this.getLayoutInflater()
                                            .inflate(R.layout.view_popup, null);

        // 为了演示效果，简单的设置了一些数据，实际中大家自己设置数据即可，相信大家都会。
        mListView = (ListView) popupView.findViewById(R.id.group_list);
        mListView.setAdapter(new ArrayAdapter<String>(ResultActivity.this,
                                                      android.R.layout.simple_list_item_1,
                                                      mFiles));

        //  创建PopupWindow对象，指定宽度和高度
        mPopupWindow = new PopupWindow(popupView, 500, 600);
        //		mPopupWindow.setWidth(LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置动画
        mPopupWindow.setAnimationStyle(R.style.popup_window_anim);
        //  设置背景颜色
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.argb(33, 255, 255, 255)));
        //  设置可以获取焦点
        mPopupWindow.setFocusable(true);
        //  设置可以触摸弹出框以外的区域
        mPopupWindow.setOutsideTouchable(true);
        // 更新popupwindow的状态
        mPopupWindow.update();
        // 以下拉的方式显示，并且可以设置显示的位置
        //		mPopupWindow.showAsDropDown(mBtnSelect, 0, 20);
        mPopupWindow.showAtLocation((View) parent.getParent(),
                                    Gravity.CENTER | Gravity.CENTER_HORIZONTAL,
                                    0,
                                    0);

        mListView.setOnItemClickListener(this);

        //产生背景变暗效果
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.4f;
        getWindow().setAttributes(lp);

        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            //在dismiss中恢复透明度
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
