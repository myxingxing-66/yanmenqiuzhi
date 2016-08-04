package com.example.ysulib.ui;

import java.util.ArrayList;

import com.example.ysulib.R;
import com.example.ysulib.adapter.ViewPagerAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

@SuppressLint("InflateParams")
public class GuideActivity extends Activity{

	private ViewPager viewpager;
	private ViewPagerAdapter viewPageradapter;
	private ArrayList<View> views;
	private View view1,view2,view3;
	private ImageView point1,point2,point3;
	private Button btn;
	@SuppressWarnings("unused")
	private int currindex=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_gudie);
		initView();
		inintData();
	}
	
	@SuppressLint("InflateParams")
	private void initView(){
		LayoutInflater ml= LayoutInflater.from(this);
		view1=ml.inflate(R.layout.guide01, null);
		view2=ml.inflate(R.layout.guide02, null);
		view3=ml.inflate(R.layout.guide03, null);
		btn=(Button)view3.findViewById(R.id.btn);
		viewpager=(ViewPager)findViewById(R.id.viewpager);
		views = new ArrayList<View>();  
		viewPageradapter=new ViewPagerAdapter(views);
		point1=(ImageView)findViewById(R.id.page0);
		point2=(ImageView)findViewById(R.id.page1);
		point3=(ImageView)findViewById(R.id.page2);
	}
	@SuppressWarnings("deprecation")
	private void inintData(){
		viewpager.setOnPageChangeListener(new MyOnPageChangeListener());
		views.add(view1);
		views.add(view2);
		views.add(view3);
		viewpager.setAdapter(viewPageradapter);

		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();  
				      intent.setClass(GuideActivity.this,SplashActivity.class);  
				      startActivity(intent);  
				      finish();  

				
			}
		});
	}
	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageSelected(int arg0) {
			switch(arg0){
			case 0:
				point1.setImageDrawable(getResources().getDrawable(R.drawable.point_select));
				point2.setImageDrawable(getResources().getDrawable(R.drawable.point_normal));
				break;
			case 1:
				point2.setImageDrawable(getResources().getDrawable(R.drawable.point_select));
				point1.setImageDrawable(getResources().getDrawable(R.drawable.point_normal));
				point3.setImageDrawable(getResources().getDrawable(R.drawable.point_normal));
				break;
			case 2:
				point3.setImageDrawable(getResources().getDrawable(R.drawable.point_select));
				point2.setImageDrawable(getResources().getDrawable(R.drawable.point_normal));
				break;
			}
			currindex = arg0;
			
			
		}
		
	}

}
