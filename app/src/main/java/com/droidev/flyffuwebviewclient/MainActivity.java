package com.droidev.flyffuwebviewclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private WebView mClientWebView, sClientWebView;
    private FrameLayout mClient, sClient;
    private LinearLayout linearLayout;
    private FloatingActionButton floatingActionButton;

    private boolean exit = false;
    private boolean isOpen = false;

    private Menu optionMenu;

    private final String url = "https://universe.flyff.com/play";
    private final String userAgent = System.getProperty("http.agent");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("FlyffU Android - Main Client");

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        linearLayout = findViewById(R.id.linearLayout);
        mClient = findViewById(R.id.frameLayoutMainClient);
        sClient = findViewById(R.id.frameLayoutSecondClient);
        floatingActionButton = findViewById(R.id.fab);

        // IMPORTANT: use Activity context, not application context
        mClientWebView = new WebView(this);
        sClientWebView = new WebView(this);

        floatingActionButton.setOnClickListener(v -> {
            if (sClient.getVisibility() == View.VISIBLE) {
                sClient.setVisibility(View.GONE);
                mClient.setVisibility(View.VISIBLE);
                setTitle("FlyffU Android - Main Client");
            } else {
                sClient.setVisibility(View.VISIBLE);
                mClient.setVisibility(View.GONE);
                setTitle("FlyffU Android - Second Client");
            }
        });

        mainClient();

        if (savedInstanceState == null) {
            mClientWebView.loadUrl(url);
            sClientWebView.loadUrl(url);
        }
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish();
        } else {
            Toast.makeText(this, "Press Back again to Exit.", Toast.LENGTH_SHORT).show();
            exit = true;
            fullScreenOff();
            new Handler().postDelayed(() -> exit = false, 3000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mClient.removeAllViews();
        sClient.removeAllViews();
        mClientWebView.destroy();
        sClientWebView.destroy();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.secondClient:
                if (sClient.getVisibility() == View.GONE && !isOpen) {

                    sClient.setVisibility(View.VISIBLE);
                    secondClient();

                    optionMenu.findItem(R.id.secondClient)
                            .setTitle("Close Second Client");

                    optionMenu.findItem(R.id.reloadSecondClient)
                            .setEnabled(true);

                    floatingActionButton.setVisibility(View.VISIBLE);
                    isOpen = true;

                } else {

                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setCancelable(false)
                            .setTitle("Are you sure you want to close the second client?")
                            .setPositiveButton("Yes", null)
                            .setNegativeButton("No", null)
                            .show();

                    Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setOnClickListener(v -> {

                        sClient.removeAllViews();
                        sClientWebView.loadUrl("about:blank");
                        sClient.setVisibility(View.GONE);

                        optionMenu.findItem(R.id.secondClient)
                                .setTitle("Open Second Client");

                        optionMenu.findItem(R.id.reloadSecondClient)
                                .setEnabled(false);

                        if (mClient.getVisibility() == View.GONE) {
                            mClient.setVisibility(View.VISIBLE);
                        }

                        floatingActionButton.setVisibility(View.GONE);
                        isOpen = false;

                        setTitle("FlyffU Android - Main Client");
                        Toast.makeText(this, "Second Client closed.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    });
                }
                break;

            case R.id.reloadMainClient:
                mClientWebView.reload();
                break;

            case R.id.reloadSecondClient:
                sClientWebView.reload();
                break;

            case R.id.fullScreen:
                fullScreenOn();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        optionMenu = menu;
        return true;
    }

    private void fullScreenOn() {
        View decorView = getWindow().getDecorView();

        int uiOptions =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        decorView.setSystemUiVisibility(uiOptions);
    }

    private void fullScreenOff() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }

    private void mainClient() {
        createWebViewer(mClientWebView, mClient);
    }

    private void secondClient() {
        createWebViewer(sClientWebView, sClient);
    }

    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    private void createWebViewer(WebView webView, FrameLayout frameLayout) {

        webView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        webView.requestFocus(View.FOCUS_DOWN);
        webView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN ||
                    event.getAction() == MotionEvent.ACTION_UP) {
                if (!v.hasFocus()) v.requestFocus();
            }
            return false;
        });

        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUserAgentString(userAgent);

        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        // Correct, modern cache behavior
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        frameLayout.addView(webView);
        webView.loadUrl(url);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mClientWebView.saveState(outState);
        sClientWebView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mClientWebView.restoreState(savedInstanceState);
        sClientWebView.restoreState(savedInstanceState);
    }
}