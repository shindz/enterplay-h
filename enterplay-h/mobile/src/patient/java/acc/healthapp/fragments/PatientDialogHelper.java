package acc.healthapp.fragments;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import acc.healthapp.R;
import acc.healthapp.model.Request;
import acc.healthapp.model.RequestType;

public class PatientDialogHelper implements DialogHelper {

    @Override
    public Dialog renderDialog(Context context, final Request request) {

        final Dialog newRequestDialog = new Dialog(context);
        newRequestDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        newRequestDialog.setContentView(R.layout.patient_dialog);
        newRequestDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button thanksButton = (Button) newRequestDialog.findViewById(R.id.thanks_button);
        thanksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newRequestDialog.dismiss();
            }
        });

        TextView dialogTitle = (TextView) newRequestDialog.findViewById(R.id.patientDialogTitle);

        StringBuilder titleBuilder = new StringBuilder();
        final int requestType = request.getRequestType().toInt().intValue();
        if (requestType == RequestType.ASSISTANCE || requestType == RequestType.TOILET) {
            titleBuilder.append("Help is on the way");
        } else if (requestType == RequestType.OTHER) {
            titleBuilder.append("Your custom request \n is on the way");
        } else {
            titleBuilder.append("Your ");
            titleBuilder.append(request.getRequestType().toString().toLowerCase());
            titleBuilder.append("\n");
            titleBuilder.append(" is on the way");
        }

        dialogTitle.setText(titleBuilder.toString());

        TextView dialogMessage = (TextView) newRequestDialog.findViewById(R.id.nurseNameText);

        String user = request.getNurseUsername();
        String message = context.getString(R.string.patient_dialog_message);

        StringBuilder completeMessage = new StringBuilder(user);
        completeMessage.append("\n");
        completeMessage.append(message);

        Spannable wordtoSpan = new SpannableString(completeMessage);

        wordtoSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimaryDark)),
                                                   0,
                                                   user.length(),
                                                   Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        wordtoSpan.setSpan(new AbsoluteSizeSpan(40), 0, user.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        dialogMessage.setText(wordtoSpan);

        newRequestDialog.show();

        return newRequestDialog;
    }

}
