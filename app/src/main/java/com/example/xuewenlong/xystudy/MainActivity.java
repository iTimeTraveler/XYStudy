package com.example.xuewenlong.xystudy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnTouchListener{

    public static final int MSG_MOTIONEVENT = 0;
    private Button clickBtn;
    private Button eventBtn;
    private EditText xEditText;
    private EditText yEditText;
    private DrawView responseSquare;
    private LinearLayout sourceRect;

    final AuxiliaryThread thread = new AuxiliaryThread();
    int[] responseLocations = new int[2];
    float x,y = 100;

    //用子线程来发送采集到的MotionEvent事件
    class AuxiliaryThread extends Thread {
        public Handler tHandler;
        private Instrumentation mInst = new Instrumentation();

        @Override
        public void run() {
            Looper.prepare();
            tHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case MSG_MOTIONEVENT:
                            Bundle bundle = msg.getData();
                            float rx = bundle.getFloat("x");
                            float ry = bundle.getFloat("y");

                            mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
                                    SystemClock.uptimeMillis(), msg.arg1,
                                    rx, ry, 0));
                            break;
                    }
                }
            };
            Looper.loop();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clickBtn = (Button) findViewById(R.id.click);
        eventBtn = (Button) findViewById(R.id.eventBtn);
        xEditText = (EditText) findViewById(R.id.xEditText);
        yEditText = (EditText) findViewById(R.id.yEditText);
        responseSquare = (DrawView) findViewById(R.id.responseSquare);
        sourceRect = (LinearLayout) findViewById(R.id.sourceRect);

        thread.start();
        sourceRect.setOnTouchListener(this);

        clickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (xEditText.getText().toString().equals("") || yEditText.getText().toString().equals("")) {
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //模拟点击click事件.
                        x = Float.valueOf(xEditText.getText().toString());
                        y = Float.valueOf(yEditText.getText().toString());
                        Instrumentation mInst = new Instrumentation();
                        mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
                                SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN,
                                x, y, 0));
                        mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
                                SystemClock.uptimeMillis(), MotionEvent.ACTION_UP,
                                x, y, 0));
                    }
                }).start();
            }
        });

        eventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("EXAMPLE Button");
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        x = event.getX();
        y = event.getY() + responseLocations[1];

        final Message msg = Message.obtain();
        msg.what = MSG_MOTIONEVENT;
        msg.arg1 = event.getAction();
        Bundle bundle = new Bundle();
        bundle.putFloat("x", x);
        bundle.putFloat("y", y);
        msg.setData(bundle);

        thread.tHandler.sendMessageDelayed(msg, 2000);
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //获取一下响应窗口的坐标
        if (hasFocus) {
            responseSquare.getLocationOnScreen(responseLocations);
            ((TextView) findViewById(R.id.markStart)).setText("(" + responseLocations[0] + "," + responseLocations[1] + ")");
            ((TextView) findViewById(R.id.markEnd)).setText( "(" + (responseLocations[0] + responseSquare.getWidth()) + "," + (responseLocations[1] + responseSquare.getHeight()) + ")");

            int[] temp = new int[2];
            eventBtn.getLocationOnScreen(temp);
            eventBtn.setText("Example Button (" + temp[0] + "," + temp[1] + ")");
        }
    }

    private void showDialog(String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(str);
        builder.setTitle(R.string.warning);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
