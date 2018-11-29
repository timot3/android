package org.hackillinois.android;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.TextView;

public class activity_schedule extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.activity_schedule_recyclerview);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        // TODO: Get the dataset
        mAdapter = new MyAdapter(dataset);
        recyclerView.setAdapter(mAdapter);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DayFragment extends Fragment {
        // TODO: fix this to fit dates
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public DayFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static DayFragment newInstance(int sectionNumber) {
            DayFragment fragment = new DayFragment();
            Bundle args = new Bundle();

            // TODO: Depending on the section number, filter out dates from the JSON
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_activity_schedule, container, false);

            // TODO: delete this part? what does it do
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return DayFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

    // Adapter for RecyclerView
    // TODO: adapt to schedule view
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private String[] mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder

        public class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            TextView tv_eventTitle;
            TextView tv_eventDetail;
            TextView tv_eventLocation;
            ImageButton imageButton_star;
            ConstraintLayout constraintLayout_recyclerView;

            public MyViewHolder(View parent) {
                super(parent);
                tv_eventTitle = itemView.findViewById(R.id.eventTitle);
                tv_eventDetail = itemView.findViewById(R.id.eventDetail);
                tv_eventLocation = itemView.findViewById(R.id.eventLocation);
                imageButton_star = itemView.findViewById(R.id.star);
                constraintLayout_recyclerView = itemView.findViewById(R.id.constraintLayout);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        // TODO: get JSON data for this
        public MyAdapter(String[] myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_events, parent, false);
            MyAdapter.MyViewHolder viewHolder = new MyAdapter.MyViewHolder(view);
            return viewHolder;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.tv_eventTitle.setText();
            holder.tv_eventDetail.setText();
            holder.tv_eventLocation.setText();
            holder.imageButton_star.setOnClickListener(new View.OnClickListener() {
                public void onClick(View button) {
                    // TODO: toggle the button
                    // Can you use checked instead of selected?
                    button.setSelected(!button.isSelected());

                    if (button.isSelected()) {
                        // TODO: pop up a snackbar? when they click the notification star
                        // make sure they don't stack up when they go through and get a lot of notifications?
                        // also make sure you can just use this instead of findviewbyid
                        // TODO: come up with a string
                        Snackbar.make(this, R.string.email_sent,
                                Snackbar.LENGTH_SHORT)
                                .show();
                        //Handle selected state change
                    } else {
                        //Handle de-select state change
                    }
                }
            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.length;
        }
    }
}
