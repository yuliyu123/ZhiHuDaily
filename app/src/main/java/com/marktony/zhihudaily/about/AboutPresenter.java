package com.marktony.zhihudaily.about;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.marktony.zhihudaily.R;
import com.marktony.zhihudaily.custom_tab.CustomTabActivityHelper;
import com.marktony.zhihudaily.inner_browser.InnerBrowserActivity;
import com.marktony.zhihudaily.open_source_license.OpenSourceLicenseActivity;

import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Lizhaotailang on 2016/9/4.
 */

public class AboutPresenter implements AboutContract.Presenter {

    private AboutContract.View view;
    private AppCompatActivity activity;
    private SharedPreferences sp;
    private CustomTabsIntent.Builder customTabsIntent;

    public AboutPresenter(AppCompatActivity activity, AboutContract.View view) {
        this.activity = activity;
        this.view = view;
        this.view.setPresenter(this);
        sp = activity.getSharedPreferences("user_settings",MODE_PRIVATE);

        customTabsIntent = new CustomTabsIntent.Builder();
        customTabsIntent.setToolbarColor(activity.getResources().getColor(R.color.colorPrimary));
        customTabsIntent.setShowTitle(true);
    }

    @Override
    public void start() {

    }


    @Override
    public void rate() {
        try {
            Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex){
            view.showRateError();
        }
    }

    @Override
    public void openLicense() {
        activity.startActivity(new Intent(activity,OpenSourceLicenseActivity.class));
    }

    // use CustomTabsIntent open the url first
    // if it get failed, use the inner browser
    @Override
    public void followOnGithub() {
        if (sp.getBoolean("in_app_browser",true)){
            CustomTabActivityHelper.openCustomTab(
                    activity,
                    customTabsIntent.build(),
                    Uri.parse(activity.getString(R.string.github_url)),
                    new CustomTabActivityHelper.CustomTabFallback() {
                        @Override
                        public void openUri(Activity activity, Uri uri) {
                            Intent i = new Intent(activity, InnerBrowserActivity.class);
                            i.putExtra("url", activity.getString(R.string.github_url));
                            activity.startActivity(i);
                        }
                    });
        } else {
            try{
                activity.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse( activity.getString(R.string.github_url))));
            } catch (android.content.ActivityNotFoundException ex){
                view.showBrowserNotFoundError();
            }
        }
    }

    @Override
    public void followOnZhihu() {
        if (sp.getBoolean("in_app_browser",true)){
            CustomTabActivityHelper.openCustomTab(
                    activity,
                    customTabsIntent.build(),
                    Uri.parse(activity.getString(R.string.zhihu_url)),
                    new CustomTabActivityHelper.CustomTabFallback() {
                        @Override
                        public void openUri(Activity activity, Uri uri) {
                            Intent i = new Intent(activity, InnerBrowserActivity.class);
                            i.putExtra("url", activity.getString(R.string.zhihu_url));
                            activity.startActivity(i);
                        }
                    });
        } else {
            try{
                activity.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse( activity.getString(R.string.zhihu_url))));
            } catch (android.content.ActivityNotFoundException ex){
                view.showBrowserNotFoundError();
            }
        }

    }

    @Override
    public void feedback() {
        try{
            Uri uri = Uri.parse(activity.getString(R.string.sendto));
            Intent intent = new Intent(Intent.ACTION_SENDTO,uri);
            intent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.mail_topic));
            intent.putExtra(Intent.EXTRA_TEXT,
                    activity.getString(R.string.device_model) + Build.MODEL + "\n"
                            + activity.getString(R.string.sdk_version) + Build.VERSION.RELEASE + "\n"
                            + activity.getString(R.string.version));
            activity.startActivity(intent);
        }catch (android.content.ActivityNotFoundException ex){
            view.showFeedbackError();
        }
    }

    @Override
    public void donate() {
        AlertDialog dialog = new AlertDialog.Builder(activity).create();
        dialog.setTitle(R.string.donate);
        dialog.setMessage(activity.getString(R.string.donate_content));
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, activity.getString(R.string.positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 将指定账号添加到剪切板
                // add the alipay account to clipboard
                ClipboardManager manager = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", activity.getString(R.string.donate_account));
                manager.setPrimaryClip(clipData);
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, activity.getString(R.string.negative), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
    }

}
