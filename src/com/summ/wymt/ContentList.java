package com.summ.wymt;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * 图片等内容列表
 * @author wfeng007
 * @date 2013-11-5 下午8:12:30
 */
public class ContentList extends Activity {

	//
	private static String TAG=ContentList.class.getName();
	//
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_content_list);
		
		
		
		
		//读取最新内容
		List<Map<String, Object>> list =getListItems();
		
		
		//设置内容到列表
		//得到view
		ListView listView = (ListView) this.findViewById(R.id.content_list);
        
		//使用simpleAdapter
//		SimpleAdapter sa =new SimpleAdapter(this,list,R.layout.activity_main_list_item,
//	        		new String[]{"simpleName","entryActivityName"},
//	        		new int[]{R.id.mainSimpleName,R.id.mainEntryActivityName});   //
		
		ListViewAdapter lva=new ListViewAdapter(this,list);
		
		//挂上事件监听器(元素单击事件) //将监听器挂载到item上
        listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
			}
		});
		
        //render
        listView.setAdapter(lva);
        
   
	}
	
	private List<Map<String, Object>> getListItems() {   
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();   
        
        Map<String, Object> map = new HashMap<String, Object>();    
//        map.put("imageUri", Uri.parse("http://wyyoutu.b0.upaiyun.com/doudou00.jpg!thum1"));  //图片资源    无法解析成功
//        map.put("imageUri", Uri.parse("http://www.51youtu.com/51youtu/svc/image?itemId=74&tq=75"));  //图片资源   无法解析成功
        
        try { 
//            URL thumb_u = new URL("http://www.51youtu.com/51youtu/svc/image?itemId=79"); 
        	URL thumb_u = new URL("http://wyyoutu.b0.upaiyun.com/doudou00.jpg!thum1"); 
            Drawable thumb_d = Drawable.createFromStream(thumb_u.openStream(), "src");  //这样是否有内存泄露？
//            myImageView.setImageDrawable(thumb_d); 
            map.put("imageDrawable", thumb_d);
        } 
        catch (Exception e) { 
        	Log.w(TAG,e.getMessage() ,e);
        } 
        
        
        map.put("title", "豆豆床");              //标题   
        map.put("info",  "这是一张豆豆床的图片。");     //信息
//            map.put("detail", goodsDetails[i]); //详情按钮   
        listItems.add(map);   
        return listItems;   
    }    
	
	
	/**
	 * content_list_text.xml的适配器
	 * 适配器
	 * @author wfeng007
	 * @date 2013-11-5 下午8:52:57
	 */
	public static class ListViewAdapter extends BaseAdapter {
		
	    private Context context;                        //运行上下文   
	    private List<Map<String, Object>> listItems;    //分项信息集合   
	    private LayoutInflater listContainer;           //视图容器   
//	    private boolean[] hasChecked;                   //记录选中状态   
	    public final class ListItemView{                //自定义控件集合     
	            public ImageView image;     
	            public TextView title;     
	            public TextView info;   
//	            public CheckBox check;   
//	            public Button detail;          
	    }     
	       
	    
	    public ListViewAdapter(Context context, List<Map<String, Object>> listItems) {   
	        this.context = context;            
	        listContainer = LayoutInflater.from(context);   //创建视图容器并设置上下文   
	        this.listItems = listItems;   
//	        hasChecked = new boolean[getCount()];   
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
//	    	 if (position < listItems.size()) {   
//	                return listItems.get(position).getFolerID();   
//	         } else  
//	                return 0;   
	    	return position;
	    }
	    
	       
	    /**  
	     * 记录勾选了哪个项  
	     * @param checkedID 选中的序号  
	     */  
//	    private void checkedChange(int checkedID) {   
//	        hasChecked[checkedID] = !hasChecked[checkedID];   
//	    }   
	       
	    /**  
	     * 判断分项是否选择  
	     * @param checkedID 物品序号  
	     * @return 返回是否选中状态  
	     */  
//	    public boolean hasChecked(int checkedID) {   
//	        return hasChecked[checkedID];   
//	    }   
	       
	    /**  
	     * 显示内容详情  
	     * @param clickID  
	     */  
	    private void showDetailInfo(int clickID) {   
	        new AlertDialog.Builder(context)   
	        .setTitle("物品详情：" + listItems.get(clickID).get("info"))   
	        .setMessage(listItems.get(clickID).get("detail").toString())                 
	        .setPositiveButton("确定", null)   
	        .show();   
	    }   
	       
	    /**  
	     * ListView Item设置  
	     */  
	    public View getView(int position, View convertView, ViewGroup parent) {   
	        Log.i("method", "getView");   
	        final int selectID = position;
	        
	        //自定义视图   
	        ListItemView  listItemView = null;   
	        if (convertView == null) {   
	            listItemView = new ListItemView();    
	            //获取list_item布局文件的视图   
	            convertView = listContainer.inflate(R.layout.content_list_item, null);   
	            //获取控件对象   
	            listItemView.image = (ImageView)convertView.findViewById(R.id.item_image);   
	            listItemView.title = (TextView)convertView.findViewById(R.id.item_title);   
	            listItemView.info = (TextView)convertView.findViewById(R.id.item_text);   
//	            listItemView.detail= (Button)convertView.findViewById(R.id.detailItem);   
//	            listItemView.check = (CheckBox)convertView.findViewById(R.id.checkItem);
	            
	            //设置控件集到convertView  ??
	            convertView.setTag(listItemView);   
	        }else {   
	            listItemView = (ListItemView)convertView.getTag();   
	        }
	        
	        //Log.e("image", (String) listItems.get(position).get("title"));  //测试   
	        //Log.e("image", (String) listItems.get(position).get("info"));   
	           
	        //设置图片   
	        Integer imageResId=(Integer) listItems.get(position).get("imageResId");
	        Drawable imageDrawable=(Drawable) listItems.get(position).get("imageDrawable");
	        Bitmap bitmap=(Bitmap) listItems.get(position).get("imageBitmap");
	        Uri imageUri=(Uri) listItems.get(position).get("imageUri");
	        Log.i("image-uri", "to set image...");   
	        if(imageResId!=null){
		        listItemView.image.setBackgroundResource(imageResId);
	        }else if(bitmap!=null ){
	        	listItemView.image.setImageBitmap(bitmap);
	        }else if(imageDrawable!=null ){
	        	listItemView.image.setImageDrawable(imageDrawable);
	        }else if(imageUri!=null ){
	        	Log.i("image-uri", "uri"+imageUri);   
	        	listItemView.image.setImageURI(imageUri);
	        }
	        
	        //设置文字
	        listItemView.title.setText((String) listItems.get(position).get("title"));   
	        listItemView.info.setText((String) listItems.get(position).get("info"));   
	        
//	        listItemView.detail.setText("商品详情");   
	        //注册按钮点击时间爱你   
//	        listItemView.detail.setOnClickListener(new View.OnClickListener() {   
//	            @Override  
//	            public void onClick(View v) {   
//	                //显示物品详情   
//	                showDetailInfo(selectID);   
//	            }   
//	        });   
	        // 注册多选框状态事件处理   
//	        listItemView.check   
//	                .setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {   
//	                    @Override  
//	                    public void onCheckedChanged(CompoundButton buttonView,   
//	                            boolean isChecked) {   
//	                        //记录物品选中状态   
//	                        checkedChange(selectID);   
//	                    }   
//	        });   
	           
	        return convertView;   
	    }   
	}   
	

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.content_list, menu);
//		return true;
//	}

}
