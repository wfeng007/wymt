package com.summ.wymt;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.HandlerThread;
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
import android.widget.Toast;

import com.summ.android.util.XListView;
import com.summ.android.util.XListView.IXListViewListener;
import com.summ.wymt.util.AsyncImageLoader;
import com.summ.wymt.util.AsyncImageLoader.ImageCallback;
import com.summ.wymt.util.HttpIoTask;
import com.summ.wymt.util.HttpIoTask.HttpIoListener;
import com.summ.wymt.util.ScreenUtil;

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
	private static String listUrlStr = baseUrlStr+"/item!listItem.act";
	
	private XListView xlistView;
	private int pagenum = 1;

	// ListView listView = null;
	//
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_content_list);

		// 读取最新内容
//		List<Map<String, Object>> list = getListItems();
		// 使用异步方法获取列表信息
		final HttpIoTask loadTask = new HttpIoTask(new GetListData()); // 生成task 这个task
															// 本身即是future又是executor代理，同时是runnable本身还分装状态。
		loadTask.execute(listUrlStr); // 开始执行

		// loadTask.get();//获取返回值会block
		//
		ViewGroup gv = null;
		// 设置内容到列表
		// 得到view
		xlistView = (XListView) this.findViewById(R.id.content_list);

		// 使用simpleAdapter
		// SimpleAdapter sa =new
		// SimpleAdapter(this,list,R.layout.activity_main_list_item,
		// new String[]{"simpleName","entryActivityName"},
		// new int[]{R.id.mainSimpleName,R.id.mainEntryActivityName}); //

//		ListViewAdapter lva = new ListViewAdapter(this, list);

		// 挂上事件监听器(元素单击事件) //将监听器挂载到item上
		xlistView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
			}
		});

		// render
		// listView.setAdapter(lva); //
		
		xlistView.setXListViewListener(new IXListViewListener() {
			
			@Override
			public void onRefresh() {
				pagenum=1;
				final HttpIoTask loadTask = new HttpIoTask(new GetListData()); // 生成task 这个task
				loadTask.execute(listUrlStr); // 开始执行
			}
			
			@Override
			public void onLoadMore() {
				pagenum++;
				Log.i(TAG,"onLoadMore:"+pagenum);
				final HttpIoTask<Object, String> loadTask = new HttpIoTask(new GetListData()); // 生成task 这个task
				loadTask.execute(listUrlStr+"?pageNum="+pagenum); // 开始执行
			}
		});
		xlistView.setPullLoadEnable(true);

	}
	
	/*
	 * 当数据刷新完成  界面状态回归
	 */
	private void onLoad() {
		xlistView.stopRefresh();
		xlistView.stopLoadMore();
		xlistView.setRefreshTime("刚刚");
	}
	
/*	private List<Map<String, Object>> getListItems() {
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
	}*/

	// //获取数据后的回调？
	// protected void paserAndRender(String data){
	//
	// }
	
	/**
	 * 
	 * @author wfeng007
	 * @date 2013-11-12 下午10:50:33
	 */
	public class GetListData implements HttpIoListener<Object, List<Map<String, Object>>>{
		private Toast t=Toast.makeText(ContentList.this, "网络数据读取失败！请稍后再试！", Toast.LENGTH_SHORT);
		/**
		 * 读取数据前可以预设置界面
		 */
		@Override
		public void onPreExecute() {
			Log.i(TAG, "pre execute task!");
		}

		/**
		 * 到读到数据解析数据 
		 * 该方法在后台线程中执行。。不能直接操作界面。
		 */
		@Override
		public List<Map<String, Object>> onDataLoaded(String data) {
			Log.v(TAG, "on data loaded. data:"+data);
			if (data == null)return null;
			JSONObject resultJson;
			try {
				resultJson = new JSONObject(data);
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				JSONArray rowsJson = resultJson.getJSONArray("rows");
				for (int i = 0; i < rowsJson.length(); i++) {
					JSONObject rowJson = rowsJson.getJSONObject(i);
					Log.v(TAG, "row[" + i + "]:" + rowJson);
					Map<String, Object> item = new HashMap<String, Object>();
					item.put("title", rowJson.getString("name"));
					item.put("info", rowJson.getString("text"));
					// item.put("imageDrawable",null);//FIXME 应该提供一个线程专门异步获取图片。
					String imgUrlStr = rowJson.getString("thumbnailUrl");
					item.put("imageUrlStr", (imgUrlStr != null) ? baseUrlStr
							+ "/" + imgUrlStr : null);
					list.add(item);
				}
				return list;
			} catch (JSONException e) {
				Log.w(TAG, e.getMessage(), e);
				return null; //解析失败则不做任何事情。
			}
		}
		
		/**
		 * 解析完数据后将数据布置到界面
		 */
		@Override
		public void onPostExecute(List<Map<String, Object>> list) {
			Log.i(TAG, "get result:"+list);
			if (list == null){
				//TODO 读取数据失败的具体信息可以传递过来的。
				t.show();
				return;
			}
			ListView listView = (ListView) ContentList.this
					.findViewById(R.id.content_list);
			//这里不应重新生成对象。
			//重新
			ListViewAdapter lva = new ListViewAdapter(ContentList.this,list);
			// render
			listView.setAdapter(lva); // render数据到界面。
			onLoad();// 项目相关。。
			//
			// 启动给一个独立线程来load一组图片。
			HandlerThread handlerThread = new HandlerThread(
					"handler_thread");
			handlerThread.start(); // 启动自定义处理线程
		}

		/* (non-Javadoc)
		 * @see com.summ.wymt.util.HttpIoTask.HttpIoListener#onProgressUpdate(Progress[])
		 */
		@Override
		public void onProgressUpdate(Object... values) {
		}
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


}
