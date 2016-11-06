package com.example.googlemap;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends TabActivity implements OnMapReadyCallback {
    SQLiteDatabase sqlDB;
    DBHelper db;
    ScheduleAdapter sa = null;
    ArrayList<LinearLayout> exerciseList = new ArrayList<LinearLayout>();
    ArrayList<LinearLayout> scheduleList = new ArrayList<LinearLayout>();
    ArrayList<LinearLayout> searchList = new ArrayList<LinearLayout>();
    int currentY,currentM,currentD;
    GoogleMap gm;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbSetting();
        TabHost th = getTabHost();
        TabHost.TabSpec tc1 = th.newTabSpec("").setContent(R.id.tab1).setIndicator("운동리스트");
        TabHost.TabSpec tc2 = th.newTabSpec("").setContent(R.id.tab2).setIndicator("일정표");
        TabHost.TabSpec tc3 = th.newTabSpec("").setContent(R.id.tab3).setIndicator("헬스장 찾기");
        th.addTab(tc1);
        th.addTab(tc2);
        th.addTab(tc3);
        tab1Setting();
        tab2Setting();
        tab3Setting();
    }
    private void dbSetting()
    {
        db = new DBHelper(MainActivity.this);
        sqlDB =  db.getWritableDatabase();
        db.onUpgrade(sqlDB,1,2);

    }

    private void tab1Setting()
    {

        sqlDB = db.getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery("select * from exercise;",null);
        while(cursor.moveToNext())
        {
            String name = cursor.getString(0);
            int imgId = cursor.getInt(1);
            String desc = cursor.getString(2);
            LinearLayout ll = new LinearLayout(MainActivity.this);
            ll.setPadding(10,10,10,10);
            ll.setOrientation(LinearLayout.VERTICAL);
            ImageView iv = new ImageView(MainActivity.this);
            iv.setImageResource(imgId);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 700);
            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
            iv.setLayoutParams(lp);
            TextView tv = new TextView(MainActivity.this);
            tv.setText(name);
            tv.setTextSize(20);
            TextView tv2 = new TextView(MainActivity.this);
            tv2.setText(desc);
            tv2.setTextSize(15);
            tv.setTag(desc);
            ll.addView(iv);
            ll.addView(tv);
            ll.addView(tv2);

            exerciseList.add(ll);

        }
        ExerciseAdapter ea = new ExerciseAdapter();
        ea.notifyDataSetChanged();
        ListView lv = (ListView)findViewById(R.id.listView);
        lv.setAdapter(ea);
    }
    private void tab2Setting()
    {
        sa = new ScheduleAdapter();
        final CalendarView cv = (CalendarView)findViewById(R.id.cal);
        final TextView dateTv = (TextView)findViewById(R.id.dateTv);
        final TextView resultZero = (TextView)findViewById(R.id.resultZero);
        final ListView lv=  (ListView)findViewById(R.id.calList);
        System.out.println(new Date(cv.getDate()));
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(cv.getDate());
        currentY = cl.get(Calendar.YEAR);
        currentM = cl.get(Calendar.MONTH);
        currentD = cl.get(Calendar.DAY_OF_MONTH);
        Button addBtn = (Button)findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View inflateView = View.inflate(getApplicationContext(),R.layout.cal_dialog,null);
                final AlertDialog.Builder alg = new AlertDialog.Builder(MainActivity.this);
                final EditText editText = (EditText) inflateView.findViewById(R.id.exerEdit);
                final TextView cntText = (TextView) inflateView.findViewById(R.id.cntTv);
                Button plusOne = (Button)inflateView.findViewById(R.id.plusOneBtn);
                Button minusOne = (Button)inflateView.findViewById(R.id.minusOneBtn);
                Button plusFive = (Button)inflateView.findViewById(R.id.plusFiveBtn);
                Button minusFive = (Button)inflateView.findViewById(R.id.minusFiveBtn);
                plusOne.setTag(1);
                minusOne.setTag(-1);
                plusFive.setTag(5);
                minusFive.setTag(-5);
                View.OnClickListener vc = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int count = (int)view.getTag();
                        int editCount = Integer.parseInt(cntText.getText().toString());
                        if(editCount+count>=0)
                        {
                            cntText.setText(Integer.toString(count+editCount));
                        }
                    }
                };
                plusOne.setOnClickListener(vc);
                minusOne.setOnClickListener(vc);
                plusFive.setOnClickListener(vc);
                minusFive.setOnClickListener(vc);
                Spinner spinner = (Spinner)inflateView.findViewById(R.id.spinner);
                final ArrayList<String> arrayList = new ArrayList<String>();
                arrayList.add("러시안 트위스트");
                arrayList.add("런지");
                arrayList.add("마운틴 클라이머");
                arrayList.add("벽에 등대고 앉기");
                arrayList.add("스쿼트");
                arrayList.add("윗몸일으키기");
                arrayList.add("점핑잭");
                arrayList.add("트리셉스 체어 딥");
                arrayList.add("팔굽혀펴기");
                arrayList.add("플랭크");
                spinner.setAdapter(new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_dropdown_item,arrayList));
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        editText.setText(arrayList.get(i));
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {}
                });
                alg.setTitle("운동 추가");
                alg.setView(inflateView);
                alg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = editText.getText().toString();
                        try{
                            int cnt = Integer.parseInt(cntText.getText().toString());
                            sqlDB = db.getWritableDatabase();
                            long milis = new Date(currentY,currentM,currentD).getTime();

                            System.out.println(milis);
                            Date date = new Date(currentY,currentM,currentD);
                            final SimpleDateFormat sd  =new SimpleDateFormat("yy년 MM월 dd일");
                            String dateStr = sd.format(date).toString();
                            sqlDB.execSQL("insert into schedule values('"+dateStr+"','"+name+"',"+cnt+",'회',"+(milis+System.currentTimeMillis())+")");
                            //CREATE TABLE schedule(date CHAR(30) PRIMARY KEY,name CHAR(30),value INTEGER ,comment CHAR(40)
                            sqlDB.close();
                            showScheduleList(milis,dateTv,sa,resultZero);
                        }catch (Exception e)
                        {
                            System.out.println(e);
                            Toast.makeText(MainActivity.this,"올바른 값을 입력해주세요",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                alg.setNegativeButton("취소",null);

                alg.show();
            }
        });
        showScheduleList(cv.getDate(),dateTv,sa,resultZero);
        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                currentY = i;
                currentM = i1;
                currentD = i2;
                showScheduleList(new Date(i,i1,i2).getTime(),dateTv,sa,resultZero);
            }
        });
        lv.setAdapter(sa);

    }
    private int showScheduleList(long millis,TextView dateTv,ScheduleAdapter sa,TextView resultZero)
    {
        Date date = new Date(millis);
        final SimpleDateFormat sd  =new SimpleDateFormat("yy년 MM월 dd일");
        String dateStr = sd.format(date).toString();
        dateTv.setText(dateStr);
        int cnt = rawCount(millis,dateStr,resultZero);


        return cnt;
    }
    private int rawCount(final long dateMilis, final String dateStr , final TextView resultZero)
    {
        sqlDB = db.getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery("select * from schedule where date='"+dateStr+"'",null);
        int count = cursor.getCount();
        scheduleList = new ArrayList<LinearLayout>();
        while(cursor.moveToNext())
        {
            //CREATE TABLE schedule(date CHAR(30) PRIMARY KEY,name CHAR(30),value INTEGER ,comment CHAR(40)
            String name = cursor.getString(1);
            int value = cursor.getInt(2);
            String comment = cursor.getString(3);
            final long milis = cursor.getLong(4);
            Button button = new Button(getApplicationContext());
            button.setText("삭제");
            LinearLayout ll = new LinearLayout(getApplicationContext());
            ll.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.weight = 1;
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp2.weight = 6;

            TextView tv = new TextView(getApplicationContext());
            tv.setLayoutParams(lp);
            String msg = name+" "+Integer.toString(value)+" "+comment;
            tv.setText(msg);
            tv.setTextColor(Color.BLACK);
            ll.addView(tv);
            ll.addView(button);
            button.setTag(msg);
            button.setLayoutParams(lp2);
            button.setOnClickListener(new View.OnClickListener() {
                long btnmilis=milis;
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alg = new AlertDialog.Builder(MainActivity.this);
                    alg.setTitle("정말로 삭제하시겠습니까?");
                    alg.setMessage((String)view.getTag());
                    alg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            sqlDB = db.getWritableDatabase();
                            sqlDB.execSQL("delete from schedule where dateTime ="+btnmilis+";");
                            sa.notifyDataSetChanged();
                            rawCount(dateMilis,dateStr,resultZero);
                        }
                    });
                    alg.setNegativeButton("취소",null);
                    alg.show();
                }
            });
            scheduleList.add(ll);
        }
        sa.notifyDataSetChanged();
        cursor.close();
        sqlDB.close();
        if(count==0)
        {
            resultZero.setVisibility(View.VISIBLE);
        }
        else
        {
            resultZero.setVisibility(View.GONE);
        }
        return count;
    }
    private void tab3Setting()
    {
        ListView lvSearch = (ListView)findViewById(R.id.listSearch);
        Button searchBtn = (Button)findViewById(R.id.searchBtn);
        final EditText searchEdit = (EditText)findViewById(R.id.editSearch);
        final SearchAdapter sa = new SearchAdapter();
        lvSearch.setAdapter(sa);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqlDB = db.getReadableDatabase();
                Cursor cursor = sqlDB.rawQuery("select * from health;",null);
                String address = searchEdit.getText().toString();
                searchList = new ArrayList<LinearLayout>();
                while(cursor.moveToNext())
                {
                    final String nameStr = cursor.getString(0);
                    final String addressStr = cursor.getString(1);
                    final double latDouble = cursor.getDouble(2);
                    final double lngDouble = cursor.getDouble(3);
                    final int id = cursor.getInt(4);
                    if(addressStr.contains(address) || nameStr.contains(address))
                    {
                        LinearLayout ll = new LinearLayout(MainActivity.this);
                        ll.setOrientation(LinearLayout.HORIZONTAL);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lp.weight = 1;
                        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lp2.weight = 4;
                        TextView tv = new TextView(MainActivity.this);
                        tv.setText(nameStr+"   "+addressStr);
                        tv.setLayoutParams(lp);
                        Button button = new Button(MainActivity.this);
                        button.setText("지도");
                        button.setLayoutParams(lp2);
                        ll.addView(tv);
                        ll.addView(button);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                                intent.putExtra("name",nameStr);
                                intent.putExtra("address",addressStr);
                                intent.putExtra("lat",latDouble);
                                intent.putExtra("lng",lngDouble);
                                startActivity(intent);
                            }
                        });
                        searchList.add(ll);
                        ll.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                                intent.putExtra("id",id);
                                startActivity(intent);
                            }
                        });

                    }
                    sa.notifyDataSetChanged();
                }
            }

        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    public class DBHelper extends SQLiteOpenHelper{
        public DBHelper(Context context)
        {
            super(context,"groupDB3",null,1);
        }
        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("CREATE TABLE exercise(name CHAR(20) PRIMARY KEY,imgid INTEGER,desc CHAR(50));");
            sqLiteDatabase.execSQL("insert into exercise values('[러시안 트위스트]',"+R.drawable.one+",'윗몸 일으키기 자세처럼 무릎을 굽히고 바닥에 앉습니다. 발바닥을 땅에 붙인채로, 양쪽의 팔을 상체에서 떨어뜨려 잡습니다. 몸을 한 쪽 방향으로 최대한 비트는 동작입니다. 이 동작은 복부 근육을 비틀어서, 복부 근윤을 강화하는데 효과적입니다.')");
            sqLiteDatabase.execSQL("insert into exercise values('[런지]',"+R.drawable.two+",'다른 다리가 뒤에 위치하는 동안 바닥에 무릎을 구부리고 발 평면 앞으로 한쪽 다리를 넣어 상체를 세운 자세를 유지한 채 앉았다 일어납니다. 런지는 대퇴사 두근, 대둔근뿐만 아니라 햄스트링을 강화합니다.')");
            sqLiteDatabase.execSQL("insert into exercise values('[마운틴 클라이머]',"+R.drawable.three+",'푸시업 자세에서, 한 쪽 무릎을 가슴쪽으로 구부리세요. 가볍게 점프하듯 다리를 바꾸어 구부립니다. 이 동작은 전신 근육과 심혈관에 좋은 영향을 줍니다.')");
            sqLiteDatabase.execSQL("insert into exercise values('[벽에 등대고 앉기]',"+R.drawable.four+",'등을 벽에 붙힌 자세에서 무릎이 직각을 형성 할 때까지 무릎을 굽힙니다. 그 자세로 버티는 운동입니다. 이 운동은 대퇴사 두근 근육을 강화하기 위한 것입니다.')");
            sqLiteDatabase.execSQL("insert into exercise values('[스쿼트]',"+R.drawable.five+",'웨이트 트레이닝의 가장 대표적인 운동 중 하나로 일반적으로는 바벨을 어깨에 짊어지고 서서 깊이 웅크린 다음 일어서는 운동으로, 무릎을 구부릴 때는 무릎이 발끝보다 앞으로 나와서는 안되며, 허리를 구부리지 말고 엉덩이를 뒤로 뺀 자세로 시행해야 합니다. 이 운동은 허벅지, 엉덩이, 오금의 힘줄뿐만 아니라 하체에 사용되는 대부분의 근육을 발달시키는데 도움이 됩니다.')");
            sqLiteDatabase.execSQL("insert into exercise values('[윗몸일으키기]',"+R.drawable.six+",'등을 대고 누워서 양 손을 머리 뒤에 놓습니다.  그런 다음 상체를 바닥에서 최대한 멀리 들어올립니다.  누운 자세로 천천히 돌아가서 운동을 반복합니다.')");
            sqLiteDatabase.execSQL("insert into exercise values('[점핑잭]',"+R.drawable.seven+",'다리와 팔을 활용한 운동으로 다리를 벌림과 동시에 두 팔을 머리 위로 올리며 뛰었다가 다시 원위치 시키는 운동입니다. 이 운동은 전신 운동을 제공합니다.')");
            sqLiteDatabase.execSQL("insert into exercise values('[트리셉스 체어 딥]',"+R.drawable.eight+",'자신의 뒤에 있는 의자의 앞부분을 두 손으로 잡아 몸을 지탱한 후, 다리를 뻗어 두 발의 뒤꿈치로 고정합니다. 그리고 팔꿈치를 직각으로 만들며 몸을 낮추었다 다시 원위치 시키는 운동입니다. 이 운동은 이두근, 삼두근을 발달시키는 운동입니다.')");
            sqLiteDatabase.execSQL("insert into exercise values('[팔굽혀펴기]',"+R.drawable.nine+",'손바닥을 바닥에 붙힌 상태에서 허리와 다리를 곧게 핀 후 팔꿈치를 접으며 상체를 바닥에 닿지 않으면서 최대한 낮추었다가 다시 팔꿈치를 곧게 펴서 상체를 올리는 운동입니다. 이 운동은 가슴, 어깨, 삼두근, 다리의 근육을 발달시킵니다.')");
            sqLiteDatabase.execSQL("insert into exercise values('[플랭크]',"+R.drawable.ten+",'푸시업 준비동작으로 몸의 무게를 팔로 버티는 운동입니다. 이 운동은 팔꿈치, 발가락, 복근 등과 어깨 근육을 강화하는데 도움이 됩니다.')");

            try {
                sqLiteDatabase.execSQL("CREATE TABLE schedule(date CHAR(30) ,name CHAR(30),value INTEGER ,comment CHAR(40),dateTime LONG );");
            }catch(SQLiteException e)
            {
                //이미 생성되어있음
            }
            sqLiteDatabase.execSQL("CREATE TABLE health(name CHAR(30) ,address CHAR(30),lat DOUBLE ,lng DOUBLE, id INTEGER);");
            insertDB(sqLiteDatabase,"커브스동탄클럽","경기도 화성시 반송동 107-7",37.199688, 127.072039,11694460); // id값 추가
            insertDB(sqLiteDatabase,"바디앤소울 스포츠클럽 동탄점","경기도 화성시 반송동 96",37.204894, 127.068908,17192257);
            insertDB(sqLiteDatabase,"크로스핏마블","경기 화성시 노작로 161 신라스테이션",37.202547, 127.073184,2065147984);
            insertDB(sqLiteDatabase,"숀리바디스쿨 논현점","서울 강남구 논현로116길 6",37.509297, 127.033660,11124690);
            insertDB(sqLiteDatabase,"강남YMCA강남지회","서울 강남구 언주로 615",37.510671, 127.035850,8148017);
            insertDB(sqLiteDatabase,"박찬호피트니스","서울 강남구 테헤란로2길 27",37.497938, 127.027626,18468555);
            insertDB(sqLiteDatabase,"스포애니 논현역점","서울 강남구 학동로4길 15 동화히스토리",37.510493, 127.023489,26495379);
            insertDB(sqLiteDatabase,"나비정원휘트니스","서울 강남구 학동로20길 13",37.550397, 127.145546,11539730);
            insertDB(sqLiteDatabase,"스마트휘트니스","서울 마포구 양화로 127",37.554875, 126.920482,12065481);
            insertDB(sqLiteDatabase,"VIP휘트니스","서울 마포구 월드컵북로 30 동서빌딩",37.557412, 126.919158,16990262);
            //37.554875, 126.920482

        }
        public void insertDB(SQLiteDatabase sqLiteDatabase,String name,String address , double lat , double lng,int id)
        {
            sqLiteDatabase.execSQL("INSERT INTO health values('"+name+"','"+address+"',"+lat+","+lng+","+id+");");
        }
        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS exercise");
             sqLiteDatabase.execSQL("DROP TABLE IF EXISTS health");
            onCreate(sqLiteDatabase);
        }

    }
    public class ScheduleAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return scheduleList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return scheduleList.get(i);
        }
    }
    public class ExerciseAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return exerciseList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return exerciseList.get(i);
        }
    }
    public class SearchAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return searchList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return searchList.get(i);
        }
    }
}