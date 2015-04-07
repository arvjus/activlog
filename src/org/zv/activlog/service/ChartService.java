package org.zv.activlog.service;

import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

public class ChartService {
	private Context context;
	
	public ChartService(Context context) {
		this.context = context;
	}
	
	public static class SeriesData {
		public SeriesData(String title, int color, Double [] values) {
			this.title = title;
			this.color = color;
			this.values = values;
		}
		String title;
		int color;
		Double [] values;
	};
	
	public Intent createTimeChartIntent(String chartTitle, String xTitle, String yTitle, List<Date> dates, List<SeriesData> seriesData, String datePattern) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		for (SeriesData sd : seriesData) {
			TimeSeries series = new TimeSeries(sd.title);
			for (int k = 0; k < dates.size(); k++) {
				series.add(dates.get(k), sd.values[k]);
			}
			dataset.addSeries(series);

			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(sd.color);
			r.setPointStyle(PointStyle.POINT);
			renderer.addSeriesRenderer(r);
		}
		
		renderer.setMargins(new int[] { 20, 20, 0, 10});
		renderer.setPointSize(1f);
		renderer.setChartTitle(chartTitle);
		renderer.setXTitle(xTitle);
		renderer.setXLabels(datePattern.length() < 6 ? 10 : 8);
		renderer.setYTitle(yTitle);
		renderer.setYLabels(10);
		renderer.setApplyBackgroundColor(true);
		renderer.setMarginsColor(Color.WHITE);
		renderer.setBackgroundColor(Color.WHITE);
		renderer.setAxesColor(Color.BLACK);
		renderer.setLabelsColor(Color.BLACK);

		return ChartFactory.getTimeChartIntent(context, dataset, renderer, datePattern);
	}
}
