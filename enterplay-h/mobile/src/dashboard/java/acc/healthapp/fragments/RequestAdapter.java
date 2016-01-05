package acc.healthapp.fragments;

import android.content.Context;
import android.media.Image;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import acc.healthapp.R;
import acc.healthapp.model.Request;
import acc.healthapp.model.RequestCurrentPriority;

public class RequestAdapter extends ArrayAdapter<Request> {

    private LayoutInflater mInflater;
    private RequestIconDecorator requestIconDecorator;

    public RequestAdapter(Context context,
                          int resource,
                          int textViewResourceId,
                          List<Request> objects,
                          RequestIconDecorator iconDecorator) {
        super(context, resource, textViewResourceId, objects);
        mInflater = LayoutInflater.from(context);
        requestIconDecorator = iconDecorator;
    }

    public RequestAdapter(Context context,
                          int resource,
                          int textViewResourceId,
                          RequestIconDecorator iconDecorator) {
        super(context, resource, textViewResourceId);
        mInflater = LayoutInflater.from(context);
        requestIconDecorator = iconDecorator;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Request request = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.dashboard_request_list, parent, false);

            ImageView requestIcon =
                    (ImageView) convertView.findViewById(R.id.open_request_list_request_icon);

            TextView requestTypeTextView =
                    (TextView) convertView.findViewById(R.id.open_request_list_request_type);

            TextView requestElapsedTimeTextView =
                    (TextView) convertView.findViewById(R.id.open_request_list_elapsed_time);

            TextView requestAssignedTextView =
                    (TextView) convertView.findViewById(R.id.open_request_list_assigned);


            viewHolder = new ViewHolder(requestIcon,
                    requestTypeTextView,
                    requestElapsedTimeTextView,
                    requestAssignedTextView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (request != null) {

            final RequestCurrentPriority currentPriority = request.getRequestCurrentPriority();
            switch (currentPriority) {
                case HIGH:
                    viewHolder.requestIcon.setBackgroundResource(R.drawable.request_status_dot_high);
                    break;

                case MEDIUM:
                    viewHolder.requestIcon.setBackgroundResource(R.drawable.request_status_dot_medium);
                    break;

                case LOW:
                    viewHolder.requestIcon.setBackgroundResource(R.drawable.request_status_dot_low);
                    break;
            }

            viewHolder.requestType.setText(request.getRequestType().toString());

            Date date = request.getDateCreated();
            long epoch = date.getTime();

            CharSequence timePassedString =
                    DateUtils.getRelativeTimeSpanString(epoch,
                            System.currentTimeMillis(),
                            DateUtils.SECOND_IN_MILLIS);

            viewHolder.requestElapsedTime.setText(timePassedString);

            viewHolder.requestAssigned.setText(request.getNurseUsername());

        }

        return convertView;
    }


    private class ViewHolder {
        private ImageView requestIcon;
        private TextView requestType;
        private TextView requestElapsedTime;
        private TextView requestAssigned;

        public ViewHolder(ImageView requestIcon,
                          TextView requestType,
                          TextView requestElapsedTime,
                          TextView requestAssigned) {
            this.requestIcon = requestIcon;
            this.requestType = requestType;
            this.requestElapsedTime = requestElapsedTime;
            this.requestAssigned = requestAssigned;
        }
    }
}
