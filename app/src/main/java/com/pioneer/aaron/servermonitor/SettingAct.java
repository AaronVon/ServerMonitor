package com.pioneer.aaron.servermonitor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pioneer.aaron.servermonitor.Constants.Constants;
import com.pioneer.aaron.servermonitor.SharedPres.SharedPres;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by Aaron on 6/24/15.
 */
public class SettingAct extends SwipeBackActivity{
    private SeekBar seekBar;
    private TextView refreshGapTip;
    private Button resetButton;
    private ImageView settingTipImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
        init();
    }

    private void init() {
        settingTipImageView = (ImageView) findViewById(R.id.swipebackTip);
        final SharedPres preferences = new SharedPres(this);

        boolean isFirst = preferences.getBoolean(Constants.SETTINGTIP, true);
        Log.d("settingisfirst", isFirst + "");
        if (isFirst) {
            settingTipImageView.setVisibility(View.VISIBLE);
        } else {
            settingTipImageView.setVisibility(View.GONE);
        }
        settingTipImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingTipImageView.setVisibility(View.GONE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(Constants.SETTINGTIP, false);
                editor.commit();
                editor.clear();
            }
        });
        seekBar = (SeekBar) findViewById(R.id.refreshgap_seekBar);
        refreshGapTip = (TextView) findViewById(R.id.refreshgap);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (seekBar.getId()) {
                    case R.id.refreshgap_seekBar:
                        int valueSeekBar = seekBar.getProgress();
                        refreshGapTip.setText(String.valueOf(valueSeekBar) + "s");
                        if (valueSeekBar == 0) {
                            valueSeekBar = 1;
                        }
                        SharedPres pres = new SharedPres(SettingAct.this);
                        pres.setRefreshGap(valueSeekBar);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        resetButton = (Button) findViewById(R.id.resetbutton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SettingAct.this)
                        .setIcon(R.drawable.ic_launcher)
                        .setTitle("Alert")
                        .setMessage("Are you sure to reset user data?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPres preference = new SharedPres(SettingAct.this);
                                preference.resetPrefs();
                                Toast.makeText(SettingAct.this, "Please restart App to get setting functional", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();

            }
        });
    }
}
