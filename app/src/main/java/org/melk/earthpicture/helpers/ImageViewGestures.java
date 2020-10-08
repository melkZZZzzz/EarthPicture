package org.melk.earthpicture.helpers;

import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import androidx.annotation.NonNull;

public class ImageViewGestures extends ScaleGestureDetector.SimpleOnScaleGestureListener {

	private static final int POINTER_NONE = -1;

	public ImageView imageView;

	private ScaleGestureDetector scaleGestureDetector;
	public float scaleFactor;

	private float pointerLastTouchX;
	private float pointerLastTouchY;
	private float pointerPosX;
	private float pointerPosY;
	private int pointerId;

	public ImageViewGestures(@NonNull ImageView imageView ) {
		this.imageView = imageView;

		scaleGestureDetector = new ScaleGestureDetector(imageView.getContext(), this);
		this.scaleFactor = 1.0f;

		pointerLastTouchX = 0;
		pointerLastTouchY = 0;
		pointerPosX = 0;
		pointerPosY = 0;
		pointerId = POINTER_NONE;
	}

	public void resetScale() {
		scaleFactor = 1.0f;

		imageView.setScaleX(scaleFactor);
		imageView.setScaleY(scaleFactor);
		imageView.setX(0.f);
		imageView.setY(0.f);
		pointerLastTouchX = 0;
		pointerLastTouchY = 0;
		pointerPosX = 0;
		pointerPosY = 0;
		pointerId = POINTER_NONE;
	}

	public Rect getVisibleRect() {
		BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
		double bmpw = bitmapDrawable.getBitmap().getWidth();
		double bmph = bitmapDrawable.getBitmap().getHeight();
		double ivw  = imageView.getWidth();
		double ivh  = imageView.getHeight();
		double bmpScaleFactor = ( ivw < ivh ) ? bmpw / ivw : bmph / ivh;

		double centerX = 0.5f*bmpw - imageView.getX()/scaleFactor*bmpScaleFactor;
		double centerY = 0.5f*bmph - imageView.getY()/scaleFactor*bmpScaleFactor;

		double offset_left   = centerX - 0.5f*ivw/scaleFactor*bmpScaleFactor;
		double offset_top    = centerY - 0.5f*ivh/scaleFactor*bmpScaleFactor;
		double offset_right  = centerX + 0.5f*ivw/scaleFactor*bmpScaleFactor;
		double offset_bottom = centerY + 0.5f*ivh/scaleFactor*bmpScaleFactor;

		int left   = (int) Math.max(0,offset_left);
		int top    = (int) Math.max(0,offset_top);
		int right  = (int) Math.min(bitmapDrawable.getBitmap().getWidth(),offset_right);
		int bottom = (int) Math.min(bitmapDrawable.getBitmap().getHeight(),offset_bottom);

		return new Rect(left, top, right, bottom);
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		float imageViewFocusX = imageView.getX() / scaleFactor;
		float imageViewFocusY = imageView.getY() / scaleFactor;

		scaleFactor *= detector.getScaleFactor();
		scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 10.0f));
		imageView.setScaleX(scaleFactor);
		imageView.setScaleY(scaleFactor);

		pointerLastTouchX = detector.getFocusX();
		pointerLastTouchY = detector.getFocusY();
		pointerPosX = ( imageViewFocusX * scaleFactor );
		pointerPosY = ( imageViewFocusY * scaleFactor );
		imageView.setX(pointerPosX);
		imageView.setY(pointerPosY);

		return true;
	}

	public boolean onTouchEvent(MotionEvent motionEvent) {

		scaleGestureDetector.onTouchEvent(motionEvent);

		if ( ! scaleGestureDetector.isInProgress() ) {
			final int action = motionEvent.getAction();
			switch (action & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN: {
					pointerLastTouchX = motionEvent.getX();
					pointerLastTouchY = motionEvent.getY();
					pointerId = motionEvent.getPointerId(0);

					break;
				}

				case MotionEvent.ACTION_MOVE: {
					final int pointerIndex = motionEvent.findPointerIndex(pointerId);
					final float x = motionEvent.getX(pointerIndex);
					final float y = motionEvent.getY(pointerIndex);

					final double dx = x - pointerLastTouchX;
					final double dy = y - pointerLastTouchY;

					pointerPosX += dx;
					pointerPosY += dy;

					imageView.setX(pointerPosX);
					imageView.setY(pointerPosY);

					pointerLastTouchX = x;
					pointerLastTouchY = y;

					break;
				}

				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL: {
					pointerId = POINTER_NONE;

					break;
				}

				case MotionEvent.ACTION_POINTER_UP: {
					final int pointerIndex = (motionEvent.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
					final int id = motionEvent.getPointerId(pointerIndex);
					if (id == pointerId) {
						final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
						pointerLastTouchX = motionEvent.getX(newPointerIndex);
						pointerLastTouchY = motionEvent.getY(newPointerIndex);
						pointerId = motionEvent.getPointerId(newPointerIndex);
					}

					break;
				}
			}
		}

		return true;
	}
}
