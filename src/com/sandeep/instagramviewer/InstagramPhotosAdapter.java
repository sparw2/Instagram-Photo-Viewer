package com.sandeep.instagramviewer;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class InstagramPhotosAdapter extends ArrayAdapter<InstagramPhoto> {

	public InstagramPhotosAdapter(Context context, List<InstagramPhoto> photos) {
		super(context,R.layout.item_photo,photos);
	}

	 // View lookup cache
    private static class ViewHolder {
        TextView tvComments;
        TextView tvComment1;
        TextView tvComment2;
        TextView tvCaption;
        TextView tvLikeCount;
        TextView tvUsername;
        TextView tvCreatedTime;
        ImageView imgPhoto;
        ImageView profileImage;
    }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		InstagramPhoto photo = getItem(position);
		
		// view lookup cache stored in tag
		ViewHolder viewHolder = null; 
		
		if (convertView == null){
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent,false);
			viewHolder = new ViewHolder();
			convertView.setTag(viewHolder);
			
			viewHolder.tvCreatedTime = (TextView)convertView.findViewById(R.id.tvCreatedTime);
			viewHolder.tvComments = (TextView)convertView.findViewById(R.id.tvComments);
			viewHolder.tvComment1 = (TextView)convertView.findViewById(R.id.tvComment2);
			viewHolder.tvComment2 = (TextView)convertView.findViewById(R.id.tvComment1);
			viewHolder.tvLikeCount = (TextView)convertView.findViewById(R.id.tvLikeCount);
			viewHolder.tvCaption = (TextView)convertView.findViewById(R.id.tvCaption);
			viewHolder.tvUsername = (TextView)convertView.findViewById(R.id.tvUsername);
			viewHolder.imgPhoto = (ImageView)convertView.findViewById(R.id.imgPhoto);
			viewHolder.profileImage = (ImageView)convertView.findViewById(R.id.profileImage);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		// internationalized e.g. "X likes" 
		viewHolder.tvLikeCount.setText(String.format(getContext().getString(R.string.likecount_caption), photo.likesCount));
		viewHolder.tvLikeCount.setTextColor(convertView.getResources().getColor(R.color.navyblue));
		
		viewHolder.tvUsername.setText(photo.username);
		
		viewHolder.tvCreatedTime.setText(DateUtils.getRelativeTimeSpanString(photo.createdTime*1000,
				System.currentTimeMillis(),DateUtils.SECOND_IN_MILLIS));
		
		if (photo.caption != null && !photo.caption.isEmpty()){
			String captionText = "<b><font color=\"#326072\">"+photo.username+" </b></font>"+photo.caption;
			viewHolder.tvCaption.setText(Html.fromHtml(captionText));
		}
		
		if (photo.commentsCount > 0){
			viewHolder.tvComments.setText(String.format(getContext().getString(R.string.comments_caption), photo.commentsCount));
		}
		
		if (photo.comment1 != null && !photo.comment1.isEmpty()){
			String comment1Text = "<b><font color=\"#326072\">"+photo.userNameComment1+" </b></font>"+photo.comment1;
			viewHolder.tvComment1.setText(Html.fromHtml(comment1Text));
		}	
		
		if (photo.comment2 != null && !photo.comment2.isEmpty()){
			String comment2Text = "<b><font color=\"#326072\">"+photo.userNameComment2+" </b></font>"+photo.comment2;
			viewHolder.tvComment2.setText(Html.fromHtml(comment2Text));
		}
		
		viewHolder.imgPhoto.getLayoutParams().height=photo.imageHeight;

		// reset image from recycled view
		viewHolder.imgPhoto.setImageResource(0);
	
		// load the photos
		Picasso.with(getContext()).load(photo.imageUrl).resize(parent.getWidth(),parent.getHeight()).centerInside().into(viewHolder.imgPhoto);
		
		// set the profile image
		viewHolder.profileImage.setImageResource(0);
		
		Transformation transformation = new RoundedTransformationBuilder()
        	.borderColor(Color.TRANSPARENT)
        	.borderWidthDp(3)
        	.cornerRadiusDp(35)
        	.oval(false).build();
       
		Picasso.with(getContext()).load(photo.userImageUrl).transform(transformation).into(viewHolder.profileImage);
		
		return convertView;
	}

}
