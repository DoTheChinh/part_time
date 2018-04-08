package com.example.kaihuynh.part_timejob;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kaihuynh.part_timejob.controllers.UserManager;
import com.example.kaihuynh.part_timejob.models.User;
import com.example.kaihuynh.part_timejob.others.CustomViewPager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalDescriptionFragment extends Fragment {

    private Button mPreviousButton, mDoneButton;
    private CustomViewPager mViewPager;
    private EditText mDescription;
    private ProgressDialog mProgress;

    //Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabaseReference;
    private FirebaseAuth mAuth;


    private static PersonalDescriptionFragment sInstance = null;

    public PersonalDescriptionFragment() {
        // Required empty public constructor
        sInstance = this;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_personal_description, container, false);

        addComponents(view);
        initialize();
        addEvents();

        return view;
    }

    private void initialize() {
        mProgress = new ProgressDialog(getContext());
        mProgress.setMessage("Đang xử lý thông tin...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mUserDatabaseReference = mFirebaseDatabase.getReference().child("users");


    }

    private void addEvents() {
        previousButtonEvents();
        doneButtonEvents();
    }

    private void doneButtonEvents() {
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dob = PersonalInfoFragment.getInstance().getPersonalInfo().get("dob");
                String[] dateSplit = dob.split("-");
                int dayOfMonth = Integer.parseInt(dateSplit[0]);
                int month = Integer.parseInt(dateSplit[1]) - 1;
                int year = Integer.parseInt(dateSplit[2]);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
                calendar.set(year, month, dayOfMonth);
                User user = UserManager.getInstance().getUser();
                user.setAddress(PersonalInfoFragment.getInstance().getPersonalInfo().get("address"));
                user.setGender(PersonalInfoFragment.getInstance().getPersonalInfo().get("gender"));
                user.setDayOfBirth(calendar.getTime());
                user.setEducation(PersonalInfoFragment.getInstance().getPersonalInfo().get("education"));
                user.setPhoneNumber(PersonalInfoFragment.getInstance().getPersonalInfo().get("phone"));
                user.setForeignLanguages(ForeignLanguageFragment.getInstance().getLanguages());
                user.setSkills(SkillFragment.getInstance().getSkills());
                user.setPersonalDescription(mDescription.getText().toString());
                Log.v("date", calendar.getTime().toString());
                UserManager.getInstance().updateUser(user);
                showAlertDialog();
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater().from(getContext());
        View view = inflater.inflate(R.layout.register_notification_dialog, null);
        TextView t1 = view.findViewById(R.id.tv_success);
        TextView t2 = view.findViewById(R.id.tv_content_success);
        t1.setText("Đăng ký thông tin thành công");
        t2.setVisibility(View.GONE);
        builder.setView(view);
        builder.setPositiveButton("Tiếp tục", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(getActivity().getIntent().getStringExtra("activity").equals("RegisterAccountInfoActivity")){
                    startActivity(new Intent(getContext(), HomePageActivity.class));
                    LoginMethodActivity.getInstance().finish();
                }else if(getActivity().getIntent().getStringExtra("activity").equals("HomePageActivity")){
                    startActivity(new Intent(getContext(), ProfileActivity.class));
                }else if(getActivity().getIntent().getStringExtra("activity").equals("RecruitingActivity")){
                    startActivity(new Intent(getContext(), RecruitingActivity.class));
                }
                getActivity().finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void previousButtonEvents() {
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem()-1);
            }
        });
    }

    private void addComponents(View view) {
        mDescription = view.findViewById(R.id.et_personal_description);
        mPreviousButton = view.findViewById(R.id.btn_previous_description);
        mDoneButton = view.findViewById(R.id.btn_done_description);
        mViewPager = RegisterPersonalInfoActivity.getInstance().findViewById(R.id.viewPage_register);
    }

    public static PersonalDescriptionFragment getInstance(){
        if (sInstance == null) {
            sInstance = new PersonalDescriptionFragment();
        }
        return sInstance;
    }

}
