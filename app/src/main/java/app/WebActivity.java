package app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.*;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wang.avi.AVLoadingIndicatorView;

import app.webview.FullscreenHolder;
import app.webview.IWebPageView;
import app.webview.MyWebChromeClient;
import app.webview.MyWebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;

import static app.R.id.video_fullView;

/**
 * 网页可以处理:
 * 点击相应控件:拨打电话、发送短信、发送邮件、上传图片、播放视频
 * 进度条、返回网页上一层、显示网页标题
 */
public class WebActivity extends Activity implements IWebPageView,View.OnClickListener,View.OnTouchListener{

    // 进度条
    @BindView(R.id.pb_progress)
    ProgressBar mProgressBar;
    @BindView(R.id.webview_detail)
    WebView webView;
    // 全屏时视频加载view
    @BindView(video_fullView)
    FrameLayout videoFullView;
    // 进度条是否加载到90%
    public boolean mProgress90;
    // 网页是否加载完成
    public boolean mPageFinish;
    // 加载视频相关
    private MyWebChromeClient mWebChromeClient;

    Activity activity;
    FloatingActionButton fabMenu;
    ImageButton open;
    ImageButton close;
    ImageButton right;
    ImageButton refresh;
    ImageButton left;
    ImageButton share;
    //菜单栏是否显示
    boolean menuIsOpen = true;
    // back button exit time
    private long exitTime = 0;
    LinearLayout menuLinearLayout = null;
    RelativeLayout boxRelativeLayout = null;

    public static AVLoadingIndicatorView loadingView = null;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //
        activity = this;
        super.onCreate(savedInstanceState);
        //
        String mainUrl = activity.getText(R.string.main_web_page_url).toString();
        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);
        //加载网页
        initWebView();
        webView.loadUrl(mainUrl);
        //获取盒子和按钮布局
        menuLinearLayout = findViewById(R.id.menuLinearLayout);
        boxRelativeLayout = findViewById(R.id.boxRelativeLayout);
        loadingView = findViewById(R.id.loadingView);


        //设置撤退按钮
        webView.setOnKeyListener((v, keyCode, event) -> {
            if(event.getAction()==KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK){
                if (webView.canGoBack()){
                    webView.goBack();
                    return true;
                }
                if((System.currentTimeMillis()-exitTime) < 2000){
                    return true;
                }
                Toast.makeText(getApplicationContext(), activity.getString(R.string.back_exit_tip_text), Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
                return true;
            }
            return false;
        });

        //设置按钮点击事件
        fabMenu = findViewById(R.id.fabMenu);
        fabMenu.setOnClickListener(this);
        fabMenu.setOnTouchListener(this);
        open = findViewById(R.id.open);
        open.setOnClickListener(this);
        open.setOnTouchListener(this);
        close = findViewById(R.id.close);
        close.setOnClickListener(this);
        close.setOnTouchListener(this);
        right = findViewById(R.id.right);
        right.setOnClickListener(this);
        right.setOnTouchListener(this);
        left = findViewById(R.id.left);
        left.setOnClickListener(this);
        left.setOnTouchListener(this);
        refresh = findViewById(R.id.refresh);
        refresh.setOnClickListener(this);
        refresh.setOnTouchListener(this);
        share = findViewById(R.id.share);
        share.setOnClickListener(this);
        share.setOnTouchListener(this);

        menuLinearLayout.setVisibility(View.INVISIBLE);
        menuIsOpen = false;

        TinyTools.setStatusBarColor(this, R.color.app_main_page_color);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        mProgressBar.setVisibility(View.VISIBLE);
        webView.setBackgroundColor(0);
        webView.clearCache(true);
        WebSettings ws = webView.getSettings();
        // 网页内容的宽度是否可大于WebView控件的宽度
        ws.setLoadWithOverviewMode(false);
        // 保存表单数据
        ws.setSaveFormData(true);
        // 是否应该支持使用其屏幕缩放控件和手势缩放
        ws.setSupportZoom(true);
        ws.setBuiltInZoomControls(true);
        ws.setDisplayZoomControls(false);
        // 启动应用缓存
        ws.setAppCacheEnabled(true);
        // 设置缓存模式
        ws.setCacheMode(WebSettings.LOAD_DEFAULT);
        // setDefaultZoom  api19被弃用
        // 设置此属性，可任意比例缩放。
        ws.setUseWideViewPort(true);
        // 缩放比例 1
        webView.setInitialScale(1);
        // 告诉WebView启用JavaScript执行。默认的是false。
        ws.setJavaScriptEnabled(true);
        //  页面加载好以后，再放开图片
        ws.setBlockNetworkImage(false);
        // 使用localStorage则必须打开
        ws.setDomStorageEnabled(true);
        // 设置字体默认缩放大小(改变网页字体大小,setTextSize  api14被弃用)
        ws.setTextZoom(100);

        // 排版适应屏幕
//        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        // WebView是否支持多个窗口。
//        ws.setSupportMultipleWindows(true);


        // webview从5.0开始默认不允许混合模式,https中不能加载http资源,需要设置开启。
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }

        mWebChromeClient = new MyWebChromeClient(this);
        webView.setWebChromeClient(mWebChromeClient);
        // 与js交互
//        webView.addJavascriptInterface(new ImageClickInterface(this), "injectedObject");
        webView.setWebViewClient(new MyWebViewClient(this));
    }

    @Override
    public void hindProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void startProgress() {
        startProgress90();
    }

    @Override
    public void showWebView() {
        webView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hindWebView() {
        webView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void fullViewAddView(View view) {
        FrameLayout decor = (FrameLayout) getWindow().getDecorView();
        videoFullView = new FullscreenHolder(WebActivity.this);
        videoFullView.addView(view);
        decor.addView(videoFullView);
    }

    @Override
    public void showVideoFullView() {
        videoFullView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hindVideoFullView() {
        videoFullView.setVisibility(View.GONE);
    }

    @Override
    public void progressChanged(int newProgress) {
        if (mProgress90) {
            int progress = newProgress * 100;
            if (progress > 900) {
                mProgressBar.setProgress(progress);
                if (progress == 1000) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void addImageClickListener() {
        // 这段js函数的功能就是，遍历所有的img节点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
        // 如要点击一张图片在弹出的页面查看所有的图片集合,则获取的值应该是个图片数组
//        webView.loadUrl("javascript:(function(){" +
//                "var objs = document.getElementsByTagName(\"img\");" +
//                "for(var i=0;i<objs.length;i++)" +
//                "{" +
//                //  "objs[i].onclick=function(){alert(this.getAttribute(\"has_link\"));}" +
//                "objs[i].onclick=function(){window.injectedObject.imageClick(this.getAttribute(\"src\"),this.getAttribute(\"baseURI\"));}" +
//                "}" +
//                "})()");
//
//        // 遍历所有的a节点,将节点里的属性传递过去(属性自定义,用于页面跳转)
//        webView.loadUrl("javascript:(function(){" +
//                "var objs =document.getElementsByTagName(\"a\");" +
//                "for(var i=0;i<objs.length;i++)" +
//                "{" +
//                "objs[i].onclick=function(){" +
//                "window.injectedObject.textClick(this.getAttribute(\"href\"),this.getAttribute(\"baseURI\"));}" +
//                "}" +
//                "})()");
    }

    /**
     * 进度条 假装加载到90%
     */
    public void startProgress90() {
        for (int i = 0; i < 900; i++) {
            final int progress = i + 1;
            mProgressBar.postDelayed(() -> {
                mProgressBar.setProgress(progress);
                if (progress == 900) {
                    mProgress90 = true;
                    if (mPageFinish) {
                        startProgress90to100();
                    }
                }
            }, (i + 1) * 2);
        }
    }

    /**
     * 进度条 加载到100%
     */
    public void startProgress90to100() {
        for (int i = 900; i <= 1000; i++) {
            final int progress = i + 1;
            mProgressBar.postDelayed(() -> {
                mProgressBar.setProgress(progress);
                if (progress == 1000) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }, (i + 1) * 2);
        }
    }


    public FrameLayout getVideoFullView() {
        return videoFullView;
    }

    /**
     * 全屏时按返加键执行退出全屏方法
     */
    public void hideCustomView() {
        mWebChromeClient.onHideCustomView();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * 上传图片之后的回调
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == MyWebChromeClient.FILECHOOSER_RESULTCODE) {
            mWebChromeClient.mUploadMessage(intent, resultCode);
        } else if (requestCode == MyWebChromeClient.FILECHOOSER_RESULTCODE_FOR_ANDROID_5) {
            mWebChromeClient.mUploadMessageForAndroid5(intent, resultCode);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //全屏播放退出全屏
            if (mWebChromeClient.inCustomView()) {
                hideCustomView();
                return true;

                //返回网页上一页
            } else if (webView.canGoBack()) {
                webView.goBack();
                return true;

                //退出网页
            } else {
                webView.loadUrl("about:blank");
                finish();
            }
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
        // 支付宝网页版在打开文章详情之后,无法点击按钮下一步
        webView.resumeTimers();
        // 设置为横屏
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoFullView.removeAllViews();
        if (webView != null) {
            ViewGroup parent = (ViewGroup) webView.getParent();
            if (parent != null) {
                parent.removeView(webView);
            }
            webView.removeAllViews();
            webView.loadUrl("about:blank");
            webView.stopLoading();
            webView.setWebChromeClient(null);
            webView.setWebViewClient(null);
            webView.destroy();
            webView = null;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch(v.getId()){
            case R.id.close:
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    v.setBackgroundResource(R.drawable.close_press);
                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    v.setBackgroundResource(R.drawable.close);
                }
                break;
            case R.id.right:
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    v.setBackgroundResource(R.drawable.right_press);
                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    v.setBackgroundResource(R.drawable.right);
                }
                break;
            case R.id.refresh:
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    v.setBackgroundResource(R.drawable.refresh_press);
                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    v.setBackgroundResource(R.drawable.refresh);
                }
                break;
            case R.id.left:
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    v.setBackgroundResource(R.drawable.left_press);
                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    v.setBackgroundResource(R.drawable.left);
                }
                break;
            case R.id.open:
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    v.setBackgroundResource(R.drawable.open_press);
                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    v.setBackgroundResource(R.drawable.open);
                }
                break;
            case R.id.share:
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    v.setBackgroundResource(R.drawable.share_press);
                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    v.setBackgroundResource(R.drawable.share);
                }
                break;
            default:
                break;
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.close:
                finish();
                break;
            case R.id.right:
                if(webView.canGoForward()){
                    webView.goForward();
                }
                break;
            case R.id.refresh:
                webView.reload();
                break;
            case R.id.left:
                if(webView.canGoBack()){
                    webView.goBack();
                }
                break;
            case R.id.fabMenu:
                if(menuIsOpen){
//                    boxRelativeLayout.removeView(menuLinearLayout);
                    menuLinearLayout.setVisibility(View.INVISIBLE);
                    menuIsOpen = false;
                }else{
//                    boxRelativeLayout.addView(menuLinearLayout);
                    menuLinearLayout.setVisibility(View.VISIBLE);
                    menuIsOpen = true;
                }
                //Toast.makeText(WebActivity.this, boxRelativeLayout+"---"+menuLinearLayout, Toast.LENGTH_SHORT).show();
                break;
            case R.id.open:
                Toast.makeText(WebActivity.this, "تور كۆرگۈچتە ئېچىلىۋاتىدۇ ...", Toast.LENGTH_SHORT).show();
                //在系统浏览器中打开
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri newUrl = Uri.parse(webView.getUrl());
                intent.setData(newUrl);
                startActivity(intent);
                break;
            case R.id.share:
                //
                Intent intentShare = new Intent();
                intentShare.setAction(Intent.ACTION_SEND);

                intentShare.setType("text/plain");
                intentShare.putExtra(Intent.EXTRA_SUBJECT, "ھەمبەھىرلەش");
                intentShare.putExtra(Intent.EXTRA_TEXT, webView.getTitle() + " :\n " + webView.getUrl());
                intentShare.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intentShare, getTitle()));

                // startActivity(intentShare);
                break;
            default:
                break;
        }
    }



}
