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
 * ͼƬ�������б�
 * 
 * @author wfeng007
 * @date 2013-11-5 ����8:12:30
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

		// ��ȡ��������
		List<Map<String, Object>> list = getListItems();
		// ʹ���첽������ȡ�б���Ϣ
		final ListLoadTask loadTask = new ListLoadTask(); // ����task ���task
															// ������future����executor����ͬʱ��runnable������װ״̬��
		loadTask.execute(listUrlStr); // ��ʼִ��

		// loadTask.get();//��ȡ����ֵ��block
		//
		ViewGroup gv = null;
		// �������ݵ��б�
		// �õ�view
		ListView listView = (ListView) this.findViewById(R.id.content_list);

		// ʹ��simpleAdapter
		// SimpleAdapter sa =new
		// SimpleAdapter(this,list,R.layout.activity_main_list_item,
		// new String[]{"simpleName","entryActivityName"},
		// new int[]{R.id.mainSimpleName,R.id.mainEntryActivityName}); //

//		ListViewAdapter lva = new ListViewAdapter(this, list);

		// �����¼�������(Ԫ�ص����¼�) //�����������ص�item��
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
		// //ͼƬ��Դ �޷������ɹ�
		// map.put("imageUri",
		// Uri.parse("http://www.51youtu.com/51youtu/svc/image?itemId=74&tq=75"));
		// //ͼƬ��Դ �޷������ɹ�

		try {
			// URL thumb_u = new
			// URL("http://www.51youtu.com/51youtu/svc/image?itemId=79");
			URL thumb_u = new URL(
					"http://wyyoutu.b0.upaiyun.com/doudou00.jpg!thum1");
			Drawable thumb_d = Drawable.createFromStream(thumb_u.openStream(),
					"src"); // �����Ƿ����ڴ�й¶��
			// myImageView.setImageDrawable(thumb_d);
			map.put("imageDrawable", thumb_d);
		} catch (Exception e) {
			Log.w(TAG, e.getMessage(), e);
		}

		map.put("title", "������"); // ����
		map.put("info", "����һ�Ŷ�������ͼƬ��"); // ��Ϣ
		// map.put("detail", goodsDetails[i]); //���鰴ť
		listItems.add(map);
		return listItems;
	}

	// //��ȡ���ݺ�Ļص���
	// protected void paserAndRender(String data){
	//
	// }

	//
	// ����ҵ����
	// Զ��ͨѶ��task
	public class ListLoadTask extends AsyncTask<Object, Object, String> {

		private static final int REQUEST_TIMEOUT = 5 * 1000;// ��������ʱ10����
		private static final int SO_TIMEOUT = 5 * 1000; // ���õȴ����ݳ�ʱʱ��10����

		private HttpClient httpClient;

		// private Activity activity=null;

		public ListLoadTask() {
			// ����DefaultHttpClient����
			// ��ʱ��httpClient
			BasicHttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,
					REQUEST_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
			//
			httpClient = new DefaultHttpClient(httpParams);
			// httpClient = new DefaultHttpClient();
		}

		/**
		 * UIִ��
		 */
		@Override
		protected void onPreExecute() {
			Log.i(TAG, "pre execute task!");
		}

		/**
		 * ��̨�߳�ִ�У��أ�ִ��
		 */
		@Override
		protected String doInBackground(Object... params) {
			String url = (String) params[0];
			if (url == null) {
				Log.w(TAG, "url must not be NULL");
				return null;
			}
			// ��ȡ����

			HttpGet get = new HttpGet(
			// "http://192.168.2.20:8080/foo/secret.jsp");
			// "http://wyyoutu.b0.upaiyun.com/doudou00.jpg!exif");
					url);

			// ����GET����
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
			// System.out.println("******************��Ӧ״̬��Ϣ********************");
			// System.out.println(httpResponse.getStatusLine());
			// System.out.println("******************��Ӧͷ��Ϣ********************");
			// Header[] headers= httpResponse.getAllHeaders();
			// for (Header header : headers) {
			// System.out.println(header.getName()+" : "+header.getValue());
			// }
			// System.out.println("******************����********************");
			StatusLine sl = httpResponse.getStatusLine();
			int status = sl.getStatusCode();
			if (status < 200 || status >= 300) {
				// ����ʧ��
				Log.w(TAG, "Data get failed!!status:" + status);
				// return null;
			}
			HttpEntity entity = httpResponse.getEntity();
			//
			if (entity != null) {
				// ��ȡ��������Ӧ ������
				BufferedReader br = null;
				try {
					br = new BufferedReader(new InputStreamReader(
							entity.getContent()));
					StringBuilder sb = new StringBuilder();
					String line = null;
					// response.setText("");
					while ((line = br.readLine()) != null) {
						// ʹ��response�ı�����ʾ��������Ӧ
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
		 * �������ݲ�����
		 * 
		 * UIִ�� �������ݼ����ý��档
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
					// item.put("imageDrawable",null);//FIXME Ӧ���ṩһ���߳�ר���첽��ȡͼƬ��

					String imgUrlStr = rowJson.getString("thumbnailUrl");
					item.put("imageUrlStr", (imgUrlStr != null) ? baseUrlStr
							+ "/" + imgUrlStr : null);
					list.add(item);
				}
				ListViewAdapter lva = new ListViewAdapter(ContentList.this,
						list);

				// render
				listView.setAdapter(lva); // render���ݵ����档

				// ������һ�������߳���loadһ��ͼƬ��
				HandlerThread handlerThread = new HandlerThread(
						"handler_thread");
				handlerThread.start(); // �����Զ��崦���߳�

			} catch (JSONException e) {
				Log.w(TAG, "Parser Data failed!!", e);
			}

		}

		// private void renderView(){
		//
		// }

	}

	/**
	 * content_list_text.xml�������� ������
	 * 
	 * @author wfeng007
	 * @date 2013-11-5 ����8:52:57
	 */
	public static class ListViewAdapter extends BaseAdapter {

		private Context context; // ����������
		private List<Map<String, Object>> listItems; // ������Ϣ����
		private LayoutInflater listContainer; // ��ͼ����
		// private boolean[] hasChecked; //��¼ѡ��״̬

		public final class ListItemView { // �Զ���ؼ����� ֻ�ڱ�adapter�����ڲ�ʹ��
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
			listContainer = LayoutInflater.from(context); // ������ͼ����������������
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
		 * ��¼��ѡ���ĸ���
		 * 
		 * @param checkedID
		 *            ѡ�е����
		 */
		// private void checkedChange(int checkedID) {
		// hasChecked[checkedID] = !hasChecked[checkedID];
		// }

		/**
		 * �жϷ����Ƿ�ѡ��
		 * 
		 * @param checkedID
		 *            ��Ʒ���
		 * @return �����Ƿ�ѡ��״̬
		 */
		// public boolean hasChecked(int checkedID) {
		// return hasChecked[checkedID];
		// }

		/**
		 * ��ʾ��������
		 * 
		 * @param clickID
		 */
		private void showDetailInfo(int clickID) {
			new AlertDialog.Builder(context)
					.setTitle("��Ʒ���飺" + listItems.get(clickID).get("info"))
					.setMessage(listItems.get(clickID).get("detail").toString())
					.setPositiveButton("ȷ��", null).show();
		}

		/**
		 * 
		 * ����Ƚ�ǿ��������Ƴ����沢�����ƶ�����������ִ�и÷�����
		 * 
		 * ListView Item����
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.i("method", "getView");
			final int selectID = position;

			// �Զ�����ͼ
			ListItemView listItemView = null;
			if (convertView == null) {
				listItemView = new ListItemView();
				// ��ȡlist_item�����ļ�����ͼ
				convertView = listContainer.inflate(R.layout.content_list_item,
						null);
				// ��ȡ�ؼ�����
				listItemView.image = (ImageView) convertView
						.findViewById(R.id.item_image);
				
				//���ù̶���С��view
				ViewGroup.LayoutParams ps = listItemView.image.getLayoutParams();
//		        ps.height = (int)((new ScreenUtil(context).screenWidth)*0.75);	
				// 
				//ע��LayoutParams�ڴ��б��涼��pixelΪ��λ�ĳߴ硣������dip�������Ҫ���㡣��ǰ��Ҫչʾ����ǿ�match_parent��
				ps.height = (int)(((new ScreenUtil(context).screenWidthPx))*(9f/16f)); //����Ļ�ߴ���ʵӦ�ô�match_parent���ʵ�ʳߴ���㣬��һ��չʾǰ�ǲ�֪�����ġ���ֻ�ý���Ļ��С���㡣
		        listItemView.image.setLayoutParams(ps);
		        listItemView.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
		        
		        //��Щ���������fill_parent������������Ҫ�ȵ�һ��չʾ����ܻ�ȡ��չʾǰһֱδ0�ġ�
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

				// ���ÿؼ�����convertView ??
				convertView.setTag(listItemView);
			} else {
				listItemView = (ListItemView) convertView.getTag();
			}

			// Log.e("image", (String) listItems.get(position).get("title"));
			// //����
			// Log.e("image", (String) listItems.get(position).get("info"));

			//
			// listItemView.imageUrlStr=(String)listItems.get(position).get("imageUrlStr");
			// ����ͼƬ
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

			// �첽����ͼƬ
			String imgUrlStr = (String) listItems.get(position).get(
					"imageUrlStr");
			if (imgUrlStr != null) {
				//
				// ���Դӻ����ȡ�����û�����첽����ͼƬ
				Drawable cachedImage = imageLoader.loadDrawable(imgUrlStr,
						listItemView.image, new ImageCallback() {
							public void imageLoaded(Drawable imageDrawable,
									ImageView imageView, String imageUrl) {
								if (imageDrawable == null || imageView == null)
									return; // �����ȡʧ����ֱ�ӷ��ء�
//								Drawable imgforView = imageDrawable;
								imageView.setImageDrawable(imageDrawable);

							}
						});
				// ��������ֱ������ͼƬ
				if (cachedImage != null) {
					listItemView.image.setImageDrawable(cachedImage);
				}
			}

			// ��������
			listItemView.title.setText((String) listItems.get(position).get(
					"title"));
			listItemView.info.setText((String) listItems.get(position).get(
					"info"));

			// listItemView.detail.setText("��Ʒ����");
			// ע�ᰴť���ʱ�䰮��
			// listItemView.detail.setOnClickListener(new View.OnClickListener()
			// {
			// @Override
			// public void onClick(View v) {
			// //��ʾ��Ʒ����
			// showDetailInfo(selectID);
			// }
			// });
			// ע���ѡ��״̬�¼�����
			// listItemView.check
			// .setOnCheckedChangeListener(new
			// CheckBox.OnCheckedChangeListener() {
			// @Override
			// public void onCheckedChanged(CompoundButton buttonView,
			// boolean isChecked) {
			// //��¼��Ʒѡ��״̬
			// checkedChange(selectID);
			// }
			// });

			return convertView;
		}

		// �첽����ͼƬ���߳�
		private AsyncImageLoader imageLoader;
		// ��ǰ�Ļ���
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
	 * ��������첽����ͼƬ�����ࡣ
	 * 
	 * TODO ֮���ǳ־û��� TODO һ��ͼƬ��һ�������Ҫ�Ż��� һ����ȡͼƬ�̡߳�
	 * 
	 * @author wfeng007
	 * @date 2013-11-10 ����7:22:14
	 */
	public static class AsyncImageLoader {

		// SoftReference�������ã���Ϊ�˸��õ�Ϊ��ϵͳ���ձ���
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
				// �ӻ����л�ȡ
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

			// FIXME ����һ���̶߳�ȡһ��ͼƬ�ķ�ʽ�£�����û����粻��ͻȻ���ߡ��ᵼ��ÿ��ͼƬ��ȡ���ᱨ������ʧ�ܡ�
			// FIXME Ӧ�ÿ��ǳ���������ͨ����ʱֱ��ȡ�����б��η�������Ĳ�����ӦҪ���û����´�����ȡ���ݡ�
			// TODO ����ʹ���̳߳ض�����
			// ������һ���µ��߳�����ͼƬ
			new Thread() {
				@Override
				public void run() {
					Drawable drawable = null;
					try {
						URL thumb_u = new URL(imageUrl);
						Drawable thumb_d = Drawable.createFromStream(
								thumb_u.openStream(), "src"); // FIXME // �����Ƿ����ڴ�й¶��
																
						
						// = ImageUtil.geRoundDrawableFromUrl(imageUrl, 20);
//						Drawable imageDrawable = thumb_d;
//						// ���� Ӧ���ڶ�ȡʱ���С�
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

		// �ص��ӿ�
		public interface ImageCallback {
			public void imageLoaded(Drawable imageDrawable,
					ImageView imageView, String imageUrl);
		}
	}

}
