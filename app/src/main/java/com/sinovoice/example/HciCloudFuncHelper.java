package com.sinovoice.example;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.TextView;

import com.sinovoice.hcicloudsdk.api.HciCloudSys;
import com.sinovoice.hcicloudsdk.api.hwr.HciCloudHwr;
import com.sinovoice.hcicloudsdk.common.HciErrorCode;
import com.sinovoice.hcicloudsdk.common.Session;
import com.sinovoice.hcicloudsdk.common.hwr.HwrConfig;
import com.sinovoice.hcicloudsdk.common.hwr.HwrInitParam;
import com.sinovoice.hcicloudsdk.common.hwr.HwrPenScriptResult;
import com.sinovoice.hcicloudsdk.common.hwr.HwrPenScriptResultItem;
import com.sinovoice.hcicloudsdk.common.hwr.HwrRecogResult;
import com.sinovoice.hcicloudsdk.common.hwr.HwrRecogResultItem;
import com.sinovoice.hcicloudsdk.common.hwr.HwrAssociateWordsResult;
import com.sinovoice.hcicloudsdk.common.hwr.PenScriptConfig;

public class HciCloudFuncHelper extends HciCloudHelper{
    private static final String TAG = HciCloudFuncHelper.class.getSimpleName();

    //实时每次识别传入笔迹的个数，以-1,0，-1，-1结尾
  	static int g_nStrokeLen[] = { 28, 60, 94, 148, 198,250 };

    // 参数识别的数据， “一个人”，点集每一笔以(-1,0)结束，最后一笔增加(-1,-1)作为笔迹的结束符
    // 多字识别
    static short sShortData[] = { 103, 283, 105, 283, 107, 283, 113, 283,
            120, 283, 129, 283, 138, 283, 146, 283, 156, 283, 162, 283, 165,
            283, 166, 283, -1, 0, 282, 245, 277, 27, 270, 251, 266, 255, 263,
            257, 259, 261, 254, 266, 250, 273, 246, 281, 243, 286, 240, 292,
            240, 294, 239, 296, 238, 297, 238, 298, -1, 0, 262, 271, 264, 272,
            266, 272, 268, 272, 270, 273, 272, 274, 275, 274, 278, 276, 280,
            278, 283, 279, 286, 281, 289, 282, 289, 283, 291, 284, 292, 285,
            292, 286, -1, 0, 268, 281, 268, 282, 268, 284, 270, 287, 270, 290,
            270, 294, 270, 297, 270, 299, 270, 301, 270, 303, 270, 304, 270,
            306, 270, 308, 269, 309, 269, 310, 269, 311, 269, 312, 269, 314,
            269, 316, 269, 318, 269, 319, 269, 321, 269, 322, 269, 323, 269,
            324, 268, 324, -1, 0, 382, 255, 382, 256, 382, 260, 382, 263, 381,
            267, 378, 274, 375, 278, 373, 282, 372, 287, 371, 291, 369, 294,
            368, 297, 367, 300, 367, 301, 366, 302, 365, 304, 364, 305, 364,
            306, 363, 308, 362, 308, 362, 309, 361, 310, 361, 311, 360, 311,
            -1, 0, 376, 289, 377, 290, 378, 290, 380, 291, 381, 292, 382, 293,
            384, 294, 385, 294, 387, 297, 388, 298, 390, 299, 393, 300, 394,
            301, 396, 302, 398, 303, 400, 305, 401, 306, 403, 307, 404, 309,
            405, 309, 407, 311, 408, 312, 409, 314, 410, 314, 411, 314, -1, 0,
            -1, -1 };


    // 英文笔迹 连写"psychology"
    static short englishWord[] ={218, 91, -1, 0, 218, 101, 218, 104,218, 107, 217, 113, 216, 119, 214, 128, 212, 138, 210, 148, 208,159, 206, 170, 205, 182, 204, 194, 202, 208, 201, 221, 200, 234,199, 247, 198, 259, 197, 272, 196, 283, 195, 295, 194, 305, 193,315, 192, 324, 191, 332, 190, 338, 190, 342, -1, 0, 210, 102, 213,100, 219, 96, 224, 94, 229, 92, 234, 90, 239, 89, 244, 87, 249, 87,254, 87, 259, 88, 263, 89, 267, 91, 270, 94, 273, 97, 275, 101,276, 105, 277, 110, 277, 114, 277, 120, 277, 125, 276, 130, 274,135, 271, 141, 269, 146, 265, 152, 262, 157, 258, 161, 254, 166,250, 170, 245, 174, 240, 179, 235, 182, 230, 186, 225, 189, 220,192, 217, 194, 213, 195, 209, 196, 204, 197, 199, 199, -1, 0, 341,165, 341, 163, 341, 159, 341, 156, 340, 153, 340, 151, 338, 148,335, 145, 331, 144, 328, 146, 324, 148, 321, 151, 317, 155, 314,158, 311, 162, 309, 166, 307, 170, 307, 174, 307, 179, 308, 183,309, 186, 311, 190, 314, 194, 317, 197, 321, 200, 325, 203, 328,206, 331, 208, 333, 211, 335, 213, 336, 216, 336, 219, 336, 221,336, 224, 335, 227, 334, 230, 331, 233, 329, 236, 326, 238, 323,240, 319, 243, 315, 245, 311, 246, 307, 247, 304, 246, 300, 245,295, 242, 291, 238, 289, 236, 288, 233, 287, 230, 286, 226, 285,223, 285, 221, -1, 0, 364, 152, 367, 156, 367, 159, 368, 164, 369,170, 369, 177, 370, 183, 370, 189, 370, 194, 371, 198, 371, 202,372, 204, 376, 207, 380, 206, 384, 203, 386, 200, 390, 195, 392,189, 395, 183, 397, 177, 399, 171, 402, 165, 403, 160, 405, 156,406, 151, 407, 147, 407, 143, 408, 140, 408, 138, 409, 136, 409,140, 409, 144, 409, 148, 409, 154, 409, 159, 409, 165, 409, 171,409, 176, 410, 182, 410, 188, 411, 194, 411, 198, 411, 204, 411,209, 411, 214, 411, 220, 410, 225, 410, 231, 409, 237, 408, 243,408, 249, 407, 254, 405, 260, 404, 265, 402, 270, 400, 275, 397,280, 394, 284, 391, 288, 388, 291, 384, 294, 380, 297, 377, 299,373, 301, 369, 302, 365, 303, 362, 304, 359, 304, 355, 304, 350,303, 346, 299, 344, 297, 343, 294, 342, 291, 341, 288, 340, 285,338, 281, 338, 278, 338, 275, 337, 272, 337, 270, -1, 0, 466, 148,466, 146, 464, 142, 463, 138, 462, 135, 460, 133, 458, 129, 455,128, 451, 130, 447, 135, 445, 139, 444, 143, 443, 148, 442, 153,442, 158, 441, 164, 441, 169, 441, 174, 441, 178, 441, 182, 442,185, 444, 188, 447, 191, 450, 191, 453, 191, 456, 191, 460, 190,463, 189, 466, 188, 469, 186, 471, 186, -1, 0, 504, 57, 506, 60,506, 63, 506, 67, 506, 71, 506, 76, 506, 82, 506, 88, 506, 96, 506,103, 506, 110, 507, 119, 507, 126, 508, 134, 508, 142, 508, 150,509, 158, 509, 166, 509, 173, 509, 180, 509, 185, 509, 189, 509,192, 509, 195, 511, 193, 511, 189, 512, 185, 513, 179, 514, 174,515, 169, 517, 165, 518, 160, 520, 156, 522, 152, 525, 147, 527,144, 530, 141, 535, 138, 538, 141, 540, 143, 541, 147, 542, 152,543, 157, 544, 162, 544, 167, 545, 171, 546, 175, 546, 179, 547,182, 547, 185, 548, 187, 549, 190, 550, 191, -1, 0, 578, 144, 575,148, 575, 152, 574, 155, 574, 160, 573, 165, 573, 169, 573, 173,573, 177, 575, 179, 575, 182, 578, 185, 581, 186, 585, 183, 589,179, 591, 177, 592, 174, 593, 172, 595, 169, 596, 166, 597, 163,597, 159, 597, 157, 597, 155, 596, 153, 594, 150, 593, 148, 592,147, -1, 0, 636, 58, 636, 62, 636, 65, 636, 68, 636, 70, 636, 72,636, 74, 636, 77, 636, 80, 637, 84, 637, 88, 637, 92, 637, 95, 637,97, 636, 100, 636, 103, 636, 106, 635, 110, 635, 113, 634, 116,634, 119, 634, 122, 634, 125, 634, 127, 634, 130, 634, 132, 634,135, 634, 137, 634, 140, 634, 142, 634, 144, 635, 150, 635, 152,635, 155, 635, 157, 636, 159, 636, 162, 636, 164, 638, 167, 638,170, 640, 174, 641, 176, 642, 179, 645, 182, 648, 183, 651, 181,652, 179, 654, 176, 655, 173, 657, 170, 658, 168, 659, 165, 659,163, 660, 161, -1, 0, 686, 139, 683, 141, 682, 144, 681, 147, 680,151, 680, 155, 679, 159, 679, 162, 679, 166, 680, 169, 681, 172,682, 174, 685, 177, 690, 176, -1, 0, 699, 163, 700, 160, 700, 156,700, 153, 700, 151, 700, 147, 700, 144, 699, 142, 698, 139, 695,136, 691, 136, 690, 135, -1, 0, 741, 146, -1, 0, 742, 136, 742,134, 740, 131, 739, 128, 736, 126, -1, 0, 728, 135, 727, 138, 726,140, 725, 144, 724, 148, 723, 152, 723, 155, 723, 158, 723, 162,725, 165, 730, 165, 733, 164, 737, 161, 738, 158, 740, 155, 741,152, 742, 149, 743, 147, 744, 144, 744, 142, 745, 139, 745, 136,745, 134, 746, 136, 746, 139, 746, 141, 746, 145, 746, 148, 746,152, 746, 155, 746, 158, 746, 162, 746, 165, 746, 168, 746, 171,746, 174, 746, 177, 747, 180, 747, 183, 747, 186, 747, 189, 747,192, 746, 196, 746, 199, 746, 202, 745, 206, 745, 209, 745, 211,744, 214, 744, 217, 743, 220, 742, 223, 741, 226, 741, 228, 740,231, 739, 234, 738, 236, 737, 239, 735, 242, 732, 246, 729, 249,725, 252, 721, 254, 717, 254, 713, 254, 708, 252, 704, 250, 700,247, 697, 243, 696, 241, 695, 238, 694, 236, 692, 233, 692, 230,691, 227, 690, 225, 689, 222, 689, 220, 688, 217, 688, 216, -1, 0,780, 116, 781, 119, 782, 122, 782, 126, 783, 129, 786, 128, 787,138, 789, 144, 790, 147, 794, 149, 798, 147, 801, 144, 803, 141,804, 138, 806, 134, 807, 131, 809, 127, 811, 123, 812, 120, 813,118, 813, 116, 815, 119, 815, 122, 815, 126, 815, 130, 815, 134,816, 138, 816, 142, 816, 146, 816, 150, 817, 155, 817, 159, 817,164, 817, 168, 817, 172, 816, 176, 816, 179, 816, 183, 815, 187,815, 191, 814, 194, 813, 198, 811, 201, 810, 205, 809, 208, 808,211, 807, 214, 806, 217, 805, 220, 804, 223, 803, 225, 802, 228,800, 231, 797, 233, 793, 235, 790, 236, 786, 234, 783, 232, 780,231, 776, 227, 774, 225, 771, 221, 770, 217, 770, 215, -1, 0, -1,-1};

    public static void demo(Context context, String capkey, TextView view, ImageView imageView) {
        setContext(context);
        setTextView(view);
        setImageView(imageView);

        // HWR 初始化
        // 使用初始化参数，主要参数为 datapath, 用于指定本地资源所在的路径
        HwrInitParam hwrInitParam = new HwrInitParam();
        String dataPath = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "sinovoice"
                + File.separator + context.getPackageName() + File.separator + "data";
        hwrInitParam.addParam(HwrInitParam.PARAM_KEY_DATA_PATH, dataPath);
        // 将assets下的资源拷贝到外部存储，供具体能力使用
            HciCloudHelper.copyAssetsFiles(context,dataPath);

        int errCode = HciCloudHwr.hciHwrInit(hwrInitParam.getStringConfig());
        if (errCode != HciErrorCode.HCI_ERR_NONE) {
            ShowMessage("hciHwrInit error:" + errCode + " " +HciCloudSys.hciGetErrorInfo(errCode));
            return;
        } else {
            ShowMessage("hciHwrInit Success");
        }

        // 进行具体功能的使用
        if(capkey.equalsIgnoreCase("hwr.local.associateword")){
            assoicateWordSzh();
            assoicateWordEn();
        }else if(capkey.equalsIgnoreCase("hwr.local.freestylus")){
            recog();
        }else if(capkey.equalsIgnoreCase("hwr.local.freestylus.v7")){
            recogV7();
        }else if(capkey.equalsIgnoreCase("hwr.local.penscript")){
            penScript();
        }


        //HWR反初始化
        HciCloudHwr.hciHwrRelease();
        ShowMessage("hciHwrRelease");
    }


    // 中文多字识别
    public static void recog() {
        int errCode = -1;
        HwrConfig sessionConfig = new HwrConfig();
        sessionConfig.addParam(HwrConfig.SessionConfig.PARAM_KEY_CAP_KEY, "hwr.local.freestylus");
        // 将在datapath/freestylus/chinese/ 下加载资源
        sessionConfig.addParam(HwrConfig.SessionConfig.PARAM_KEY_RES_PREFIX, "freestylus/chinese/");
        //sessionConfig.addParam(HwrConfig.SessionConfig.PARAM_KEY_RES_PREFIX, "freestylus/japan/");
        // 同一个Session可以多次调用识别, 本例仅演示单次调用。
        Session session = new Session();
        ShowMessage("HciCloudHwr hciHwrSessionStart config " + sessionConfig.getStringConfig());

        // 开始会话
        errCode = HciCloudHwr.hciHwrSessionStart(sessionConfig.getStringConfig(), session);
        if (HciErrorCode.HCI_ERR_NONE != errCode) {
        	ShowMessage("hciHwrSessionStart error:" + HciCloudSys.hciGetErrorInfo(errCode));
            return;
        }     
        ShowMessage("hciHwrSessionStart Success");

        // 开始识别，同一个session，可以使用不同配置的recogConfig进行识别
        HwrRecogResult recogResult = new HwrRecogResult();
        HwrConfig recogConfig = new HwrConfig();

        // 本此识别配置为行写（默认即为行写）
        recogConfig.addParam(HwrConfig.InputConfig.PARAM_KEY_SPLIT_MODE, "line");


        errCode = HciCloudHwr.hciHwrRecog(session, sShortData,
                recogConfig.getStringConfig(), recogResult);
        if (HciErrorCode.HCI_ERR_NONE != errCode) {
            System.out.println("HciCloudHwr hciHwrRecog return " + errCode);
            return;
        }

        showRecogResultResult(recogResult);

        System.out.println("HciCloudHwr hciHwrRecog Success");
        // 停止会话
        HciCloudHwr.hciHwrSessionStop(session);
        ShowMessage("hciHwrSessionStop");
    }


    // 英文连写识别
    public static void recogV7() {
        int errCode = -1;
        HwrConfig sessionConfig = new HwrConfig();
        sessionConfig.addParam(HwrConfig.SessionConfig.PARAM_KEY_CAP_KEY, "hwr.local.freestylus.v7");
        // 将在datapath/freestylusv7/ 下加载资源
        sessionConfig.addParam(HwrConfig.SessionConfig.PARAM_KEY_RES_PREFIX, "freestylusv7/");

        // 同一个Session可以多次调用识别, 本例仅演示单次调用。
        Session session = new Session();
        ShowMessage("HciCloudHwr hciHwrSessionStart config " + sessionConfig.getStringConfig());
        // 开始会话
        errCode = HciCloudHwr.hciHwrSessionStart(sessionConfig.getStringConfig(), session);
        if (HciErrorCode.HCI_ERR_NONE != errCode) {
            ShowMessage("hciHwrSessionStart error:" + HciCloudSys.hciGetErrorInfo(errCode));
            return;
        }
        ShowMessage("hciHwrSessionStart Success");

        HwrRecogResult recogResult = new HwrRecogResult();
        // 开始识别
        errCode = HciCloudHwr.hciHwrRecog(session, englishWord,
                sessionConfig.getStringConfig(), recogResult);
        if (HciErrorCode.HCI_ERR_NONE != errCode) {
            System.out.println("HciCloudHwr hciHwrRecog return " + errCode);
            return;
        }
        showRecogResultResult(recogResult);
        System.out.println("HciCloudHwr hciHwrRecog Success");
        // 停止会话
        HciCloudHwr.hciHwrSessionStop(session);
        ShowMessage("hciHwrSessionStop");
    }

    // 中文简体联想
    public static void assoicateWordSzh(){
        String word = "中国";

        // 同一个Session可以多次调用联想, 本例仅演示单次调用。
        Session assSession = new Session();
        HwrConfig assConfig = new HwrConfig();
        assConfig.addParam(HwrConfig.SessionConfig.PARAM_KEY_CAP_KEY,"hwr.local.freestylus;hwr.local.associateword");

        // 将在datapath/szh 下加载资源
        assConfig.addParam(HwrConfig.SessionConfig.PARAM_KEY_RES_PREFIX, "szh/");
        assConfig.addParam(HwrConfig.AssociateConfig.PARAM_KEY_ASSOCIATE_MODEL, "multi");
        ShowMessage("神牛的HciCloudHwr hciHwrSessionStart config " + assConfig.getStringConfig());
        int errorCode = HciCloudHwr.hciHwrSessionStart(assConfig.getStringConfig(), assSession);
        HwrAssociateWordsResult hwrAssWordsResult = new HwrAssociateWordsResult();


        int result = HciCloudHwr.hciHwrAssociateWords(assSession, null, word, hwrAssWordsResult);
        ShowMessage( "hciHwrAssociateWords result = " + result);
        ShowMessage( "["+ word +"] 的联想词：");
        if(hwrAssWordsResult.getResultList() != null && hwrAssWordsResult.getResultList().size() >0){
            for(int i = 0; i < hwrAssWordsResult.getResultList().size() ;i ++){
                ShowMessage("["+i+"] " + hwrAssWordsResult.getResultList().get(i));
            }
        }

        String wordsToAdjust = "中国AAAA";
        String config ="";

        errorCode = HciCloudHwr.hciHwrAssociateWordsAdjust(assSession, config, wordsToAdjust);
        result = HciCloudHwr.hciHwrAssociateWords(assSession, null, word, hwrAssWordsResult);
        ShowMessage( "hciHwrAssociateWords result = " + result);
        ShowMessage( "["+ word +"] 的联想词(调整后)：AAAA会出现在前列");
        if(hwrAssWordsResult.getResultList() != null && hwrAssWordsResult.getResultList().size() >0){
            for(int i = 0; i < hwrAssWordsResult.getResultList().size() ;i ++){
                ShowMessage("["+i+"] " + hwrAssWordsResult.getResultList().get(i));
            }
        }

        errorCode = HciCloudHwr.hciHwrSessionStop(assSession);
        ShowMessage("stopAssociateSession return: " + errorCode);
    }

    // 英文联想
    // 与中文联想的区别在与联想字典的不同，以及配置参数只支持capkey
    public static void assoicateWordEn(){
        String word = "do";

        // 同一个Session可以多次调用联想, 本例仅演示单次调用。
        Session assSession = new Session();
        HwrConfig assConfig = new HwrConfig();
        assConfig.addParam(HwrConfig.SessionConfig.PARAM_KEY_CAP_KEY,"hwr.local.associateword");

        // 将在datapath/en 下加载资源
        assConfig.addParam(HwrConfig.SessionConfig.PARAM_KEY_RES_PREFIX, "en/");
        int errorCode = HciCloudHwr.hciHwrSessionStart(assConfig.getStringConfig(), assSession);
        HwrAssociateWordsResult hwrAssWordsResult = new HwrAssociateWordsResult();
        int result = HciCloudHwr.hciHwrAssociateWords(assSession, null, word, hwrAssWordsResult);
        ShowMessage( "hciHwrAssociateWords result = " + result);
        ShowMessage( "["+ word +"] 的联想词：");
        if(hwrAssWordsResult.getResultList() != null && hwrAssWordsResult.getResultList().size() >0){
            for(int i = 0; i < hwrAssWordsResult.getResultList().size() ;i ++){
                ShowMessage("["+i+"] " + hwrAssWordsResult.getResultList().get(i));
            }
        }



        int errCode = HciCloudHwr.hciHwrSessionStop(assSession);
        ShowMessage("stopAssociateSession return: " + errCode);
    }
    
    // 本地实时识别
 	public static void realtimeRecog(String capkey,HwrConfig recogConfig, short[] strokes) {
 	
 		HwrConfig sessionConfig = new HwrConfig();
        sessionConfig.addParam(HwrConfig.SessionConfig.PARAM_KEY_CAP_KEY, capkey);       
        sessionConfig.addParam(HwrConfig.SessionConfig.PARAM_KEY_REALTIME, "yes");
        ShowMessage("HciCloudHwr hciHwrSessionStart config " + sessionConfig.getStringConfig());
 
 		Session nSessionId = new Session();
 		int errCode = -1;
 		// 开始会话
 		errCode = HciCloudHwr.hciHwrSessionStart(sessionConfig.getStringConfig(), nSessionId);
 		if (HciErrorCode.HCI_ERR_NONE != errCode) {
 			System.out.println("HciCloudHwr hciHwrSessionStart return " + errCode);
 			return;
 		}
 		

		for (int nIndex = 0; nIndex < g_nStrokeLen.length; nIndex++) {
			int nCount = g_nStrokeLen[nIndex];
			short sTempShortData[] = new short[nCount];
			System.arraycopy(strokes, 0, sTempShortData, 0, nCount);
			sTempShortData[nCount-2] = -1;
			sTempShortData[nCount-1] = -1;
			
			HwrRecogResult recogResult = new HwrRecogResult();
			// 开始识别
			errCode = HciCloudHwr.hciHwrRecog(nSessionId, sTempShortData,
					 recogConfig.getStringConfig(), recogResult);
			if (HciErrorCode.HCI_ERR_NONE != errCode) {
				System.out.println("HciCloudHwr hciHwrRecog return " + errCode);
				return;
			}
			System.out.println("HciCloudHwr hciHwrRecog Success");
			// 结果输出
			showRecogResultResult(recogResult);
		}
		
		// 停止会话
		errCode = HciCloudHwr.hciHwrSessionStop(nSessionId);
		if (HciErrorCode.HCI_ERR_NONE != errCode) {
				System.out.println("HciCloudHwr hciHwrSessionStop return " + errCode);
				return;
			}
		ShowMessage("HciCloudHwr hciHwrSessionStop Success");
	}

	// 笔形库
    // 输入一组笔迹点，输出对应的笔形图
	public static void penScript(){
        Session penSession = new Session();
        HwrConfig penSessionConfig = new HwrConfig();
        penSessionConfig.addParam(HwrConfig.SessionConfig.PARAM_KEY_CAP_KEY,"hwr.local.penscript");
        int errorCode = HciCloudHwr.hciHwrSessionStart(penSessionConfig.getStringConfig(), penSession);

        // 笔迹参数
        PenScriptConfig config = new PenScriptConfig();
        config.addParam(PenScriptConfig.PARAM_KEY_PEN_COLOR, PenScriptConfig.HCI_HWR_SCRIPT_PEN_COLOR_RAINBOW);
        config.addParam(PenScriptConfig.PARAM_KEY_PEN_MODE,PenScriptConfig.HCI_HWR_SCRIPT_PEN_MODE_BRUSH);

        // sShortData 的坐标范围最大不超过500，故准备一个500X500的图片
        Bitmap bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);

        HwrPenScriptResult result = new HwrPenScriptResult();
        int size = sShortData.length;
        for(int positionIndex = 0; positionIndex < size; positionIndex +=2) {
            short x = sShortData[positionIndex];
            short y = sShortData[positionIndex + 1];
            if( x == -1 && y == -1){
                break;
            }
            errorCode = HciCloudHwr.hciHwrPenScript(penSession, config.getStringConfig(), x, y, result);
            if(result.getPenScriptResultList()!= null){
                for(int i = 0; i < result.getPenScriptResultList().size(); i++){
                    HwrPenScriptResultItem item = result.getPenScriptResultList().get(i);
                    short[] pageImg = item.getPageImg();
                    long colorL = item.getPenColor();
                    colorL = colorL & 0xffffffL;
                    colorL = colorL | 0xff000000L;
                    for (int h = 0; h < item.getHeight(); h++) {
                        for (int w = 0; w < item.getWidth(); w++) {
                            int pos = h * item.getWidth() + w;
                            if (pageImg[pos]== 0) {

                                bitmap.setPixel(w+item.getX(), h + item.getY(), (int) colorL);
                            }
                        }
                    }
                    updateRes(bitmap);
                }

            }
        }

        // 停止会话
        errorCode = HciCloudHwr.hciHwrSessionStop(penSession);
        if (HciErrorCode.HCI_ERR_NONE != errorCode) {
            ShowMessage("HciCloudHwr hciHwrSessionStop return " + errorCode);
            return;
        }


    }

    /**
     * 显示结果集合
     *
     * @param recogResult
     */
    private static void showRecogResultResult(HwrRecogResult recogResult) {
        ShowMessage("\n识别结果:");
        String strResult = "";
        if (recogResult != null) {
            ArrayList<HwrRecogResultItem> recogItemList = recogResult
                    .getResultItemList();
            for (int index = 0; index < recogItemList.size(); index++) {
                String strTmp = recogItemList.get(index).getResult();
                ShowMessage("["+index+"] " + strTmp);
            }
        }
        ShowMessage("\n");

    }
    

}
