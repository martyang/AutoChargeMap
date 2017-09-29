package com.gionee.autochargemap.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.gionee.autochargemap.R;
import com.gionee.autochargemap.adapter.ViewPagerAdapter;
import com.gionee.autochargemap.bean.DataBean;
import com.gionee.autochargemap.utils.ChargeUtils;
import com.gionee.autochargemap.utils.Condition;
import com.gionee.autochargemap.utils.Constants;
import com.gionee.autochargemap.utils.FileHelper;
import com.gionee.autochargemap.utils.FileUtils;
import com.gionee.autochargemap.utils.GraphUtils;
import com.gionee.autochargemap.utils.ImageUtils;
import com.gionee.autochargemap.utils.JxlUtil;
import com.gionee.autochargemap.utils.Log;
import com.gionee.autochargemap.utils.Preference;
import com.gionee.autochargemap.utils.Utils;
import com.github.mikephil.charting.charts.LineChart;

import java.io.File;
import java.util.ArrayList;

public class MainActivity
        extends BaseActivity
        implements View.OnClickListener
{
    private        ViewPager  mViewPager;
    private        Button     mBtnStart;
    private        TextView   mTvShow;
    private        TextView   mTvTestTime;
    private        LineChart  mLineChart;
    private Switch mSwitch;
    private  boolean    mBatteryStatus;//电池是否为冲电状态
    public   boolean    sBatteryFull;//电池是否充满
    private  boolean    mIsTest;//是否在测试
    private        RecordTask mRecordTask;//异步任务

    private static boolean sIsFirstChart = true;//记录初始电量标记
    private static String           sNowBattery;//当前的电量
    private        ArrayList<View>  mViewList;//
    private        ViewPagerAdapter mAdapter;
    private boolean mFullIsMonitor;
    @Override
    protected void initViews() {
        setContentView(R.layout.activity_main);
        mBtnStart = (Button) findViewById(R.id.main_btn_start);
        mTvShow = (TextView) findViewById(R.id.main_tv_show);
        mBtnStart.setOnClickListener(this);
        mViewPager = (ViewPager) findViewById(R.id.main_viewPager);
        mLineChart = (LineChart) findViewById(R.id.main_chart);
        mTvTestTime = (TextView) findViewById(R.id.main_testTime);
        mSwitch = (Switch) findViewById(R.id.main_switch);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mFullIsMonitor = b;
            }
        });
    }


    @Override
    protected void initDatas() {
        sNowBattery = "";
        Condition.addToWhileList(this, "com.gionee.autochargemap");
        FileUtils.createDir(Constants.DATA_PATH);
        FileUtils.createDir(Constants.LOG_PATH);
        FileUtils.createDir(Constants.IMAGE_PATH);
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mIntentReceiver, mIntentFilter);
        mViewList = new ArrayList<>();

    }

    @Override
    public void onClick(View view) {
        if(!fastClick()){return;}
        if (view == mBtnStart) {
            if (!mIsTest) {
                startTest();
            } else {
                stopTest();
            }
        }
    }

    private void startTest() {
        if (!mBatteryStatus&& !mFullIsMonitor) {
        Toast.makeText(this, "请连接电源后再进行测试...", Toast.LENGTH_SHORT)
             .show();
        return;
        }
        if (sNowBattery.equals("100%")&&!mFullIsMonitor) {
            Toast.makeText(this, "已经是满电状态...", Toast.LENGTH_SHORT)
                 .show();
            return;
        }
        mViewPager.setVisibility(View.INVISIBLE);
        mTvTestTime.setVisibility(View.INVISIBLE);
        mViewList.clear();
        mBtnStart.setText("停止监控");
        mIsTest = true;
        mRecordTask = new RecordTask();
        mRecordTask.execute();

    }


    private void stopTest() {
        sIsFirstChart = false;
        mIsTest = false;
        mBtnStart.setText("开始监控");
        Preference.putString(this, Constants.STOP_TIME, Utils.getTimeHms());
        Preference.putString(this, Constants.NOW_LEVEL, sNowBattery);
        if (mRecordTask != null && !mRecordTask.isCancelled()) {
            mRecordTask.cancel(true);
        }
        mTvTestTime.setVisibility(View.VISIBLE);
        mTvTestTime.setText(Utils.getLeadTime());
    }


    private class RecordTask
            extends AsyncTask<Void, Integer, Void>
    {
        private ArrayList<DataBean> mList;//数据集合
        File logFile;
        @Override
        protected void onPreExecute() {
            Preference.putString(MainActivity.this, Constants.START_TIME, Utils.getTimeHms());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mList = new ArrayList<>();
            File file = new File(Constants.IMAGE_PATH + File.separator + Preference.getString(
                    MainActivity.this,
                    Constants.START_TIME), "数据记录.xls");
            logFile = new File(Constants.IMAGE_PATH + File.separator + Preference.getString(
                    MainActivity.this,
                    Constants.START_TIME), "log.txt");
            Log.i(file.getAbsolutePath());
            FileUtils.createDir(file.getParent());
            while (true) {
                if (sBatteryFull && !mFullIsMonitor) {
                    JxlUtil.writeAppinfoData(file, mList);
                    return null;
                }
                if (isCancelled()) {
                    JxlUtil.writeAppinfoData(file, mList);
                    return null;
                }
                if (!mBatteryStatus&& !mFullIsMonitor) {
                    JxlUtil.writeAppinfoData(file, mList);
                    publishProgress(0);
                    return null;
                }
                getData();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (values[0] == 0) {
                Toast.makeText(MainActivity.this, "电源已拔出,测试停止...", Toast.LENGTH_LONG)
                     .show();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.i("onPostExecute");
            stopTest();
            createImage(mList);
        }

        @Override
        protected void onCancelled() {
            Log.i("onCancelled");
            super.onCancelled();
            stopTest();
            createImage(mList);
        }

        /**
         * 获取数据
         */
        private void getData() {
            DataBean bean = new DataBean();
            bean.setTime(Utils.getTime());
            bean.setCurrent(ChargeUtils.getCharge(ChargeUtils.ChargeUtils_TYPE_CURRENT));
            bean.setVoltage(ChargeUtils.getCharge(ChargeUtils.ChargeUtils_TYPE_VOLTAGE));
            bean.setTemp(ChargeUtils.getCharge(ChargeUtils.ChargeUtils_TYPE_TEMP));
            bean.setNewEnergy(sNowBattery);
            mList.add(bean);
            FileHelper.write(logFile.getAbsolutePath(),bean.toString(),true);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /**
         * 生成图片
         */
        private void createImage(ArrayList<DataBean> list) {
            String details = " 时间:" + Preference.getString(MainActivity.this,
                                                           Constants.START_TIME) + "->" + Preference.getString(
                    MainActivity.this,
                    Constants.FILL_TIME/*STOP_TIME*/) + "充电:" + Preference.getString(MainActivity.this,
                                                                        Constants.START_LEVEL) + "->" + sNowBattery;
            for (int i = 0; i < 3; i++) {
                LineChart lineChart = new LineChart(MainActivity.this);
                GraphUtils.setLineChartStyle(lineChart, list, i, details);
                GraphUtils.setLineChartStyle(mLineChart, list, i, details);
                ImageUtils.viewSaveToImage(mLineChart,
                                           Constants.IMAGE_PATH + File.separator + Preference.getString(
                                                   MainActivity.this,
                                                   Constants.START_TIME),
                                           Constants.TABLENAME[i] + ".jpg");//绘制图
                mViewList.add(lineChart);
            }
            mAdapter = new ViewPagerAdapter(mViewList);
            mViewPager.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            mViewPager.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 监听电量变化
     */
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //要看看是不是我们要处理的消息
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                if (intent.getIntExtra("status",
                                       BatteryManager.BATTERY_STATUS_UNKNOWN) == BatteryManager.BATTERY_STATUS_CHARGING)
                {
//                    Log.i("充电状态");
                    mBatteryStatus = true;
                } else {
//                    Log.i("没有充电");
                    mBatteryStatus = false;
                }
                int level = intent.getIntExtra("level", 0);
                sNowBattery = level + "%";
                mTvShow.setText("当前电量：" + level + "%");
                if (sIsFirstChart) {
                    Preference.putString(context, Constants.START_LEVEL, level + "%");
                    sIsFirstChart = false;
                }

                if (level == 100) {
//                    Log.i("已经充满");
                    sBatteryFull = true;
                } else {
//                    Log.i("没有充满");
                    sBatteryFull = false;
                    //满电之前所有的时间点
                    Preference.putString(context, Constants.FILL_TIME, Utils.getTimeHms());
                }
            }

        }
    };

    @Override
    protected void onDestroy() {
        Condition.removeFromWhileList(this, "com.gionee.autochargemap");
        unregisterReceiver(mIntentReceiver);
        if (mIsTest) {
            stopTest();
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_result:
                startActivity(new Intent(this, ResultActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected String setAuther() {
        return "彭北林";
    }
    /**
     *记录上次点击按钮的时间
     */
    private long lastClickTime;
    /**
     * 防止快速点击
     */
    private boolean fastClick() {
        if (System.currentTimeMillis() - lastClickTime <= 500) {
            return false;
        }
        lastClickTime = System.currentTimeMillis();
        return true;
    }
}
