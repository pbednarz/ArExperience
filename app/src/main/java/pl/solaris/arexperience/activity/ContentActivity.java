package pl.solaris.arexperience.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pnikosis.materialishprogress.ProgressWheel;

import java.lang.reflect.InvocationTargetException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.solaris.arexperience.R;
import pl.solaris.arexperience.utils.Utils;
import rx.Observable;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.schedulers.Schedulers;

public class ContentActivity extends ActionBarActivity {

    public final static String WEBPAGE_EXTRA = "webpage";

    @InjectView(R.id.webview)
    WebView mWebView;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.pb)
    ProgressWheel pb;

    private Subscription screenshotSubscription;
    private Observable<String> screenshotObservable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        configureWebSettings();
        String url = "";
        if (getIntent() != null && getIntent().getStringExtra(WEBPAGE_EXTRA) != null) {
            url = getIntent().getStringExtra(WEBPAGE_EXTRA);
        }
        if (TextUtils.isEmpty(url)) {
            url = "http://google.com";
        }
        pb.spin();
        mWebView.loadUrl(url);
        screenshotObservable = AppObservable.bindActivity(this, Utils.getViewBitmapObservable(mWebView)
                .flatMap(bitmap ->
                        Observable.just(Utils.saveToExternalStorage(ContentActivity.this.getApplicationContext(),
                                bitmap, "webview", ".jpg").toString())))
                .subscribeOn(Schedulers.io());
    }

    public void configureWebSettings() {
        WebSettings s = mWebView.getSettings();
        s.setBuiltInZoomControls(true);
        s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        s.setUseWideViewPort(true);
        s.setLoadWithOverviewMode(true);
        s.setSaveFormData(true);
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setJavaScriptCanOpenWindowsAutomatically(true);
        s.setPluginState(WebSettings.PluginState.ON);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                toolbar.setTitle(title);
            }
        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pb.setVisibility(View.GONE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pb.setVisibility(View.VISIBLE);
            }
        });
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        s.setDomStorageEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_content, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_copy:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("url", mWebView.getUrl());
                clipboard.setPrimaryClip(clip);
                return true;
            case R.id.action_refresh:
                mWebView.reload();
                return true;
            case R.id.action_share:
                screenshotSubscription = screenshotObservable.subscribe(bitmapPath -> {
                    Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("image/*");
                    shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                            "CAR EXAMPLE");
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "message");
                    shareIntent.putExtra(Intent.EXTRA_STREAM,
                            Uri.parse(bitmapPath));
                    startActivity(Intent.createChooser(shareIntent, "Share"));
                });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null) {
            mWebView.stopLoading();
            mWebView.loadUrl("");
            mWebView.reload();
            mWebView = null;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (screenshotSubscription != null) {
            screenshotSubscription.unsubscribe();
        }
        if (mWebView != null) {
            mWebView.destroy();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mWebView != null) {
            mWebView.onPause();
            try {
                Class.forName("android.webkit.WebView")
                        .getMethod("onPause", (Class[]) null)
                        .invoke(mWebView, (Object[]) null);

            } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException ignored) {
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mWebView != null) {
            mWebView.saveState(outState);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mWebView.restoreState(savedInstanceState);
    }
}
