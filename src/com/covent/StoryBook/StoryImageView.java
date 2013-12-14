package com.covent.StoryBook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class StoryImageView extends View {
	
	//Bitmap
	private Path    mPath;
	private Paint   mBitmapPaint;
	private Paint   mGraphPaint;

	static Bitmap  mLoadedBitmap;

	//Paint
	private static Paint mPaint;
	private MaskFilter  mEmboss;
	private MaskFilter  mBlur;
	private static Bitmap  mBitmap;
	private static Canvas  mCanvas;
	private static boolean mEraseMode = false;
	private static final int PAINT_WIDTH = 5;
	private static final int ERASE_WIDTH = 50;
	private static int RESULT_LOAD_IMAGE = 1;
	private static boolean mSaveType = true;  //temporary for testing
	private static int mScreenHeight = 0;
	private static int mScreenWidth = 0;
	private static boolean mEmbossState = false;
	private static boolean mFadedState = false;
	private static boolean mBlurState = false;
	private static boolean mGraphPaperState = false;
	
	//Touch 
	private float mX, mY;
	private static final float TOUCH_TOLERANCE = 4;


	public StoryImageView(Context context) {
		super(context);
		mPath = new Path();
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		mGraphPaint = new Paint(Paint.DITHER_FLAG);
	}

	public StoryImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPath = new Path();
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		mGraphPaint = new Paint(Paint.DITHER_FLAG);
	}

	public StoryImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mPath = new Path();
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		mGraphPaint = new Paint(Paint.DITHER_FLAG);
	}



	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mScreenHeight = h;
		mScreenWidth = w;
		mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		//http://stackoverflow.com/questions/9901024/android-bitmap-how-to-save-canvas-with-green-background-in-android
		mCanvas.drawColor(Color.WHITE);
		super.onSizeChanged(w, h, oldw, oldh);
	}


	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(0x00000000);
		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
		canvas.drawPath(mPath, mPaint);
	}

	private void touch_start(float x, float y) {
		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
		mCanvas.drawPoint(x, y, mPaint);
	}
	
	@SuppressLint("WrongCall")
	public void clearDrawing(){
		mBitmap.eraseColor(Color.WHITE);
		mPath = null;
		mPath = new Path();
		this.invalidate();
	}

	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
			mX = x;
			mY = y;
		}
	}
	
	
	public void removeGraph()
	{
		float mHeight = mScreenHeight;
		float mWidth = mScreenWidth;
		float mHeightInc = mScreenHeight / 25;
		float mWidthInc = mScreenWidth / 25;
		mGraphPaint.setColor(Color.WHITE);
		while(mWidth > 0) {
			mCanvas.drawLine(mWidth, 0, mWidth, mScreenHeight, mGraphPaint);
			mWidth = mWidth - mWidthInc;
		}
		while(mHeight > 0) {
			mCanvas.drawLine(0, mHeight, mScreenWidth, mHeight, mGraphPaint);
			mHeight = mHeight - mHeightInc;
		}
		this.invalidate();
	}
	
	public void loadCanvas(Bitmap mNewBitMap)
	{
		mCanvas.drawBitmap(mNewBitMap, 0, 0, null);
		this.invalidate();
	}

	private void touch_up() {
		mPath.lineTo(mX, mY);
		// commit the path to our off screen
		mCanvas.drawPath(mPath, mPaint);
		// kill this so we don't double draw
		mPath.reset();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touch_start(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			touch_move(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			touch_up();
			invalidate();
			break;
		}
		return true;
	}
}