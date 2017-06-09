package com.jwkj.fragment;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import com.jwkj.NVRPlayBackActivity;
import com.jwkj.adapter.NVRRecoderAdapter;
import com.jwkj.data.Contact;
import com.jwkj.data.JAContact;
import com.jwkj.entity.NVRRecodeTime;
import com.jwkj.widget.HeaderView;
import com.jwkj.widget.ProgressTextView;
import com.jwkj.widget.SearchTimeDialog;
import com.jwkj.widget.SearchTimeDialog.TimeSelectDialogClickListner;
import com.nuowei.smarthome.R;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

public class NVRPlayBackFrag extends BaseFragment implements OnClickListener{
	private Context mContext;
	private ListView lvRecode;
	private NVRRecoderAdapter adpter;
	private Button btnMode,btnPlay;
	private List<NVRRecodeTime> recoderList = new ArrayList<NVRRecodeTime>();
	private ImageView ivBack;
	private HeaderView header;
	private TextView txId;
	private Contact nvrContact;
	private JAContact jaContact;
	private TextView txSearch;
	private ProgressTextView btnSearch;
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.fragment_nvrplayback, container, false);
		mContext=getActivity();
		if(savedInstanceState==null){
			nvrContact = (Contact) getArguments().getSerializable("contact");
			jaContact=(JAContact) getArguments().getSerializable("jacontatct");
		}else{
			nvrContact=(Contact) savedInstanceState.getSerializable("contact");
			jaContact= (JAContact) savedInstanceState.getSerializable("jacontatct");
		}
		init(view);
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("contact", nvrContact);
		outState.putSerializable("jacontatct", jaContact);
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}

	private void init(View view) {
		lvRecode = (ListView) view.findViewById(R.id.lv_recoders);
		ivBack=(ImageView) view.findViewById(R.id.back_btn);
		adpter = new NVRRecoderAdapter(recoderList);
		header=(HeaderView) view.findViewById(R.id.hv_nvrplayback);
		txId=(TextView) view.findViewById(R.id.tx_nvrid);
		txSearch=(TextView) view.findViewById(R.id.tv_searchtime);
		btnSearch=(ProgressTextView) view.findViewById(R.id.btn_search);
		initSearchTime();
		
		lvRecode.setAdapter(adpter);
		ivBack.setOnClickListener(this);
		btnSearch.setOnClickListener(this);
		lvRecode.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(listner!=null){
					listner.onPlayBack(recoderList.get(position),position);
				}
				//播放某段视屏
			}
		});
		
		
		upDateHeader(nvrContact);
	}
	
	private void initSearchTime() {
		Calendar calendar = Calendar.getInstance(Locale.getDefault());
		Calendar Zro = new GregorianCalendar(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0,
				0, 0);
		long startTime = Zro.getTimeInMillis() / 1000;
		long endTime = calendar.getTimeInMillis() / 1000;
		
		setSearhText(ProgressTextView.STATE_PROGRESS, NVRPlayBackActivity.initSearchText(startTime, endTime, 0));
	}
	
	/**
	 * 设置NVR信息
	 * @param contatc
	 */
	public void upDateHeader(Contact contatc){
		if(header!=null&&contatc!=null&&txId!=null){
			header.updateImage(contatc.contactId, false, contatc.contactType);
			txId.setText(contatc.contactId);
		}
	}

	

	public List<NVRRecodeTime> getRecoderList() {
		return recoderList;
	}

	public void setRecoderList(List<NVRRecodeTime> recoderList) {
		this.recoderList = recoderList;
		adpter.Notify(recoderList);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			if(listner!=null){
				listner.onBackClick();
			}
			break;
		case R.id.btn_search:
			showTimeSelectDialog();
			break;
		default:
			break;
		}
		
	}
	
	private void showTimeSelectDialog() {
		SearchTimeDialog TimeDialog=new SearchTimeDialog(mContext, jaContact.getChannl());
		TimeDialog.setOnTimeSelectDialogClickListner(TimeListner);
		TimeDialog.TimeSelectShow();
	}
	
	private TimeSelectDialogClickListner TimeListner=new TimeSelectDialogClickListner() {
		
		@Override
		public void searchClick(long start, long end, int channl) {
			listner.onSearchClick(start, end, channl);
		}
	};
	
	private NVRFragmentListner listner;
	public interface NVRFragmentListner{
		void onBackClick();
		void onSearchClick(long start,long end,int channl);
		void onPlayBack(NVRRecodeTime time,int position);
	}
	public void setOnNVRFragmentListner(NVRFragmentListner listner){
		this.listner=listner;
	}
	
	public void setSearhText(int state,int TextR){
		String s=getString(TextR);
		setSearhText(state,s);
	}

	public void setSearhText(int state,String Text){
		if(state!=ProgressTextView.STATE_ERROR){
			txSearch.setText(Text);
		}
		btnSearch.setModeStatde(state,Text);
	}
	
	private String paserTime(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date((time * 1000L)));
	}

}
