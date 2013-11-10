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
 * ��ʱ�Ĺ����ࡣ
 * 
 * @author wfeng007
 * @date 2013-11-10 ����8:32:34
 */
public class ScreenUtil {

	// private int screenWidth =
	// ContentList.this.getWindowManager().getDefaultDisplay().getWidth();
	// // ��Ļ�����أ��磺480px��
	// private int screenHeight =
	// ContentList.this.getWindowManager().getDefaultDisplay().getHeight();
	// // ��Ļ�ߣ����أ��磺800p��

	DisplayMetrics dm;
	//
	public float density; // ��Ļ�ܶȣ����ر�����0.75/1.0/1.5/2.0��
	public int densityDPI; // ��Ļ�ܶȣ�ÿ�����أ�120/160/240/320��
	public float xdpi;
	public float ydpi;

	public int screenWidthPx; // ��Ļ�����أ��磺480px��
	public int screenHeightPx; // ��Ļ�ߣ����أ��磺800px��
//	public int screenWidth = (int) (dm.widthPixels * density + 0.5f); // ��Ļ��px���磺480px��
//	public int screenHeight = (int) (dm.heightPixels * density + 0.5f); // ��Ļ�ߣ�px���磺800px��
	
	
	public int screenWidthDip ;
	public int screenHeightDip ;
	//
	public ScreenUtil(Context context){
		
		dm = context.getResources().getDisplayMetrics();
		
		density = dm.density; // ��Ļ�ܶ� px��dip�ȣ����ر�����0.75/1.0/1.5/2.0��
		densityDPI = dm.densityDpi; // ��Ļ�ܶȣ�ÿ�����أ�120/160/240/320��
		
		//ÿinch��dip ����
		xdpi = dm.xdpi; // 
		ydpi = dm.ydpi; //
		
		screenWidthPx = dm.widthPixels; // ��Ļ�����أ��磺480px�� dip?��ʵ��dip
		screenHeightPx = dm.heightPixels; // ��Ļ�ߣ����أ��磺800px��dip?
		
		screenWidthDip=(int)(screenWidthPx/density + 0.5f*(screenWidthPx>=0?1:-1));
		screenHeightDip=(int)(screenHeightPx/density + 0.5f*(screenHeightPx>=0?1:-1)); 
		
//		screenWidth = (int) (dm.widthPixels * density + 0.5f); // ��Ļ��px���磺480px��
//		screenHeight = (int) (dm.heightPixels * density + 0.5f); // ��Ļ�ߣ�px���磺800px��
		
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

	public Bitmap drawableToBitmap(Drawable drawable) // drawable ת����bitmap
	{
		int width = drawable.getIntrinsicWidth(); // ȡdrawable�ĳ���
		int height = drawable.getIntrinsicHeight();
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565; // ȡdrawable����ɫ��ʽ
		Bitmap bitmap = Bitmap.createBitmap(width, height, config); // ������Ӧbitmap
		Canvas canvas = new Canvas(bitmap); // ������Ӧbitmap�Ļ���
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas); // ��drawable���ݻ���������
		return bitmap;
	}

	public Drawable zoomDrawable(Drawable drawable, int dstW, int dstH) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap oldbmp = drawableToBitmap(drawable); // drawableת����bitmap
		Matrix matrix = new Matrix(); // ��������ͼƬ�õ�Matrix����
		float scaleWidth = ((float) dstW / width); // �������ű���
		float scaleHeight = ((float) dstH / height);
		matrix.postScale(scaleWidth, scaleHeight); // �������ű���
		Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
				matrix, true); // �����µ�bitmap���������Ƕ�ԭbitmap�����ź��ͼ
		return new BitmapDrawable(newbmp); // ��bitmapת����drawable������
	}

	public int culcDstHight(int srcW, int srcH, int dstW) {
		System.out.println("srcW:"+srcW+"srcH:"+srcH+"dstW:"+dstW);
		int re=(int) (dstW * ((float)srcH / (float)srcW));
		System.out.println("dstH (int) (dstW * (srcH / srcW)) :"+re);
		return re;// dstH
	}

}
