package com.summ.wymt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.summ.wymt.ContentList.AsyncImageLoader.ImageCallback;
import com.summ.wymt.util.ScreenUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 图片等内容列表
 * 
 * @author wfeng007
 * @date 2013-11-5 下午8:12:30
 */
public class ContentList extends Activity {

	//
	private static String TAG = ContentList.class.getName();

	private static String baseUrlStr = "http://www.51youtu.com/51youtu";
	private static String listUrlStr = baseUrlStr+"/item!listItem.act?pageNum=1";

	// ListView listView = null;
	//
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_content_list);

		// 读取最新内容
		List<Map<String, Object>> list = getListItems();
		// 使用异步方法获取列表信息
		final ListLoadTask loadTask = new ListLoadTask(); // 生成task 这个task
															// 本身即是future又是executor代理，同时是runnable本身还分装状态。
		loadTask.execute(listUrlStr); // 开始执行

		// loadTask.get();//获取返回值会block
		//
		ViewGroup gv = null;
		// 设置内容到列表
		// 得到view
		ListView listView = (ListView) this.findViewById(R.id.content_list);

		// 使用simpleAdapter
		// SimpleAdapter sa =new
		// SimpleAdapter(this,list,R.layout.activity_main_list_item,
		// new String[]{"simpleName","entryActivityName"},
		// new int[]{R.id.mainSimpleName,R.id.mainEntryActivityName}); //

//		ListViewAdapter lva = new ListViewAdapter(this, list);

		// 挂上事件监听器(元素单击事件) //将监听器挂载到item上
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
			}
		});

		// render
		// listView.setAdapter(lva); //

	}

	private List<Map<String, Object>> getListItems() {
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		// map.put("imageUri",
		// Uri.parse("http://wyyoutu.b0.upaiyun.com/doudou00.jpg!thum1"));
		// //图片资源 无法解析成功
		// map.put("imageUri",
		// Uri.parse("http://www.51youtu.com/51youtu/svc/image?itemId=74&tq=75"));
		// //图片资源 无法解析成功

		try {
			// URL thumb_u = new
			// URL("http://www.51youtu.com/51youtu/svc/image?itemId=79");
			URL thumb_u = new URL(
					"http://wyyoutu.b0.upaiyun.com/doudou00.jpg!thum1");
			Drawable thumb_d = Drawable.createFromStream(thumb_u.openStream(),
					"src"); // 这样是否有内存泄露？
			// myImageView.setImageDrawable(thumb_d);
			map.put("imageDrawable", thumb_d);
		} catch (Exception e) {
			Log.w(TAG, e.getMessage(), e);
		}

		map.put("title", "豆豆床"); // 标题
		map.put("info", "这是一张豆豆床的图片。"); // 信息
		// map.put("detail", goodsDetails[i]); //详情按钮
		listItems.add(map);
		return listItems;
	}

	// //获取数据后的回调？
	// protected void paserAndRender(String data){
	//
	// }

	//
	// 增加业务处理
	// 远程通讯的task
	public class ListLoadTask extends AsyncTask<Object, Object, String> {

		private static final int REQUEST_TIMEOUT = 5 * 1000;// 设置请求超时10秒钟
		private static final int SO_TIMEOUT = 5 * 1000; // 设置等待数据超时时间10秒钟

		private HttpClient httpClient;

		// private Activity activity=null;

		public ListLoadTask() {
			// 创建DefaultHttpClient对象
			// 超时的httpClient
			BasicHttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,
					REQUEST_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
			//
			httpClient = new DefaultHttpClient(httpParams);
			// httpClient = new DefaultHttpClient();
		}

		/**
		 * UI执行
		 */
		@Override
		protected void onPreExecute() {
			Log.i(TAG, "pre execute task!");
		}

		/**
		 * 后台线程执行（池）执行
		 */
		@Override
		protected String doInBackground(Object... params) {
			String url = (String) params[0];
			if (url == null) {
				Log.w(TAG, "url must not be NULL");
				return null;
			}
			// 读取数据

			HttpGet get = new HttpGet(
			// "http://192.168.2.20:8080/foo/secret.jsp");
			// "http://wyyoutu.b0.upaiyun.com/doudou00.jpg!exif");
					url);

			// 发送GET请求
			HttpResponse httpResponse = null;
			try {
				httpResponse = httpClient.execute(get);
			} catch (ClientProtocolException cpe) {
				Log.w(TAG, cpe.getMessage(), cpe);
				return null;
			} catch (IOException ioe) {
				Log.w(TAG, ioe.getMessage(), ioe);
				return null;
			}
			// System.out.println("******************响应状态信息********************");
			// System.out.println(httpResponse.getStatusLine());
			// System.out.println("******************响应头信息********************");
			// Header[] headers= httpResponse.getAllHeaders();
			// for (Header header : headers) {
			// System.out.println(header.getName()+" : "+header.getValue());
			// }
			// System.out.println("******************内容********************");
			StatusLine sl = httpResponse.getStatusLine();
			int status = sl.getStatusCode();
			if (status < 200 || status >= 300) {
				// 访问失败
				Log.w(TAG, "Data get failed!!status:" + status);
				// return null;
			}
			HttpEntity entity = httpResponse.getEntity();
			//
			if (entity != null) {
				// 读取服务器响应 的流。
				BufferedReader br = null;
				try {
					br = new BufferedReader(new InputStreamReader(
							entity.getContent()));
					StringBuilder sb = new StringBuilder();
					String line = null;
					// response.setText("");
					while ((line = br.readLine()) != null) {
						// 使用response文本框显示服务器响应
						// response.append(line + "\n");
						// System.out.println(line);
						sb.append(line);
					}
					return sb.toString();
				} catch (Exception e) {
					Log.w(TAG, e.getMessage(), e);
					return null;
				} finally {
					try {
						if (br != null)
							br.close();
					} catch (IOException e) {
						Log.e(TAG, "fetal!!", e);
					}
				}
			} else {
				Log.w(TAG, "Data get failed!!Response have no body");
				return null;
			}
		}

		// protected paserData(String data){
		//
		// }

		/**
		 * 
		 * 解析数据并布局
		 * 
		 * UI执行 解析数据及设置界面。
		 */
		@Override
		protected void onPostExecute(String result) {
			if (result == null)
				return;

			ListView listView = (ListView) ContentList.this
					.findViewById(R.id.content_list);

			try {

				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				JSONObject resultJson = new JSONObject(result);
				JSONArray rowsJson = resultJson.getJSONArray("rows");
				for (int i = 0; i < rowsJson.length(); i++) {
					JSONObject rowJson = rowsJson.getJSONObject(i);
					Log.i(TAG, "row[" + i + "]:" + rowJson);

					Map<String, Object> item = new HashMap<String, Object>();
					item.put("title", rowJson.getString("name"));
					item.put("info", rowJson.getString("text"));
					// item.put("imageDrawable",null);//FIXME 应该提供一个线程专门异步获取图片。

					String imgUrlStr = rowJson.getString("thumbnailUrl");
					item.put("imageUrlStr", (imgUrlStr != null) ? baseUrlStr
							+ "/" + imgUrlStr : null);
					list.add(item);
				}
				ListViewAdapter lva = new ListViewAdapter(ContentList.this,
						list);

				// render
				listView.setAdapter(lva); // render数据到界面。

				// 启动给一个独立线程来load一组图片。
				HandlerThread handlerThread = new HandlerThread(
						"handler_thread");
				handlerThread.start(); // 启动自定义处理线程

			} catch (JSONException e) {
				Log.w(TAG, "Parser Data failed!!", e);
			}

		}

		// private void renderView(){
		//
		// }

	}

	/**
	 * content_list_text.xml的适配器 适配器
	 * 
	 * @author wfeng007
	 * @date 2013-11-5 下午8:52:57
	 */
	public static class ListViewAdapter extends BaseAdapter {

		private Context context; // 运行上下文
		private List<Map<String, Object>> listItems; // 分项信息集合
		private LayoutInflater listContainer; // 视图容器
		// private boolean[] hasChecked; //记录选中状态

		public final class ListItemView { // 自定义控件集合 只在本adapter操作内部使用
			public ImageView image;
			public TextView title;
			public TextView info;
			// public String imageUrlStr;
			// public CheckBox check;
			// public Button detail;
		}

		public ListViewAdapter(Context context,
				List<Map<String, Object>> listItems) {
			this.context = context;
			listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
			this.listItems = listItems;
			// hasChecked = new boolean[getCount()];
			imageLoader = new AsyncImageLoader(this.context);
		}

		public int getCount() {
			return listItems.size();
		}

		public Object getItem(int position) {
			if (position < listItems.size()) {
				return listItems.get(position);
			} else
				return null;
		}

		public long getItemId(int position) {
			// if (position < listItems.size()) {
			// return listItems.get(position).getFolerID();
			// } else
			// return 0;
			return position;
		}

		/**
		 * 记录勾选了哪个项
		 * 
		 * @param checkedID
		 *            选中的序号
		 */
		// private void checkedChange(int checkedID) {
		// hasChecked[checkedID] = !hasChecked[checkedID];
		// }

		/**
		 * 判断分项是否选择
		 * 
		 * @param checkedID
		 *            物品序号
		 * @return 返回是否选中状态
		 */
		// public boolean hasChecked(int checkedID) {
		// return hasChecked[checkedID];
		// }

		/**
		 * 显示内容详情
		 * 
		 * @param clickID
		 */
		private void showDetailInfo(int clickID) {
			new AlertDialog.Builder(context)
					.setTitle("物品详情：" + listItems.get(clickID).get("info"))
					.setMessage(listItems.get(clickID).get("detail").toString())
					.setPositiveButton("确定", null).show();
		}

		/**
		 * 
		 * 这里比较强的是如果移除界面并重新移动回来会重新执行该方法。
		 * 
		 * ListView Item设置
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.i("method", "getView");
			final int selectID = position;

			// 自定义视图
			ListItemView listItemView = null;
			if (convertView == null) {
				listItemView = new ListItemView();
				// 获取list_item布局文件的视图
				convertView = listContainer.inflate(R.layout.content_list_item,
						null);
				// 获取控件对象
				listItemView.image = (ImageView) convertView
						.findViewById(R.id.item_image);
				
				//设置固定大小的view
				ViewGroup.LayoutParams ps = listItemView.image.getLayoutParams();
//		        ps.height = (int)((new ScreenUtil(context).screenWidth)*0.75);	
				// 
				//注意LayoutParams内存中保存都是pixel为单位的尺寸。而不是dip。如果需要则换算。当前主要展示风格是宽：match_parent。
				ps.height = (int)(((new ScreenUtil(context).screenWidthPx))*(9f/16f)); //这里幕尺寸其实应该从match_parent后的实际尺寸计算，第一次展示前是不知道多大的。故只用接屏幕大小计算。
		        listItemView.image.setLayoutParams(ps);
		        listItemView.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
		        
		        //这些代码如果是fill_parent这样的设置需要等第一次展示后才能获取，展示前一直未0的。
//		        int mw=listItemView.image.getWidth();
//				System.out.println("mw:"+mw);
		        
		        
				listItemView.title = (TextView) convertView
						.findViewById(R.id.item_title);
				listItemView.info = (TextView) convertView
						.findViewById(R.id.item_text);
				// listItemView.detail=
				// (Button)convertView.findViewById(R.id.detailItem);
				// listItemView.check =
				// (CheckBox)convertView.findViewById(R.id.checkItem);

				// 设置控件集到convertView ??
				convertView.setTag(listItemView);
			} else {
				listItemView = (ListItemView) convertView.getTag();
			}

			// Log.e("image", (String) listItems.get(position).get("title"));
			// //测试
			// Log.e("image", (String) listItems.get(position).get("info"));

			//
			// listItemView.imageUrlStr=(String)listItems.get(position).get("imageUrlStr");
			// 设置图片
			// Integer imageResId=(Integer)
			// listItems.get(position).get("imageResId");
			// Drawable imageDrawable=(Drawable)
			// listItems.get(position).get("imageDrawable");
			// Bitmap bitmap=(Bitmap)
			// listItems.get(position).get("imageBitmap");
			// Uri imageUri=(Uri) listItems.get(position).get("imageUri");
			// Log.i("image-uri", "to set image...");
			// if(imageResId!=null){
			// listItemView.image.setBackgroundResource(imageResId);
			// }else if(bitmap!=null ){
			// listItemView.image.setImageBitmap(bitmap);
			// }else if(imageDrawable!=null ){
			// listItemView.image.setImageDrawable(imageDrawable);
			// }else if(imageUri!=null ){
			// // Log.i("image-uri", "uri"+imageUri);
			// listItemView.image.setImageURI(imageUri);
			// }

			// 异步加载图片
			String imgUrlStr = (String) listItems.get(position).get(
					"imageUrlStr");
			if (imgUrlStr != null) {
				//
				// 尝试从缓存获取，如果没有则异步加载图片
				Drawable cachedImage = imageLoader.loadDrawable(imgUrlStr,
						listItemView.image, new ImageCallback() {
							public void imageLoaded(Drawable imageDrawable,
									ImageView imageView, String imageUrl) {
								if (imageDrawable == null || imageView == null)
									return; // 如果读取失败则直接返回。
//								Drawable imgforView = imageDrawable;
								imageView.setImageDrawable(imageDrawable);

							}
						});
				// 缓存有则直接设置图片
				if (cachedImage != null) {
					listItemView.image.setImageDrawable(cachedImage);
				}
			}

			// 设置文字
			listItemView.title.setText((String) listItems.get(position).get(
					"title"));
			listItemView.info.setText((String) listItems.get(position).get(
					"info"));

			// listItemView.detail.setText("商品详情");
			// 注册按钮点击时间爱你
			// listItemView.detail.setOnClickListener(new View.OnClickListener()
			// {
			// @Override
			// public void onClick(View v) {
			// //显示物品详情
			// showDetailInfo(selectID);
			// }
			// });
			// 注册多选框状态事件处理
			// listItemView.check
			// .setOnCheckedChangeListener(new
			// CheckBox.OnCheckedChangeListener() {
			// @Override
			// public void onCheckedChanged(CompoundButton buttonView,
			// boolean isChecked) {
			// //记录物品选中状态
			// checkedChange(selectID);
			// }
			// });

			return convertView;
		}

		// 异步加载图片的线程
		private AsyncImageLoader imageLoader;
		// 当前的缓存
		private Map<Integer, View> viewMap = new HashMap<Integer, View>();

	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.content_list, menu);
	// return true;
	// }

	/**
	 * 
	 * 带缓存的异步加载图片工具类。
	 * 
	 * TODO 之后考虑持久化。 TODO 一个图片就一个这个需要优化。 一部读取图片线程。
	 * 
	 * @author wfeng007
	 * @date 2013-11-10 下午7:22:14
	 */
	public static class AsyncImageLoader {

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
//						Drawable imageDrawable = thumb_d;
//						// 缩放 应该在读取时进行。
//						ScreenUtil su = new ScreenUtil(context);
//						int width = imageDrawable.getIntrinsicWidth();
//						int height = imageDrawable.getIntrinsicHeight();
//						System.out.println("imageDrawable h:" + height);
//						int dstW = su.screenWidth;
//						int dstH = su.culcDstHight(width, height, dstW);
//						Drawable imgforView = su.zoomDrawable(imageDrawable,
//								dstW, dstH);
						//
						drawable = thumb_d;
//						drawable = imgforView;
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

		// 回调接口
		public interface ImageCallback {
			public void imageLoaded(Drawable imageDrawable,
					ImageView imageView, String imageUrl);
		}
	}

}
