package com.angcyo.y2androidapi.view.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;

import com.angcyo.y2androidapi.R;

/**
 * Created by angcyo on 15-07-26-026.
 */
public class ProgressFragment extends DialogFragment {

    TextView txView;
    String msg = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        msg = getArguments().getString("msg") == null ? "" : getArguments().getString("msg");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(STYLE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow()
                .getDecorView()
                .setBackgroundColor(
                        getResources().getColor(android.R.color.transparent));

        View view = getActivity().getLayoutInflater().inflate(
                R.layout.progress_layout, null);
        txView = (TextView) view.findViewById(R.id.msg);
        txView.setText(msg);
        dialog.setContentView(view);
        return dialog;
    }
}
