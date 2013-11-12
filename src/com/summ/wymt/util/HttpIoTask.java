/**
 * 
 */
package com.summ.wymt.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import android.os.AsyncTask;
import android.util.Log;

/**
 * // 增加业务处理 // 远程通讯的task
 * 
 * @author wfeng007
 * @date 2013-11-12 下午9:44:26
 */

public class HttpIoTask<Progress, Result> extends AsyncTask<Object, Progress, Result> {

	private static String TAG = HttpIoTask.class.getName();
	
	private static final int REQUEST_TIMEOUT = 3 * 1000;// 设置请求超时3秒钟
	private static final int SO_TIMEOUT = 5 * 1000; // 设置等待数据超时时间5秒钟

	private HttpClient httpClient;
	private HttpIoListener<Progress, Result> httpIoListener;
	public interface HttpIoListener<Progress, Result>{
		public void onPreExecute();
		public Result onDataLoaded(String data);
		public void onPostExecute(Result result);
		public void onProgressUpdate(Progress... values);
	};
	
	// private Activity activity=null;

	public HttpIoTask(HttpIoListener<Progress, Result> httpIoListener) {
		if (httpIoListener == null)
			throw new NullPointerException("HttpIoListener not be null!!");
		// 创建DefaultHttpClient对象
		// 超时的httpClient
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
		//
		httpClient = new DefaultHttpClient(httpParams);
		// httpClient = new DefaultHttpClient();
		this.httpIoListener = httpIoListener;
	}

	/**
	 * UI执行
	 */
	@Override
	protected void onPreExecute() {
		if(httpIoListener!=null)
			httpIoListener.onPreExecute();
	}
	
	public final AsyncTask<Object, Progress, Result> executeIo(String url,String method) {
		return this.execute((Object)url,(Object)method); //可能会有castexception
	}

	/**
	 * 后台线程执行（池）执行。 读取数据。
	 */
	@Override
	protected Result doInBackground(Object... params) {

		// 可变 处理参数获取
		String url = (String) params[0];
		// String method="GET";
		//

		if (url == null) {
			Log.w(TAG, "url must not be NULL");
			return null;
		}
		// 读取数据
		Log.i(TAG, "to get data with url:" + url);
		HttpGet get = new HttpGet(url);

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
			return null;
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
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				if (httpIoListener != null) {
					return httpIoListener.onDataLoaded(sb.toString());
				}
				// return sb.toString(); //返回结果
				else
					return null;
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
	protected void onPostExecute(Result result) {
		if(httpIoListener!=null)
			httpIoListener.onPostExecute(result);
	}
	
	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
	 */
	@Override
	protected void onProgressUpdate(Progress... values) {
		if(httpIoListener!=null)
			httpIoListener.onProgressUpdate(values);
	}
	
}
