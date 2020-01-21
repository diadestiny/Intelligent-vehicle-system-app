package com.test.xander.carplay.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



import com.test.xander.carplay.R;
import com.test.xander.carplay.Utils.FakeDataUtil;
import com.test.xander.carplay.Data.RecordItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;


public class MonthStsFragment extends Fragment {
    RecordItem[] recordItems;

    //BufferKnife
    private Unbinder unbinder;
    @BindView(R.id.month_sts_chart) ColumnChartView chart;
    @BindView(R.id.month_sts_hp) TextView highPressure;
    @BindView(R.id.month_sts_lp) TextView lowPressure;
    @BindView(R.id.month_sts_hr) TextView heartRate;
    @BindView(R.id.month_sts_temper) TextView temper;
    @BindView(R.id.month_sts_weight) TextView weight;
    @BindView(R.id.selected_time_sts_textview) TextView selected_time_sts;

    public MonthStsFragment() {
        // Required empty public constructor
    }

    public static MonthStsFragment newInstance() {
        MonthStsFragment fragment = new MonthStsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_month_sts, container, false);
        unbinder = ButterKnife.bind(this, view);
        initChart();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initChart(){
        //recordItems= DataBaseKit.getMonRecord();
        recordItems= FakeDataUtil.getMonRecord();
        setInfo(recordItems[2]);
        int numColumns = 3;

        List<Column> columns = new ArrayList<>();
        List<SubcolumnValue> values;
        final int[] colors = {ChartUtils.nextColor(), ChartUtils.nextColor(), ChartUtils.nextColor(), ChartUtils.nextColor(), ChartUtils.nextColor()};
        for (int i = 0; i < numColumns; ++i) {
            values = new ArrayList<>();
            int con=recordItems[i].estimate();
            values.add(new SubcolumnValue(con, colors[con]));
            Column column = new Column(values);
            column.setHasLabels(false);
            column.setHasLabelsOnlyForSelected(false);
            columns.add(column);
        }

        ColumnChartData data = new ColumnChartData(columns);
        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(false);
        ArrayList<AxisValue> axisValuesX = new ArrayList<>();//定义X轴刻度值的数据集合
        ArrayList<AxisValue> axisValuesY = new ArrayList<>();//定义Y轴刻度值的数据集合
        axisValuesY.add(new AxisValue(0).setValue(0).setLabel("无数据"));
        axisValuesY.add(new AxisValue(1).setValue(1).setLabel("正常"));
        axisValuesY.add(new AxisValue(2).setValue(2).setLabel("需注意"));
        axisValuesY.add(new AxisValue(3).setValue(3).setLabel("警告"));

        for (int i = 0; i < numColumns; i++) {
            axisValuesX.add(new AxisValue(i).setValue(i).setLabel(recordItems[i].getTime()==0?"本月":recordItems[i].getTime() + "月前"));
        }

        axisX.setValues(axisValuesX);//为X轴显示的刻度值设置数据集合
        axisY.setValues(axisValuesY);
        axisX.setName("时间");
        axisY.setName("健康状况");
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);
        chart.setColumnChartData(data);

        //解决显示错位的问题
        Viewport viewport = new Viewport(chart.getMaximumViewport());
        viewport.bottom = 0;
        viewport.top = 4;
        chart.setMaximumViewport(viewport);
        chart.setCurrentViewport(viewport);

        chart.setOnValueTouchListener(new ColumnChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
                setInfo(recordItems[columnIndex]);
            }

            @Override
            public void onValueDeselected() {

            }
        });
    }


    private void setInfo(RecordItem recordItem){
        selected_time_sts.setText(recordItem.getTime()==0?"本月":(recordItem.getTime() + "月前"));
        highPressure.setText(""+recordItem.gethBlood());
        lowPressure.setText(""+recordItem.getlBlood());
        heartRate.setText(""+recordItem.getHeartRate());
        temper.setText(""+recordItem.getBody_temp()/10.0);
        weight.setText(""+recordItem.getWeight()/10.0);
    }
}
