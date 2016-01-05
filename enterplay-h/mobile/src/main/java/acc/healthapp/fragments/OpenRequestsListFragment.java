package acc.healthapp.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;


import acc.healthapp.R;
import acc.healthapp.api.GetAllRequestCallback;
import acc.healthapp.api.HealthAppApi;
import acc.healthapp.model.Request;
import acc.healthapp.model.RequestCurrentPriority;
import acc.healthapp.model.RequestStatus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class OpenRequestsListFragment extends Fragment implements
        AbsListView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener{

    private static final String ARG_DIALOG_HELPER = "dialogHelper";
    private static final String ARG_USERNAME = "userNameParameter";

    private String mUserName;

    private OnFragmentInteractionListener mListener;

    private SwipeRefreshLayout swipeLayout;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ArrayAdapter mAdapter;

    private DialogHelper dialogHelper;
    private RequestIconDecorator iconDecorator;

    private Integer backgroundColor;
    private RequestCurrentPriority filterPriority;

    public static OpenRequestsListFragment newInstance(String username,
                                                       DialogHelper dialogHelper,
                                                       RequestIconDecorator iconDecorator,
                                                       Integer backgroundColor,
                                                       RequestCurrentPriority filterByPriority) {
        OpenRequestsListFragment fragment = new OpenRequestsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username);

        if (backgroundColor != null) {
            fragment.setBackgroundColor(backgroundColor);
        }

        if (dialogHelper != null) {
            fragment.setDialogHelper(dialogHelper);
        }

        if (iconDecorator != null) {
            fragment.setIconDecorator(iconDecorator);
        }

        if (filterByPriority != null) {
            fragment.setFilterPriority(filterByPriority);
        }

        fragment.setArguments(args);
        return fragment;
    }

    public static OpenRequestsListFragment newInstance(String username,
                                                       DialogHelper dialogHelper,
                                                       RequestIconDecorator iconDecorator,
                                                       Integer backgroundColor) {
        OpenRequestsListFragment fragment = new OpenRequestsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username);

        if (backgroundColor != null) {
            fragment.setBackgroundColor(backgroundColor);
        }

        if (dialogHelper != null) {
            fragment.setDialogHelper(dialogHelper);
        }

        if (iconDecorator != null) {
            fragment.setIconDecorator(iconDecorator);
        }

        fragment.setArguments(args);
        return fragment;
    }

    public static OpenRequestsListFragment newInstance(String username,
                                                       DialogHelper dialogHelper,
                                                       RequestIconDecorator iconDecorator) {
        OpenRequestsListFragment fragment = new OpenRequestsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username);

        if (dialogHelper != null) {
            fragment.setDialogHelper(dialogHelper);
        }

        if (iconDecorator != null) {
            fragment.setIconDecorator(iconDecorator);
        }

        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OpenRequestsListFragment() {
    }

    public void setDialogHelper(DialogHelper helper) {
        this.dialogHelper = helper;
    }

    public void setIconDecorator(RequestIconDecorator decorator) { this.iconDecorator = decorator; }

    public void setBackgroundColor(Integer backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setFilterPriority(RequestCurrentPriority filterPriority) {
        this.filterPriority = filterPriority;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mUserName = getArguments().getString(ARG_USERNAME);
        }

        mAdapter = new RequestAdapter(getActivity(),
                                      android.R.layout.simple_list_item_1,
                                      android.R.id.text1,
                                      iconDecorator);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.request_list, container, false);

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);

        if (backgroundColor != null) {
            View layoutView = view.findViewById(R.id.request_list_layout);
            layoutView.setBackgroundResource(backgroundColor);
        }

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        downloadOpenRequests();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (null != mListener) {
            if (dialogHelper != null) {

                final Request request = (Request) mAdapter.getItem(position);

                if (request != null) {
                    dialogHelper.renderDialog(view.getContext(), request)
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            downloadOpenRequests();
                        }
                    });
                }

            }
        }
    }

    @Override public void onRefresh() {
        downloadOpenRequests();
    }

    public void updateData() {
        downloadOpenRequests();
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    public void disableDivider() {
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String id);
    }

    public void addRequest(final Request request) {
        if (mAdapter != null) {
            mAdapter.add(request);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void downloadOpenRequests() {
        HealthAppApi healthAppApi = new HealthAppApi();

        healthAppApi.getUserRequests(mUserName, new GetAllRequestCallback() {
            @Override
            public void getAllRequests(List<Request> requests) {
                updateList(requests);

                if (swipeLayout.isRefreshing()) {
                    swipeLayout.setRefreshing(false);
                }

            }
        });
    }

    private void updateList(final List<Request> requests) {

        List<Request> copyOfRequests = new ArrayList<>();

        for (Request request : requests) {
            if (request.getRequestStatus().toInt().intValue() != RequestStatus.COMPLETED) {

                // if there is a filter just add those that matches the filter
                if (filterPriority != null) {
                    if (request.getRequestCurrentPriority() == filterPriority) {
                        copyOfRequests.add(request);
                    }

                } else {
                    copyOfRequests.add(request);
                }


            }
        }

        if (mListView != null) {

            /// Sort the initial list by time
            Collections.sort(copyOfRequests, new Comparator<Request>() {
                @Override
                public int compare(Request lhs, Request rhs) {
                    if( lhs.getDateCreated().getTime() < rhs.getDateCreated().getTime() ){
                        return 1;
                    } else if( lhs.getDateCreated().getTime() > rhs.getDateCreated().getTime() ){
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });

            /// Sort the initial list by priority
            Collections.sort(copyOfRequests, new Comparator<Request>() {
                @Override
                public int compare(Request lhs, Request rhs) {
                    if( lhs.getPriority() < rhs.getPriority() ){
                        return 1;
                    } else if( lhs.getPriority() > rhs.getPriority() ){
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });

            mAdapter = new RequestAdapter(getActivity(),
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    copyOfRequests,
                    iconDecorator);

            mListView.setAdapter(mAdapter);

            mAdapter.notifyDataSetChanged();
        }

    }

}
