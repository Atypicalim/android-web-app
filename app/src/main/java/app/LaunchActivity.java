package app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;
import java.util.Timer;
import java.util.TimerTask;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        ButterKnife.bind(this);
        TinyTools.setStatusBarColor(this, R.color.app_launch_page_color);

        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(LaunchActivity.this, WebActivity.class);
                LaunchActivity.this.startActivity(intent);
                finish();
            }
        };
        Timer t = new Timer();
        t.schedule(tt, 1000);

    }

}
