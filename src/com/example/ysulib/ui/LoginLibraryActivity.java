package com.example.ysulib.ui;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import cn.bmob.v3.listener.UpdateListener;

import com.example.ysulib.R;
import com.example.ysulib.bean.User;


public class LoginLibraryActivity extends ActivityBase implements OnClickListener{
	
	public static final int SHOW_RESPONSE = 0;
	public static final int SHOW_CODE =1;
	private static Bitmap bitmap;
	
	private Button login;
	private EditText checkCode;
	private EditText number;
	private EditText passwd;
	private ImageView image;
	private CheckBox rememberpass;
	private SharedPreferences pref;
	private SharedPreferences.Editor editor;
	String numbertext;
	String passwdtext;
	String checkcode;
	
	Send sends = new Send();
	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_RESPONSE:
				ShowToast("登陆成功");	
				YANZHENG=true;
				YAN=true;
				Intent intent = new Intent(LoginLibraryActivity.this,MainActivity.class);
				/*String s="true";
				intent.putExtra("aaa", s);*/
				startActivity(intent);
				/*setResult(RESULT_OK, intent);*/
				finish();
				break;
			case SHOW_CODE:
				bitmap = (Bitmap)msg.obj;
				image.setImageBitmap(bitmap);
				//实现验证码的自动填写
				String aa=BreakYzM.ocr(bitmap);
				checkCode.setText(aa);
				ShowToast("验证码已自动填写");
			default:
				break;
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_library_login);
		try {
			init();
			sends.getCheckCode();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void init() throws Exception{
		pref=PreferenceManager.getDefaultSharedPreferences(this);
		login = (Button)findViewById(R.id.loginId);
		image = (ImageView)findViewById(R.id.checkCodeImageId);
		number=(EditText)findViewById(R.id.numberId);
		passwd=(EditText)findViewById(R.id.passwdId);
		checkCode = (EditText)findViewById(R.id.captchaId);
		login.setOnClickListener(this);
		rememberpass=(CheckBox)findViewById(R.id.rememberPasswordId);
		boolean isRemember=pref.getBoolean("remember_password", false);
		if(isRemember){
			String accountId=pref.getString("accountId", " ");
			String passwordId=pref.getString("passwordId", " ");
			number.setText(accountId);
			passwd.setText(passwordId);
			rememberpass.setChecked(true);
		}
	}
		
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.loginId:
			String accountId=number.getText().toString();
			String passwordId=passwd.getText().toString();
			editor=pref.edit();
			if(rememberpass.isChecked()){
				editor.putBoolean("remember_password", true);
				editor.putString("accountId", accountId);
				editor.putString("passwordId", passwordId);
			}else{
				editor.clear();
			}
			editor.commit();
			Check();
			final ProgressDialog progress = new ProgressDialog(LoginLibraryActivity.this);
			progress.setMessage("正在登陆...");
			progress.setCanceledOnTouchOutside(false);
			progress.show();
			sends.loginClient();
			break;
		default:
			break;
		}
	}
	
	public void Check(){
		if(TextUtils.isEmpty(number.getText().toString())){
			ShowToast("请输入用户名");
		}else if(TextUtils.isEmpty(passwd.getText().toString())){
			ShowToast("请输入密码");
		}else if(TextUtils.isEmpty(checkCode.getText().toString())){
			ShowToast("请输入验证码");
		}else{
			
		}
	}
		
	
	public class Send{
		
		private void getCheckCode(){
			new Thread(new Runnable() {			
				@Override
				public void run() {
					try {											
						HttpGet httpGet = new HttpGet("http://202.206.242.99/reader/captcha.php");
						HttpResponse checkResponse = BaseActivityInterFace.httpClient.execute(httpGet);
						if(checkResponse.getStatusLine().getStatusCode() == 200){
							HttpEntity entity = checkResponse.getEntity();
							InputStream is = entity.getContent();
							byte[] bytes = new byte[1024];
							ByteArrayOutputStream bos = new ByteArrayOutputStream();
							int count = 0;
							while ((count = is.read(bytes)) != -1){
								bos.write(bytes, 0, count);
							}
							byte[] byteArray = bos.toByteArray();

							Message message = new Message();
							message.what = SHOW_CODE;
							message.obj = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);;
							handler.sendMessage(message);							
						}						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
		
		private void loginClient(){
			new Thread(new Runnable() {				
				@Override
				public void run() {
					try {
						checkCode = (EditText)findViewById(R.id.captchaId);
						number = (EditText)findViewById(R.id.numberId);
						passwd = (EditText)findViewById(R.id.passwdId);
						
						checkcode = checkCode.getText().toString();
						numbertext = number.getText().toString();
						passwdtext = passwd.getText().toString();
						
						HttpPost httpPost = new HttpPost("http://202.206.242.99/reader/redr_verify.php");
						httpPost.setHeader("Connection", "keep-alive");
						httpPost.setHeader("Host", "202.206.242.99");
						httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:40.0) Gecko/20100101 Firefox/40.0");
						httpPost.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
						httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
						httpPost.setHeader("Accept-Encoding", "gzip, deflate");
						httpPost.setHeader("Referer", "http://202.206.242.99/reader/login.php");
						
						Log.d("MainActivity", "CheckCode= " + checkcode);
						
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("number", numbertext));
						params.add(new BasicNameValuePair("passwd", passwdtext));
						params.add(new BasicNameValuePair("captcha", checkcode));
						params.add(new BasicNameValuePair("select", "cert_no"));
						params.add(new BasicNameValuePair("returnUrl", ""));
						
						httpPost.setEntity(new UrlEncodedFormEntity(params,"utf-8"));
						
						HttpResponse httpResponse = BaseActivityInterFace.httpClient.execute(httpPost);
						
						
						if(httpResponse.getStatusLine().getStatusCode() == 200){
							//请求和响应都成功了
							//解析当前用户图书馆借阅图书数量并同步到聊天的借书数量中去
							HttpGet httpGet1 = new HttpGet("http://202.206.242.99/reader/redr_info_rule.php");
							HttpResponse Response1;
							Response1 = BaseActivityInterFace.httpClient.execute(httpGet1);
							HttpEntity entity1 = Response1.getEntity();
							String response1 = EntityUtils.toString(entity1, "utf-8");
							Document doc=Jsoup.parse(response1);
							Element mylib_info=doc.getElementById("mylib_info");
							String lendnum1=mylib_info.getElementsByTag("tr").get(3).getElementsByTag("td").get(2).childNode(1).toString();
							String lendnumb=lendnum1.substring(0, lendnum1.length()-2);
							Log.d("借阅数量", lendnumb);	
							int lendnum=Integer.parseInt(lendnumb);
												
							final User user = userManager.getCurrentUser(User.class);
							user.setLendnum(lendnum);
							user.update(LoginLibraryActivity.this, new UpdateListener() {

								@Override
								public void onSuccess() {
									//ShowToast("修改成功");
								}

								@Override
								public void onFailure(int arg0, String arg1) {
									ShowToast("onFailure:" + arg1);
								}
							});
							
							Log.d("当前借阅量为：",user.getLendnum()+"");
							
							Message message = new Message();
							message.what = SHOW_RESPONSE;
							//将服务器返回的结果存放到Message中
							handler.sendMessage(message);
						}
						
					} catch (Exception e) {
						e.printStackTrace();
						ShowToast("账号和密码不匹配，请重新输入");
						finish();
						Intent intent=new Intent(LoginLibraryActivity.this,LoginLibraryActivity.class);
						startActivity(intent);
					}
				}
			}).start();
		}
	}

}
