package com.example.ysulib.ui.fragment;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.UpdateListener;
import com.example.ysulib.R;
import com.example.ysulib.adapter.BookDiscussListAdapter;
import com.example.ysulib.adapter.BookDiscussListAdapter.MyClickListener;
import com.example.ysulib.adapter.NewbookAdapter;
import com.example.ysulib.adapter.NotelistAdapter;
import com.example.ysulib.adapter.SearchBookAdapter;
import com.example.ysulib.bean.BookDiscuss;
import com.example.ysulib.bean.Discuss;
import com.example.ysulib.bean.NewBook;
import com.example.ysulib.bean.NoteList;
import com.example.ysulib.bean.SearchBookBean;
import com.example.ysulib.bean.SearchBookItem;
import com.example.ysulib.bean.Up;
import com.example.ysulib.bean.User;
import com.example.ysulib.config.Config;
import com.example.ysulib.ui.AddBookActivity;
import com.example.ysulib.ui.BookDetailActivity;
import com.example.ysulib.ui.DiscussActivity;
import com.example.ysulib.ui.FragmentBase;
import com.example.ysulib.ui.MainActivity;
import com.example.ysulib.util.NewbookUtil;
import com.example.ysulib.util.SearchBookUtil;
import com.example.ysulib.util.ToplendUtil;
import com.example.ysulib.view.xlist.XListView;
import com.example.ysulib.view.xlist.XListView.IXListViewListener;


public class BookFragment extends FragmentBase implements OnItemClickListener,OnClickListener,OnItemSelectedListener,OnItemLongClickListener{
	
	private int mScreenWidth;
	private RelativeLayout layout_action,progress;
	private LinearLayout layout_all,layout_no,layout_book_search_linearLayout,layout_new_book;
	private TextView tv_book,tv_no,tv_current_page;
	private XListView listview;
	private Button btn_add,layout_book_discuss,layout_book_search,btn_search_ok,layout_btn_hot_book,btn_shangyiye,btn_xiayiye,btn_layout_new_book;
	private Spinner firstSpinner;
	private EditText et_title;
	private RelativeLayout relativelayout;
	PopupWindow morePop;
	
	private BookDiscussListAdapter bookDiscussListAdapter;
	private SearchBookAdapter searchBookAdapter;
	private NotelistAdapter notelistAdapter;
	private NewbookAdapter newbookAdapter;
	
	private List<BookDiscuss> bookDiscussList = new ArrayList<BookDiscuss>();
	private List<SearchBookBean> searchBookBeanList = new ArrayList<SearchBookBean>();
	private List<SearchBookItem> searchBookItemsList = new ArrayList<SearchBookItem>();
	private List<NoteList> noteLists = new ArrayList<NoteList>();
	private List<NewBook> newBooksList = new ArrayList<NewBook>();
	private List<NewBook> newBooksList1 = new ArrayList<NewBook>();
	private boolean ifup=true;
	private ImageButton im_button_more;
	
	private String strSearchType,strText;
	private int limit=5;
	private int loadnum=5;
	private int page = 0;
	private int seachpage=0;
	private int item_click_tag = SHOW_DISCUSS_ITEM_CLICK;
	public static final int SHOW_DISCUSS = 1;
	public static final int SHOW_SEARCH = 2;
	public static final int SHOW_TOPLEND = 3;
	public static final int SHOW_NEWBOOK = 4;
	public static final int SEARCH_ITEM_CLICK = 5;
	public static final int HOT_BOOK_ITEM_CLICK = 6;
	public static final int NEW_BOOK_ITEM_CLICK = 7; 
	public static final int SHOW_DISCUSS_ITEM_CLICK=8;
	public static final int REFESH_DISCUSS=9;
	public static final int UPDATE=10;
	private Handler handler = new Handler(){
		public void handleMessage(Message msg){
			switch (msg.what) {
			case SHOW_DISCUSS:
				if(bookDiscussList != null && bookDiscussList.size() != 0){
					showSuccessView();
					bookDiscussListAdapter = new BookDiscussListAdapter(getActivity(), R.layout.item_book_discuss, bookDiscussList,mListener);
					listview.setAdapter(bookDiscussListAdapter);
				}
				break;
			case SHOW_SEARCH:
				if(searchBookItemsList != null && searchBookItemsList.size() != 0){
					showSuccessView();
					searchBookAdapter = new SearchBookAdapter(getActivity(), R.layout.item_search_book, searchBookItemsList);
					
					listview.setAdapter(searchBookAdapter);		
					listview.setSelection(listview.getCount()-21);
					searchBookAdapter.notifyDataSetChanged();			
				}else {
					showErrorView();
				}
				break;
			case SHOW_NEWBOOK:
				if(newBooksList != null && newBooksList.size() != 0){
					showSuccessView();
					tv_current_page.setText("��" + page + "ҳ");
					newbookAdapter = new NewbookAdapter(getActivity(), R.layout.item_new_book, newBooksList1);
					listview.setAdapter(newbookAdapter);
					listview.setSelection(listview.getCount()-8);
					newbookAdapter.notifyDataSetChanged();
				}else {
					showErrorView();
				}
				break;		
				
			case SHOW_TOPLEND:
				if(noteLists!=null &&noteLists.size() != 0){
					showSuccessView();
					notelistAdapter = new NotelistAdapter(getActivity(), R.layout.item_lend, noteLists);
					listview.setAdapter(notelistAdapter);
				}else {
					showErrorView();
				}
				break;
			case UPDATE:
				updatethis(msg.arg1);
			default:
				break;
			}
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_book, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();				
	}	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) { 
		case Config.REQUESTCODE_ADD:
			if(resultCode == getActivity().RESULT_OK){
				queryBooks(limit);
			}
			break;
		case REFESH_DISCUSS:
			if(resultCode == getActivity().RESULT_OK){
				Log.d("yanzheng", "��֤��Ϣ2");
				queryBooks(limit);
			}
			break;
		default:
			break;
		}
	}

	private void initView(){
		
		DisplayMetrics metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		mScreenWidth = metrics.widthPixels;
		
		firstSpinner = (Spinner)findViewById(R.id.first_spinner);//�����е�spinner
		et_title = (EditText)findViewById(R.id.title_Id);//�����е�EditText
		
		im_button_more = (ImageButton)findViewById(R.id.im_button_more);
		tv_current_page = (TextView)findViewById(R.id.tv_current_page);//��ǰҳ
		layout_new_book = (LinearLayout)findViewById(R.id.layout_new_book);//����ͨ������
		btn_shangyiye = (Button)findViewById(R.id.shangyiye);//��һҳ
		btn_xiayiye = (Button)findViewById(R.id.xiayiye);//��һҳ
		btn_search_ok = (Button)findViewById(R.id.searchok);//����Button
		layout_book_search_linearLayout = (LinearLayout)findViewById(R.id.layout_book_search); //��������
		progress = (RelativeLayout)findViewById(R.id.progress);//���ڻ�ȡ����progress
		layout_no = (LinearLayout)findViewById(R.id.layout_no);//������Ϣ����
		tv_no = (TextView)findViewById(R.id.tv_no);		//������Ϣ
		layout_action = (RelativeLayout)findViewById(R.id.layout_action);//bookFragment�Ķ�������
		layout_all = (LinearLayout)findViewById(R.id.layout_all);	//��������ť����	
		tv_book = (TextView)findViewById(R.id.tv_book);//������ť�����е���ʾ����
		tv_book.setTag("Book");		
		listview = (XListView)findViewById(R.id.book_discuss_list);//listview
		btn_add = (Button)findViewById(R.id.btn_add);//�����Button
		relativelayout=(RelativeLayout)findViewById(R.id.xlistview_footer_content);
		
		btn_shangyiye.setOnClickListener(this);
		btn_xiayiye.setOnClickListener(this);
		layout_all.setOnClickListener(this);
		btn_add.setOnClickListener(this);	
		btn_search_ok.setOnClickListener(this);
		im_button_more.setOnClickListener(this);
		listview.setOnItemClickListener(this);
		listview.setOnItemLongClickListener(this);
		
		//��ʼ��������Ŀ
		String[] firstsp = getResources().getStringArray(R.array.titlespinner);
		ArrayAdapter<String> firstAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,firstsp);
		firstSpinner.setAdapter(firstAdapter);	
		firstSpinner.setOnItemSelectedListener(this);	
		queryBooks(limit);
		initXlistView();
	}
	
	private MyClickListener mListener = new MyClickListener() {
		
		@Override
		public void myOnClick(final int position,  View v) {	
			switch (v.getId()) {
			case R.id.btn_up:			
				String id;
				int nUp;
				String useridd=User.getCurrentUser(getActivity(), User.class).getObjectId();				
				id=bookDiscussListAdapter.getItem(position).getObjectId();
				BmobQuery<Discuss> query = new BmobQuery<Discuss>();
				String sql="select * from Up Where bookid = ? and userid= ?";
				query.doSQLQuery(getActivity(), sql, new SQLQueryListener<Discuss>() {			
					@Override
					public void done(BmobQueryResult<Discuss> arg0, BmobException arg1) {
						if(arg1==null){
							if(arg0.getResults().size()!=0)
							{
								ifup=false;
								ShowToast("�����޹�");
							}else{
							ifup=true;	
							Message message = new Message();
							message.arg1=position;
							message.what = UPDATE;
							handler.sendMessage(message);
							}						
						}
					}
				}, id,useridd);			
				break;
			default:
				break;
			}
		}
		
	};

	@Override	
	public void onClick(View v) {
		if(v == layout_all){//����������
			showListPop();
			
		}else if (v == btn_add) { //�����鼮
			startActivityForResult(new Intent(getActivity(), AddBookActivity.class), Config.REQUESTCODE_ADD);
			
		}else if (v == layout_book_discuss) {//��_����
			changeTextView(v);
			item_click_tag=SHOW_DISCUSS_ITEM_CLICK;
			btn_add.setVisibility(View.VISIBLE);
			morePop.dismiss();
			listview.setOnItemClickListener(this);
			layout_book_search_linearLayout.setVisibility(View.GONE);
			layout_new_book.setVisibility(View.GONE);
			queryBooks(limit);
			
		}else if (v == layout_book_search) {//����	
			layout_book_search_linearLayout.setFocusable(true);
			item_click_tag = SEARCH_ITEM_CLICK;
			listview.setOnItemClickListener(this);
			changeTextView(v);
			listview.setAdapter(null);	
			layout_new_book.setVisibility(View.GONE);
			progress.setVisibility(View.GONE);
			btn_add.setVisibility(View.GONE);
			layout_book_search_linearLayout.setVisibility(View.VISIBLE);
			morePop.dismiss();
			listview.setVisibility(View.GONE);
		}else if (v == layout_btn_hot_book) {//���Ž���
			btn_add.setVisibility(View.GONE);
			layout_new_book.setVisibility(View.GONE);
			progress.setVisibility(View.VISIBLE);
			changeTextView(v);
			item_click_tag = HOT_BOOK_ITEM_CLICK;
			listview.setOnItemClickListener(this);
			layout_book_search_linearLayout.setVisibility(View.GONE);
			morePop.dismiss();
			initTopLend();			
			
		}else if (v == btn_layout_new_book) {//����ͨ��
			page = 0;
			btn_add.setVisibility(View.GONE);
			item_click_tag = NEW_BOOK_ITEM_CLICK;
			listview.setOnItemClickListener(this);
			changeTextView(v);
			layout_book_search_linearLayout.setVisibility(View.GONE);
			morePop.dismiss();
			initNewBook(true);			
			
		}else if (v == btn_search_ok) {//������ť
			progress.setVisibility(View.VISIBLE);
			seachpage=0;
			searchBookItemsList.clear();
			initSearchBook(true);;
		}else if (v == btn_xiayiye) {
			initNewBook(true);
		}else if (v == btn_shangyiye) {
			initNewBook(false);
		}else if (v == im_button_more) {
			MainActivity mainActivity = (MainActivity)getActivity();
			mainActivity.showMenu();
		}
	}
	
	private void showListPop(){
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.pop_book, null);
		//ע��
		btn_layout_new_book = (Button)view.findViewById(R.id.btn_layout_new_book);
		layout_book_discuss = (Button)view.findViewById(R.id.btn_layout_book_discuss);
		layout_book_search = (Button)view.findViewById(R.id.btn_layout_book_search);
		layout_btn_hot_book = (Button)view.findViewById(R.id.btn_hot_book);
		layout_book_discuss.setOnClickListener(this);//�����
		layout_book_search.setOnClickListener(this);//����
		layout_btn_hot_book.setOnClickListener(this);	//��������	
		btn_layout_new_book.setOnClickListener(this);//����ͨ��
		
		morePop = new PopupWindow(view, mScreenWidth, 600);		
		morePop.setTouchInterceptor(new OnTouchListener() {		
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_OUTSIDE){
					morePop.dismiss();
					return true;
				}
				return false;
			}
		});		
		morePop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		morePop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		morePop.setTouchable(true);
		morePop.setFocusable(true);
		morePop.setOutsideTouchable(true);
		morePop.setBackgroundDrawable(new BitmapDrawable());
		//����Ч���Ӷ�������
		morePop.setAnimationStyle(R.style.MenuPop);
		morePop.showAsDropDown(layout_action, 0,-dip2px(getActivity(), 2.0F));		
	}
	
	
	private void initSearchBook(boolean b){

		progress.setVisibility(View.VISIBLE);
		if(b == true){
			seachpage++;
		}else if(b == false){
			if(seachpage == 1){
				seachpage=1;
			}else {
				seachpage--;
			}	
		}
		new Thread(new Runnable() {		
			@Override
			public void run() {			
				
				SearchBookUtil searchBookUtil = new SearchBookUtil(seachpage);
				strText = et_title.getText().toString();
				searchBookUtil.setStrSearchType(strSearchType);
				searchBookUtil.setStrText(strText);
				searchBookBeanList = searchBookUtil.Search();
				for(SearchBookBean searchBookBean : searchBookBeanList){
					searchBookItemsList.add(new SearchBookItem(searchBookBean.getName(), 
							searchBookBean.getAuther() + searchBookBean.getPublish(),
							searchBookBean.getIsbn(), 
							searchBookBean.getSave_num()+searchBookBean.getNow_num(), 
							searchBookBean.getBook_url()));			
				}
				Message message = new Message();
				message.what = SHOW_SEARCH;
				handler.sendMessage(message);
			}
		}).start();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {	
		Intent intent = new Intent(getActivity(), BookDetailActivity.class);
		switch (item_click_tag) {
		case SEARCH_ITEM_CLICK:		
			intent.putExtra("detailUrl", searchBookItemsList.get(position-1).getDetailUrl().toString());		
			startActivity(intent);	
			break;
		case HOT_BOOK_ITEM_CLICK:
			intent.putExtra("detailUrl", noteLists.get(position-1).getBookhref().toString());
			startActivity(intent);
			break;
		case NEW_BOOK_ITEM_CLICK:
			intent.putExtra("detailUrl", newBooksList.get(position-1).getDetailUrl().toString());
			startActivity(intent);
			break;
		case SHOW_DISCUSS_ITEM_CLICK:
			Intent intent2=new Intent(getActivity(),DiscussActivity.class);
			String discussId=bookDiscussListAdapter.getItem(position-1).getObjectId();
			String author=bookDiscussListAdapter.getItem(position-1).getBook_author();
			String describe=bookDiscussListAdapter.getItem(position-1).getBook_describe();
			String title=bookDiscussListAdapter.getItem(position-1).getBook_title();
			Log.d("book", discussId+author+describe+title);
			int up=bookDiscussListAdapter.getItem(position-1).getUp();
			intent2.putExtra("objectId", discussId);
			intent2.putExtra("author", author);
			intent2.putExtra("describe", describe);
			intent2.putExtra("title", title);
			intent2.putExtra("up", up);
			intent2.putExtra("user", bookDiscussListAdapter.getItem(position-1).getBook_user());
			startActivityForResult(intent2, REFESH_DISCUSS);
			break;
		default:
			break;
		}
			
	}
	
	private void showErrorView(){
		progress.setVisibility(View.GONE);
		listview.setVisibility(View.GONE);
		layout_no.setVisibility(View.VISIBLE);
		tv_no.setText("������Ϣ");
	}	
	
	private void showSuccessView(){
		progress.setVisibility(View.GONE);
		listview.setVisibility(View.VISIBLE);
		layout_no.setVisibility(View.GONE);
	}
	
	private void initTopLend(){
		new Thread(new Runnable() {			
			@Override
			public void run() {
				ToplendUtil toplendUtil = new ToplendUtil();
				noteLists = toplendUtil.lendTop();
				Message  message = new Message();
				message.what = SHOW_TOPLEND;
				handler.sendMessage(message);
			}
		}).start();	
	}
	
	private void initNewBook(boolean b){
		//newBooksList.clear();
		progress.setVisibility(View.VISIBLE);
		if(b == true){
			page++;
		}else if(b == false){
			if(page == 1){
				return;
			}
			page--;
		}
		new Thread(new Runnable() {		
			@Override
			public void run() {			
				NewbookUtil newbookUtil = new NewbookUtil(page);
				newBooksList = newbookUtil.show();
				for (int i = 0; i < newBooksList.size(); i++) {
					newBooksList1.add(newBooksList.get(i));
				}
				Message message = new Message();
				message.what = SHOW_NEWBOOK;
				handler.sendMessage(message);
			}
		}).start();
		
	}
	
	private void queryBooks(int limit){		
		bookDiscussList.clear();
		BmobQuery<BookDiscuss> query = new BmobQuery<BookDiscuss>();
		
		query.order("-createdAt");
		query.setLimit(limit);
		query.findObjects(getActivity(), new FindListener<BookDiscuss>() {
			@Override
			public void onError(int arg0, String arg1) {
				
			}
			@Override
			public void onSuccess(List<BookDiscuss> books) {
				if(books == null || books.size() == 0){
					showErrorView();
					bookDiscussListAdapter.notifyDataSetChanged();
					return;
				}
				
				for(BookDiscuss book : books){
					bookDiscussList.add(book);
				}
				progress.setVisibility(View.GONE);
				Message message = new Message();
				message.what = SHOW_DISCUSS;
				handler.sendMessage(message);
			}
		});
		
	}
	

	
	private void changeTextView(View v){
		switch (v.getId()) {
		case R.id.btn_layout_book_discuss:
			tv_book.setText("�麣");
			tv_book.setTag("�麣");
			break;
		case R.id.btn_layout_book_search:
			tv_book.setText("����");
			tv_book.setTag("����");
			break;
		case R.id.btn_hot_book:
			tv_book.setText("���Ž���");
			tv_book.setTag("���Ž���");
			break;
		case R.id.btn_layout_new_book:
			tv_book.setText("����ͨ��");
			tv_book.setTag("����ͨ��");
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
		String temp = parent.getItemAtPosition(position).toString();
		if("����".equals(temp)){
			strSearchType = "title";
		}else if("������".equals(temp)){
			strSearchType = "author";
		}else if("�����".equals(temp)){
			strSearchType = "keyword";
		}else if("ISBN/ISSN".equals(temp)){
			strSearchType = "isbn";
		}else if("������".equals(temp)){
			strSearchType = "asordno";
		}else if("�����".equals(temp)){
			strSearchType = "coden";
		}else if("�����".equals(temp)){
			strSearchType = "callno";
		}else if("������".equals(temp)){
			strSearchType = "publisher";
		}else if("������".equals(temp)){
			strSearchType = "series";
		}else if("����ƴ��".equals(temp)){
			strSearchType = "tpinyin";
		}else if("������ƴ��".equals(temp)){
			strSearchType = "apinyin";
		}
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		strSearchType = "title";
	}
	
	private void updatethis(int position){
		BookDiscuss book=bookDiscussListAdapter.getItem(position);
		String id=bookDiscussListAdapter.getItem(position).getObjectId();
		int nUp=bookDiscussListAdapter.getItem(position).getUp();
		nUp++;
		book.setUp(nUp);				
		book.update(getActivity(), id, new UpdateListener() {					
			@Override
			public void onSuccess() {
				queryBooks(limit);
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				
			}
		});
		Up u=new Up();
		u.setUserid(User.getCurrentUser(getActivity(), User.class).getObjectId());
		u.setBookid(id);
		u.save(getActivity());
	}
	
	private void initXlistView (){
		listview.setPullLoadEnable(true);
		listview.setPullRefreshEnable(true);
		listview.setXListViewListener(new IXListViewListener() {
			
			@Override
			public void onRefresh() {
				handler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						switch (item_click_tag) {
						case SEARCH_ITEM_CLICK:	
							listview.stopRefresh();
							break;
						case HOT_BOOK_ITEM_CLICK:
							listview.stopRefresh();
							break;
						case NEW_BOOK_ITEM_CLICK:
							listview.stopRefresh();
							break;
						case SHOW_DISCUSS_ITEM_CLICK:
							refreshMsg();
							listview.stopRefresh();
							break;
						default:
							break;
						}
							
					}
				}, 1000);
			}
			
			@Override
			public void onLoadMore() {
	            handler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						switch (item_click_tag) {
						case SEARCH_ITEM_CLICK:	
							initSearchBook(true);;
							break;
						case HOT_BOOK_ITEM_CLICK:
							listview.stopLoadMore();
							break;
						case NEW_BOOK_ITEM_CLICK:
							initNewBook(true);
							break;
						case SHOW_DISCUSS_ITEM_CLICK:
							loadMsg();
							break;
						default:
							break;
						}
					}
				}, 1000);
			
			}
		});
	}

	private void refreshMsg(){
		BmobQuery<BookDiscuss> query = new BmobQuery<BookDiscuss>();
		
		query.count(getActivity(), BookDiscuss.class, new CountListener() {
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onSuccess(int arg0) {
					int currents = bookDiscussListAdapter.getCount();
					if (arg0 <= currents) {
						ShowToast("���ݼ�������Ŷ");
					} else {
						queryBooks(limit);
						listview.setSelection(bookDiscussListAdapter.getCount() - currents - 1);
					}
					listview.stopRefresh();				
			}
		});

		}
	private void loadMsg(){
		loadnum+=5;
        BmobQuery<BookDiscuss> query = new BmobQuery<BookDiscuss>();		
		query.count(getActivity(), BookDiscuss.class, new CountListener() {			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onSuccess(int arg0) {
					int currents = bookDiscussListAdapter.getCount();
					if (arg0 <= currents) {
						ShowToast("���ݼ�������Ŷ");
						
					} else {
						queryBooks(loadnum);
						listview.setSelection(bookDiscussListAdapter.getCount() - currents - 1);
						
					}
					listview.stopLoadMore();				
			}
		});
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		// TODO Auto-generated method stub
		final Intent intent = new Intent(getActivity(), AddBookActivity.class);
		switch (item_click_tag) {
		case SEARCH_ITEM_CLICK:
			intent.putExtra("detailUrl", searchBookItemsList.get(position-1).getDetailUrl().toString());
			intent.putExtra("book_name", searchBookItemsList.get(position-1).getTitletextView().toString());
			intent.putExtra("book_auther",searchBookItemsList.get(position-1).getAuthortextView().toString());
			break;
		case HOT_BOOK_ITEM_CLICK:
			intent.putExtra("detailUrl", noteLists.get(position-1).getBookhref().toString());
			intent.putExtra("book_name", noteLists.get(position-1).getTitle().toString());
			intent.putExtra("book_auther",noteLists.get(position-1).getAuthor().toString());
			
			break;
		case NEW_BOOK_ITEM_CLICK:
			intent.putExtra("detailUrl", newBooksList.get(position-1).getDetailUrl().toString());
			intent.putExtra("book_name", newBooksList.get(position-1).getName().toString());
			intent.putExtra("book_auther",newBooksList.get(position-1).getInfo().toString());
			break;
		default:
			return false;
		}
		AlertDialog.Builder normalDia=new AlertDialog.Builder(this.getActivity());  
        normalDia.setIcon(R.drawable.ic_launcher);  
        normalDia.setTitle("����");  
        normalDia.setMessage("�Ƿ����ѡ�е�ͼ��");  
          
        normalDia.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {  
            @Override  
            public void onClick(DialogInterface dialog, int which) {  
                // TODO Auto-generated method stub 
            	startActivity(intent);
            }  
        });  
        normalDia.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {  
            @Override  
            public void onClick(DialogInterface dialog, int which) {  
                // TODO Auto-generated method stub   
            }  
        });  
        normalDia.create().show();  
		
		return true;
	}

}
