package com.example.ysulib.ui.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.UpdateListener;

import com.example.ysulib.CustomApplcation;
import com.example.ysulib.R;
import com.example.ysulib.adapter.UserFriendAdapter;
import com.example.ysulib.bean.User;
import com.example.ysulib.ui.AddFriendActivity;
import com.example.ysulib.ui.FragmentBase;
import com.example.ysulib.ui.MainActivity;
import com.example.ysulib.ui.NewFriendActivity;
import com.example.ysulib.ui.SetMyInfoActivity;
import com.example.ysulib.util.CharacterParser;
import com.example.ysulib.util.CollectionUtils;
import com.example.ysulib.util.PinyinComparator;
import com.example.ysulib.view.MyLetterView;
import com.example.ysulib.view.MyLetterView.OnTouchingLetterChangedListener;
import com.example.ysulib.view.dialog.DialogTips;


	//��ϵ��
public class ContactFragment extends FragmentBase implements OnItemClickListener,OnItemLongClickListener{

	private ImageButton im_button_more;
	private Button bt_add_button;
	EditText mClearEditText;

	TextView dialog;

	ListView list_friends;
	MyLetterView right_letter;

	private UserFriendAdapter userAdapter;// ����

	List<User> friends = new ArrayList<User>();

	private InputMethodManager inputMethodManager;
	
	private CharacterParser characterParser;// ����ת����ƴ������
	
	private PinyinComparator pinyinComparator;//����ƴ��������ListView�����������

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_contacts, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		init();
	}

	private void init() {
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		
		im_button_more = (ImageButton)findViewById(R.id.im_button_more);
		im_button_more.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MainActivity mainActivity = (MainActivity)getActivity();
				mainActivity.showMenu();
			}
		});
		bt_add_button = (Button)findViewById(R.id.btn_add);
		bt_add_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startAnimActivity(AddFriendActivity.class);
			}
		});
		initListView();
		initRightLetterView();
		initEditText();
	}

	private void initEditText() {
		mClearEditText = (EditText)findViewById(R.id.et_msg_search);
		// �������������ֵ�ĸı�����������
		mClearEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,int count) {
				// ������������ֵΪ�գ�����Ϊԭ�����б�����Ϊ���������б�
				filterData(s.toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
				
			}
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
	}

	 //����������е�ֵ���������ݲ�����ListView
	private void filterData(String filterStr) {
		List<User> filterDateList = new ArrayList<User>();
		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = friends;
		} else {
			filterDateList.clear();
			for (User sortModel : friends) {
				String name = sortModel.getUsername();
				if (name != null) {
					if (name.indexOf(filterStr.toString()) != -1|| characterParser.getSelling(name).startsWith(filterStr.toString())) {
						filterDateList.add(sortModel);
					}
				}
			}
		}
		// ����a-z��������
		Collections.sort(filterDateList, pinyinComparator);
		userAdapter.updateListView(filterDateList);
	}

	 //ΪListView�������
	private void filledData(List<BmobChatUser> datas) {
		friends.clear();
		int total = datas.size();
		for (int i = 0; i < total; i++) {
			BmobChatUser user = datas.get(i);
			User sortModel = new User();
			sortModel.setAvatar(user.getAvatar());
			System.out.println("user" + user.getAvatar());
			Log.d("lixing", user.getAvatar() + "");
			sortModel.setNick(user.getNick());
			sortModel.setUsername(user.getUsername());
			sortModel.setObjectId(user.getObjectId());
			sortModel.setContacts(user.getContacts());
			// ����ת����ƴ��
			String username = sortModel.getUsername();
			// ��û��username
			if (username != null) {
				String pinyin = characterParser.getSelling(sortModel.getUsername());
				String sortString = pinyin.substring(0, 1).toUpperCase();
				// ������ʽ���ж�����ĸ�Ƿ���Ӣ����ĸ
				if (sortString.matches("[A-Z]")) {
					sortModel.setSortLetters(sortString.toUpperCase());
				} else {
					sortModel.setSortLetters("#");
				}
			} else {
				sortModel.setSortLetters("#");
			}
			friends.add(sortModel);
		}
		// ����a-z��������
		Collections.sort(friends, pinyinComparator);
	}
		
	ImageView iv_msg_tips;
	TextView tv_new_name;
	LinearLayout layout_new;//������
	
	private void initListView() {
		list_friends= (ListView)findViewById(R.id.list_friends);
		RelativeLayout headView = (RelativeLayout) mInflater.inflate(R.layout.include_new_friend, null);
		iv_msg_tips = (ImageView)headView.findViewById(R.id.iv_msg_tips);
		layout_new =(LinearLayout)headView.findViewById(R.id.layout_new);
		layout_new.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), NewFriendActivity.class);
				intent.putExtra("from", "contact");
				startAnimActivity(intent);
			}
		});
		
		list_friends.addHeaderView(headView);
	    userAdapter = new UserFriendAdapter(getActivity(), friends);
		list_friends.setAdapter(userAdapter);
		list_friends.setOnItemClickListener(this);
		list_friends.setOnItemLongClickListener(this);
		
		list_friends.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// ���������
				if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
					if (getActivity().getCurrentFocus() != null)
						inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
				}
				return false;
			}
		});
		
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (isVisibleToUser) {
			queryMyfriends();
		}
		super.setUserVisibleHint(isVisibleToUser);
	}
	
	private void initRightLetterView() {
		right_letter = (MyLetterView)findViewById(R.id.right_letter);
		dialog = (TextView)findViewById(R.id.dialog);
		right_letter.setTextView(dialog);
		right_letter.setOnTouchingLetterChangedListener(new LetterListViewListener());
	}

	private class LetterListViewListener implements OnTouchingLetterChangedListener {

		@Override
		public void onTouchingLetterChanged(String s) {
			// ����ĸ�״γ��ֵ�λ��
			int position = userAdapter.getPositionForSection(s.charAt(0));
			if (position != -1) {
				list_friends.setSelection(position);
			}
		}
	}

	//��ȡ�����б�
	private void queryMyfriends() {
		//�Ƿ����µĺ�������
		if(BmobDB.create(getActivity()).hasNewInvite()){
			iv_msg_tips.setVisibility(View.VISIBLE);
		}else{
			iv_msg_tips.setVisibility(View.GONE);
		}
		//����������һ�α��صĺ������ݿ�ļ�飬��Ϊ�˱��غ������ݿ����Ѿ�����˶Է������ǽ���ȴû����ʾ����������
		// �����������ڴ��б���ĺ����б�
		CustomApplcation.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(getActivity()).getContactList()));
	
		Map<String,BmobChatUser> users = CustomApplcation.getInstance().getContactList();
		//��װ�µ�User
		filledData(CollectionUtils.map2list(users));
		if(userAdapter==null){
			userAdapter = new UserFriendAdapter(getActivity(), friends);
			list_friends.setAdapter(userAdapter);
		}else{
			userAdapter.notifyDataSetChanged();
		}
	}
	
	private boolean hidden;
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if(!hidden){
			refresh();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(!hidden){
			refresh();
		}
	}
	
	public void refresh(){
		try {
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					queryMyfriends();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		User user = (User) userAdapter.getItem(position-1);
		//�Ƚ�����ѵ���ϸ����ҳ��
		Intent intent =new Intent(getActivity(),SetMyInfoActivity.class);
		intent.putExtra("from", "other");
		intent.putExtra("username", user.getUsername());
		startAnimActivity(intent);
		
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
		User user = (User) userAdapter.getItem(position-1);
		showDeleteDialog(user);
		return true;
	}
	
	public void showDeleteDialog(final User user) {
		DialogTips dialog = new DialogTips(getActivity(),user.getUsername(),"ɾ����ϵ��", "ȷ��",true,true);
		// ���óɹ��¼�
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				deleteContact(user);
			}
		});
		// ��ʾȷ�϶Ի���
		dialog.show();
		dialog = null;
	}
	
	 //ɾ����ϵ��
	private void deleteContact(final User user){
		final ProgressDialog progress = new ProgressDialog(getActivity());
		progress.setMessage("����ɾ��...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		userManager.deleteContact(user.getObjectId(), new UpdateListener() {		
			@Override
			public void onSuccess() {
				ShowToast("ɾ���ɹ�");
				//ɾ���ڴ�
				CustomApplcation.getInstance().getContactList().remove(user.getUsername());
				//���½���
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						progress.dismiss();
						userAdapter.remove(user);
					}
				});
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				ShowToast("ɾ��ʧ�ܣ�"+arg1);
				progress.dismiss();
			}
		});
	}
}
