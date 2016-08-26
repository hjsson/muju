package ssonsoft.mujucc;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

public class Intro extends AppCompatActivity {
    Handler x ;  //액티비티 핸들러 인트로 지연시간때문에
    Intent intent = null;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        System.gc();    //가비지컬렉션 호출

        setContentView(R.layout.intro); //전체화면 레이아웃 호출

        //Handler x = new Handler();  //액티비티 핸들러 인트로 지연시간때문에
        x = new Handler();           //딜래이를 주기 위해 핸들러 생성

        if (MarketVersionCheck.isNetworkConnected(this)){   //인터넷 사용가능한지
            x.postDelayed(mrun, 1500);
        }else{
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("네트워크 연결").setMessage("네트워크 연결이 완료된 후 다시 시도해 주십시요.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick( DialogInterface dialog, int which )
                        {
                            finish();
                        }
                    }).show();
        }
    }

    Runnable mrun = new Runnable(){
        @Override
        public void run(){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
    };
    /*public class splashhandler implements Runnable{

        public void run(){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
    }*/
    //인트로 중에 뒤로가기를 누를 경우 핸들러를 끊어버려 아무일 없게 만드는 부분
    //미 설정시 인트로 중 뒤로가기를 누르면 인트로 후에 홈화면이 나옴.
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        x.removeCallbacks(mrun);
    }

    private void clearApplicationCache(java.io.File dir){
        if(dir == null)
            dir = getCacheDir();
        else
            ;
        if (dir == null)
            return;
        else
            ;
        java.io.File[] children = dir.listFiles();
        try{
            for (int i=0; i < children.length; i++)
                if (children[i].isDirectory())
                    clearApplicationCache(children[i]);
                else
                    children[i].delete();
        } catch (Exception e) {
        }

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        clearApplicationCache(null);
    }

}
