package ru.chocco_crokko.musikant.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;

import ru.chocco_crokko.musikant.R;


public class AboutFragment extends DialogFragment {

    @Nullable
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.about, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.about).setPositiveButton("OK", null).setView(view);
        view.findViewById(R.id.about_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                        Uri.fromParts("mailto", getString(R.string.email_box), null));
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Отзыв из MusiKant");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });
        return builder.create();
    }



}
