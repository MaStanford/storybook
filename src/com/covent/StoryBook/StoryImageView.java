package com.covent.StoryBook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/*
 * TODO: for some reason i cant get the loaded bitmap to be drawn
 */

public class StoryImageView extends View {

	// Bitmap
	private Path mPath;
	private Paint mBitmapPaint;
	private Paint mGraphPaint;

	// Paint
	private static Paint mPaint;
	private static Bitmap mBitmap;
	private static Canvas mCanvas;
	private static int mScreenHeight = 0;
	private static int mScreenWidth = 0;
	private static final float PAINT_WIDTH = 10;

	// Touch
	private float mX, mY;
	private static final float TOUCH_TOLERANCE = 4;
	private static final String TAG = "StoryImageView";

	public StoryImageView(Context context) {
		super(context);
		mPath = new Path();
		{
			mBitmapPaint = new Paint(Paint.DITHER_FLAG);
			mGraphPaint = new Paint(Paint.DITHER_FLAG);
			mPaint = new Paint();
			mPaint.setAntiAlias(true);
			mPaint.setDither(true);
			mPaint.setColor(Color.BLACK);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeJoin(Paint.Join.ROUND);
			mPaint.setStrokeCap(Paint.Cap.ROUND);
			mPaint.setStrokeWidth(PAINT_WIDTH);
		}
	}

	public StoryImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPath = new Path();
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		mGraphPaint = new Paint(Paint.DITHER_FLAG);
		{
			mBitmapPaint = new Paint(Paint.DITHER_FLAG);
			mGraphPaint = new Paint(Paint.DITHER_FLAG);
			mPaint = new Paint();
			mPaint.setAntiAlias(true);
			mPaint.setDither(true);
			mPaint.setColor(Color.BLACK);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeJoin(Paint.Join.ROUND);
			mPaint.setStrokeCap(Paint.Cap.ROUND);
			mPaint.setStrokeWidth(PAINT_WIDTH);
		}
	}

	public StoryImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mPath = new Path();
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		mGraphPaint = new Paint(Paint.DITHER_FLAG);
		{
			mBitmapPaint = new Paint(Paint.DITHER_FLAG);
			mGraphPaint = new Paint(Paint.DITHER_FLAG);
			mPaint = new Paint();
			mPaint.setAntiAlias(true);
			mPaint.setDither(true);
			mPaint.setColor(Color.BLACK);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeJoin(Paint.Join.ROUND);
			mPaint.setStrokeCap(Paint.Cap.ROUND);
			mPaint.setStrokeWidth(PAINT_WIDTH);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mScreenHeight = h;
		mScreenWidth = w;
		mBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight,Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		mCanvas.drawColor(Color.WHITE);
		drawLines();
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
	public void clearDrawing() {
		mBitmap.eraseColor(Color.WHITE);
		mPath = null;
		mPath = new Path();
		this.invalidate();
	}

	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
	}

	public void drawLines(){
		float mHeight = mScreenHeight;
		float mWidth = mScreenWidth;
		float mHeightInc = mScreenHeight / 15;
		float mWidthInc = mScreenWidth / 15;
		mGraphPaint.setColor(Color.GRAY);
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

	public void loadCanvas(Bitmap mNewBitMap) {
		mCanvas.drawBitmap(mNewBitMap, 0, 0, mBitmapPaint);
		this.invalidate();
	}

	private void touch_up() {
		mPath.lineTo(mX, mY);
		// commit the path to our off screen
		mCanvas.drawPath(mPath, mPaint);
		// kill this so we don't double draw
		mPath.reset();
		// Create the intent to send to save changes
		Intent mIntent = new Intent(Constants.KEY_IMAGE_DRAW_INTENT);
		getContext().sendBroadcast(mIntent);
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

	public void setBitmap(Bitmap bitmapText) {
		mBitmap = bitmapText;
		Constants
		.DEBUG_LOG(TAG, "SetBitmap called. Is Canvas Null? " + mCanvas);
		if (mCanvas != null)
			loadCanvas(bitmapText);
	}

	public Bitmap getBitmap() {
		return mBitmap;
	}
}