package com.example.theworld.ui.notifications;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.theworld.R;
import com.example.theworld.logic.Logic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import database.ProcessingDatabase;

public class StatsActivity extends Activity {
    SharedPreferences statistics;
    ArrayList<TextView> whatStatViews = new ArrayList<>();
    ArrayList<TextView> theStatViews = new ArrayList<>();

    ArrayList<TextView> whatStatViewsFlag = new ArrayList<>();
    ArrayList<TextView> theStatViewsFlag = new ArrayList<>();
    LinkedHashMap<String,String> dataFlag = new LinkedHashMap<>();
    LinkedHashMap<String,String> dataMap = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_layout);
        addViews();
        setText();

    }

    private LinkedHashMap<String,String> getStats(boolean forMap) {

        dataFlag.put("Number of guesses in 1","0");
        dataFlag.put("Number of guesses in 2","0");
        dataFlag.put("Number of guesses in 3","0");
        dataFlag.put("Number of guesses in 4","0");
        dataFlag.put("Number of guesses in 5","0");
        dataFlag.put("Number of guesses in 6","0");
        dataFlag.put("Number of fails","0");
        dataFlag.put("Win percentage","n/a");

        dataMap.put("Number of guesses in 1","0");
        dataMap.put("Number of guesses in 2","0");
        dataMap.put("Number of guesses in 3","0");
        dataMap.put("Number of guesses in 4","0");
        dataMap.put("Number of guesses in 5","0");
        dataMap.put("Number of guesses in 6","0");
        dataMap.put("Number of fails","0");
        dataMap.put("Win percentage","n/a");

//        String value = "";
//        double fails = 0;
//        double wins = 0;

        SharedPreferences spFlag = getSharedPreferences("stats", Context.MODE_PRIVATE);
        Map<String, ?> flagInfo = spFlag.getAll();
        if(!forMap) {
           return processSPSInfo(flagInfo, dataFlag);
        }
        SharedPreferences spMap = getSharedPreferences("map stats", Context.MODE_PRIVATE);
        Map<String, ?> mapInfo = spMap.getAll();
        return  processSPSInfo(mapInfo,dataMap);
//
//        for (Map.Entry<String, ?> entry : info.entrySet()) {
//            if (info.isEmpty()) {
//                return data;
//            }
//
//            value = entry.getValue() + " ";
//            switch(entry.getKey()){
//                case("Number of guesses 1"):
//                    data.put("Number of guesses in 1",value);
//                    break;
//               case("Number of guesses 2"):
//                    data.put("Number of guesses in 2",value);
//                    break;
//                case("Number of guesses 3"):
//                    data.put("Number of guesses in 3",value);
//                    break;
//                case("Number of guesses 4"):
//                    data.put("Number of guesses in 4",value);
//                    break;
//                case("Number of guesses 5"):
//                    data.put("Number of guesses in 2",value);
//                    break;
//                case("Number of guesses 6"):
//                    data.put("Number of guesses in 6",value);
//                    break;
//                case("Fails"):
//                    fails = Double.parseDouble(value);
//
//                    data.put("Number of fails",value);
//                    break;
//                case("Correct guesses"):
//                    wins = Double.parseDouble(value);
//                    //data.put("Number of fails",value);
//                    break;
 //       }

//        double winPercentage = (wins / (wins + fails)) * 100;
//
//            data.put("Win percentage",winPercentage+ "");
//        }
//
//        return data;
    }

    private LinkedHashMap<String, String> processSPSInfo(Map<String,?> getSPInfo, LinkedHashMap<String,String> data){
        String value = "";
        double fails = 0;
        double wins = 0;

        for (Map.Entry<String, ?> entry : getSPInfo.entrySet()) {
            if (getSPInfo.isEmpty()) {
                return data;
            }

            value = entry.getValue() + " ";
            switch(entry.getKey()){
                case("Number of guesses 1"):
                    data.put("Number of guesses in 1",value);
                    break;
                case("Number of guesses 2"):
                    data.put("Number of guesses in 2",value);
                    break;
                case("Number of guesses 3"):
                    data.put("Number of guesses in 3",value);
                    break;
                case("Number of guesses 4"):
                    data.put("Number of guesses in 4",value);
                    break;
                case("Number of guesses 5"):
                    data.put("Number of guesses in 5",value);
                    break;
                case("Number of guesses 6"):
                    data.put("Number of guesses in 6",value);
                    break;
                case("Fails"):
                    fails = Double.parseDouble(value);

                    data.put("Number of fails",value);
                    break;
                case("Correct guesses"):
                    wins = Double.parseDouble(value);
                    //data.put("Number of fails",value);
                    break;
            }

            double winPercentage = (wins / (wins + fails)) * 100;

            data.put("Win percentage",winPercentage+ "");
        }

        return data;
    }

    private void setText(){
        LinkedHashMap<String,String> flagStats = getStats(false);
        int counter = 0;
        for (Map.Entry<String, String> entry : flagStats.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            whatStatViews.get(counter).setText(key);
            theStatViews.get(counter).setText(value);
            counter++;
        }
        LinkedHashMap<String,String> mapStats = getStats(true);
        counter = 0;
        for (Map.Entry<String, String> entry : mapStats.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            whatStatViewsFlag.get(counter).setText(key);
            theStatViewsFlag.get(counter).setText(value);
            counter++;
        }
    }

    //sorry
    private void addViews() {
            TextView view0 = findViewById(R.id.output0);
            TextView view1 = findViewById(R.id.output1);
            TextView view2 = findViewById(R.id.output2);
            TextView view3 = findViewById(R.id.output3);
            TextView view4 = findViewById(R.id.output4);
            TextView view5 = findViewById(R.id.output5);
            TextView view6 = findViewById(R.id.output6);
            TextView view7 = findViewById(R.id.output7);
            whatStatViews.add(view0);
            whatStatViews.add(view1);
            whatStatViews.add(view2);
            whatStatViews.add(view3);
            whatStatViews.add(view4);
            whatStatViews.add(view5);
            whatStatViews.add(view6);
            whatStatViews.add(view7);
            TextView numview0 = findViewById(R.id.bd0);
            TextView numview1 = findViewById(R.id.bd1);
            TextView numview2 = findViewById(R.id.bd2);
            TextView numview3 = findViewById(R.id.bd3);
            TextView numview4 = findViewById(R.id.bd4);
            TextView numview5 = findViewById(R.id.bd5);
            TextView numview6 = findViewById(R.id.bd6);
            TextView numview7 = findViewById(R.id.bd7);
            theStatViews.add(numview0);
            theStatViews.add(numview1);
            theStatViews.add(numview2);
            theStatViews.add(numview3);
            theStatViews.add(numview4);
            theStatViews.add(numview5);
            theStatViews.add(numview6);
            theStatViews.add(numview7);

        TextView view0stat = findViewById(R.id.outputMap0);
        TextView view1stat = findViewById(R.id.outputMap1);
        TextView view2stat = findViewById(R.id.outputMap2);
        TextView view3stat = findViewById(R.id.outputMap3);
        TextView view4stat = findViewById(R.id.outputMap4);
        TextView view5stat = findViewById(R.id.outputMap5);
        TextView view6stat = findViewById(R.id.outputMap6);
        TextView view7stat = findViewById(R.id.outputMap7);
        whatStatViewsFlag.add(view0stat);
        whatStatViewsFlag.add(view1stat);
        whatStatViewsFlag.add(view2stat);
        whatStatViewsFlag.add(view3stat);
        whatStatViewsFlag.add(view4stat);
        whatStatViewsFlag.add(view5stat);
        whatStatViewsFlag.add(view6stat);
        whatStatViewsFlag.add(view7stat);
        TextView numview0stat = findViewById(R.id.bdMap0);
        TextView numview1stat = findViewById(R.id.bdMap1);
        TextView numview2stat = findViewById(R.id.bdMap2);
        TextView numview3stat = findViewById(R.id.bdMap3);
        TextView numview4stat = findViewById(R.id.bdMap4);
        TextView numview5stat = findViewById(R.id.bdMap5);
        TextView numview6stat = findViewById(R.id.bdMap6);
        TextView numview7stat = findViewById(R.id.bdMap7);
        theStatViewsFlag.add(numview0stat);
        theStatViewsFlag.add(numview1stat);
        theStatViewsFlag.add(numview2stat);
        theStatViewsFlag.add(numview3stat);
        theStatViewsFlag.add(numview4stat);
        theStatViewsFlag.add(numview5stat);
        theStatViewsFlag.add(numview6stat);
        theStatViewsFlag.add(numview7stat);

    }

}


