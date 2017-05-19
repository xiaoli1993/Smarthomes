package com.lib.showtipview;

import java.lang.reflect.Field;

import com.juan.video.RecordPlayControl.loginRecordListener;
import com.jwkj.utils.TextViewUtils;
import com.jwkj.utils.Utils;
import com.nuowei.ipclibrary.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * @author Frederico Silva (fredericojssilva@gmail.com)
 * @date Oct 31, 2014
 */
public class ShowTipsView extends RelativeLayout {
	public static final int SHOWTYPE_NONE=0;
	public static final int SHOWTYPE_STOKEN=1;
	
	public static final int TIPS_WEAKPASSWORD=0;
	public static final int TIPS_ADDCONTACT=1;
	
	private Point showhintPoints;
	private int radius = 0;

	private String title, description;
	private boolean custom, displayOneTime;
	private int displayOneTimeID = 0;
	private int delay = 0;

	private ShowTipsViewInterface callback;

	private View targetView;
	private int screenX, screenY;

	private int title_color, description_color, background_color, circleColor, buttonColor, buttonTextColor;
	private Drawable closeButtonDrawableBG;

	private int background_alpha = (int) (0.68*255);

	private StoreUtils showTipsStore;

	private Bitmap bitmap;
	private Canvas temp;
	private Paint paint;
	private Paint bitmapPaint;
	private Paint circleline;
	private Paint transparentPaint;
	private PorterDuffXfermode porterDuffXfermode;
	private PathEffect effect;

    private int part;
	private final static int LayoutPadingLeftRigh=50;
	private int top=0;
	private int ShowType=0;
	public boolean isrefreshCommit=true;
	public static final int NO_REFRESH=0;
	public static final int REFRESH=1;
	public int type=NO_REFRESH;
	private int TipsType=0;
	public ShowTipsView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public ShowTipsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ShowTipsView(Context context) {
		super(context);
		init();

	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void init() {
		this.setVisibility(View.GONE);
		this.setBackgroundColor(getResources().getColor(R.color.alpha));
        //top=getWindowTop();
		this.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (getCallback() != null)
					getCallback().gotItClicked();

				setVisibility(View.GONE);
				((ViewGroup) ((Activity) getContext()).getWindow().getDecorView())
						.removeView(ShowTipsView.this);
			}
		});


		showTipsStore = new StoreUtils(getContext());

		paint = new Paint();
		bitmapPaint = new Paint();
		circleline = new Paint();
		transparentPaint = new Paint();
		transparentPaint.setAntiAlias(true);
		porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
			this.setAlpha((float) 0.68);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// Get screen dimensions
		screenX = w;
		screenY = h;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		this.setBackgroundColor(getResources().getColor(R.color.alpha));
		if (bitmap == null) {
			bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
			temp = new Canvas(bitmap);
		}

		if (background_color != 0)
			paint.setColor(background_color);
		else
			paint.setColor(Color.parseColor("#000000"));

		paint.setAlpha(background_alpha);
		temp.drawRect(0, 0, temp.getWidth(), temp.getHeight(), paint);
		canvas.drawBitmap(bitmap, 0, 0, bitmapPaint);

		transparentPaint.setColor(getResources().getColor(R.color.alpha));
		porterDuffXfermode=new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
		transparentPaint.setXfermode(porterDuffXfermode);
		if(showhintPoints==null){
			return;
		}
		int x = showhintPoints.x;
		int y = showhintPoints.y;
		temp.drawCircle(x, y, radius, transparentPaint);
		
		if(ShowType==SHOWTYPE_STOKEN){
			circleline.setStyle(Paint.Style.STROKE);
			circleline.setPathEffect(effect);
			if (circleColor != 0)
				circleline.setColor(circleColor);
			else
				circleline.setColor(Color.parseColor("#01b6ba"));
			circleline.setAntiAlias(true);
			circleline.setStrokeWidth(10);
			canvas.drawCircle(x, y, radius, circleline);	
		}
	}

	boolean isMeasured;

	public void show(final Activity activity) {
		if (isDisplayOneTime() && showTipsStore.hasShown(getDisplayOneTimeID())) {
			setVisibility(View.GONE);
			((ViewGroup) ((Activity) getContext()).getWindow().getDecorView()).removeView(ShowTipsView.this);
			return;
		} else {
			if (isDisplayOneTime())
				showTipsStore.storeShownId(getDisplayOneTimeID());
		}

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				((ViewGroup) activity.getWindow().getDecorView()).addView(ShowTipsView.this);

				ShowTipsView.this.setVisibility(View.VISIBLE);
				Animation fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
				//ShowTipsView.this.startAnimation(fadeInAnimation);

				final ViewTreeObserver observer = targetView.getViewTreeObserver();
				observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						if(TipsType==TIPS_ADDCONTACT){
							if (isMeasured)
								return;

							if (targetView.getHeight() > 0 && targetView.getWidth() > 0) {
								isMeasured = true;
							}
						}else if(TipsType==TIPS_WEAKPASSWORD){
							if (isMeasured)
								return;

							if (targetView.getHeight() > 0 && targetView.getWidth() > 0) {
								isMeasured = true;
							}
						}
						if (custom == false) {
							int[] location = new int[2];
							targetView.getLocationInWindow(location);
							int x = location[0] + targetView.getWidth() / 2;
							int y = location[1] + targetView.getHeight() / 2+top;
							// Log.d("FRED", "X:" + x + " Y: " + y);

							Point p = new Point(x, y);

							showhintPoints = p;
							// 因为弱密码显示图标不规则性，需要把园画大一点
							if(TipsType==TIPS_WEAKPASSWORD){
								radius=Math.min(targetView.getWidth(), targetView.getHeight())/2;
							}else{
								radius=Math.max(targetView.getWidth(), targetView.getHeight())/2;
							}
							part=getGap(radius,9);
							effect = new DashPathEffect(new float[] { part, part}, 1);
						} else {
							int[] location = new int[2];
							targetView.getLocationInWindow(location);
							int x = location[0] + showhintPoints.x;
							int y = location[1] + showhintPoints.y+top;
							// Log.d("FRED", "X:" + x + " Y: " + y);

							Point p = new Point(x, y);

							showhintPoints = p;

						}
						invalidate();
						createViews();
					}
				});
			}
		}, getDelay());
	}

	/*
	 * Create text views and close button
	 */
	private void createViews() {
		if(TipsType==TIPS_ADDCONTACT){
			CreatAddTipsView();
		}else if(TipsType==TIPS_WEAKPASSWORD){
			CreateWeakPassViews();
		}
	}
	
	private void CreatAddTipsView(){
		this.removeAllViews();
		Log.e("dxsTest", "showTips----------------------");
		RelativeLayout texts_layout = new RelativeLayout(getContext());
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		ImageView iv=new ImageView(getContext());

		iv.setId(456);
		iv.setImageResource(R.drawable.guide_row);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params.rightMargin=screenX-showhintPoints.x+radius-LayoutPadingLeftRigh;
		iv.setLayoutParams(params);
		texts_layout.addView(iv);
		/*
		 * Title
		 */
		StrokeTextView textTitle = new StrokeTextView(getContext());
		textTitle.setText(getTitle());
		if (getTitle_color() != 0)
			textTitle.setTextColor(getTitle_color());
		else
			textTitle.setTextColor(getResources().getColor(android.R.color.holo_blue_bright));
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.BELOW, 456);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params.topMargin=Utils.dip2px(getContext(), 14);
		textTitle.setId(123);
		textTitle.setTextSize(18);
		textTitle.setTypeface(TextViewUtils.setTypeFont(textTitle,"monitor.ttf"));
		textTitle.setLayoutParams(params);
		texts_layout.addView(textTitle);


		/*
		 * Description
		 */
		StrokeTextView text = new StrokeTextView(getContext());

		text.setText(getDescription());
		if (getDescription_color() != 0)
			text.setTextColor(getDescription_color());
		else
			text.setTextColor(Color.WHITE);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.BELOW, 123);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		text.setLayoutParams(params);
		texts_layout.addView(text);
		text.setTextSize(18);
		text.setTypeface(TextViewUtils.setTypeFont(text,"monitor.ttf"));

		params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		LayoutParams paramsTexts = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		if (screenY / 2 > showhintPoints.y) {
			// textBlock under the highlight circle
			paramsTexts.height = (showhintPoints.y + radius) - screenY;
			paramsTexts.topMargin = (showhintPoints.y + radius);
			texts_layout.setGravity(Gravity.START | Gravity.TOP);
			texts_layout.setPadding(LayoutPadingLeftRigh, 0, LayoutPadingLeftRigh, 50);
		} else {
			// textBlock above the highlight circle
			paramsTexts.height = showhintPoints.y - radius;
			texts_layout.setGravity(Gravity.START | Gravity.BOTTOM);
			texts_layout.setPadding(LayoutPadingLeftRigh, 100, LayoutPadingLeftRigh, 50);
		}

		texts_layout.setLayoutParams(paramsTexts);
		this.addView(texts_layout);

	}
	
	private void CreateWeakPassViews(){
		this.removeAllViews();
		Log.e("dxsTest", "showTips---CreateWeakPassViews");
		RelativeLayout texts_layout = new RelativeLayout(getContext());
		LayoutParams  params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

			ImageView iv=new ImageView(getContext());
			iv.setId(456);
			iv.setImageResource(R.drawable.arrow_guide_green);
			params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			params.leftMargin=showhintPoints.x-LayoutPadingLeftRigh-2*radius;
			iv.setLayoutParams(params);
			texts_layout.addView(iv);
			
			/*
			 * Title
			 */
			StrokeTextView textTitle = new StrokeTextView(getContext());
			textTitle.setText(getTitle());
			if (getTitle_color() != 0)
				textTitle.setTextColor(getTitle_color());
			else
				textTitle.setTextColor(getResources().getColor(android.R.color.holo_blue_bright));
			params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ABOVE, 456);
//			params.addRule(RelativeLayout.ALIGN_LEFT,456);
			params.leftMargin=showhintPoints.x-LayoutPadingLeftRigh-2*radius-LayoutPadingLeftRigh;
//			params.topMargin=Utils.dip2px(getContext(), 14);
//			params.leftMargin=Utils.dip2px(getContext(), 14);
			textTitle.setId(123);
			textTitle.setTextSize(18);
			textTitle.setTypeface(TextViewUtils.setTypeFont(textTitle,"monitor.ttf"));
			textTitle.setLayoutParams(params);
			texts_layout.addView(textTitle);
		
		/*
		 * Description
		 */
		StrokeTextView text = new StrokeTextView(getContext());

		text.setText(getDescription());
		if (getDescription_color() != 0)
			text.setTextColor(getDescription_color());
		else
			text.setTextColor(Color.WHITE);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.BELOW, 123);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		text.setLayoutParams(params);
		texts_layout.addView(text);
		text.setTextSize(18);
		text.setTypeface(TextViewUtils.setTypeFont(text,"monitor.ttf"));

		params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		LayoutParams paramsTexts = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

//		if (screenY / 2 > showhintPoints.y) {
			// textBlock under the highlight circle
//			paramsTexts.height = (showhintPoints.y + radius) - screenY;
//			paramsTexts.topMargin = (showhintPoints.y + radius);
//			texts_layout.setGravity(Gravity.START | Gravity.TOP);
//			texts_layout.setPadding(LayoutPadingLeftRigh, 0, LayoutPadingLeftRigh, 50);
//		} else {
			// textBlock above the highlight circle
			paramsTexts.height = showhintPoints.y - radius;
			texts_layout.setGravity(Gravity.START | Gravity.BOTTOM);
			texts_layout.setPadding(LayoutPadingLeftRigh, 100, LayoutPadingLeftRigh, 50);
//		}

		texts_layout.setLayoutParams(paramsTexts);
		this.addView(texts_layout);
	}
	
	private final static float PI=3.1415926f;
	public int getGap(int Radius,int part){
		return (int) (2*PI*Radius/part/2);
	};

	public void setTarget(View v) {
		targetView = v;
	}

	public void setTarget(View v, int x, int y, int radius) {
		custom = true;
		targetView = v;
		Point p = new Point(x, y);
		showhintPoints = p;
		this.radius = radius;
	}

	static Point getShowcasePointFromView(View view) {
		Point result = new Point();
		result.x = view.getLeft() + view.getWidth() / 2;
		result.y = view.getTop() + view.getHeight() / 2;
		return result;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isDisplayOneTime() {
		return displayOneTime;
	}

	public void setDisplayOneTime(boolean displayOneTime) {
		this.displayOneTime = displayOneTime;
	}

	public ShowTipsViewInterface getCallback() {
		return callback;
	}

	public void setCallback(ShowTipsViewInterface callback) {
		this.callback = callback;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public int getDisplayOneTimeID() {
		return displayOneTimeID;
	}

	public void setDisplayOneTimeID(int displayOneTimeID) {
		this.displayOneTimeID = displayOneTimeID;
	}

	public int getTitle_color() {
		return title_color;
	}

	public void setTitle_color(int title_color) {
		this.title_color = title_color;
	}

	public int getDescription_color() {
		return description_color;
	}

	public void setDescription_color(int description_color) {
		this.description_color = description_color;
	}

	public int getBackground_color() {
		return background_color;
	}

	public void setBackground_color(int background_color) {
		this.background_color = background_color;
	}

	public int getCircleColor() {
		return circleColor;
	}

	public void setCircleColor(int circleColor) {
		this.circleColor = circleColor;
	}

	public int getBackground_alpha() {
		return background_alpha;
	}

	public void setBackground_alpha(int background_alpha) {
		if(background_alpha>255)
			this.background_alpha = 255;
		else if(background_alpha<0)
			this.background_alpha = 0;
		else
			this.background_alpha = background_alpha;

	}

	public int getButtonColor() {
		return buttonColor;
	}

	public void setButtonColor(int buttonColor) {
		this.buttonColor = buttonColor;
	}

	public int getButtonTextColor() {
		return buttonTextColor;
	}

	public void setButtonTextColor(int buttonTextColor) {
		this.buttonTextColor = buttonTextColor;
	}

	public Drawable getCloseButtonDrawableBG() {
		return closeButtonDrawableBG;
	}

	public void setCloseButtonDrawableBG(Drawable closeButtonDrawableBG) {
		this.closeButtonDrawableBG = closeButtonDrawableBG;
	}
	
	public int getShowType() {
		return ShowType;
	}

	public void setShowType(int showType) {
		ShowType = showType;
	}

	public int getTipsType() {
		return TipsType;
	}

	public void setTipsType(int tipsType) {
		TipsType = tipsType;
	}

	public int getWindowTop(){
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = getResources().getDimensionPixelSize(x);
        } catch(Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }
	@Override
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		if(listner!=null){
			listner.onShowGuide(this, true);
		}
	}
	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();
		if(listner!=null){
			listner.onShowGuide(this, false);
		}
	}
	public showGuideListner listner;
	public interface showGuideListner{
		void onShowGuide(ShowTipsView view, boolean isShow);
	}
	public void setshowGuideListner(showGuideListner listner){
		this.listner=listner;
	}
}
