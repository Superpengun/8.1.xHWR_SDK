package com.sinovoice.example;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.sinovoice.hcicloudsdk.api.HciCloudSys;
import com.sinovoice.hcicloudsdk.common.AuthExpireTime;
import com.sinovoice.hcicloudsdk.common.HciErrorCode;
import com.sinovoice.hcicloudsdk.common.InitParam;


/**
 * @author sinovoice
 */
public class HciCloudExampleActivity extends Activity {
	private static final String TAG = "HciCloudExampleActivity";

    /**
     * 展示引擎返回信息的TextView
     */
    private TextView mLogView;

    private ImageView mImageView;

    /**
     * 加载用户信息工具类
     */
    private AccountInfo mAccountInfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);

		// 初始化属性
		mLogView = (TextView) findViewById(R.id.logview);

        mImageView = (ImageView) findViewById(R.id.show_pen_script_iv);

        mAccountInfo = AccountInfo.getInstance();
        boolean loadResult = mAccountInfo.loadAccountInfo(this);
        if (loadResult) {
            // 加载信息成功进入主界面
        	mLogView.setText("加载灵云账号成功");
        } else {
            // 加载信息失败，显示失败界面
        	mLogView.setText("加载灵云账号失败！请在assets/AccountInfo.txt文件中填写正确的灵云账户信息，账户需要从www.hcicloud.com开发者社区上注册申请。");
            return;
        }
        
        // 加载信息,返回InitParam, 获得配置参数的字符串
        InitParam initParam = getInitParam();
        String strConfig = initParam.getStringConfig();
        Log.i(TAG,"\nhciInit config:" + strConfig);
        
        // 初始化
        int errCode = HciCloudSys.hciInit(strConfig, this);
        if (errCode != HciErrorCode.HCI_ERR_NONE && errCode != HciErrorCode.HCI_ERR_SYS_ALREADY_INIT) {
        	mLogView.append("\nhciInit error: " + HciCloudSys.hciGetErrorInfo(errCode));
            return;
        } else {
        	mLogView.append("\nhciInit success");
        }

        // 获取授权/更新授权文件 :
        errCode = checkAuthAndUpdateAuth();
        if (errCode != HciErrorCode.HCI_ERR_NONE) {
            // 由于系统已经初始化成功,在结束前需要调用方法hciRelease()进行系统的反初始化
        	mLogView.append("\nCheckAuthAndUpdateAuth error: " + HciCloudSys.hciGetErrorInfo(errCode));
            HciCloudSys.hciRelease();
            return;
        }
        HciCloudFuncHelper.demo(this,mAccountInfo.getCapKey(),mLogView, mImageView);
        return;
	}
	
    @Override
    protected void onDestroy() {
        // 释放HciCloudSys，当其他能力全部释放完毕后，才能调用HciCloudSys的释放方法
        HciCloudSys.hciRelease();
        mLogView.append("\nhciRelease");
        super.onDestroy();
    }
    
    
    /**
     * 加载初始化信息
     *
     * @return 系统初始化参数
     */
    private InitParam getInitParam() {
        // 前置条件：无
        InitParam initparam = new InitParam();
        String authDirPath = "";
        String logPath = "";
        String sdcardState = Environment.getExternalStorageState();

        // 授权与日志路径必须存在
        if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
            String sdPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();
            String packageName = this.getPackageName();

//            authDirPath =  sdPath + File.separator + "sinovoice666"
//                    + File.separator + packageName + File.separator + "auth"
//                    + File.separator;
            authDirPath =  sdPath + File.separator + "sinovoice666";


            logPath = sdPath + File.separator + "sinovoice"
                    + File.separator + packageName + File.separator + "log"
                    + File.separator;

            // 授权文件地址
            File fileDir = new File(authDirPath);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }

            // 日志文件地址
            fileDir = new File(logPath);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
        }


        // 灵云云服务的接口地址，此项必填
        initparam.addParam(InitParam.AuthParam.PARAM_KEY_CLOUD_URL, AccountInfo
                .getInstance().getCloudUrl());
        // 开发者Key，此项必填，由捷通华声提供
        initparam.addParam(InitParam.AuthParam.PARAM_KEY_DEVELOPER_KEY, AccountInfo
                .getInstance().getDeveloperKey());
        // 应用Key，此项必填，由捷通华声提供
        initparam.addParam(InitParam.AuthParam.PARAM_KEY_APP_KEY, AccountInfo
                .getInstance().getAppKey());
        // 授权文件所在路径，此项必填
        initparam.addParam(InitParam.AuthParam.PARAM_KEY_AUTH_PATH, authDirPath);

        // 日志的路径，可选，如果不传或者为空则不生成日志
        initparam.addParam(InitParam.LogParam.PARAM_KEY_LOG_LEVEL, "5");
        initparam.addParam(InitParam.LogParam.PARAM_KEY_LOG_FILE_PATH, logPath);

        return initparam;
    }

    /**
     * 获取授权
     * 
     * @return true 成功
     */
    private int checkAuthAndUpdateAuth() {
        
    	// 获取系统授权到期时间
        int initResult;
        AuthExpireTime objExpireTime = new AuthExpireTime();
        initResult = HciCloudSys.hciGetAuthExpireTime(objExpireTime);
        if (initResult == HciErrorCode.HCI_ERR_NONE) {
            // 显示授权日期,如用户不需要关注该值,此处代码可忽略
            Date date = new Date(objExpireTime.getExpireTime() * 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
                    Locale.CHINA);
            Log.i(TAG, "expire time: " + sdf.format(date));

            if (objExpireTime.getExpireTime() * 1000 > System
                    .currentTimeMillis()) {
                // 已经成功获取了授权,并且距离授权到期有充足的时间(>7天)
                Log.i(TAG, "checkAuth success");
                return initResult;
            }
            
        } 
        
        // 获取过期时间失败或者已经过期
        initResult = HciCloudSys.hciCheckAuth();
        if (initResult == HciErrorCode.HCI_ERR_NONE) {
            Log.i(TAG, "checkAuth success");
            return initResult;
        } else {
            Log.e(TAG, "checkAuth failed: " + initResult);
            return initResult;
        }
    }

}
