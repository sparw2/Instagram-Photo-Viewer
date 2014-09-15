package com.sandeep.instagramviewer;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class PhotosActivity extends Activity {
	
	public static final String CLIENT_ID = "d4aee23d354e4087bb17edd10091f911";
	
	// setup popular url endpoint
	public String popularURL = "https://api.instagram.com/v1/media/popular?client_id="+CLIENT_ID;

	private PullToRefreshListView lvPhotos;
	
	private ArrayList<InstagramPhoto> photos;
	private InstagramPhotosAdapter photosAdapter;
	
	// creating and reusing the client instance
	private final AsyncHttpClient client = new AsyncHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        
        fetchPopularPhotos();
    }

    private void fetchPopularPhotos(){
    	
    	// initialize list
    	photos = new ArrayList<InstagramPhoto>();
    	
    	// create adapter and bind the list
    	photosAdapter = new InstagramPhotosAdapter(this, photos);
    	
    	// populate data in listView
    	lvPhotos = (PullToRefreshListView) findViewById(R.id.lvPhotos);
    
    	// Set a listener to be invoked when the list should be refreshed.
    	lvPhotos.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
            	fetchPhotos();
            }
    	});  
            
    	// setup the custom adapter
    	lvPhotos.setAdapter(photosAdapter);

    	fetchPhotos();
    }

    
    private void fetchPhotos(){
    	//trigger the network request
    	client.get(popularURL, new JsonHttpResponseHandler(){
    		// define success and failure callbacks
    		@Override
    		public void onSuccess(int statusCode, Header[] headers,
    				JSONObject response) {
    			
    			// username, caption, height, url 
    			JSONArray photosJSON = null;
    			try{
    				photosJSON = response.getJSONArray("data");
    				
    				// clear the list
    				photos.clear();
    				
    				for (int i=0; i < photosJSON.length();i++){
    					JSONObject photoJSON = photosJSON.getJSONObject(i);
    					InstagramPhoto photo = new InstagramPhoto();
    					
    					if (!photoJSON.isNull("user")){
    						photo.username=photoJSON.getJSONObject("user").getString("username");
    						photo.userImageUrl=photoJSON.getJSONObject("user").getString("profile_picture");
    					}
    					
    					photo.createdTime = Integer.parseInt(photoJSON.getString("created_time"));
    						
    					if (!photoJSON.isNull("caption"))
    						photo.caption=photoJSON.getJSONObject("caption").getString("text");

    					if (!photoJSON.isNull("images") && !photoJSON.getJSONObject("images").isNull("standard_resolution")){
    						photo.imageUrl=photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
    						photo.imageHeight=photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
    					}	

    					if (!photoJSON.isNull("likes"))
    						photo.likesCount=photoJSON.getJSONObject("likes").getInt("count");
    					
    					// most recent 2 comments are last 2 comments in the list
    					if (!photoJSON.isNull("comments")){
    							JSONArray comments = photoJSON.getJSONObject("comments").getJSONArray("data");
    							photo.commentsCount=photoJSON.getJSONObject("comments").getInt("count");
    							
    							try{
    								JSONObject mostRecentComment = comments.getJSONObject(comments.length()-2);
    								photo.comment1=mostRecentComment.getString("text");
    								photo.userNameComment1= mostRecentComment.getJSONObject("from").getString("username");
    								
    								JSONObject secondMostRecentComment = comments.getJSONObject(comments.length()-1);
            						photo.comment2=secondMostRecentComment.getString("text");
            						photo.userNameComment2= secondMostRecentComment.getJSONObject("from").getString("username");
    								
    							}catch(Exception e){
    								// does not have 2 comments
    								e.printStackTrace();
    							}
    					}	
    					
    					// add photo to the list
    					photos.add(photo);
    				}
    				
    				// notify the adapter
    				photosAdapter.notifyDataSetChanged();
    				
    				
    				//call onRefreshComplete to signify refresh has finished
    				lvPhotos.onRefreshComplete();
    				
    			}catch(JSONException e){
    				e.printStackTrace();
    			}
    		}
    		
    		@Override
    		public void onFailure(int statusCode, Header[] headers,
    				Throwable throwable, JSONObject errorResponse) {
    			Log.e("Instagram API", "Response statuscode :"+statusCode+", response:"+errorResponse.toString());
    			super.onFailure(statusCode, headers, throwable, errorResponse);
    		}
    		
    	});
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
