package com.android.fanxinli;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class ShareManager {

	private static Context mContext;
	private Tencent mTencent;
	private IWXAPI mWeChatApi;
	private WbShareHandler mWbShareHandler;
	
	private BaseUiListener shareQQListener    = new BaseUiListener();
	private BaseUiListener shareQZoneListener = new BaseUiListener();
	private WeiBoShareCallBack shareWbCallback = new WeiBoShareCallBack();
	
	private static String TAG = ShareManager.class.getSimpleName();

	private static class ShareManagerHolder {
		private static final ShareManager INSTANCE = new ShareManager(mContext);
	}

	private ShareManager(Context context) {
		mContext = context;

		if (mTencent == null) {
			mTencent = Tencent.createInstance(mContext.getResources().getString(R.string.qq_app_id), mContext);
		}
	}

	public static ShareManager getInstance(Context context) {
		mContext = context;
		return ShareManagerHolder.INSTANCE;
	}

	/*************************** share method ********************************/

	public void shareToWeiBo(Activity activity, String title, String url, String imageByte) {
		mWbShareHandler = new WbShareHandler(activity);
		mWbShareHandler.registerApp();

		WeiboMultiMessage weiBoMessage = new WeiboMultiMessage();
		weiBoMessage.textObject = getWeiBoText(title, url);

		byte[] bitmapArray = null;
		if(imageByte.contains(",")){
			bitmapArray = Base64.decode(imageByte.split(",")[1], Base64.DEFAULT);
		}else {
			bitmapArray = Base64.decode(imageByte, Base64.DEFAULT);
		}
		//初始化 WXImageObject 和 WXMediaMessage 对象
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.outWidth = 720;
		options.outHeight = 1280;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length, options);
		ImageObject imageObject = new ImageObject();
		imageObject.setImageObject(bitmap);
		mWbShareHandler.shareMessage(weiBoMessage, false);
		shareWbCallback.setActivity(activity);
	}
	
	public void shareToQQ(Activity activity, String title, String url, String imageUrl) {
		if (!mTencent.isQQInstalled(mContext)) {
			Toast.makeText(mContext, R.string.qq_not_install, Toast.LENGTH_SHORT).show();
			return;
		}
		
		final Bundle params = new Bundle();
		params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
		params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
		params.putString(QQShare.SHARE_TO_QQ_SUMMARY, title);
		params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
		params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
		params.putString(QQShare.SHARE_TO_QQ_APP_NAME, activity.getString(R.string.app_name));
		shareQQListener.setActivity(activity);
		mTencent.shareToQQ(activity, params, shareQQListener);
	}

	public void shareToQQZone(Activity activity, String title, String url, ArrayList<String> imageList) {
		if (!mTencent.isQQInstalled(mContext)) {
			Toast.makeText(mContext.getApplicationContext(), R.string.qq_not_install, Toast.LENGTH_SHORT).show();
			return;
		}
		
		final Bundle params = new Bundle();
		params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);//必填
		params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, title);//选填
		params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, url);//必填
		params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
		params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageList);
		shareQZoneListener.setActivity(activity);
		mTencent.shareToQzone(activity, params, shareQZoneListener);
	}

	/**
	 *
	 * @param activity
	 * @param imageByte 要分享图片base64编码格式的字符串
	 * @param flag 0：分享到微信 1：分享到朋友圈
	 */
	public void shareImageToWx(final Activity activity, String imageByte, int flag) {

		if (mWeChatApi == null){
			regToWeChat(activity);
		}

		if (!mWeChatApi.isWXAppInstalled()) {
			Toast.makeText(activity, R.string.wechat_not_install, Toast.LENGTH_SHORT).show();
			mWeChatApi = null;
			return;
		}
		byte[] bitmapArray = null;
		if(imageByte.contains(",")){
			bitmapArray = Base64.decode(imageByte.split(",")[1], Base64.DEFAULT);
		}else {
			bitmapArray = Base64.decode(imageByte, Base64.DEFAULT);
		}
		//初始化 WXImageObject 和 WXMediaMessage 对象
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.outWidth = 720;
		options.outHeight = 1280;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length, options);
		Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, 108, 192, true);
		byte[] thumbDataArray = Utils.bitmapToByteArray(thumbBmp, true);

		WXImageObject imgObj = new WXImageObject(bitmap);
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = imgObj;
		msg.thumbData = thumbDataArray;

		//构造一个Req
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildWeChatTransaction("img");
		req.message = msg;
		if (flag == 0) {
			req.scene = SendMessageToWX.Req.WXSceneSession;
		} else if (flag == 1) {
			req.scene = SendMessageToWX.Req.WXSceneTimeline;
		} else {
			req.scene = SendMessageToWX.Req.WXSceneSession;
		}
		//调用api接口，发送数据到微信
		if (mWeChatApi != null) {
			mWeChatApi.sendReq(req);
			mWeChatApi = null;
		}
	}


	

	
	/*************************** inner class ********************************/
	private class BaseUiListener implements IUiListener {

		private Activity mActivity;

		public void setActivity(Activity shareActivity) {
			mActivity = shareActivity;
		}

		private void closeActivity() {
			if (mActivity != null){
//				if (!(mActivity instanceof DetailActivity)
//                        && !(mActivity instanceof com.teaui.news.shortvideo.DetailActivity))
//					mActivity.finish();

				mActivity = null;
			}
		}

		@Override
		public void onComplete(Object o) {
			Toast.makeText(mContext.getApplicationContext(), R.string.share_success, Toast.LENGTH_SHORT).show();
			closeActivity();
		}

		@Override
		public void onError(UiError uiError) {
			Toast.makeText(mContext.getApplicationContext(), R.string.share_failure, Toast.LENGTH_SHORT).show();
			closeActivity();
		}

		@Override
		public void onCancel() {
			Toast.makeText(mContext.getApplicationContext(), R.string.share_cancel, Toast.LENGTH_SHORT).show();
			closeActivity();
		}
	}

	private class WeiBoShareCallBack implements WbShareCallback {

		private Activity mActivity;

		public void setActivity(Activity activity){
			mActivity = activity;
		}

		private void closeActivity() {
			if (mActivity != null){
//				if (!(mActivity instanceof DetailActivity)
//						&& !(mActivity instanceof DetailActivityForTopic)
//						&& !(mActivity instanceof com.teaui.news.shortvideo.DetailActivity))
//					mActivity.finish();

				mWbShareHandler = null;
				mActivity = null;
			}
		}

		@Override
		public void onWbShareSuccess() {
			Log.i(TAG, "onWbShareSuccess");
			Toast.makeText(mContext.getApplicationContext(), R.string.share_success, Toast.LENGTH_SHORT).show();
			closeActivity();
		}

		@Override
		public void onWbShareCancel() {
			Log.i(TAG, "onWbShareCancel");
			Toast.makeText(mContext.getApplicationContext(), R.string.share_cancel, Toast.LENGTH_SHORT).show();
			closeActivity();
		}

		@Override
		public void onWbShareFail() {
			Log.i(TAG, "onWbShareFail");
			Toast.makeText(mContext.getApplicationContext(), R.string.share_failure, Toast.LENGTH_SHORT).show();
			closeActivity();
		}
	}

	/***********************************************************************/

	public void regToWeChat(Activity activity) {
//		mWeChatApi = WXAPIFactory.createWXAPI(activity, ShareConstant.WECHAT_APP_ID,  true);
		mWeChatApi = WXAPIFactory.createWXAPI(activity, activity.getResources().getString(R.string.weixin_app_id), true);

//		boolean regResult = mWeChatApi.registerApp(ShareConstant.WECHAT_APP_ID);
		boolean regResult = mWeChatApi.registerApp(activity.getResources().getString(R.string.weixin_app_id));

		Log.i(TAG, "regToWeChatResult="+regResult);
	}

	public IUiListener getShareQQListener() {
		return shareQQListener;
	}

	public WeiBoShareCallBack getShareWbCallback(){
		return shareWbCallback;
	}

	public IUiListener getShareQZoneListener() {
		return shareQZoneListener;
	}
	
	public WbShareHandler getWbShareHandler() {
		return mWbShareHandler;
	}

	private TextObject getWeiBoText(String title, String url) {
		TextObject textObject = new TextObject();
		textObject.text = title + url;
		textObject.actionUrl = url;
		return textObject;
	}

//	private ImageObject getWeiBoImage(String imageUrl) {
//		ImageObject imageObject = new ImageObject();
//		imageObject.setImageObject(ImageLoaderManager.getInstance().getLruImageLoader().loadImageSync(imageUrl));
//		return imageObject;
//	}

	private String buildWeChatTransaction(String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
}
