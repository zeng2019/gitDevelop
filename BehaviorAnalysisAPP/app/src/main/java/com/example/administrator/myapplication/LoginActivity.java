package com.example.administrator.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;

import android.content.Intent;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.myapplication.BaseAcivity.BaseActivity;
import com.example.administrator.myapplication.Model.UserInfo;
import com.example.administrator.myapplication.greendao.DaoSession;
import com.example.administrator.myapplication.greendao.UserInfoDao;
import com.example.administrator.myapplication.utils.AppManager;

import org.greenrobot.greendao.query.QueryBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * 名称：LoginActivity
 * 主要内容：用于用户登陆，目前采用数据库登陆，要修改为后台验证登陆
 * 创建人：
 * 创建时间：
 */

public class LoginActivity extends BaseActivity {


    /**
     * 包含已知用户名和密码的虚拟认证存储。
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "123@123.com:12345", "234@123.com:12345"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    private UserLoginTask mAuthTask = null;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private UserInfoDao userInfoDao;
    //以下两个参数用于显示时间
    private TextView sys_time;
    private static final int msgKey1 = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        //在密码编辑界面判断软键盘，正确后尝试登陆
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                //判断软键盘
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        TextView mRegister = (TextView) findViewById(R.id.tv_register);

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        //
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        /*
         *注册活动跳转
         */
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        /*
        * 实时时间显示
         */
        sys_time = (TextView)findViewById(R.id.timeText);
        new TimeThread().start();

    }


    /**
     * Attempt
     * rrors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     * 尝试登录或注册登录表单指定的帐户。
     * 如果存在表单错误（无效电子邮件、丢失字段等），则
     * 出现错误，不进行实际的登录尝试。
     */
     //待修改 目标不是email登录  可能用到手机登录
     //
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        //设置输入框的错误提示为空
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        // 获取输入框的邮箱和密码
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //格式（不能为空，不能小于4位）如果格式错误重新获得焦点，并提示错误内容
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        // Check for a valid email address.
        // 设置邮箱格式
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            //如果格式错误，输入框重新获得输入焦点
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //如果输入的格式正确，显示验证等待对话框，并启动验证线程
            showProgress(true);
            //revised by zeng, 20190416
            if (email.equals("123@123.com") && password.equals("12345"))
            {
                showToast("登录成功");
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
            else {
                mAuthTask = new UserLoginTask(email, password); //非测试账号，登录数据库检测，
                mAuthTask.execute((Void) null);
            }
            ////////////////////
        }
    }


    private boolean isEmailValid(String email) {

        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    //

    /**
     * Shows the progress UI and hides the login form.//指出应用程序的API版本
     * 显示过渡UI，隐藏登录文本框
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        //获取运行平台的版本与应用的版本对比实现功能的兼容性
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            //获取系统定义时间
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);//设置验证对话框为可显示
            //设置动画时间
            mLoginFormView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)//设置动画渐变效果
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                            //跟据参数控制该控件显示或隐藏
                        }
                    });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);//设置输入界面可显
            mProgressView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            // 跟据参数控制该控件显示或隐藏
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);

        }
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.后台用户验证
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

              //Json
           // HashMap<String,String> userInfo = new HashMap<>();
           // userInfo.put("usnameId",mEmail);
           // userInfo.put("password",mPassword);
           // JSONObject jsonObject= new JSONObject(userInfo);

            /*
             *
             × 这里实现验证操作
             × 基本思路：第一次验证从服务器获得token保存
             × 以后每次启动app，都是用token来登录
             *
             */
            // TODO:网络验证设计
            //URL
           // OkGo.<String>post("www.baidu.com")
            //        .tag(this)
            //        .upJson(jsonObject)
            //        .execute(new StringCallback() {
            //            @Override
           //             public void onSuccess(Response<String> response) {
                  //}
                 //   });


            //后台运行线程

            try {
                // Simulate network access.//模拟用户验证耗时

                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }
            //使用数据库登陆
            DaoSession daoSession = ((myApp)getApplication()).getDaoSession();
            userInfoDao = daoSession.getUserInfoDao();
            QueryBuilder<UserInfo> userQB = userInfoDao.queryBuilder();

            if(userQB.where(UserInfoDao.Properties.Email.eq(mEmail),UserInfoDao.Properties.Password.eq(mPassword)).list().size() > 0){
                  return true;
            }else {
                showToast("登录错误！");
                  return false;
            }

            //for (String credential : DUMMY_CREDENTIALS) {//遍历数组验证自定义用户及密码
            //    String[] pieces = credential.split(":");//分割字符串，将密码个邮箱分离开
            //    if (pieces[0].equals(mEmail)) {
            //        // Account exists, return true if the password matches.
            //        return pieces[1].equals(mPassword);
            //    }
          //  }

            // TODO: register the new account here.
          //  return true;
        }

        @Override
        //线程结束后的ui处理
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);//隐藏验证延时对话框

            if (success) {
                showToast("登录成功");
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                //密码错误，输入框获得焦点，并提示错误
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        //取消验证
        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    /*
    * 用于时间显示的线程处理程序
    * revised at 2019-04-21
     */
    public class TimeThread extends Thread{
        @Override
        public void run() {
            super.run();
            do{
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = msgKey1;
                    mHandler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case msgKey1:
                    long time = System.currentTimeMillis();
                    Date date = new Date(time);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒 EEE");
                    sys_time.setText(format.format(date));
                    break;
                    default:
                        break;
            }
        }
    };
    /**************revised at 2019-04-21*******************/


    //用于toast调用
    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
             AppManager.getAppManager().AppExit();
            return false;
        }
        return super.onKeyDown(keyCode, event);

    }



}
