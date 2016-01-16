package com.example.oozie.phonebill3;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.TimeZone;

public class GetNotificationFragment extends Fragment {

    private String TAG = "GetNotificationFragment";
    static final int FROM_DATE_DIALOG_ID = 1;
    static final int TO_DATE_DIALOG_ID = 2;

    private SharedPreferences sharedPref;

    private PendingIntent pendingIntent;

    private String isGetNotificatonOn;
    private String callDuration;
    private String fromDate;
    private String toDate;

    private EditText etCallDurationLimit;
    private Button fromDateButton;
    private EditText etFromText;
    private Button toDateButton;
    private EditText etToText;
    private CheckBox checkBox;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.v(TAG, "In GetNotificationFragment on create view");
        return inflater.inflate(R.layout.fragment_get_notification, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.v(TAG, "In GetNotificationFragment on view created");
        super.onViewCreated(view, savedInstanceState);

        etCallDurationLimit = (EditText) view.findViewById(R.id.outgoing_call_edittext);
        fromDateButton = (Button) view.findViewById(R.id.button1);
        etFromText = (EditText) view.findViewById(R.id.etFromDate);
        etFromText.setEnabled(false);
        toDateButton = (Button) view.findViewById(R.id.button2);
        etToText = (EditText) view.findViewById(R.id.etToDate);
        etToText.setEnabled(false);
        checkBox = (CheckBox) view.findViewById(R.id.notification_checkbox);

        Context context = getActivity();
        sharedPref = context.getSharedPreferences(getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);

        checkForNotificationData();

        addListenerOnFromButton();
        addListenerOnToButton();
        addListenerOnCheckBox();
    }

    private void storeData(String key, String value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void storeNotificationData () {
        storeData("callDuration", callDuration);
        storeData("fromDate", fromDate);
        storeData("toDate", toDate);
        storeData("isGetNotificatonOn", isGetNotificatonOn);

    }

    private void checkForNotificationData() {
        String tempDuration = sharedPref.getString("callDuration", null);
        String tempFromDate = sharedPref.getString("fromDate", null);
        String tempToDate = sharedPref.getString("toDate", null);
        String tempIsGetNotificatonOn = sharedPref.getString("isGetNotificatonOn", null);
        if (tempDuration != null) {
            etCallDurationLimit.setText(tempDuration);
        }
        if (tempFromDate != null) {
            etFromText.setText(tempFromDate);
        }
        if (tempToDate != null) {
            etToText.setText(tempToDate);
        }
        if (tempIsGetNotificatonOn != null) {
            if (tempIsGetNotificatonOn.equals("true")) {
                checkBox.setChecked(true);
                fromDateButton.setEnabled(false);
                toDateButton.setEnabled(false);
                etCallDurationLimit.setEnabled(false);
            }
        }
    }

    private void addListenerOnCheckBox() {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.v(TAG, "Trying to check GetNotification");

                    if( !checkConditionMet() ) {
                        checkBox.setChecked(false);
                        return;
                    }
                    start();
                    isGetNotificatonOn = "true";
                    Toast.makeText(getActivity(), "Get Notification Turned ON",
                            Toast.LENGTH_SHORT).show();

                } else {
                    Log.v(TAG, "Get Notification OFF");

                    fromDateButton.setEnabled(true);
                    toDateButton.setEnabled(true);
                    etCallDurationLimit.setEnabled(true);

                    stop();
                    isGetNotificatonOn = "false";
                    Toast.makeText(getActivity(), "Get Notification Turned OFF",
                            Toast.LENGTH_SHORT).show();
                }
                storeNotificationData();
            }
        });
    }

    private boolean checkConditionMet() {
        //ToDo Date Validation Check

        String tempDuration = etCallDurationLimit.getText().toString();
        String tempFromDate = etFromText.getText().toString();
        String tempToDate = etToText.getText().toString();

        Log.v(TAG, "Duration " + tempDuration);
        Log.v(TAG, "FromDate " + tempFromDate);
        Log.v(TAG, "ToDate " + tempToDate);

        if (tempDuration.isEmpty()) {
            Toast.makeText(getActivity(), "Call Duration is not set", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (tempFromDate.isEmpty()) {
            Toast.makeText(getActivity(), "From Date is not set", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (tempToDate.isEmpty()) {
            Toast.makeText(getActivity(), "To Date is not set", Toast.LENGTH_SHORT).show();
            return false;
        }
        callDuration = tempDuration;
        fromDate = tempFromDate;
        toDate = tempToDate;

        fromDateButton.setEnabled(false);
        toDateButton.setEnabled(false);
        etCallDurationLimit.setEnabled(false);

        return true;
    }

    private void start() {
        Intent intent = new Intent(this.getActivity(), MyAlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this.getActivity(), MyAlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager manager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        int interval = 1000 * 60 * 10;
        long firstMillis = System.currentTimeMillis();

        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, interval, pendingIntent);
    }

    private void stop() {
        AlarmManager manager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
    }

    private void addListenerOnFromButton() {
        fromDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = (DatePickerDialog) showDialog(FROM_DATE_DIALOG_ID);
                int yy = Calendar.getInstance().get(Calendar.YEAR);
                int mm = Calendar.getInstance().get(Calendar.MONTH);
                int dd = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                Log.v(TAG, "From Date Clicked");
                dialog.updateDate(yy, mm, dd);
                dialog.show();
            }
        });
    }

    private void addListenerOnToButton() {
        toDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = (DatePickerDialog) showDialog(TO_DATE_DIALOG_ID);
                int yy = Calendar.getInstance().get(Calendar.YEAR);
                int mm = Calendar.getInstance().get(Calendar.MONTH);
                int dd = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                Log.v(TAG, "To Date Clicked");
                dialog.updateDate(yy, mm, dd);
                dialog.show();
            }
        });
    }

    protected Dialog showDialog(int id) {
        switch (id) {
            case FROM_DATE_DIALOG_ID:
                // set date picker as current date
                return new DatePickerDialog(getActivity(), fromDatePickerListener, 2016, 1, 1);
            case TO_DATE_DIALOG_ID:
                return new DatePickerDialog(getActivity(), toDatePickerListener, 2016, 1, 1);

        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener fromDatePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            StringBuilder builder = new StringBuilder();
            builder.append(selectedMonth + 1).append("-").append(selectedDay).append("-")
                    .append(selectedYear);

            fromDate = builder.toString();

            // set selected date into textview
            etFromText.setText(fromDate);
        }
    };

    private DatePickerDialog.OnDateSetListener toDatePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            StringBuilder builder = new StringBuilder();
            builder.append(selectedMonth + 1).append("-").append(selectedDay).append("-")
                    .append(selectedYear);
            toDate = builder.toString();

            // set selected date into textview
            etToText.setText(toDate);
        }
    };

}
