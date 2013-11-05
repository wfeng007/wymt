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
 * ͼƬ�������б�
 * @author wfeng007
 * @date 2013-11-5 ����8:12:30
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
		
		
		
		
		//��ȡ��������
		List<Map<String, Object>> list =getListItems();
		
		
		//�������ݵ��б�
		//�õ�view
		ListView listView = (ListView) this.findViewById(R.id.content_list);
        
		//ʹ��simpleAdapter
//		SimpleAdapter sa =new SimpleAdapter(this,list,R.layout.activity_main_list_item,
//	        		new String[]{"simpleName","entryActivityName"},
//	        		new int[]{R.id.mainSimpleName,R.id.mainEntryActivityName});   //
		
		ListViewAdapter lva=new ListViewAdapter(this,list);
		
		//�����¼�������(Ԫ�ص����¼�) //�����������ص�item��
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
//        map.put("imageUri", Uri.parse("http://wyyoutu.b0.upaiyun.com/doudou00.jpg!thum1"));  //ͼƬ��Դ    �޷������ɹ�
//        map.put("imageUri", Uri.parse("http://www.51youtu.com/51youtu/svc/image?itemId=74&tq=75"));  //ͼƬ��Դ   �޷������ɹ�
        
        try { 
//            URL thumb_u = new URL("http://www.51youtu.com/51youtu/svc/image?itemId=79"); 
        	URL thumb_u = new URL("http://wyyoutu.b0.upaiyun.com/doudou00.jpg!thum1"); 
            Drawable thumb_d = Drawable.createFromStream(thumb_u.openStream(), "src");  //�����Ƿ����ڴ�й¶��
//            myImageView.setImageDrawable(thumb_d); 
            map.put("imageDrawable", thumb_d);
        } 
        catch (Exception e) { 
        	Log.w(TAG,e.getMessage() ,e);
        } 
        
        
        map.put("title", "������");              //����   
        map.put("info",  "����һ�Ŷ�������ͼƬ��");     //��Ϣ
//            map.put("detail", goodsDetails[i]); //���鰴ť   
        listItems.add(map);   
        return listItems;   
    }    
	
	
	/**
	 * content_list_text.xml��������
	 * ������
	 * @author wfeng007
	 * @date 2013-11-5 ����8:52:57
	 */
	public static class ListViewAdapter extends BaseAdapter {
		
	    private Context context;                        //����������   
	    private List<Map<String, Object>> listItems;    //������Ϣ����   
	    private LayoutInflater listContainer;           //��ͼ����   
//	    private boolean[] hasChecked;                   //��¼ѡ��״̬   
	    public final class ListItemView{                //�Զ���ؼ�����     
	            public ImageView image;     
	            public TextView title;     
	            public TextView info;   
//	            public CheckBox check;   
//	            public Button detail;          
	    }     
	       
	    
	    public ListViewAdapter(Context context, List<Map<String, Object>> listItems) {   
	        this.context = context;            
	        listContainer = LayoutInflater.from(context);   //������ͼ����������������   
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
	     * ��¼��ѡ���ĸ���  
	     * @param checkedID ѡ�е����  
	     */  
//	    private void checkedChange(int checkedID) {   
//	        hasChecked[checkedID] = !hasChecked[checkedID];   
//	    }   
	       
	    /**  
	     * �жϷ����Ƿ�ѡ��  
	     * @param checkedID ��Ʒ���  
	     * @return �����Ƿ�ѡ��״̬  
	     */  
//	    public boolean hasChecked(int checkedID) {   
//	        return hasChecked[checkedID];   
//	    }   
	       
	    /**  
	     * ��ʾ��������  
	     * @param clickID  
	     */  
	    private void showDetailInfo(int clickID) {   
	        new AlertDialog.Builder(context)   
	        .setTitle("��Ʒ���飺" + listItems.get(clickID).get("info"))   
	        .setMessage(listItems.get(clickID).get("detail").toString())                 
	        .setPositiveButton("ȷ��", null)   
	        .show();   
	    }   
	       
	    /**  
	     * ListView Item����  
	     */  
	    public View getView(int position, View convertView, ViewGroup parent) {   
	        Log.i("method", "getView");   
	        final int selectID = position;
	        
	        //�Զ�����ͼ   
	        ListItemView  listItemView = null;   
	        if (convertView == null) {   
	            listItemView = new ListItemView();    
	            //��ȡlist_item�����ļ�����ͼ   
	            convertView = listContainer.inflate(R.layout.content_list_item, null);   
	            //��ȡ�ؼ�����   
	            listItemView.image = (ImageView)convertView.findViewById(R.id.item_image);   
	            listItemView.title = (TextView)convertView.findViewById(R.id.item_title);   
	            listItemView.info = (TextView)convertView.findViewById(R.id.item_text);   
//	            listItemView.detail= (Button)convertView.findViewById(R.id.detailItem);   
//	            listItemView.check = (CheckBox)convertView.findViewById(R.id.checkItem);
	            
	            //���ÿؼ�����convertView  ??
	            convertView.setTag(listItemView);   
	        }else {   
	            listItemView = (ListItemView)convertView.getTag();   
	        }
	        
	        //Log.e("image", (String) listItems.get(position).get("title"));  //����   
	        //Log.e("image", (String) listItems.get(position).get("info"));   
	           
	        //����ͼƬ   
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
	        
	        //��������
	        listItemView.title.setText((String) listItems.get(position).get("title"));   
	        listItemView.info.setText((String) listItems.get(position).get("info"));   
	        
//	        listItemView.detail.setText("��Ʒ����");   
	        //ע�ᰴť���ʱ�䰮��   
//	        listItemView.detail.setOnClickListener(new View.OnClickListener() {   
//	            @Override  
//	            public void onClick(View v) {   
//	                //��ʾ��Ʒ����   
//	                showDetailInfo(selectID);   
//	            }   
//	        });   
	        // ע���ѡ��״̬�¼�����   
//	        listItemView.check   
//	                .setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {   
//	                    @Override  
//	                    public void onCheckedChanged(CompoundButton buttonView,   
//	                            boolean isChecked) {   
//	                        //��¼��Ʒѡ��״̬   
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
