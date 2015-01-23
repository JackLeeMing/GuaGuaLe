package com.android.guaguale.view;

import com.android.guaguale.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class GuaHuaView extends View {
	private Paint mPaint;
	private Paint tPaint;
	private Rect mTextBound = new Rect();  
	private String mText = "500,0000,000";
	private Path mPath;
	private int lastX,lastY;
	private Canvas mCanvas;
	private Bitmap mBitmap,mBackBitmap;
	private Context mContext;
	private boolean isComplete;
	public GuaHuaView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.mContext=context;
		initPaint();
	}

	public GuaHuaView(Context context) {
		this(context,null);
		// TODO Auto-generated constructor stub
	}
	protected void initPaint(){
		mBackBitmap=BitmapFactory.decodeResource(mContext.getResources(),R.drawable.af);
		mPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
		tPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
		mPath=new Path();
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width=getMeasuredWidth();
		int height=getMeasuredHeight();
		mBitmap=Bitmap.createBitmap(width,height,Config.ARGB_8888);
		mCanvas=new Canvas(mBitmap);
		mPaint.setColor(Color.DKGRAY);
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setTextScaleX(2f);
		mPaint.setTextSize(22);

		tPaint.setColor(Color.RED);  
		tPaint.setAntiAlias(true);  
		tPaint.setDither(true);  
		tPaint.setStyle(Paint.Style.STROKE);  
		tPaint.setStrokeJoin(Paint.Join.ROUND); // 圆角  
		tPaint.setStrokeCap(Paint.Cap.ROUND); // 圆角  
		// 设置画笔宽度  
		tPaint.setStrokeWidth(20);
		mPaint.getTextBounds(mText, 0, mText.length(),mTextBound);
		mCanvas.drawBitmap(BitmapFactory.decodeResource(getResources(),
				R.drawable.s_title), null, new RectF(0, 0, width+getPaddingLeft()+getPaddingRight(), height), null);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.drawText(mText, getWidth()/2-mTextBound.width()/2-getPaddingRight()/2-getPaddingLeft()/2, getHeight()/2+mTextBound.height()/2-getPaddingBottom()/2-getPaddingTop()/2,mPaint);
		if (!isComplete) {
			draPath();
			canvas.drawBitmap(mBitmap, 0, 0,null);
		}
	}

	private void draPath() {
		// TODO Auto-generated method stub
		tPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
		mCanvas.drawPath(mPath, tPaint);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		int action=event.getAction();
		int x=(int) event.getX();
		int y=(int) event.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			lastX=x;
			lastY=y;
			mPath.moveTo(lastX, lastY);
			break;
		case MotionEvent.ACTION_MOVE:
			int dx=Math.abs(x-lastX);
			int dy=Math.abs(y-lastY);
			if (dx>3||dy>3) 
				mPath.lineTo(x, y);
			lastX=x;
			lastY=y;
			mPath.moveTo(lastX, lastY);
			break;
		case MotionEvent.ACTION_UP:
			new Thread(mRunnable).start();
			break;
		}
		invalidate();
		return true;
	}
	private Runnable mRunnable=new Runnable() {
		private int[] mPixel;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int w=getWidth();
			int h=getHeight();
			float wipeArea=0;
			float totalArea=w*h;//总面积
			Bitmap bitmap=mBitmap;
			mPixel=new int[w*h];//45000
			Log.e("TAG","=="+w+"=="+h+"=="+w*h);//375   120
			bitmap.getPixels(mPixel, 0, w, 0, 0, w, h);//45000
		
			for (int i = 0; i <w; i++) {//列数
				for (int j = 0; j <h; j++) {//行数
				int index=i+j*w;//二维矩阵到一维矩阵。从一维矩阵上找对应的二维矩阵的点的规则：列数自增+行标*列数
				if (mPixel[index]==0) {
					
					wipeArea++;
				}
				}
			}
			if (wipeArea>0&&totalArea>0) {
				int percent=(int) (wipeArea*100/totalArea);
				if (percent>55) {
					isComplete=true;
					postInvalidate();
				}
			}
		}
	};
}
