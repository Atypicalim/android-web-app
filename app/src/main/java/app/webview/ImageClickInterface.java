package app.webview;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * Created by jingbin on 2016/11/17.
 * js通信接口
 */
public class ImageClickInterface {
    private Context context;

    public ImageClickInterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void imageClick(String src, String baseURI) {
        Toast.makeText(context, "----点击了图片", Toast.LENGTH_SHORT).show();
        // 查看大图
//        Intent intent = new Intent(context, ViewBigImageActivity.class);
//        context.startActivity(intent);
        Log.e("----点击了图片 src: ", "" + src);
        Log.e("----点击了图片 baseURI: ", "" + baseURI);
    }

    @JavascriptInterface
    public void textClick(String href, String baseURI) {

        Log.e("----点击了文字 href: ", "" + href);
        Log.e("----点击了文字 baseURI: ", "" + baseURI);
        if (!TextUtils.isEmpty(href) && !TextUtils.isEmpty(href)) {
            Toast.makeText(context, "----点击了文字", Toast.LENGTH_SHORT).show();

        }
    }
}
