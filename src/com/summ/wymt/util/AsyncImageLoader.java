/**
 * 
 */
package com.summ.wymt.util;

import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;


/**
 * 
 * 带缓存的异步加载图片工具类。
 * 
 * TODO 之后考虑持久化。 TODO 一个图片就一个这个需要优化。 一部读取图片线程。
 * 
 * @author wfeng007
 * @date 2013-11-12 下午8:41:08
 */
public class AsyncImageLoader {

	// SoftReference是软引用，是为了更好的为了系统回收变量
	private static HashMap<String, SoftReference<Drawable>> imageCache;
	private Context context;
	static {
		imageCache = new HashMap<String, SoftReference<Drawable>>();
	}

	public AsyncImageLoader(Context context) {
		this.context = context;
	}

	public Drawable loadDrawable(final String imageUrl,
			final ImageView imageView, final ImageCallback imageCallback) {
		if (imageCache.containsKey(imageUrl)) {
			// 从缓存中获取
			SoftReference<Drawable> softReference = imageCache
					.get(imageUrl);
			Drawable drawable = softReference.get();
			if (drawable != null) {
				return drawable;
			}
		}
		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((Drawable) message.obj,
						imageView, imageUrl);
			}
		};

		// FIXME 这种一个线程读取一个图片的方式下，如果用户网络不好突然断线。会导致每个图片读取都会报告网络失败。
		// FIXME 应该考虑出现网络连通问题时直接取消所有本次访问网络的操作。应要求用户重新触发读取数据。
		// TODO 考虑使用线程池而不是
		// 建立新一个新的线程下载图片
		new Thread() {
			@Override
			public void run() {
				Drawable drawable = null;
				try {
					URL thumb_u = new URL(imageUrl);
					Drawable thumb_d = Drawable.createFromStream(
							thumb_u.openStream(), "src"); // FIXME // 这样是否有内存泄露？
															
					
					// = ImageUtil.geRoundDrawableFromUrl(imageUrl, 20);
//					Drawable imageDrawable = thumb_d;
//					// 缩放 应该在读取时进行。
//					ScreenUtil su = new ScreenUtil(context);
//					int width = imageDrawable.getIntrinsicWidth();
//					int height = imageDrawable.getIntrinsicHeight();
//					System.out.println("imageDrawable h:" + height);
//					int dstW = su.screenWidth;
//					int dstH = su.culcDstHight(width, height, dstW);
//					Drawable imgforView = su.zoomDrawable(imageDrawable,
//							dstW, dstH);
					//
					drawable = thumb_d;
//					drawable = imgforView;
				} catch (Exception e) {
					e.printStackTrace();
				}
				imageCache.put(imageUrl, new SoftReference<Drawable>(
						drawable));
				Message message = handler.obtainMessage(0, drawable);
				handler.sendMessage(message);
			}
		}.start();
		return null;
	}

	//
	// 回调接口
	public static interface ImageCallback {
		public void imageLoaded(Drawable imageDrawable,
				ImageView imageView, String imageUrl);
	}
}
