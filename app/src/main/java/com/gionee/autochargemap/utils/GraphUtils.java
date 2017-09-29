package com.gionee.autochargemap.utils;

/*
 *  @项目名：  AutoChargeMap 
 *  @包名：    com.gionee.autochargemap.utils
 *  @文件名:   TableUtils
 *  @创建者:   gionee
 *  @创建时间:  2017/3/28 11:01
 *  @描述：    绘制曲线图
 */


import android.graphics.Color;

import com.gionee.autochargemap.bean.DataBean;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class GraphUtils {
    private static final int TYPE_CURRENT = 0;//电流
    private static final int TYPE_VOLTAGE = 1;//电压
    private static final int TYPE_TEMP    = 2;//温度
    private static   int[]    mColors = new int[]{
            Color.parseColor("#5abdfc"),    //蓝色
            Color.parseColor("#eb73f6")    //紫色
    };
    /**
     * 设置样式
     */
    public static void setLineChartStyle(LineChart chart, ArrayList<DataBean> list, int tableName,String details){
        initChartView(chart);
        //设置数据
        ArrayList<Entry> yVals = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            DataBean bean = list.get(i);
            Log.i(bean.toString());
            String str    = null;
            switch (tableName){
                case TYPE_CURRENT:
                    str = bean.getCurrent();
                    break;
                case TYPE_VOLTAGE:
                    str = bean.getVoltage();
                    break;
                case TYPE_TEMP:
                    str = bean.getTemp();
                    break;
            }
            yVals.add(new Entry(i*5/60f,Float.parseFloat(str)));
        }
        //表名
        addDataSet(yVals, Constants.TABLENAME[tableName]+details,chart);//y轴上的说明

    }
    private static void initChartView(LineChart chart) {
        chart.setDragEnabled(false);// 是否可以拖拽
        chart.setDrawGridBackground(false);//
        chart.setDescription(null);    //右下角说明文字
        chart.setDrawBorders(true);    //四周是不是有边框
        chart.setBorderWidth(0.5f);
        chart.setBorderColor(Color.parseColor("#b3b3b3"));    //边框颜色，默认黑色？
        //        mChart.setVisibleXRangeMaximum(4);

        // enable touch gestures
        chart.setTouchEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        //禁止x轴y轴同时进行缩放
        chart.setPinchZoom(false);
        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        //控制轴上的坐标绘制在什么地方 上边下边左边右边
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);    //x轴是在上边显示还是显示在下边
        xAxis.enableGridDashedLine(10f, 10f, 0f);    //背景用虚线表格来绘制  给整成虚线
        xAxis.setAxisMinimum(0f);//设置轴的最小值。这样设置将不会根据提供的数据自动计算。
        xAxis.setGranularityEnabled(true);    //粒度
        xAxis.setGranularity(1f);    //缩放的时候有用，比如放大的时候，我不想把横轴的月份再细分
        xAxis.setDrawGridLines(false);//隐藏x轴线
        //        xAxis.setAxisLineWidth(0f);    //设置坐标轴那条线的宽度
        xAxis.setDrawAxisLine(false);    //是否显示坐标轴那条轴
        xAxis.setDrawLabels(true);    //是不是显示轴上的刻度
        xAxis.setLabelCount(8);    //强制有多少个刻度
        xAxis.setTextColor(Color.parseColor("#b3b3b3"));
        //隐藏右侧坐标轴
        chart.getAxisRight().setEnabled(false);
        chart.setData(new LineData());

        //图标的下边的指示块  图例
        Legend l = chart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setXEntrySpace(40);
    }


    /**
     * 根据数据集合，动态构建DataSet，来绘制界面
     */
    private static void addDataSet(ArrayList<Entry> entryList, String dataSetName, LineChart chart) {

        LineData data = chart.getData();
        if (data != null) {
            int count = data.getDataSetCount();

            LineDataSet set = new LineDataSet(entryList, dataSetName);
            set.setLineWidth(1.5f);
            set.setCircleRadius(3.5f);
            int color = mColors[count % mColors.length];
            set.setMode(LineDataSet.Mode.LINEAR);//平滑的线
            set.setCircleColor(color);
            set.setHighLightColor(color);
            set.setValueTextSize(10f);
            set.setValueTextColor(color);
            set.enableDashedHighlightLine(10f, 5f, 0f);    //选中某个点的时候高亮显示只是线
            //            set.setDrawFilled(true);     //填充折线图折线和坐标轴之间
            set.setFillColor(color);
            // 不显示坐标点的小圆点
            set.setDrawCircles(false);
            // 不显示坐标点的数据
            set.setDrawValues(false);
            // 不显示定位线
            set.setHighlightEnabled(false);

            // set.setDrawVerticalHighlightIndicator(false);//取消纵向辅助线
            set.setDrawHorizontalHighlightIndicator(false);//取消横向辅助线

            data.addDataSet(set);
            data.notifyDataChanged();
            chart.notifyDataSetChanged();
            //设置x轴最大可显示的数值
            chart.setVisibleXRangeMaximum(2000);
            chart.invalidate();
        }
    }


}
