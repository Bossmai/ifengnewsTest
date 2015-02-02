package com.miles.ifeng.test;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jayway.android.robotium.solo.Solo;

@SuppressWarnings("rawtypes")
public class Test1 extends ActivityInstrumentationTestCase2 {

	public Solo solo;
	public Activity activity;
	private static final String RESOURCE_ID_IMAGE_IN_LIST_VIEW = "com.ifeng.news2:id/channel_right_image";
	private static final String RESOURCE_ID_VIEW_HEAD_ICON = "com.ifeng.news2:id/video_head_icon";
	private static final String RESOURCE_ID_OK = "com.ifeng.news2:id/ok";
	private static final Integer VIDEO_LENGTH = 120000; 
	
	@SuppressWarnings("unused")
	private static Class<?> launcherActivityClass;

	@SuppressWarnings("unchecked")
	public Test1() throws ClassNotFoundException {
		super(Class.forName("com.ifeng.news2.activity.SplashActivity"));
	}

	protected static void setUpBeforeClass() throws Exception {
	}

	protected static void tearDownAfterClass() throws Exception {
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.activity = this.getActivity();

		this.solo = new Solo(getInstrumentation(), getActivity());
	}
	
	private void log(String msg){
		Log.i("ifengnews.test", msg);
	}

	public void testMain() {
		try {
			solo.waitForText("头条");
			log("ready for test. Start!");
			if(randomTrue()){
				
				log("do random scrolling");
				solo.scrollToBottom();
				solo.scrollToTop();
			}
			int round = randomIn(4)+1;
			log("test subject in "+ round + " times");
			for(int i=0; i<round; i++){
				_testSubject();	
			}
			assertTrue(true);
		} catch (Exception e) {
			assertTrue(false);
		}
		
	}
	
	private void _testSubject (){
		_switchTitle();
		if(randomTrue()){
			_clickTopImage();
		}
		if(randomTrue()){
			_clickViewInRandom(6);
		}
	}
	
	private boolean randomTrue(){
		return randomIn(5)>1;
	}
	
	private void _switchTitle(){
		
		log("switch title start.");
		int times = randomIn(6);
		int balance = 0;
		for(int i=0;i<times;i++){
			boolean goLeft = !randomTrue() && (balance>0);
			solo.scrollToSide(goLeft?Solo.LEFT:Solo.RIGHT);
			if(goLeft){balance--;}else{
				balance++;
			}
		}
		if(solo.searchText("^立即登录$")){
			log("switch title hit sign-in, going back.");
			solo.goBack();
		}
		_wait();
		log("switch title end.");
		
	}
	
	private void _wait(){
		solo.waitForActivity(solo.getCurrentActivity().toString(), 7000);
	}
	
	@SuppressWarnings("unchecked")
	private List<View> getViews(Class viewClass, String resourceId){
		List<View> result = new ArrayList<View>();
		List<View> views = solo.getCurrentViews(viewClass);
		for(View view : views){
			if(view.getId()<=0){
				continue;
			}
			if(solo.getCurrentActivity().getResources().getResourceName(view.getId()).equals(resourceId) ){
				result.add(view);
			}
		}
		return result;
	}
	
	private void _clickViewInRandom(int times){
		log("random click in list view start");
		
		for(int i=0; i<times; i++){
			_scrollInRandom();
			List<View> target = getViews(ImageView.class, RESOURCE_ID_IMAGE_IN_LIST_VIEW);
			solo.clickOnView(target.get(randomIn(target.size())));
			log("click in list view");
			_wait();
			_safariOnDetailView();
		}
		log("random click in list view end");
	}
	
	private int randomIn(int range){
		return (int)Math.floor(Math.random()*range);
	}
	
	private void _rtnFromDetail(){
		log("going back from detail view");
		solo.goBack();
		_wait();
	}
	
	private void _clickTopImage(){
		log("click on top image start");
		solo.clickOnScreen(200, 200);//use position here since no clear tag to id the item
		_wait();
		_safariOnDetailView();
		log("click on top image end");
	}
	
	private void _safariOnDetailView() {
		List<View> videoIcons = getViews(ImageView.class, RESOURCE_ID_VIEW_HEAD_ICON);
		if(videoIcons.size()>0){
			
			log("has video, try to play");
			
			solo.clickOnView(videoIcons.get(0));
			_wait();
			
			View confirm = solo.getText("确定播放么?");
			if(confirm != null){
				List<View> texts = getViews(TextView.class, RESOURCE_ID_OK);
				if(texts.size()>0){
					solo.clickOnView(texts.get(0));
					log("has video, comfirm to play");
				}
			}
			solo.sleep(VIDEO_LENGTH);
			log("video play stopped");
		}else{
			
			log("no video, simulate scrolling view");
			solo.scrollToBottom();
			solo.scrollToSide(Solo.RIGHT);
			_wait();
		}
		_rtnFromDetail();
	}

	private void _scrollInRandom(){
		log("scroll in random start");
		int times = randomIn(3);
		for(int i=0;i<times;i++){
			if(randomTrue()){
				solo.scrollDown();
			}else{
				solo.scrollUp();
			}
		}
		_wait();
		log("scroll in random end");
	}
	
	protected void tearDown() throws Exception {
		log("execute tearDown");
		this.solo.finishOpenedActivities();
	}

}
