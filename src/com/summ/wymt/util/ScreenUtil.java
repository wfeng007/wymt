/**
 * 
 */
package com.summ.wymt.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

/**
 * 临时的工具类。
 * 
 * @author wfeng007
 * @date 2013-11-10 下午8:32:34
 */
public class ScreenUtil {

	// private int screenWidth =
	// ContentList.this.getWindowManager().getDefaultDisplay().getWidth();
	// // 屏幕宽（像素，如：480px）
	// private int screenHeight =
	// ContentList.this.getWindowManager().getDefaultDisplay().getHeight();
	// // 屏幕高（像素，如：800p）

	DisplayMetrics dm;
	//
	public float density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
	public int densityDPI; // 屏幕密度（每寸像素：120/160/240/320）
	public float xdpi;
	public float ydpi;

	public int screenWidthPx; // 屏幕宽（像素，如：480px）
	public int screenHeightPx; // 屏幕高（像素，如：800px）
//	public int screenWidth = (int) (dm.widthPixels * density + 0.5f); // 屏幕宽（px，如：480px）
//	public int screenHeight = (int) (dm.heightPixels * density + 0.5f); // 屏幕高（px，如：800px）
	
	
	public int screenWidthDip ;
	public int screenHeightDip ;
	//
	public ScreenUtil(Context context){
		
		dm = context.getResources().getDisplayMetrics();
		
		density = dm.density; // 屏幕密度 px与dip比（像素比例：0.75/1.0/1.5/2.0）
		densityDPI = dm.densityDpi; // 屏幕密度（每寸像素：120/160/240/320）
		
		//每inch的dip 轴向
		xdpi = dm.xdpi; // 
		ydpi = dm.ydpi; //
		
		screenWidthPx = dm.widthPixels; // 屏幕宽（像素，如：480px） dip?其实是dip
		screenHeightPx = dm.heightPixels; // 屏幕高（像素，如：800px）dip?
		
		screenWidthDip=(int)(screenWidthPx/density + 0.5f*(screenWidthPx>=0?1:-1));
		screenHeightDip=(int)(screenHeightPx/density + 0.5f*(screenHeightPx>=0?1:-1)); 
		
//		screenWidth = (int) (dm.widthPixels * density + 0.5f); // 屏幕宽（px，如：480px）
//		screenHeight = (int) (dm.heightPixels * density + 0.5f); // 屏幕高（px，如：800px）
		
		System.out.println("screen screenWidthDip:"+screenWidthDip+" screenHeightDip:"+screenHeightDip);
		System.out.println("screen xdpi:"+xdpi+" ydpi:"+ydpi);
		System.out.println("screen w:"+screenWidthPx+" h:"+screenHeightPx);
	}
	
	public Bitmap imageScale(Bitmap bitmap, int dst_w, int dst_h) {
		int src_w = bitmap.getWidth();
		int src_h = bitmap.getHeight();
		float scale_w = ((float) dst_w) / src_w;
		float scale_h = ((float) dst_h) / src_h;
		Matrix matrix = new Matrix();
		matrix.postScale(scale_w, scale_h);
		Bitmap dstbmp = Bitmap.createBitmap(bitmap, 0, 0, src_w, src_h, matrix,
				true);
		return dstbmp;
	}

	public Bitmap drawableToBitmap(Drawable drawable) // drawable 转换成bitmap
	{
		int width = drawable.getIntrinsicWidth(); // 取drawable的长宽
		int height = drawable.getIntrinsicHeight();
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565; // 取drawable的颜色格式
		Bitmap bitmap = Bitmap.createBitmap(width, height, config); // 建立对应bitmap
		Canvas canvas = new Canvas(bitmap); // 建立对应bitmap的画布
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas); // 把drawable内容画到画布中
		return bitmap;
	}

	public Drawable zoomDrawable(Drawable drawable, int dstW, int dstH) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap oldbmp = drawableToBitmap(drawable); // drawable转换成bitmap
		Matrix matrix = new Matrix(); // 创建操作图片用的Matrix对象
		float scaleWidth = ((float) dstW / width); // 计算缩放比例
		float scaleHeight = ((float) dstH / height);
		matrix.postScale(scaleWidth, scaleHeight); // 设置缩放比例
		Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
				matrix, true); // 建立新的bitmap，其内容是对原bitmap的缩放后的图
		return new BitmapDrawable(newbmp); // 把bitmap转换成drawable并返回
	}

	public int culcDstHight(int srcW, int srcH, int dstW) {
		System.out.println("srcW:"+srcW+"srcH:"+srcH+"dstW:"+dstW);
		int re=(int) (dstW * ((float)srcH / (float)srcW));
		System.out.println("dstH (int) (dstW * (srcH / srcW)) :"+re);
		return re;// dstH
	}

}
