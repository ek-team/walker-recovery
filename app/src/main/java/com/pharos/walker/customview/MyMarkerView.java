package com.pharos.walker.customview;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.pharos.walker.R;
import java.text.DecimalFormat;
import java.text.MessageFormat;

/**
 * Created by zhanglun on 2021/5/19
 * Describe:
 */
public class MyMarkerView extends MarkerView {
    private TextView tvContent;
    private String unit;
    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public MyMarkerView(Context context, int layoutResource,String unit) {
        super(context, layoutResource);
        tvContent = findViewById(R.id.tvContent);
        this.unit = unit;
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        tvContent.setText(MessageFormat.format("{0}{1}", decimalFormat.format(e.getY()),unit));
        super.refreshContent(e, highlight);
    }
}
