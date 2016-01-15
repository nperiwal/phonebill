package com.example.oozie.phonebill3;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CallLogFragment extends Fragment {

    private String TAG = "CallLogFragment";
    private String CALL_TEXT = "callText";

    private TextView textView;
    private String callText;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(TAG, "In frag's on save instance state ");
        outState.putSerializable(CALL_TEXT, callText);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.v(TAG, "In frag's on create view");
        return inflater.inflate(R.layout.fragment_call_log, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.v(TAG, "In frag's on view created");
        super.onViewCreated(view, savedInstanceState);
        int position = FragmentPagerItem.getPosition(getArguments());
        System.out.println("Tab Position: " + String.valueOf(position));
        textView = (TextView) view.findViewById(R.id.call_log_text);
        if (position == 1) {
            PhoneBillActivity activity = (PhoneBillActivity) getActivity();
            callText = activity.getCallText();
            textView = (TextView) view.findViewById(R.id.call_log_text);
            textView.setText(callText);
        } else {
            textView.setText(String.valueOf(position));
        }
    }

}
