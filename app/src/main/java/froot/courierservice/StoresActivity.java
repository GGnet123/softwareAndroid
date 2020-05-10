package froot.courierservice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

import froot.courierservice.retorfit.NetworkClient;

public class StoresActivity extends AppCompatActivity {
    private WebView webview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores);

        String url = NetworkClient.BASE_URL + "runners/stores";
        WebView webView = (WebView)findViewById(R.id.stores_view);
//you can load an html code
        webView.loadData("yourCode Html to load on the webView " , "text/html" , "utf-8");
// you can load an URL
        webView.loadUrl(url);
    }
}
