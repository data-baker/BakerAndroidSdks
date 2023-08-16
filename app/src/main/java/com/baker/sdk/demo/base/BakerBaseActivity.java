package com.baker.sdk.demo.base;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.baker.engrave.lib.BakerVoiceEngraver;
import com.baker.sdk.demo.R;

/**
 * @author hsj55
 */
public class BakerBaseActivity extends AppCompatActivity implements View.OnClickListener {
    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBack() {
        finish();
    }

    @Override
    public void onClick(View v) {

    }

    public void showNormalDialog(){
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(BakerBaseActivity.this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("如果退出了，录音信息就没有了哦。");
        normalDialog.setPositiveButton("确定",
                (dialog, which) -> {
                    //非常建议在录音过程中异常退出的话，调用此方法通知服务器，这样的话会及时释放当前训练模型所占用的名额。
                    BakerVoiceEngraver.getInstance().recordInterrupt();
                    //关闭activity
                    finish();
                });
        normalDialog.setNegativeButton("返回",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        // 显示
        normalDialog.show();
    }

    /**
     * 显示正在请求网络的进度条
     */
    public void showProgressDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
                progressDialog = new Dialog(BakerBaseActivity.this, R.style.progress_dialog);
                View view = View.inflate(BakerBaseActivity.this, R.layout.view_progress_dialog, null);
                progressDialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
                progressDialog.setCancelable(false);
                progressDialog.setContentView(view);
                if (!isFinishing()) {
                    progressDialog.show();
                }
            }
        });
    }

    /**
     * 取消进度条显示
     */
    public void disMissProgressDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        });
    }
}