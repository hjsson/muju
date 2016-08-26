package ssonsoft.mujucc;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab); //하단 메일표시이미지
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        Thread thread = new Thread(new Runnable(){  //마켓버젼얻어올시 메인에 쓰면 인터넷연결이 화면오류로 인식할 가능성이 있어서 오류남 스레드로 처리
            @Override
            public void run(){
                try{
                    String store_version = MarketVersionCheck.getMarketVersion(getPackageName());
                    String device_version = "";
                    try {
                        device_version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (store_version.compareTo(device_version) > 0) {  //store_version이 크면 양수 같으면 0 작으면 음수
                        // 업데이트 필요
                        //startAlertDialog();
                        Handler mHandler = new Handler(Looper.getMainLooper());     //스레드안에 다른 메모리를 올리면 에러남 핸들러로 컨트롤
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(MainActivity.this);
                                alert_confirm.setMessage("최신버전이 아닙니다. 업데이트 화면으로 이동하시겠습니까?").setCancelable(false).setPositiveButton("이동",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {    //마켓이동
                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.setData(Uri.parse("market://details?id="+getPackageName()));
                                                startActivity(intent);
                                                finish();
                                            }
                                        }).setNegativeButton("종료",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                                return;
                                            }
                                        });
                                AlertDialog alert = alert_confirm.create();
                                alert.show();
                            }
                        }, 0);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.tapone, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);

            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if(position == 0){
                return new TapOne();
            }else{
                return new TapTwo();
            }
        }

        @Override
        public int getCount() { //탭갯수
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {    //탭표시 text
            switch (position) {
                case 0:
                    return "향토 사료";
                case 1:
                    return "향토 간행물";
            }
            return null;
        }
    }

    public void onClick(View v) throws IOException {
        //String BOOK_NAME = String.valueOf(v.getTag())+".epub";
        String BOOK_NAME = String.valueOf(v.getTag());  //클릭시 화면tag에 있는 문서번호
        if(BOOK_NAME.equals("00")){ //준비중임
            Toast.makeText(getApplicationContext(), "자료 등록 준비중입니다.", Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent(this, BookView.class);
            intent.putExtra("display", BOOK_NAME);
            startActivity(intent);
        }
        /*contentDetails = new ArrayList<RowData>();
        //http://stackoverflow.com/questions/5640728/render-epub-files-in-android   여기에 소스있음
        AssetManager assetManager = getAssets();
        try {
            InputStream epubInputStream = assetManager.open(BOOK_NAME);
            Book book = (new EpubReader()).readEpub(epubInputStream);
            logContentsTable(book.getTableOfContents().getTocReferences(), 0);
        } catch (IOException e) {
            Log.e("epublib", e.getMessage());
        }
        RowData rowData = contentDetails.get(0);*/
    }
    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("무주 문화원 향토사료");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("프로그램을 종료하시겠습니까?");
        builder.setNegativeButton("아니오", null);
        builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
// TODO Auto-generated method stub
                moveTaskToBack(true);
                finish();
            }
        });
        builder.show();
    }
   /* private void logContentsTable(List<TOCReference> tocReferences, int depth) {
        if (tocReferences == null) {
            return;
        }
        for (TOCReference tocReference:tocReferences) {
            StringBuilder tocString = new StringBuilder();
            for (int i = 0; i < depth; i++) {
                tocString.append("\t");
            }
            tocString.append(tocReference.getTitle());
            RowData row = new RowData();
            row.setTitle(tocString.toString());
            Log.i("epublib",new String(tocString.toString()));
            row.setResource(tocReference.getResource());
            contentDetails.add(row);
            logContentsTable(tocReference.getChildren(), depth + 1);
        }
    }*/
    /*private class RowData{
        private String title;
        private Resource resource;

        public RowData() {
            super();
        }

        public String getTitle() {
            return title;
        }

        public Resource getResource() {
            return resource;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setResource(Resource resource) {
            this.resource = resource;
        }

    }*/
}
