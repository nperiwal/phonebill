package com.example.oozie.phonebill3;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

public class GetNotificationFragment extends Fragment {

    private String TAG = "GetNotificationFragment";
    static final int FROM_DATE_DIALOG_ID = 1;
    static final int TO_DATE_DIALOG_ID = 2;

    private int fromYear;
    private int fromMonth;
    private int fromDay;
    private int toYear;
    private int toMonth;
    private int toDay;

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

        addListenerOnFromButton();
        addListenerOnToButton();
        addListenerOnCheckBox();
    }

    private void addListenerOnCheckBox() {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                 if (isChecked) {
                     Log.v(TAG, "Get Notification ON");
                     String duration = etCallDurationLimit.getText().toString();
                     String fromDate = etFromText.getText().toString();
                     String toDate = etToText.getText().toString();
                     Log.v(TAG, "Duration " + duration);
                     Log.v(TAG, "FromDate " + fromDate);
                     Log.v(TAG, "ToDate " + toDate);
                 } else {
                     Log.v(TAG, "Get Notification OFF");
                 }
             }
        });
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
                return new DatePickerDialog(getActivity(), fromDatePickerListener, fromYear,
                        fromMonth, fromDay);
            case TO_DATE_DIALOG_ID:
                return new DatePickerDialog(getActivity(), toDatePickerListener, toYear, toMonth,
                        toDay);

        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener fromDatePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            fromYear = selectedYear;
            fromMonth = selectedMonth;
            fromDay = selectedDay;

            // set selected date into textview
            etFromText.setText(new StringBuilder().append(fromMonth + 1)
                    .append("-").append(fromDay).append("-").append(fromYear)
                    .append(" "));


        }
    };

    private DatePickerDialog.OnDateSetListener toDatePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            toYear = selectedYear;
            toMonth = selectedMonth;
            toDay = selectedDay;

            // set selected date into textview
            etToText.setText(new StringBuilder().append(toMonth + 1)
                    .append("-").append(toDay).append("-").append(toYear)
                    .append(" "));


        }
    };

}
