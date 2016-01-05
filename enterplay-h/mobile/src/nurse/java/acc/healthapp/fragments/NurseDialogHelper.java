package acc.healthapp.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Date;

import acc.healthapp.R;
import acc.healthapp.api.HealthAppApi;
import acc.healthapp.model.Request;
import acc.healthapp.model.RequestCurrentPriority;
import acc.healthapp.model.RequestStatus;

public class NurseDialogHelper implements DialogHelper {

    private HealthAppApi api;
    private RequestIconDecorator iconDecorator;

    public NurseDialogHelper(HealthAppApi api, RequestIconDecorator iconDecorator) {
        this.api = api;
        this.iconDecorator = iconDecorator;
    }

    @Override
    public Dialog renderDialog(Context context, final Request request) {
        final Dialog newRequestDialog = new Dialog(context);
        newRequestDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        newRequestDialog.setContentView(R.layout.nurse_custom_dialog);
        newRequestDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button acceptButton = (Button) newRequestDialog.findViewById(R.id.acceptbutton);

        if (request.getRequestStatus().toInt() == RequestStatus.IN_PROGRESS) {
            acceptButton.setText(context.getString(R.string.complete_notification));
        } else if (request.getRequestStatus().toInt() == RequestStatus.OPEN) {
            acceptButton.setText(context.getString(R.string.accept_notification));
        }

        Button delegateButton = (Button) newRequestDialog.findViewById(R.id.delegatebutton);
        Button laterButton = (Button) newRequestDialog.findViewById(R.id.laterbutton);

        ImageView icon = (ImageView) newRequestDialog.findViewById(R.id.dialogIcon);
        icon.setImageResource(iconDecorator.getDrawableForRequest(request));

        RelativeLayout dialogAlertBackground =
                (RelativeLayout) newRequestDialog.findViewById(R.id.dialog_alert_background);

        final RequestCurrentPriority currentPriority = request.getRequestCurrentPriority();

        switch (currentPriority) {
            case HIGH:
                dialogAlertBackground.setBackgroundColor(context.getResources().
                        getColor(R.color.high_priority_background));

                acceptButton.setTextColor(context.getResources().
                        getColor(R.color.high_priority_button_text_color));
                delegateButton.setTextColor(context.getResources().
                        getColor(R.color.high_priority_button_text_color));
                laterButton.setTextColor(context.getResources().
                        getColor(R.color.high_priority_button_text_color));

                break;

            case MEDIUM:
                dialogAlertBackground.setBackgroundColor(context.getResources().
                        getColor(R.color.medium_priority_background));

                acceptButton.setTextColor(context.getResources().
                        getColor(R.color.medium_priority_button_text_color));
                delegateButton.setTextColor(context.getResources().
                        getColor(R.color.medium_priority_button_text_color));
                laterButton.setTextColor(context.getResources().
                        getColor(R.color.medium_priority_button_text_color));
                break;

            case LOW:
                dialogAlertBackground.setBackgroundColor(context.getResources().
                        getColor(R.color.low_priority_background));

                acceptButton.setTextColor(context.getResources().
                        getColor(R.color.low_priority_button_text_color));
                delegateButton.setTextColor(context.getResources().
                        getColor(R.color.low_priority_button_text_color));
                laterButton.setTextColor(context.getResources().
                        getColor(R.color.low_priority_button_text_color));
                break;
        }

        TextView dialogTitle = (TextView) newRequestDialog.findViewById(R.id.dialogTittle);
        dialogTitle.setText(request.getRequestType().toString());

        TextView name = (TextView) newRequestDialog.findViewById(R.id.patientName);
        TextView condition = (TextView) newRequestDialog.findViewById(R.id.condition);
        condition.setText(request.getMessage());
        TextView time = (TextView) newRequestDialog.findViewById(R.id.time_ago);
        Date date = request.getDateCreated();
        long epoch = date.getTime();

        CharSequence timePassedString =
                DateUtils.getRelativeTimeSpanString(epoch,
                        System.currentTimeMillis(),
                        DateUtils.SECOND_IN_MILLIS);

        time.setText(timePassedString);

        StringBuilder nameString = new StringBuilder(request.getUsername());
        nameString.append("\n");
        nameString.append("Room 121");
        name.setText(nameString.toString());

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newRequestDialog.dismiss();

                if (request.getRequestStatus().toInt() == RequestStatus.IN_PROGRESS) {
                    request.updateStatus(new RequestStatus(RequestStatus.COMPLETED));
                    api.updateRequest(request);
                } else if (request.getRequestStatus().toInt() == RequestStatus.OPEN) {
                    request.updateStatus(new RequestStatus(RequestStatus.IN_PROGRESS));
                    api.updateRequest(request);
                }


            }
        });
        laterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newRequestDialog.dismiss();
            }
        });
        delegateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newRequestDialog.dismiss();
            }
        });
        newRequestDialog.show();

        return newRequestDialog;
    }
}
