package acc.healthapp.fragments;

import android.app.Dialog;
import android.content.Context;

import acc.healthapp.model.Request;

public interface DialogHelper {

    Dialog renderDialog(Context context, Request request);

}
