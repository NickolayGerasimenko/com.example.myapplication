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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    private static final int RESULT_PROFILE_ACTIVITY = 1;
    private static final int RESULT_POSTS_ACTIVITY = 2;
    private static final int RESULT_PERMISSIONS_ACTIVITY = 3;

    private static final String EMAIL = "email";
    private static final String USER_POSTS = "user_posts";
    private static final String AUTH_TYPE = "rerequest";

    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //FacebookSdk.sdkInitialize(getApplicationContext());

        Fresco.initialize(this);
        mCallbackManager = CallbackManager.Factory.create();

        // If MainActivity is reached without the user being logged in, redirect to the Login
        // Activity
        if (AccessToken.getCurrentAccessToken() != null) {
            Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(profileIntent);
        }

        LoginButton mLoginButton = findViewById(R.id.login_button);

        // Set the initial permissions to request from the user while logging in
        mLoginButton.setPermissions(Arrays.asList(EMAIL, USER_POSTS));

        mLoginButton.setAuthType(AUTH_TYPE);

        // Register a callback to respond to the user
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
            }

            @Override
            public void onCancel() {
                setResult(RESULT_CANCELED);
                finish();
            }

            @Override
            public void onError(FacebookException e) {
                // Handle exception
            }
        });


        // Make a button which leads to profile information of the user
        Button gotoProfileButton = findViewById(R.id.btn_profile);
        gotoProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AccessToken.getCurrentAccessToken() == null) {
                    Intent profileIntent = new Intent(MainActivity.this, MainActivity
                            .class);
                    startActivityForResult(profileIntent, RESULT_PROFILE_ACTIVITY);
                } else {
                    Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(profileIntent);
                }
            }
        });

        // Make a button which leads to posts made by the user
        Button gotoPostsFeedButton = findViewById(R.id.btn_posts);
        gotoPostsFeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AccessToken.getCurrentAccessToken() == null) {
                    Intent loginIntent = new Intent(MainActivity.this, MainActivity.class);
                    startActivityForResult(loginIntent, RESULT_POSTS_ACTIVITY);
                } else {
                    Intent postsFeedIntent = new Intent(MainActivity.this, PostFeedActivity.class);
                    startActivity(postsFeedIntent);
                }
            }
        });

        // Make a button which leads to request or revoke permissions
        Button gotoPermissionsButton = findViewById(R.id.btn_permissions);
        gotoPermissionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AccessToken.getCurrentAccessToken() == null) {
                    Intent loginIntent = new Intent(MainActivity.this, MainActivity.class);
                    startActivityForResult(loginIntent, RESULT_PERMISSIONS_ACTIVITY);
                } else {
                    Intent permissionsIntent = new Intent(MainActivity.this, PermissionsActivity
                            .class);
                    startActivity(permissionsIntent);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_PROFILE_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(profileIntent);
                }
                break;
            case RESULT_POSTS_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    Intent postFeedIntent = new Intent(MainActivity.this, PostFeedActivity.class);
                    startActivity(postFeedIntent);
                }
                break;
            case RESULT_PERMISSIONS_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    Intent permissionsIntent = new Intent(MainActivity.this, PermissionsActivity
                            .class);
                    startActivity(permissionsIntent);
                }
                break;
            default:
                if (AccessToken.getCurrentAccessToken() == null) {
                    Intent profileIntent = new Intent(MainActivity.this, MainActivity
                            .class);
                    startActivityForResult(profileIntent, RESULT_PROFILE_ACTIVITY);
                } else {
                    Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(profileIntent);
                }
                //super.onActivityResult(requestCode,resultCode, data);
        }
    }
}
