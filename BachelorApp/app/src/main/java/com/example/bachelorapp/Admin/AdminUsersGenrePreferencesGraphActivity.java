package com.example.bachelorapp.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.charts.TreeMap;
import com.anychart.chart.common.dataentry.TreeDataEntry;
import com.anychart.core.ui.Title;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.anychart.enums.Orientation;
import com.anychart.enums.SelectionMode;
import com.anychart.enums.TreeFillingMethod;
import com.example.bachelorapp.Model.Users;
import com.example.bachelorapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdminUsersGenrePreferencesGraphActivity extends AppCompatActivity {

    private static final String TAG = AdminUsersGenrePreferencesGraphActivity.class.getSimpleName();
    HashMap<String, Integer> data = new HashMap<>();
    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users_genre_preferences_graph);

        data = (HashMap<String, Integer>) getIntent().getSerializableExtra("data");


        Log.d(TAG, "onCreate: before");
        Log.d(TAG, "onCreate: " + data.get("romance").toString());



        generateGraph(data);


    }


    private void generateGraph(HashMap<String, Integer> data) {
        AnyChartView chart = findViewById(R.id.chart);
        Pie pie = AnyChart.pie();
//        TreeMap treeMap = AnyChart.treeMap();
        List<DataEntry> dataEntries = new ArrayList<>();
//        dataEntries.add(new TreeDataEntry("Customers' Genre Preferences", null, 0));
        for(Map.Entry<String, Integer> pair : data.entrySet()) {
            dataEntries.add(new ValueDataEntry(pair.getKey(),pair.getValue()));
            Log.d(TAG, "onCreate: " + pair.getKey() + pair.getValue().toString());
        }

        pie.data(dataEntries);
        chart.setChart(pie);
//
//        treeMap.data(dataEntries, TreeFillingMethod.AS_TABLE);
//        Title title = treeMap.title();
//        title.enabled(true);
//        title.useHtml(true);
//        title.padding(0d, 0d, 20d, 0d);
//        title.text("Top Genre Preferences by Number of Customers<br/>' +\n");
//
//        treeMap.colorScale().ranges(new String[]{
//                "{ less: 10 }",
//                "{ from: 10, to: 20 }",
//                "{ from: 20, to: 30 }",
//                "{ from: 30, to: 40 }",
//                "{ from: 40, to: 50 }",
//                "{ greater: 50 }"
//        });
//
//        treeMap.colorScale().colors(new String[]{
//                "#ffee58", "#fbc02d", "#f57f17", "#c0ca33", "#689f38", "#2e7d32"
//        });
//
//        treeMap.padding(10d, 10d, 10d, 20d);
//        treeMap.maxDepth(2d);
//        treeMap.hovered().fill("#bdbdbd", 1d);
//        treeMap.selectionMode(SelectionMode.NONE);
//
//        treeMap.legend().enabled(true);
//        treeMap.legend()
//                .padding(0d, 0d, 0d, 20d)
//                .position(Orientation.RIGHT)
//                .align(Align.TOP)
//                .itemsLayout(LegendLayout.VERTICAL);
//
//        treeMap.labels().useHtml(true);
//        treeMap.labels().fontColor("#212121");
//        treeMap.labels().fontSize(12d);
//        treeMap.labels().format(
//                "function() {\n" +
//                        "      return this.getData('id');\n" +
//                        "    }");
//
//        treeMap.headers().format(
//                "function() {\n" +
//                        "    return this.getData('id');\n" +
//                        "  }");
//
//        treeMap.tooltip()
//                .useHtml(true)
//                .titleFormat("{%id}")
//                .format("function() {\n" +
//                        "      return '<span style=\"color: #bfbfbf\">Number: </span>$' +\n" +
//                        "        anychart.format.number(this.value, {\n" +
//                        "          groupsSeparator: ' '\n" +
//                        "        });\n" +
//                        "    }");

       // chart.setChart(treeMap);


        Log.d(TAG, "onCreate: after");

    }



}