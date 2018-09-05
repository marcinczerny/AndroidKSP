package net.client.TabLayout.TabView;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import net.client.Database.DaoSession;
import net.client.Database.Measure;
import net.client.MainActivity;
import net.client.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GraphFragment extends Fragment {

    LineGraphSeries<DataPoint> seriesHumidity;
    LineGraphSeries<DataPoint> seriesTemperature;
    LineGraphSeries<DataPoint> seriesPressure;
    GraphView graph;
    ArrayList<Measure> data;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_graph, container, false);

        graph = (GraphView) view.findViewById(R.id.graph_view);

        refreshData();

        return view;
    }

    @Override
    public void onResume() {
        refreshData();
        super.onResume();
    }

    private void refreshData() {

        seriesHumidity = new LineGraphSeries<DataPoint>();
        seriesTemperature = new LineGraphSeries<DataPoint>();
        seriesPressure = new LineGraphSeries<DataPoint>();

        // styling series
        seriesHumidity.setTitle(getString(R.string.humidity));
        seriesHumidity.setColor(Color.GREEN);
        seriesHumidity.setDrawDataPoints(true);
        seriesHumidity.setDataPointsRadius(10);
        seriesHumidity.setThickness(10);

        // styling series
        seriesTemperature.setTitle(getString(R.string.temperature));
        seriesTemperature.setColor(Color.BLUE);
        seriesTemperature.setDrawDataPoints(true);
        seriesTemperature.setDataPointsRadius(10);
        seriesTemperature.setThickness(10);

        // styling series
        seriesPressure.setTitle(getString(R.string.pressure));
        seriesPressure.setColor(Color.RED);
        seriesPressure.setDrawDataPoints(true);
        seriesPressure.setDataPointsRadius(10);
        seriesPressure.setThickness(10);

        data = new ArrayList<>();
        data.clear();

        graph.removeAllSeries();

        DaoSession daoSession = ((MainActivity) getContext()).getDatabaseHelper().getDaoSession();
        for (Measure measure : daoSession.getMeasureDao().loadAll()) {
            data.add(new Measure(measure.getId(), measure.getDate(), measure.getHumidity(), measure.getPressure(), measure.getTemperature()));
        }

        for (Measure measure : data) {

            seriesHumidity.appendData(new DataPoint(measure.getDate(), measure.getHumidity()), true, data.size(), true);
            seriesTemperature.appendData(new DataPoint(measure.getDate(), measure.getPressure()), true, data.size(), true);
            seriesPressure.appendData(new DataPoint(measure.getDate(), measure.getTemperature()), true, data.size(), true);
        }

        graph.addSeries(seriesHumidity);
        graph.addSeries(seriesTemperature);
        graph.addSeries(seriesPressure);

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity(), formatter));

        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setScrollableY(true);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);
        graph.getGridLabelRenderer().setHorizontalLabelsAngle(90);
    }

}
