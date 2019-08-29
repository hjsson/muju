package ssonsoft.mujucc;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.view.Menu;
import android.view.MenuItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private String baseHomeUrl = "http://muju.kccf.or.kr";  //html 화면파싱후 붙일 기본 홈페이지 주소
    private String urlIl = "http://muju.kccf.or.kr/home/ebook/index.php?conf_kccf_addr=muju&page=1";
    private String urlE = "http://muju.kccf.or.kr/home/ebook/index.php?conf_kccf_addr=muju&page=2";
    private String urlSam = "http://muju.kccf.or.kr/home/ebook/index.php?conf_kccf_addr=muju&page=3";
    private String urlSa = "http://muju.kccf.or.kr/home/ebook/index.php?conf_kccf_addr=muju&page=4";
    private String urlO = "http://muju.kccf.or.kr/home/ebook/index.php?conf_kccf_addr=muju&page=5";
    private String urlYuk = "http://muju.kccf.or.kr/home/ebook/index.php?conf_kccf_addr=muju&page=6";
    private String urlChl = "http://muju.kccf.or.kr/home/ebook/index.php?conf_kccf_addr=muju&page=7";
    private String urlPal = "http://muju.kccf.or.kr/home/ebook/index.php?conf_kccf_addr=muju&page=8";
    private String urlGu = "http://muju.kccf.or.kr/home/ebook/index.php?conf_kccf_addr=muju&page=9";
    private List<String> nameListBan = new ArrayList<String>();
    private List<String> addrNumListBan = new ArrayList<String>();
    private List<String> imgListBan = new ArrayList<String>();
    private List<String> nameListHiangto = new ArrayList<String>();
    private List<String> addrNumListHiangto = new ArrayList<String>();
    private List<String> imgListHiangto = new ArrayList<String>();

    ProgressDialog mProgressDialog;      //로딩메시지

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //메인화면셋팅

        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setTitle("향토사료 자료를 불러오는 중입니다.");
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();

        JsoupAsyncTask jsoupAsyncTaskOne = new JsoupAsyncTask();
        jsoupAsyncTaskOne.execute();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); //툴바
        setSupportActionBar(toolbar);

        Thread thread = new Thread(new Runnable(){  //마켓버젼얻어올시 메인에 쓰면 인터넷연결이 화면오류로 인식할 가능성이 있어서 오류남 스레드로 처리
            @Override
            public void run(){  //버젼체크하여 현재게시된 버젼보다 낮으면 플레이 스토어 화면으로 이동
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

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if(position == 0){
                return new TapOne(nameListHiangto,addrNumListHiangto,imgListHiangto); //향토사료
                //return new TapOne(); //향토사료
            }else{
                return new TapTwo(nameListBan,addrNumListBan,imgListBan); //반딧불문화
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
                    return "반딧골 문화";
            }
            return null;
        }
    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {
        String aName = "";    //화면에서 가져온 책이름
        String aAddr= "";     //화면에서 가져온 책번호
        String aImg= "";      //화면에서 가져온 책이미지주소
        String bName = "";
        String bAddr= "";
        String bImg= "";
        String cName = "";
        String cAddr= "";
        String cImg= "";
        String dName = "";
        String dAddr= "";
        String dImg= "";
        String eName = "";
        String eAddr= "";
        String eImg= "";
        String fName = "";
        String fAddr= "";
        String fImg= "";
        String gName = "";
        String gAddr= "";
        String gImg= "";
        String hName = "";
        String hAddr= "";
        String hImg= "";
        String iName = "";
        String iAddr= "";
        String iImg= "";
        String nameTotal = "";       //화면에서 가져온 책이름 모두 합친거
        String bookAddrTotal= "";   //화면에서 가져온 책번호 모두 합친거
        String imgTotal= "";         //화면에서 가져온 이미지 주소 모두 합친거
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                //1page
                Document docOne = Jsoup.connect(urlIl).get();
                Elements content_book_one = docOne.select("div.book_list_box");
                Elements name_book_one = content_book_one.select("dt");
                Elements addr_book_one = content_book_one.select("a");
                Elements img_addr_one = content_book_one.select("img");

                for (org.jsoup.nodes.Element book_names_one : name_book_one) {
                    aName += book_names_one.text() + "\n";        //줄바꿈으로 구분할경우
                }
                for (org.jsoup.nodes.Element addr_books_one : addr_book_one) {
                    aAddr += addr_books_one.attr("onclick") + "\n";
                }
                for (org.jsoup.nodes.Element img_addrs_one : img_addr_one) {
                    aImg += baseHomeUrl+img_addrs_one.attr("src") + "\n";
                }
                //2page
                Document docTwo = Jsoup.connect(urlE).get();
                Elements content_book_two = docTwo.select("div.book_list_box");
                Elements name_book_two = content_book_two.select("dt");
                Elements addr_book_two = content_book_two.select("a");
                Elements img_addr_two = content_book_two.select("img");

                for (org.jsoup.nodes.Element book_names_two : name_book_two) {
                    bName += book_names_two.text() + "\n";
                }
                for (org.jsoup.nodes.Element addr_books_two : addr_book_two) {
                    bAddr += addr_books_two.attr("onclick") + "\n";
                }
                for (org.jsoup.nodes.Element img_addrs_two : img_addr_two) {
                    bImg += baseHomeUrl+img_addrs_two.attr("src") + "\n";
                }
                //3page
                Document docThree = Jsoup.connect(urlSam).get();
                Elements content_book_three = docThree.select("div.book_list_box");
                Elements name_book_three = content_book_three.select("dt");
                Elements addr_book_three = content_book_three.select("a");
                Elements img_addr_three = content_book_three.select("img");

                for (org.jsoup.nodes.Element book_names_three : name_book_three) {
                    cName += book_names_three.text() + "\n";
                }
                for (org.jsoup.nodes.Element addr_books_three : addr_book_three) {
                    cAddr += addr_books_three.attr("onclick") + "\n";
                }
                for (org.jsoup.nodes.Element img_addrs_three : img_addr_three) {
                    cImg += baseHomeUrl+img_addrs_three.attr("src") + "\n";
                }
                //4page
                Document docfour = Jsoup.connect(urlSa).get();
                Elements content_book_four = docfour.select("div.book_list_box");
                Elements name_book_four = content_book_four.select("dt");
                Elements addr_book_four = content_book_four.select("a");
                Elements img_addr_four = content_book_four.select("img");

                for (org.jsoup.nodes.Element book_names_four : name_book_four) {
                    dName += book_names_four.text() + "\n";
                }
                for (org.jsoup.nodes.Element addr_books_four : addr_book_four) {
                    dAddr += addr_books_four.attr("onclick") + "\n";
                }
                for (org.jsoup.nodes.Element img_addrs_four : img_addr_four) {
                    dImg += baseHomeUrl+img_addrs_four.attr("src") + "\n";
                }
                //5page
                Document docfive = Jsoup.connect(urlO).get();
                Elements content_book_five = docfive.select("div.book_list_box");
                Elements name_book_five = content_book_five.select("dt");
                Elements addr_book_five = content_book_five.select("a");
                Elements img_addr_five = content_book_five.select("img");

                for (org.jsoup.nodes.Element book_names_five : name_book_five) {
                    eName += book_names_five.text() + "\n";
                }
                for (org.jsoup.nodes.Element addr_books_five : addr_book_five) {
                    eAddr += addr_books_five.attr("onclick") + "\n";
                }
                for (org.jsoup.nodes.Element img_addrs_five : img_addr_five) {
                    eImg += baseHomeUrl+img_addrs_five.attr("src") + "\n";
                }
                //6page
                Document docsix = Jsoup.connect(urlYuk).get();
                Elements content_book_six = docsix.select("div.book_list_box");
                Elements name_book_six = content_book_six.select("dt");
                Elements addr_book_six = content_book_six.select("a");
                Elements img_addr_six = content_book_six.select("img");

                for (org.jsoup.nodes.Element book_names_six : name_book_six) {
                    fName += book_names_six.text() + "\n";
                }
                for (org.jsoup.nodes.Element addr_books_six : addr_book_six) {
                    fAddr += addr_books_six.attr("onclick") + "\n";
                }
                for (org.jsoup.nodes.Element img_addrs_six : img_addr_six) {
                    fImg += baseHomeUrl+img_addrs_six.attr("src") + "\n";
                }
                //7page
                Document docseven = Jsoup.connect(urlChl).get();
                Elements content_book_seven = docseven.select("div.book_list_box");
                Elements name_book_seven = content_book_seven.select("dt");
                Elements addr_book_seven = content_book_seven.select("a");
                Elements img_addr_seven = content_book_seven.select("img");

                for (org.jsoup.nodes.Element book_names_seven : name_book_seven) {
                    gName += book_names_seven.text() + "\n";
                }
                for (org.jsoup.nodes.Element addr_books_seven : addr_book_seven) {
                    gAddr += addr_books_seven.attr("onclick") + "\n";
                }
                for (org.jsoup.nodes.Element img_addrs_seven : img_addr_seven) {
                    gImg += baseHomeUrl+img_addrs_seven.attr("src") + "\n";
                }
                //8page
                Document doceight = Jsoup.connect(urlPal).get();
                Elements content_book_eight = doceight.select("div.book_list_box");
                Elements name_book_eight = content_book_eight.select("dt");
                Elements addr_book_eight = content_book_eight.select("a");
                Elements img_addr_eight = content_book_eight.select("img");

                for (org.jsoup.nodes.Element book_names_eight : name_book_eight) {
                    hName += book_names_eight.text() + "\n";
                }
                for (org.jsoup.nodes.Element addr_books_eight : addr_book_eight) {
                    hAddr += addr_books_eight.attr("onclick") + "\n";
                }
                for (org.jsoup.nodes.Element img_addrs_eight : img_addr_eight) {
                    hImg += baseHomeUrl+img_addrs_eight.attr("src") + "\n";
                }
                //9page
                Document docnine = Jsoup.connect(urlGu).get();
                Elements content_book_nine = docnine.select("div.book_list_box");
                Elements name_book_nine = content_book_nine.select("dt");
                Elements addr_book_nine = content_book_nine.select("a");
                Elements img_addr_nine = content_book_nine.select("img");

                for (org.jsoup.nodes.Element book_names_nine : name_book_nine) {
                    iName += book_names_nine.text() + "\n";
                }
                for (org.jsoup.nodes.Element addr_books_nine : addr_book_nine) {
                    iAddr += addr_books_nine.attr("onclick") + "\n";
                }
                for (org.jsoup.nodes.Element img_addrs_nine : img_addr_nine) {
                    iImg += baseHomeUrl+img_addrs_nine.attr("src") + "\n";
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            nameTotal = aName+bName+cName+dName+eName+fName+gName+hName+iName;
            bookAddrTotal = aAddr+bAddr+cAddr+dAddr+eAddr+fAddr+gAddr+hAddr+iAddr;
            imgTotal = aImg+bImg+cImg+dImg+eImg+fImg+gImg+hImg+iImg;

            if(nameTotal.equals("")){
                finish();
            }

            String[] nameList;
            String[] AddrNumList;
            String[] imgList;

            nameList = nameTotal.split("\n");  //줄바꿈으로 구분하여 배열로 담음
            AddrNumList = bookAddrTotal.split("\n");
            imgList = imgTotal.split("\n");

            for(int i=0;i<AddrNumList.length;i++){
                String[] temp = new String[4];
                temp = AddrNumList[i].split("'");           //'00' 안의 숫자만 필요함
                AddrNumList[i] = temp[1];                   //두번째 배열에 책번호가 들어있음 23^1 형식으로 저장
            };

            for(int i=0;i<nameList.length;i++){           //책제목과 이미지 주소에 ^0, ^1, ^2 순서를 붙여줌
                nameList[i] = nameList[i]+";"+i;
                if(nameList[i].indexOf("반딧골") > -1){    //반딧골과 향토를 분리
                    nameListBan.add(nameList[i]);
                }else{
                    nameListHiangto.add(nameList[i]);
                };
            };
            //String[] 생성했을때 정렬
            //Arrays.sort(nameListBan, String.CASE_INSENSITIVE_ORDER);   //향토사료 가나다순으로 정렬함
            //Arrays.sort(nameListHiangto, String.CASE_INSENSITIVE_ORDER);   //반딧골문화 가나다순으로 정렬함
            //ArryList 생성했을때 정렬
            Collections.sort(nameListBan);
            Collections.sort(nameListHiangto);

            //세부정렬 요청
            String tempchengi = "";
            String tempaul = "";
            int remeberchengi = 0;
            if(nameListHiangto.size() > 23){
                for(int i=0;i<nameListHiangto.size();i++){  //향토 임의 정렬
                    tempchengi = nameListHiangto.get(i);
                    tempaul = nameListHiangto.get(i);
                    if(tempchengi.indexOf("적성지 속지") > -1){    //적성지 순서 바꿔달라요청함 속지 지 천 순으로나옴 (천,지,속지 순으로) 김정호 국장
                        remeberchengi = i;
                    }
                    if(tempchengi.indexOf("적성지(천)") > -1){
                        nameListHiangto.set(i,nameListHiangto.get(remeberchengi));    //천 자리에 속지를 먼저 넣고
                        nameListHiangto.set(remeberchengi,tempchengi);                    //속지 자리에 천을 넣고
                    }
                    if(tempaul.indexOf("얼(1-4)") > -1){      //가나다순으로는 얼1-4, 증보판, 종합편순이지만 얼(증보판), 얼(종합편), 얼(1-4) 순으로 변경요청함
                        nameListHiangto.set(i,nameListHiangto.get(i+1));    //증보판,증보판 종합편
                        nameListHiangto.set(i+1,nameListHiangto.get(i+2));  //증보판, 종합편, 종합편
                        nameListHiangto.set(i+2,tempaul);                      //증보판, 봉합편, 1-4
                        break;  //마지막에 또 돌아가면 에러남
                    }
                };
            }
            List<String> list_temp = new ArrayList<String>();   //10번이하와 창간호 담을곳
            String tempBan = "";
            if(nameListBan.size() > 22) {
                for (int i = 0; i < nameListBan.size(); i++) {  //반딧골 임의 정렬 가나다 정렬이므로 10번대가 가장 앞에 나옴
                    tempBan = nameListBan.get(i);
                    if (tempBan.indexOf("문화 2호") > -1) {
                        list_temp.add(nameListBan.get(i));
                        list_temp.add(nameListBan.get(i + 1));
                        list_temp.add(nameListBan.get(i + 2));
                        list_temp.add(nameListBan.get(i + 3));
                        list_temp.add(nameListBan.get(i + 4));
                        list_temp.add(nameListBan.get(i + 5));
                        list_temp.add(nameListBan.get(i + 6));
                        list_temp.add(nameListBan.get(i + 7));
                    }
                    if (tempBan.indexOf("창간호") > -1) {
                        list_temp.add(0, nameListBan.get(i));
                        for (int j = 0; j < 9; j++) {  //반딧골 임의 정렬
                            nameListBan.remove(i - 8);    //뒤에 붙은 10번이하대와 창간호 삭제
                        }
                    }
                };
                nameListBan.addAll(0,list_temp);
            };

            //이미지주소와 책번호를 책이름 정렬대로 정렬
            String[] tempIndexArry;
            List<String> nameIdxListHiangto = new ArrayList<String>(); //순번만 저장할 리스트
            List<String> nameIdxListBan = new ArrayList<String>();

            for(int i=0;i<nameListHiangto.size();i++){
                tempIndexArry = nameListHiangto.get(i).split(";");  //향토 뒤에 순번을 분리
                nameListHiangto.set(i,tempIndexArry[0]);            //순번분리한 제목을 원자리에 셋팅
                nameIdxListHiangto.add(tempIndexArry[1]);              //분리한 순번을 임시리스트에 저장
            };
            for(int i=0;i<nameListBan.size();i++){                  //반딧골문화
                tempIndexArry = nameListBan.get(i).split(";");
                nameListBan.set(i,tempIndexArry[0]);
                nameIdxListBan.add(tempIndexArry[1]);
            };
            //향토 책번호, 이미지 주소 따로 저장
            for(int j=0;j<nameIdxListHiangto.size();j++){   //따로 저장한 향토 순번수량만큼
                addrNumListHiangto.add(AddrNumList[Integer.parseInt(nameIdxListHiangto.get(j))]);
                imgListHiangto.add(imgList[Integer.parseInt(nameIdxListHiangto.get(j))]);
            };
            //반딧골 책번호, 이미지 주소 따로 저장
            for(int j=0;j<nameIdxListBan.size();j++){   //따로 저장한 향토 순번수량만큼
                addrNumListBan.add(AddrNumList[Integer.parseInt(nameIdxListBan.get(j))]);
                imgListBan.add(imgList[Integer.parseInt(nameIdxListBan.get(j))]);
            };

            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());  //각각의 탭을 찾아 탭어탭터 셋팅
            mViewPager = (ViewPager) findViewById(R.id.container);      //각각의 탭화면을 넣을곳
            mViewPager.setAdapter(mSectionsPagerAdapter);             //생성한 탭어댑터를 화면에 넣는다
            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);    //탭표시부분
            tabLayout.setupWithViewPager(mViewPager);

            mProgressDialog.dismiss();  //로딩이미지 제거
        }
    }

    @Override
    public void onBackPressed() {   //백버튼호출시 종료여부팝업창 호출

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("무주 문화원 향토사료");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("프로그램을 종료하시겠습니까?");
        builder.setNegativeButton("아니오", null);
        builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                moveTaskToBack(true);
                finish();
            }
        });
        builder.show();
    }
}