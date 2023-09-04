package com.example.Greenland.BottomNavi;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.Greenland.R;

import android.graphics.Color;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.github.mikephil.charting.charts.LineChart;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;


public class GraphFragment extends Fragment {

    String Title = null;
    String Title1 = null;
    String ipp = null;
    int Flag = 0;
    static List<Entry> entries = new ArrayList<>();
    static List<Entry> retail_entries = new ArrayList<>();
    ArrayList<String> xVals = new ArrayList<>();
    private String selectedValue;
    String key="";
    Spinner spinner;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_graph, container, false);
        Button btn_chart = view.findViewById(R.id.chart_button);
        btn_chart.setOnClickListener(new View.OnClickListener() {
          public void onClick(View v) {
              // 버튼 클릭 이벤트 처리
              switch (v.getId()) {
                  case R.id.chart_button:
                      new Thread(new Runnable() {
                          @Override
                          public void run() {
                              String data = getXmlData();
                              getActivity().runOnUiThread(new Runnable() { // 수정된 부분
                                  @Override
                                  public void run() {
                                      Spinner spinner = getView().findViewById(R.id.spinner);
                                      String selectedValue = spinner.getSelectedItem().toString();
                                      TextView text = getView().findViewById(R.id.result);
                                      TextView text1 = getView().findViewById(R.id.result1);
                                      Title = Title.replace(">" , "");
                                      Title1 = Title1.replace(">" , "");
                                      text.setText(Title1);
                                      text1.setText(Title);

                                      // 첫 번째 차트 생성
                                      LineChart chart1 = getView().findViewById(R.id.chart);
                                      LineDataSet dataSet1 = new LineDataSet(entries, selectedValue);
                                      LineData lineData1 = new LineData(dataSet1);
                                      Legend legend1 = chart1.getLegend();
                                      legend1.setForm(Legend.LegendForm.LINE);
                                      legend1.setTextColor(Color.BLACK);
                                      legend1.setFormSize(40f);
                                      dataSet1.setLineWidth(2);
                                      dataSet1.setCircleRadius(6);
                                      dataSet1.setValueTextSize(10f);
                                      dataSet1.setCircleColor(Color.parseColor("#009900"));
                                      dataSet1.setColor(Color.parseColor("#33FF33"));
                                      dataSet1.setDrawCircleHole(true);
                                      dataSet1.setDrawCircles(true);
                                      dataSet1.setDrawHorizontalHighlightIndicator(false);
                                      dataSet1.setDrawHighlightIndicators(false);
                                      chart1.setData(lineData1);
                                      chart1.getDescription().setEnabled(false);
                                      XAxis xAxis1 = chart1.getXAxis();
                                      xAxis1.setDrawGridLines(false);
                                      xAxis1.setDrawAxisLine(false);
                                      xAxis1.setDrawLabels(true);
                                      xAxis1.setPosition(XAxis.XAxisPosition.BOTTOM);
                                      xAxis1.setValueFormatter(new IndexAxisValueFormatter(xVals));
                                      YAxis yAxis1 = chart1.getAxisLeft();
                                      yAxis1.setDrawGridLines(false);
                                      yAxis1.setDrawAxisLine(false);
                                      chart1.invalidate();
                                      chart1.animateX(1000);

                                      // 두 번째 차트 생성
                                      LineChart chart2 = getView().findViewById(R.id.chart2);
                                      LineDataSet dataSet2 = new LineDataSet(retail_entries, selectedValue);
                                      LineData lineData2 = new LineData(dataSet2);
                                      Legend legend2 = chart2.getLegend();
                                      legend2.setForm(Legend.LegendForm.LINE);
                                      legend2.setTextColor(Color.BLACK);
                                      legend2.setFormSize(40f);
                                      dataSet2.setLineWidth(2);
                                      dataSet2.setCircleRadius(6);
                                      dataSet2.setValueTextSize(10f);
                                      dataSet2.setCircleColor(Color.parseColor("#990000"));
                                      dataSet2.setColor(Color.parseColor("#FF3333"));
                                      dataSet2.setDrawCircleHole(true);
                                      dataSet2.setDrawCircles(true);
                                      dataSet2.setDrawHorizontalHighlightIndicator(false);
                                      dataSet2.setDrawHighlightIndicators(false);
                                      chart2.setData(lineData2);
                                      chart2.getDescription().setEnabled(false);
                                      XAxis xAxis2 = chart2.getXAxis();
                                      xAxis2.setDrawGridLines(false);
                                      xAxis2.setDrawAxisLine(false);
                                      xAxis2.setDrawLabels(true);
                                      xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
                                      xAxis2.setValueFormatter(new IndexAxisValueFormatter(xVals));
                                      YAxis yAxis2 = chart2.getAxisLeft();
                                      yAxis2.setDrawGridLines(false);
                                      yAxis2.setDrawAxisLine(false);
                                      chart2.invalidate();
                                      chart2.animateX(1000);
                                  }
                              });
                          }
                      }).start();
                      break;
                    }
                }
            });

        spinner = view.findViewById(R.id.spinner);
        ArrayAdapter monthAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.veg_menu, android.R.layout.simple_spinner_dropdown_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(monthAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedValue = parent.getItemAtPosition(position).toString();
                //Toast.makeText(getActivity(), selectedValue, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent){}
        });
        return view;
    }

    String getXmlData() {
        entries.clear();
        xVals.clear();
        retail_entries.clear();
        int countlist=0;
        StringBuffer buffer = new StringBuffer();
        Spinner spinner = getView().findViewById(R.id.spinner);
        String selectedValue = spinner.getSelectedItem().toString();

        try {
            String baseURL = "https://www.kamis.or.kr/service/price/xml.do?action=monthlySalesList&p_yyyy=2023&p_period=3&p_convert_kg_yn=N&p_cert_id=261&p_returntype=xml";
            String itemCode = "";
            String kindCode = "";

            switch(selectedValue) {
                case "시금치":
                    itemCode = "213";
                    kindCode = "";
                    break;
                case "상추":
                    itemCode = "214";
                    kindCode = "";
                    break;
                case "수박":
                    itemCode = "221";
                    kindCode = "";
                    break;
                case "오이":
                    itemCode = "223";
                    kindCode = "";
                    break;
                case "당근":
                    itemCode = "232";
                    kindCode = "";
                    break;
                case "무":
                    itemCode = "231";
                    kindCode = "";
                    break;
                case "피망":
                    itemCode = "255";
                    kindCode = "";
                    break;
                case "토마토":
                    itemCode = "225";
                    kindCode = "";
                    break;
                case "호박":
                    itemCode = "224";
                    kindCode = "";
                    break;
                case "감자":
                    itemCode = "152";
                    kindCode = "";
                    baseURL = "https://www.kamis.or.kr/service/price/xml.do?action=monthlySalesList&p_yyyy=2023&p_period=3&p_itemcategorycode=100&p_convert_kg_yn=N&p_cert_key=d8cd2d30c38b483f9e72e68389e42e67&p_cert_id=261&p_returntype=xml";
                    break;
                case "양배추":
                    itemCode = "212";
                    kindCode = "";
                    break;
                case "고구마":
                    itemCode = "151";
                    kindCode = "";
                    break;
                case "양파":
                    itemCode = "245";
                    kindCode = "";
                    break;
                case "파프리카":
                    itemCode = "256";
                    kindCode = "";
                    break;
                case "풋고추":
                    itemCode = "242";
                    kindCode = "";
                    break;
                case "생강":
                    itemCode = "247";
                    kindCode = "";
                    break;
                case "미나리":
                    itemCode = "252";
                    kindCode = "";
                    break;
                case "깻잎":
                    itemCode = "253";
                    kindCode = "";
                    break;
                case "방울토마토":
                    itemCode = "422";
                    kindCode = "";
                    break;
                case "느타리버섯":
                    itemCode = "315";
                    kindCode = "";
                    break;
                case "팽이버섯":
                    itemCode = "316";
                    kindCode = "";
                    break;

                case "포도":
                    itemCode = "414";
                    kindCode = "";
                    break;
                case "사과":
                    itemCode = "411";
                    kindCode = "";
                    break;
                case "배":
                    itemCode = "412";
                    kindCode = "";
                    break;
                case "단감":
                    itemCode = "416";
                    kindCode = "";
                    break;
                case "바나나":
                    itemCode = "418";
                    kindCode = "";
                    break;
                case "참다래":
                    itemCode = "419";
                    kindCode = "";
                    break;

                case "오렌지":
                    itemCode = "421";
                    kindCode = "";
                    break;
                case "레몬":
                    itemCode = "424";
                    kindCode = "";
                    break;

                case "망고":
                    itemCode = "428";
                    kindCode = "";
                    break;
            }
            String queryUrl = baseURL + key + "&p_itemcode=" + itemCode + "&p_kindcode=" + kindCode + "&p_countycode=1101&p_graderank=1";

            URL url = new URL(queryUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            String tag="";
            InputStream is = conn.getInputStream();
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is, "UTF-8"));

            int a = 1;
            String year = null;
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();
                        if (tag.equals("productclscode")){
                            ipp=xpp.nextText();
                            if (ipp.equals("02")){
                                Flag=2;
                            }
                            else {
                                Flag=1;
                                countlist=0;
                                a=1;
                            }
                            buffer.append(ipp).append("\n");
                        }
                        else if (tag.equals("caption")) {
                            if (Flag==2){
                                Title=xpp.nextText();
                            }
                            else{
                                Title1=xpp.nextText();
                            }

                            buffer.append(Title).append("\n");
                        } else if (tag.equals("item_name")) {
                            buffer.append("상품명 : ").append(xpp.nextText()).append("\n");
                        } else if (tag.equals("yyyy")) {
                            year = xpp.nextText();
                            buffer.append("연도 : ").append(year).append("\n");
                        } else if (tag.matches("m[1-9]|m1[0-2]")) {
                            String str = xpp.nextText();
                            if (a == 13){
                                a = 1;
                            }
                            buffer.append(tag.substring(1)).append("월 : ").append(str).append("\n");
                            str = str.replace("," , "");
                            if (Flag==1) {
                                try {
                                    entries.add(new Entry(countlist, Float.parseFloat(str)));
                                    xVals.add(year + '.' + a);
                                } catch (Exception e) {
                                }
                            }
                            else if (Flag==2) {
                                try {
                                    retail_entries.add(new Entry(countlist, Float.parseFloat(str)));
                                } catch (Exception e) {
                                }
                            }
                            countlist++;
                            a++;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        tag = xpp.getName();
                        if (tag.equals("item")) buffer.append("\n");
                        break;
                }
                eventType = xpp.next();
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }


}