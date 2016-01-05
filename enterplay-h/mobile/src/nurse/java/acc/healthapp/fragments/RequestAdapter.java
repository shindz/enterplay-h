package acc.healthapp.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

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
            convertView = mInflater.inflate(R.layout.nurse_requests_list_view, parent, false);

            ImageView iconImageView = (ImageView) convertView.findViewById(R.id.request_image);
            TextView messageTextView = (TextView) convertView.findViewById(R.id.request_message_text);
            TextView titleTextView = (TextView) convertView.findViewById(R.id.request_title_left);

            viewHolder = new ViewHolder(titleTextView, messageTextView, iconImageView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (request != null) {
            viewHolder.requestStatus.setText(request.getMessage());
            viewHolder.requestType.setText(request.getRequestType().toString());

            final RequestCurrentPriority currentPriority = request.getRequestCurrentPriority();

            switch (currentPriority) {
                case HIGH:
                    viewHolder.requestType.setTextColor(getContext().
                            getResources().getColor(R.color.high_priority_msg_color));
                    break;

                case MEDIUM:
                    viewHolder.requestType.setTextColor(getContext().
                            getResources().getColor(R.color.med_priority_msg_color));
                    break;

                case LOW:
                    viewHolder.requestType.setTextColor(getContext().
                            getResources().getColor(R.color.low_priority_msg_color));
                    break;
            }


            if (requestIconDecorator != null) {
                viewHolder.requestImage.setImageResource(requestIconDecorator.getDrawableForRequest(request));
            }

        }

        return convertView;
    }


    private class ViewHolder {
        private TextView requestType;
        private TextView requestStatus;
        private ImageView requestImage;

        public ViewHolder(TextView requestType, TextView requestStatus, ImageView requestImage) {
            this.requestType = requestType;
            this.requestStatus = requestStatus;
            this.requestImage = requestImage;
        }
    }
}
