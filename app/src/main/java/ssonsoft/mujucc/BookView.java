package ssonsoft.mujucc;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.bumptech.glide.Glide;

public class BookView extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookview);
        webView = (WebView) findViewById(R.id.webview);
        //webView.getSettings().setJavaScriptEnabled(true);

        Button btnApple=(Button)findViewById(R.id.CloseButton);
        btnApple.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });


        String displayString = getIntent().getExtras().getString("display");
        //AssetManager am = getResources().getAssets();
        //Log.i("epublib",new String(am+displayString));
        String tempUrl = "http://muju.kccf.or.kr/pdf_js/web/viewer_pdf.html?ebook_seq="+displayString+"&conf_kccf_addr=muju";
        //Log.i("epublib",new String(tempUrl));
        //webView.loadUrl("http://muju.kccf.or.kr/pdf_js/web/viewer_pdf.html?ebook_seq=26&conf_kccf_addr=muju");
        //Log.i("epublib",new String("file:///android_asset/"+displayString));

       if(displayString != null) {
           // webView.loadUrl(tempUrl);
           startWebView(webView,tempUrl);
        }
    }

    private void startWebView(WebView webView,String url) {
        webView.setWebViewClient(new WebViewClient() {
            ProgressDialog progressDialog;
            int tempindex = 0;
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            public void onLoadResource (WebView view, String url) {

                if (progressDialog == null) {
                    tempindex++;
                    progressDialog = new ProgressDialog(BookView.this);
                    progressDialog.setMessage("Loading...");
                    if(tempindex < 3){
                        progressDialog.show();
                    }

                }

            }
            public void onPageFinished(WebView view, String url) {
                try{
                    if (progressDialog.isShowing()) {

                        progressDialog.dismiss();
                        progressDialog.cancel();
                        progressDialog = null;
                    }

                }catch(Exception exception){
                    exception.printStackTrace();
                }
            }

        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setBackgroundColor(0x00000000);
        webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        webView.loadUrl(url);
    }

}
