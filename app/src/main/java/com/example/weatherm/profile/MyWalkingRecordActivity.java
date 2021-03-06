package com.example.weatherm.profile;

import android.os.Bundle;
import android.util.EventLogTags;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.weatherm.Model.RouteInfo;
import com.example.weatherm.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyWalkingRecordActivity extends AppCompatActivity {

    private Toolbar myToolbar;
    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("yyyy??? MM???", Locale.KOREA);
    private SimpleDateFormat dateFormatForMonth2 = new SimpleDateFormat("yyyy-MM", Locale.KOREA);
    private SimpleDateFormat dateFormatForMonth3 = new SimpleDateFormat("MM", Locale.KOREA);
    private ArrayList<String> routeInfoIdList=new ArrayList<String>();
    private ArrayList<RouteInfo> routeInfoList=new ArrayList<RouteInfo>();
    private ArrayList<LatLng> ListStartLatLng=new ArrayList<>();
    private ArrayList<LatLng> ListEndLatLng=new ArrayList<>();
    private ArrayList<LatLng> ListTrashLatLng=new ArrayList<>();
    private ArrayList<LatLng> ListWarningLatLng=new ArrayList<>();
    private double totalDistance;
    private long totalTime;
    private Date walkingDate;
    private String walkingContent=null;
    //????????? ??????
    private RelativeLayout loaderLayout;


    private ArrayList<Integer> MonthTimeList = new ArrayList<>(); // ArrayList ??????
    private ArrayList<Integer> MonthDistanceList = new ArrayList<>(); // ArrayList ??????
    private ArrayList<String> labelList = new ArrayList<String>(); // ArrayList ??????

    private int flag=0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myrecordwalking);


        labelList.add("Jan");
        labelList.add("Feb");
        labelList.add("Mar");
        labelList.add("Api");
        labelList.add("May");
        labelList.add("Jun");
        labelList.add("Jul");
        labelList.add("Aug");
        labelList.add("Sep");
        labelList.add("Oct");
        labelList.add("Nov");
        labelList.add("Dec");

        //?????? ??????
        final CompactCalendarView compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);

        // Toolbar??? ??????.
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar_profileModify);
        setSupportActionBar(myToolbar);

        loaderLayout = findViewById(R.id.loaderLayout);
        TextView textView_month = (TextView) findViewById(R.id.textView_month);
        TextView MonthTotalTime_textView=(TextView) findViewById(R.id.Month_Total_Time_TextVIew);
        TextView MonthTotalDistance_textView=(TextView) findViewById(R.id.Month_Total_Distance_TextView);

        BarChart barChart_time=findViewById(R.id.Barchart);
        BarChart barChart_distance=findViewById(R.id.Barchart2);


        textView_month.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));

        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);

        // ????????? ?????? ??????
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            //?????? ?????? ???
            @Override
            public void onDayClick(Date dateClicked) {

                List<Event> events = compactCalendarView.getEvents(dateClicked);

                SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
                String date1 = transFormat.format(dateClicked);

                String event_name = "";
                String event_date = "";

                if (events.size() > 0) {
                    event_name = events.get(0).getData().toString();
                    long time1 = events.get(0).getTimeInMillis();
                    event_date = transFormat.format(new Date(time1));
                }


            }


            //?????? ????????????
            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                textView_month.setText(dateFormatForMonth.format(firstDayOfNewMonth));
                int MonthTime=0;
                int MonthDistance=0;
                for(int k=0;k<routeInfoList.size();k++){

                    String temp=dateFormatForMonth3.format(routeInfoList.get(k).getWalkingDate());
                    String temp2=dateFormatForMonth3.format(firstDayOfNewMonth);

                    if(temp.equals(temp2))
                    {
                        MonthTime=MonthTime+(int)routeInfoList.get(k).getTotalTime();
                        MonthDistance=MonthDistance+(int)routeInfoList.get(k).getTotalDistance();
                    }

                }
                int meter=(int)MonthDistance%1000;
                int kilMmeter=(int)MonthDistance/1000;
                MonthTotalDistance_textView.setText(""+String.format("%02d",kilMmeter)+"."+String.format("%03d",meter)+"km");

                int seconds=(int)MonthTime%60;
                int minutes=(int)MonthTime/60;
                int hours=(int)minutes/60;
//                Log.e("???",""+seconds);
//                Log.e("???",""+minutes);
                MonthTotalTime_textView.setText(""+String.format("%02d",hours)+":"+String.format("%02d",minutes) + ":"+  String.format("%02d",seconds));
            }
        });


        //Toolbar??? ????????? ????????? ???????????? ????????? ???????????? ?????????.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.left_arrow_24);
        getSupportActionBar().setTitle("?????? ??????");  //??? ??????????????? ????????? ?????? ???????????? ?????????

        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String userUid=firebaseUser.getUid();

        loaderLayout.setVisibility(View.VISIBLE);
        DocumentReference documentReference=firebaseFirestore.collection("users").document(userUid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot result=task.getResult();
                if(result.getData().get("walkingList")!=null)
                routeInfoIdList=(ArrayList<String>) result.getData().get("walkingList");
                else{
                    loaderLayout.setVisibility(View.GONE);
                    barChart_distance.setVisibility(View.GONE);
                    barChart_time.setVisibility(View.GONE);
                }
                Log.d("hello","Route List: "+routeInfoIdList.toString());
                for(int i=0; i<routeInfoIdList.size();i++){
                    DocumentReference documentReference2=firebaseFirestore.collection("RouteInfo").document(routeInfoIdList.get(i));
                    Log.d("hello","Route Number: "+routeInfoIdList.get(i).toString());
                    documentReference2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            DocumentSnapshot result=task.getResult();
                            if(result.getData().get("listWarningLatLng")!=null)
                            {
                                ListWarningLatLng=(ArrayList<LatLng>) result.getData().get("listWarningLatLng");
                            }else
                                ListWarningLatLng=null;

                            if(result.getData().get("listTrashLatLng")!=null)
                            {
                                ListTrashLatLng=(ArrayList<LatLng>) result.getData().get("listTrashLatLng");
                            }else
                                ListTrashLatLng=null;

                            ListStartLatLng=(ArrayList<LatLng>) result.getData().get("listStartLatLng");
                            ListEndLatLng=(ArrayList<LatLng>) result.getData().get("listEndLatLng");
                            totalDistance=(double) result.getData().get("totalDistance");
                            totalTime=(long) result.getData().get("totalTime");
                            if(result.getData().get("walkingContent")!=null)
                            {
                                walkingContent=result.getData().get("walkingContent").toString();
                            }else
                                walkingContent=null;
                            walkingDate= new Date(result.getDate("walkingDate").getTime());
                            routeInfoList.add(new RouteInfo(ListStartLatLng,ListEndLatLng,totalDistance,totalTime,walkingDate,walkingContent,ListTrashLatLng,ListWarningLatLng));
                            flag++;
                            if(flag==routeInfoIdList.size()-1) {
                                loaderLayout.setVisibility(View.GONE);
                                Date today=new Date();
                                int MonthTime=0;
                                int MonthDistance=0;
                                for(int k=0;k<routeInfoList.size();k++){

                                    String temp=dateFormatForMonth3.format(routeInfoList.get(k).getWalkingDate());
                                    String temp2=dateFormatForMonth3.format(today);

                                    if(temp.equals(temp2))
                                    {
                                        MonthTime=MonthTime+(int)routeInfoList.get(k).getTotalTime();
                                        MonthDistance=MonthDistance+(int)routeInfoList.get(k).getTotalDistance();
                                    }

                                }
                                int meter=(int)MonthDistance%1000;
                                int kilMmeter=(int)MonthDistance/1000;
                                MonthTotalDistance_textView.setText(""+MonthDistance+"m");

                                int seconds=(int)MonthTime%60;
                                int minutes=(int)MonthTime/60;
                                int hours=(int)minutes/60;
//                Log.e("???",""+seconds);
//                Log.e("???",""+minutes);
                                MonthTotalTime_textView.setText(""+String.format("%02d",hours)+":"+String.format("%02d",minutes) + ":"+  String.format("%02d",seconds));

                                for(int j=1;j<13;j++)
                                {
                                    MonthTime=0;
                                    MonthDistance=0;
                                    for(int k=0;k<routeInfoList.size();k++){

                                        String temp=dateFormatForMonth3.format(routeInfoList.get(k).getWalkingDate());
                                        int to = Integer.parseInt(temp);

                                        if(to==j)
                                        {
                                            MonthTime=MonthTime+(int)routeInfoList.get(k).getTotalTime();
                                            MonthDistance=MonthDistance+(int)routeInfoList.get(k).getTotalDistance();
                                        }

                                    }

                                    Log.e("?????? ?????? ??????",""+MonthTime);
                                    MonthTimeList.add(MonthTime/60);
                                    MonthDistanceList.add(MonthDistance);
                                }
                                BarChartGraph(barChart_time,"?????? ?????? ??????(???)",labelList, MonthTimeList);
                                BarChartGraph(barChart_distance,"?????? ?????? ??????(m)",labelList, MonthDistanceList);
                            }
                        }
                    });

                }

            }
        });


//        for(int i=1;i<13;i++)
//        {
//            MonthTimeList.add(i);
//        }

//        BarChartGraph(labelList, MonthDistanceList);


    }
    private void BarChartGraph(BarChart barChart,String description,ArrayList<String> labelList, ArrayList<Integer> valList) {
        // BarChart ?????????

        ArrayList<BarEntry> entries = new ArrayList<>();
        for(int i=0; i < valList.size();i++){
            entries.add(new BarEntry(i+1,(int) valList.get(i)));
        }

        BarDataSet depenses = new BarDataSet(entries, description); // ????????? ????????? ???????????? ???

        depenses.setAxisDependency(YAxis.AxisDependency.RIGHT);

        depenses.setColor(R.color.button_green);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add((IBarDataSet) depenses);

        BarData data = new BarData(dataSets); // ??????????????? v3.x ???????????? ?????? ?????????
        barChart.setData(data);
        barChart.animateXY(1,1);

        XAxis xaxis = barChart.getXAxis();
        xaxis.setValueFormatter(new IndexAxisValueFormatter());

        barChart.getXAxis().setDrawGridLines(false);
        //barChart.getAxisLeft().setDrawAxisLine(false);

        barChart.invalidate();


        Description des = new Description();
        des.setText("");
        barChart.setDescription(des);
    }

    // ToolBar??? menu.xml??? ??????????????????
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.information_toolbar_menu, menu);
        return true;
    }

    //ToolBar??? ????????? ????????? select ???????????? ???????????? ??????
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {

            //android.R.id.home ??? ????????? ??? ???????????? ????????? ?????????.
            case android.R.id.home:
                finish();
                return true;

            default:
                return true;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}
