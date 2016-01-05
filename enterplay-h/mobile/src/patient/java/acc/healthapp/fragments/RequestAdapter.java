package acc.healthapp.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import acc.healthapp.R;
import acc.healthapp.model.Request;

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
            convertView = mInflater.inflate(R.layout.request_list_view, parent, false);

            TextView statusTextView = (TextView) convertView.findViewById(R.id.request_status_text);
            TextView titleTextView = (TextView) convertView.findViewById(R.id.request_title);

            viewHolder = new ViewHolder(titleTextView, statusTextView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (request != null) {
            viewHolder.requestStatus.setText(request.getRequestStatus().toString());
            viewHolder.requestType.setText(request.getRequestType().toString());
        }

        return convertView;
    }


    private class ViewHolder {
        private TextView requestType;
        private TextView requestStatus;

        public ViewHolder(TextView requestType, TextView requestStatus) {
            this.requestType = requestType;
            this.requestStatus = requestStatus;
        }
    }
}
