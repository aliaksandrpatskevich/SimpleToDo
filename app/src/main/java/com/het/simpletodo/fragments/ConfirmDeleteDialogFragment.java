package com.het.simpletodo.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.het.simpletodo.R;


public class ConfirmDeleteDialogFragment extends DialogFragment implements Button.OnClickListener {

    private Button btnOK;

    public interface ConfirmDeleteDialogListener {
        void onFinishConfirmDeleteDialog(boolean confirm);
    }

    public ConfirmDeleteDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    public static ConfirmDeleteDialogFragment newInstance(String text) {
        ConfirmDeleteDialogFragment frag = new ConfirmDeleteDialogFragment();
        Bundle args = new Bundle();
        args.putString("text", text);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete_item, container);
        btnOK = (Button) view.findViewById(R.id.btnOK);
        TextView tvItem = (TextView) view.findViewById(R.id.tvItem);
        String text = getArguments().getString("text");
        tvItem.setText(text);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);

        btnOK.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == btnOK) {
            ConfirmDeleteDialogListener listener = (ConfirmDeleteDialogListener) getActivity();
            listener.onFinishConfirmDeleteDialog(true);
        }
        dismiss();
    }
}