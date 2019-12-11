/**
 * Copyright (c) 2017-present, Facebook, Inc. All rights reserved.
 * <p>
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Facebook.
 * <p>
 * As with any software that integrates with the Facebook platform, your use of
 * this software is subject to the Facebook Developer Principles and Policies
 * [http://developers.facebook.com/policy/]. This copyright notice shall be
 * included in all copies or substantial portions of the software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.TelephonyManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.facebook.drawee.view.SimpleDraweeView;
import com.example.myapplication.callbacks.GetUserCallback;
import com.example.myapplication.entities.User;
import com.example.myapplication.requests.UserRequest;
import com.google.android.material.textfield.TextInputLayout;

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
    private TelephonyManager tMgr;
    private TextView mPhoneNumberText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mProfilePhotoView = findViewById(R.id.profile_photo);
        mName = findViewById(R.id.name);
        mId = findViewById(R.id.id);
        mEmail = findViewById(R.id.email);
        mPermissions = findViewById(R.id.permissions);

        mHintUsernameText = (TextInputLayout)findViewById(R.id.username);
        mUsernameText = (EditText)findViewById(R.id.username_edit);

        mHintUserEmailText = (TextInputLayout)findViewById(R.id.useremail);
        mUserEmailText = (EditText)findViewById(R.id.useremail_edit);

        mPhoneNumberText = (EditText)findViewById(R.id.userphone_edit);
        mPhoneNumberText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());


        tMgr = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.READ_PHONE_STATE ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, PERMISSION_READ_STATE);
    }

        String mPhoneNumber = tMgr.getLine1Number();

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_READ_STATE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted!
                    // you may now do the action that requires this permission
                } else {
                    // permission denied
                    finish();
                }
                return;
            }

        }
    }
}
