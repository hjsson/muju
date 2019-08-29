package ssonsoft.mujucc;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import tech.qiji.android.mupdf.FilePicker;
import tech.qiji.android.mupdf.MuPDFCore;
import tech.qiji.android.mupdf.MuPDFPageAdapter;
import tech.qiji.android.mupdf.MuPDFReaderView;

public class BookView extends AppCompatActivity {
    private final int MY_PERMISSION_REQUEST_STORAGE = 100;
    private RelativeLayout mBookView;
    private String File_Name = "bookName.pdf";
    private String book_url = "";
    private String tag_string = "";
    private String fileURL = "http://muju.kccf.or.kr";// URL
    private String Save_Path = Environment.getExternalStorageDirectory().getPath() + File.separator; //getPath() 는 /가 붙어서 옴
    private String Save_folder = "muju";
    private DownloadThread dThread;
    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookview);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("E-BOOK 자료를 불러오는 중입니다.");
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();

        String displayString = getIntent().getExtras().getString("display");    //탭에서 넘어온 책 번호

        if(displayString.equals("")) {
            Toast.makeText(getApplication(), "E-BOOK 자료를 찾을수가 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }

        book_url = "http://muju.kccf.or.kr/pdf_js/web/viewer_pdf.html?ebook_seq="+displayString+"&conf_kccf_addr=muju";

        JsoupAsyncTask jsoupAsyncTaskOne = new JsoupAsyncTask();
        jsoupAsyncTaskOne.execute();

        mBookView = (RelativeLayout) findViewById(R.id.bookview);

        Button btnApple=(Button)findViewById(R.id.CloseButton);

        btnApple.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                String ext = Environment.getExternalStorageState(); //저장소 읽기 쓰기 가능한지
                if (ext.equals(Environment.MEDIA_MOUNTED)) {
                    // 다운로드 폴더에 동일한 파일명이 존재하는지 확인해서 있으면 삭제
                    if (new File(Save_Path + Save_folder + "/" + File_Name).exists() != false) {
                        //loadingBar.setVisibility(View.VISIBLE);
                        File file = new File(Save_Path + Save_folder + File.separator + File_Name);
                        file.delete();
                    }
                };
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        String ext = Environment.getExternalStorageState(); //저장소 읽기 쓰기 가능한지
        if (ext.equals(Environment.MEDIA_MOUNTED)) {
            // 다운로드 폴더에 동일한 파일명이 존재하는지 확인해서 있으면 삭제
            if (new File(Save_Path + Save_folder + File.separator + File_Name).exists() != false) {
                //loadingBar.setVisibility(View.VISIBLE);
                File file = new File(Save_Path + Save_folder + File.separator + File_Name);
                file.delete();
            }
        };

        finish();
    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document docOne = Jsoup.connect(book_url).get();
                Elements content_book = docOne.select("script");
                tag_string = content_book+ "";
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            int tempStart = tag_string.indexOf("/home/main");
            int tempEnd = tag_string.indexOf("muju");
            fileURL = fileURL+tag_string.substring(tempStart,tempEnd+4);
            //Log.i("bbbbbbbbbbbbb", "파일주소 : " + fileURL);
            // 다운로드 경로를 외장메모리 사용자 지정 폴더로 함.
            String ext = Environment.getExternalStorageState();
            if (ext.equals(Environment.MEDIA_MOUNTED)) {

                // 다운로드 폴더에 동일한 파일명이 존재하는지 확인해서 있으면 삭제
                if (new File(Save_Path + Save_folder + File.separator + File_Name).exists() != false) {
                    //loadingBar.setVisibility(View.VISIBLE);
                    File file = new File(Save_Path + Save_folder + File.separator + File_Name);
                    file.delete();
                }

                File dir = new File(Save_Path + Save_folder);
                // 19(4.4)이상부터는 새로운 코드 적용
                if (Build.VERSION.SDK_INT > 22) {
                    checkPermission();
                } else {
                    // 18이하는 기존코드
                    // 폴더가 존재하지 않을 경우 폴더를 만듦
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    // 다운로드 폴더에 동일한 파일명이 존재하는지 확인해서
                    // 없으면 다운받고 있으면 해당 파일 실행시킴.
                    if (new File(Save_Path + Save_folder + File.separator + File_Name).exists() == false){

                        //loadingBar.setVisibility(View.VISIBLE);
                        dThread = new DownloadThread(fileURL, Save_Path + Save_folder + File.separator + File_Name);
                        dThread.start();

                        try{
                            dThread.join();
                        }catch(InterruptedException e){
                            Log.e("ERROR3", e.getMessage());
                        }

                        mupdfStart();
                        mProgressDialog.dismiss();
                    } else {
                        mupdfStart();
                        mProgressDialog.dismiss();
                    }
                }
            };
        }
    }
    /**
     * Permission check.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {
        //Log.i("aaaa", "CheckPermission : " + checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE));
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to write the permission.
                Toast.makeText(this, "Read/Write external storage", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST_STORAGE);

            // MY_PERMISSION_REQUEST_STORAGE is an
            // app-defined int constant

        } else {
            writeFile();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    writeFile();

                    // permission was granted, yay! do the
                    // calendar task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
        }
    }

    /**
     * Create file example.
     * Save_Path = Environment.getExternalStorageDirectory()
     .getAbsolutePath() + Save_folder;
     */
    private void writeFile() {
        String ext = Environment.getExternalStorageState();
        if (ext.equals(Environment.MEDIA_MOUNTED)) {

            File dir = new File(Save_Path + Save_folder);
            // 폴더가 존재하지 않을 경우 폴더를 만듦
            if (!dir.exists()) {
                dir.mkdir();
            }
            // 다운로드 폴더에 동일한 파일명이 존재하는지 확인해서
            // 없으면 다운받고 있으면 해당 파일 실행시킴.

            if (new File(Save_Path + Save_folder + File.separator + File_Name).exists() == false) {
                //loadingBar.setVisibility(View.VISIBLE);
                dThread = new DownloadThread(fileURL,
                        Save_Path + Save_folder + File.separator + File_Name);
                dThread.start();
                try{
                    dThread.join();
                }catch(InterruptedException e){
                    Log.e("ERROR3", e.getMessage());
                }
                mupdfStart();
                mProgressDialog.dismiss();
            } else {
                mupdfStart();
                mProgressDialog.dismiss();
            }
        };



        //Log.d("aaaa", "create new File : " + Environment.getExternalStorageDirectory().getPath() + File.separator + "temp.txt");

    }

    private void mupdfStart() {

        MuPDFCore core = null;
        try {
            core = new MuPDFCore(getApplication(), Save_Path + Save_folder + File.separator + File_Name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MuPDFReaderView reder = new MuPDFReaderView(getApplication());

        reder.setAdapter(new MuPDFPageAdapter(getApplication(), new FilePicker.FilePickerSupport() {
            @Override
            public void performPickFor(FilePicker filePicker) {

            }
        }, core));
        mBookView.addView(reder);

    }

    // 다운로드 쓰레드로 돌림..
    class DownloadThread extends Thread {
        String ServerUrl;
        String LocalPath;

        DownloadThread(String serverPath, String localPath) {
            ServerUrl = serverPath;
            LocalPath = localPath;
        }

        @Override
        public void run() {
            URL imgurl;
            int Read;
            try {
                imgurl = new URL(ServerUrl);
                HttpURLConnection conn = (HttpURLConnection) imgurl
                        .openConnection();
                int len = conn.getContentLength();
                byte[] tmpByte = new byte[len];
                InputStream is = conn.getInputStream();
                File file = new File(LocalPath);
                FileOutputStream fos = new FileOutputStream(file);
                for (;;) {
                    Read = is.read(tmpByte);
                    if (Read <= 0) {
                        break;
                    }
                    fos.write(tmpByte, 0, Read);
                }
                is.close();
                fos.close();
                conn.disconnect();

            } catch (MalformedURLException e) {
                Log.e("ERROR1", e.getMessage());
            } catch (IOException e) {
                Log.e("ERROR2", e.getMessage());
                e.printStackTrace();
            }
        }
    }
    /*private void showDownloadFile() {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        File file = new File(Save_Path + "/" + File_Name);

        // 파일 확장자 별로 mime type 지정해 준다.
        if (File_extend.equals("mp3")) {
            intent.setDataAndType(Uri.fromFile(file), "audio*//*");
        } else if (File_extend.equals("mp4")) {
            intent.setDataAndType(Uri.fromFile(file), "vidio*//*");
        } else if (File_extend.equals("jpg") || File_extend.equals("jpeg")
                || File_extend.equals("JPG") || File_extend.equals("gif")
                || File_extend.equals("png") || File_extend.equals("bmp")) {
            intent.setDataAndType(Uri.fromFile(file), "image*//*");
        } else if (File_extend.equals("txt")) {
            intent.setDataAndType(Uri.fromFile(file), "text*//*");
        } else if (File_extend.equals("doc") || File_extend.equals("docx")) {
            intent.setDataAndType(Uri.fromFile(file), "application/msword");
        } else if (File_extend.equals("xls") || File_extend.equals("xlsx")) {
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.ms-excel");
        } else if (File_extend.equals("ppt") || File_extend.equals("pptx")) {
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.ms-powerpoint");
        } else if (File_extend.equals("pdf")) {
            intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        }
        startActivity(intent);
    }*/
}
