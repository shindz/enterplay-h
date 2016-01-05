package acc.healthapp.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import acc.healthapp.MainDashActivity;
import acc.healthapp.R;
import acc.healthapp.api.HealthAppApi;
import acc.healthapp.model.Request;
import acc.healthapp.model.RequestType;
import acc.healthapp.video.PlayerActivity;
import acc.healthapp.video.SearchActivity;
import acc.healthapp.calendar.CalendarActivity;

public class ReqFragment extends Fragment implements View.OnClickListener{

    private TextView textView;
    private OpenRequestsListFragment listFragment;
    ImageView pillowRequest, waterRequest, toiletRequest, otherRequest, assistanceRequest;

    private String patientUsername;
    private String nurseUsername;
    private RequestIconDecorator iconDecorator;

    public static ReqFragment getInstance(final int position,
                                          final String patientUsername,
                                          final String nurseUsername,
                                          RequestIconDecorator iconDecorator) {
        ReqFragment fragmentDummy = new ReqFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString(MainDashActivity.PATIENT_USER_NAME_KEY, patientUsername);
        args.putString(MainDashActivity.NURSE_USER_NAME_KEY, nurseUsername);

        fragmentDummy.setArguments(args);
        fragmentDummy.setIconDecorator(iconDecorator);
        return fragmentDummy;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_requests, container, false);
        textView = (TextView) layout.findViewById(R.id.position);
        waterRequest = (ImageView) layout.findViewById(R.id.waterRequest);
        toiletRequest = (ImageView) layout.findViewById(R.id.toiletRequest);
        pillowRequest = (ImageView) layout.findViewById(R.id.pillowRequest);
        assistanceRequest = (ImageView) layout.findViewById(R.id.assistanceRequest);
        otherRequest = (ImageView) layout.findViewById(R.id.otherRequest);
        Bundle bundle = getArguments();
        waterRequest.setOnClickListener(this);
        otherRequest.setOnClickListener(this);
        pillowRequest.setOnClickListener(this);
        toiletRequest.setOnClickListener(this);
        assistanceRequest.setOnClickListener(this);


        if (listFragment == null) {

            this.patientUsername = bundle.getString(MainDashActivity.PATIENT_USER_NAME_KEY);
            this.nurseUsername = bundle.getString(MainDashActivity.NURSE_USER_NAME_KEY);
            listFragment = OpenRequestsListFragment.newInstance(this.patientUsername,
                                                                null,
                                                                iconDecorator,
                                                                R.color.patient_request_list_background_color);
        }

        final FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.openRequests, listFragment)
                .commit();

        return layout;

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        Bitmap sentImage, oldImage;
        switch (v.getId()){
            case R.id.waterRequest:
                 sentImage = BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.requestsent);
                 oldImage = BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.water);
                ImageViewAnimatedChange(waterRequest,sentImage,oldImage);
                sendRequest("water request",
                            Request.LOW_PRIORITY,
                            new RequestType(RequestType.WATER));
                break;
            case R.id.toiletRequest:
                sentImage = BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.requestsent);
                oldImage = BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.toilet);
                ImageViewAnimatedChange(toiletRequest,sentImage,oldImage);
                sendRequest("toilet request",
                        Request.HIGH_PRIORITY,
                        new RequestType(RequestType.TOILET));
                break;
            case R.id.pillowRequest:
                sentImage = BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.requestsent);
                oldImage = BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.pillow);
                ImageViewAnimatedChange(pillowRequest,sentImage,oldImage);
                sendRequest("pillow request",
                        Request.LOW_PRIORITY,
                        new RequestType(RequestType.PILLOW));
                break;
            case R.id.otherRequest:
                // Intent searchActivity = new Intent(getActivity(), CalendarActivity.class);
                // startActivity(searchActivity);

                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Other");
                alert.setMessage("Add message");

                // Set an EditText view to get user input
                final EditText input = new EditText(getActivity());
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Bitmap sentImage = BitmapFactory.decodeResource(getActivity().getResources(),
                                R.drawable.requestsent);
                        Bitmap oldImage = BitmapFactory.decodeResource(getActivity().getResources(),
                                R.drawable.other);
                        ImageViewAnimatedChange(otherRequest,sentImage,oldImage);
                        sendRequest(input.getText().toString(),
                                Request.LOW_PRIORITY,
                                new RequestType(RequestType.OTHER));
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                alert.show();
                break;
            case R.id.assistanceRequest:
                sentImage = BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.requestsent);
                oldImage = BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.assistance);
                ImageViewAnimatedChange(assistanceRequest,sentImage,oldImage);
                sendRequest("water request",
                        Request.HIGH_PRIORITY,
                        new RequestType(RequestType.ASSISTANCE));
                break;
        }

    }

    public void updateData() {
        if (listFragment != null) {
            listFragment.updateData();
        }
    }

    public void setIconDecorator(RequestIconDecorator iconDecorator) {
        this.iconDecorator = iconDecorator;
    }

    private void sendRequest(final String text,
                             final int priority,
                             final RequestType requestType) {
        Request newRequest =
                new Request(patientUsername,
                        text,
                        priority,
                        requestType,
                        nurseUsername);

        HealthAppApi healthAppApi = new HealthAppApi();
        healthAppApi.sendRequest(newRequest);

        if (listFragment != null) {
            listFragment.addRequest(newRequest);
        }

    }

    public void ImageViewAnimatedChange( final ImageView v, final Bitmap new_image, final Bitmap old_image) {
        final Animation anim_out = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.fade_out);
        final Animation anim_in  = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.fade_in);
        anim_out.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation)
            {
                v.setImageBitmap(new_image);
                anim_in.setDuration(2000);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationRepeat(Animation animation) {}
                    @Override public void onAnimationEnd(Animation animation) {
                        anim_out.setDuration(2000);
                        anim_out.setAnimationListener(new Animation.AnimationListener() {
                            @Override public void onAnimationStart(Animation animation) {}
                            @Override public void onAnimationRepeat(Animation animation) {}
                            @Override public void onAnimationEnd(Animation animation) {
                                v.setImageBitmap(old_image);
                            }
                        });
                        v.startAnimation(anim_out);
                   }
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }

}