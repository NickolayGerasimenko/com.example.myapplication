package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.facebook.drawee.view.SimpleDraweeView;
import com.example.myapplication.callbacks.GetUserCallback;
import com.example.myapplication.entities.User;
import com.example.myapplication.requests.UserRequest;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;


public class ProfileActivity extends Activity implements GetUserCallback.IGetUserResponse {
    private static final int PERMISSION_READ_STATE = 666;
    private SimpleDraweeView mProfilePhotoView;
    private TextView mName;
    private TextView mId;
    private TextView mEmail;
    private TextView mPermissions;
    private TextInputLayout mHintUsernameText;
    private EditText mUsernameText;
    private TextInputLayout mHintUserEmailText;
    private EditText mUserEmailText;
    private EditText editTextCarrierNumber;
    private CountryCodePicker ccp;
    private TextView textview1;
    private Runnable hideNotification;
    private Handler h;
    private View send_button;
    private boolean mIsValidNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mProfilePhotoView = findViewById(R.id.profile_photo);
        mName = findViewById(R.id.name);
        mId = findViewById(R.id.id);
        mEmail = findViewById(R.id.email);
        mPermissions = findViewById(R.id.permissions);

        mHintUsernameText = findViewById(R.id.username);
        mUsernameText = findViewById(R.id.username_edit);

        mHintUserEmailText = findViewById(R.id.useremail);
        mUserEmailText = findViewById(R.id.useremail_edit);

        ccp = findViewById(R.id.ccp);
        editTextCarrierNumber = findViewById(R.id.editText_carrierNumber);
        ccp.registerCarrierNumberEditText(editTextCarrierNumber);

        ccp.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {

            @Override
            public void onValidityChanged(boolean isValidNumber) {
                mIsValidNumber = isValidNumber;
            }
        });

        textview1 = findViewById(R.id.tw_notification);

        h = new Handler();

        send_button = findViewById(R.id.messenger_send_button);
        send_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showNotification(mIsValidNumber ? "Phone: " + ccp.getFullNumber() : "Phone number invalid");
                if(mIsValidNumber)
                {
                    MailService mailer = new MailService(ProfileActivity.this,"nickoleg@gmail.com","eneeri@gmail.com","Test Android Mail","Test Android TextBody", "<b>Test Android TextBody</b>");
                    try {
                        mailer.send();
                    } catch (Exception e) {
                        showNotification("Failed sending email: " + e.getMessage());
                    }
                }
            }
        });

        hideNotification = new Runnable() {
            @Override
            public void run() {
                textview1.setVisibility(View.GONE);
            }
        };
    }

    protected void showNotification(String msg)
    {
        textview1.setText(msg);
        textview1.setVisibility(View.VISIBLE);

        // Remove the existing hide instruction
        h.removeCallbacks(hideNotification);
        // Hide Toast notification after few secs
        h.postDelayed(hideNotification, 4000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserRequest.makeUserRequest(new GetUserCallback(ProfileActivity.this).getCallback());
    }

    @Override
    public void onCompleted(User user) {
        mProfilePhotoView.setImageURI(user.getPicture());
        mName.setText(user.getName());
        mId.setText(user.getId());
        if (user.getEmail() == null) {
            mEmail.setText(R.string.no_email_perm);
            mEmail.setTextColor(Color.RED);
        } else {
            mEmail.setText(user.getEmail());
            mEmail.setTextColor(Color.BLACK);
        }
        mPermissions.setText(user.getPermissions());

        //mHintUsernameText.setHint(R.string.form_username);
        mUsernameText.setText(user.getName());
        mUserEmailText.setText(user.getEmail());
    }
}
