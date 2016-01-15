package com.example.oozie.phonebill3;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.Date;


public class DemoActivity extends AppCompatActivity {

    private String callText;

    public String getCallText() {
        return callText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.demo_title_basic);

        callText = getCallDetails();

        ViewGroup tab = (ViewGroup) findViewById(R.id.tab);
        tab.addView(LayoutInflater.from(this).inflate(R.layout.demo_basic, tab, false));

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        setup(viewPagerTab);

        FragmentPagerItems pages = new FragmentPagerItems(this);
        for (int titleResId : tabs()) {
            pages.add(FragmentPagerItem.of(getString(titleResId), DemoFragment.class));
        }

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), pages);

        viewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(viewPager);

    }

    private void setup(final SmartTabLayout layout) {
        //Do nothing.
    }

    private static int[] tab3() {
        return new int[]{
                //R.string.tab_1,
                R.string.tab_2,
                R.string.tab_3
        };
    }

    private int[] tabs() {
        return tab3();
    }

    private String getCallDetails() {
        StringBuffer sb = new StringBuffer();
        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";

        /* Query the CallLog Content Provider */

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) !=
                PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return "";
        }
        Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null,
                null, strOrder);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        //int account = managedCursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_COMPONENT_NAME);
        while (managedCursor.moveToNext()) {
            String phNum = managedCursor.getString(number);
            String callTypeCode = managedCursor.getString(type);
            String strcallDate = managedCursor.getString(date);
            Date callDate = new Date(Long.valueOf(strcallDate));
            String callDuration = managedCursor.getString(duration);
            String callType = null;
            int callcode = Integer.parseInt(callTypeCode);
            switch (callcode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    callType = "Outgoing";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    callType = "Incoming";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    callType = "Missed";
                    break;
            }
            //String accountName = managedCursor.getString(account);
            sb.append("\nPhone Number:--- " + phNum + " \nCall Type:--- "
                    + callType + " \nCall Date:--- " + callDate
                    + " \nCall duration in sec :--- " + callDuration);
                    //+ " \nOwner :--- " + accountName);
            sb.append("\n----------------------------------");
        }
        return sb.toString();
    }
}