package com.angcyo.y2androidapi.view.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.angcyo.y2androidapi.R;
import com.angcyo.y2androidapi.view.ExitEvent;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by angcyo on 15-07-27-027.
 */
public class SparseExitFragment extends DialogFragment {
    @Bind(R.id.exit_layout)
    RelativeLayout mExitLayout;

    @Bind(R.id.exit_text)
    TextView mExitTextView;

    @Bind(R.id.exit1)
    View mExitView1;
    @Bind(R.id.exit2)
    View mExitView2;
    @Bind(R.id.exit3)
    View mExitView3;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
//        Dialog dialog = new Dialog(getActivity(), R.style.TranDialog);
        dialog.getWindow().requestFeature(STYLE_NO_TITLE);
        dialog.getWindow()
                .getDecorView()
                .setBackgroundColor(
                        getResources().getColor(android.R.color.transparent));
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);//重点

        if (Build.VERSION.SDK_INT >= 14) {
            dialog.getWindow().setDimAmount(0f);//设置窗口暗淡的程度
        }
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        View view = getActivity().getLayoutInflater().inflate(
                R.layout.exit_layout, null);
        ButterKnife.bind(this, view);

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    EventBus.getDefault().post(new ExitEvent());
                }
                return false;
            }
        });

        dialog.setContentView(view);
        handler.postDelayed(runnable, EXIT_DELAY_TIME);
        return dialog;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        handler.removeCallbacks(runnable);
    }

    private static final int EXIT_DELAY_TIME = 400;
    private int code = 0;
    Handler handler = new Handler(Looper.getMainLooper());
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mExitLayout == null) return;

            switch (code) {
                case 0:
                    mExitView3.setVisibility(View.INVISIBLE);
                    ++code;
                    handler.postDelayed(runnable, EXIT_DELAY_TIME);
                    break;
                case 1:
                    mExitView2.setVisibility(View.INVISIBLE);
                    ++code;
                    handler.postDelayed(runnable, EXIT_DELAY_TIME);
                    break;
                case 2:
                    mExitView1.setVisibility(View.INVISIBLE);
                    ++code;
                    handler.postDelayed(runnable, EXIT_DELAY_TIME);
                    break;
                default:
                    code = 0;
                    SparseExitFragment.this.dismiss();
                    SparseExitFragment.this.getActivity().getSupportFragmentManager().beginTransaction().remove(SparseExitFragment.this);
                    break;
            }
        }
    };
}
