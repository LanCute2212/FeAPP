package com.example.caloriesapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class SegmentedCircularProgressView extends View {
  private static final int CARBS_COLOR = 0xFF4CAF50;
  private static final int PROTEIN_COLOR = 0xFFF44336;
  private static final int FAT_COLOR = 0xFFFF9800;
  private static final int TRACK_COLOR = 0xFFE0E0E0;
  
  private Paint trackPaint;
  private Paint carbsPaint;
  private Paint proteinPaint;
  private Paint fatPaint;
  private RectF rectF;
  
  private float carbsPercent = 0;
  private float proteinPercent = 0;
  private float fatPercent = 0;
  private float strokeWidth = 0;
  private float startAngle = -90;

  public SegmentedCircularProgressView(Context context) {
    super(context);
    init();
  }

  public SegmentedCircularProgressView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public SegmentedCircularProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {
    trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    trackPaint.setStyle(Paint.Style.STROKE);
    trackPaint.setColor(TRACK_COLOR);
    trackPaint.setStrokeWidth(strokeWidth);
    trackPaint.setStrokeCap(Paint.Cap.ROUND);

    carbsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    carbsPaint.setStyle(Paint.Style.STROKE);
    carbsPaint.setColor(CARBS_COLOR);
    carbsPaint.setStrokeWidth(strokeWidth);
    carbsPaint.setStrokeCap(Paint.Cap.ROUND);

    proteinPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    proteinPaint.setStyle(Paint.Style.STROKE);
    proteinPaint.setColor(PROTEIN_COLOR);
    proteinPaint.setStrokeWidth(strokeWidth);
    proteinPaint.setStrokeCap(Paint.Cap.ROUND);

    fatPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    fatPaint.setStyle(Paint.Style.STROKE);
    fatPaint.setColor(FAT_COLOR);
    fatPaint.setStrokeWidth(strokeWidth);
    fatPaint.setStrokeCap(Paint.Cap.ROUND);

    rectF = new RectF();
  }

  public void setMacros(float carbsPercent, float proteinPercent, float fatPercent) {
    this.carbsPercent = carbsPercent;
    this.proteinPercent = proteinPercent;
    this.fatPercent = fatPercent;
    invalidate();
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    strokeWidth = 8 * getResources().getDisplayMetrics().density;
    trackPaint.setStrokeWidth(strokeWidth);
    carbsPaint.setStrokeWidth(strokeWidth);
    proteinPaint.setStrokeWidth(strokeWidth);
    fatPaint.setStrokeWidth(strokeWidth);
    float padding = strokeWidth / 2;
    rectF.set(padding, padding, w - padding, h - padding);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    
    float totalPercent = carbsPercent + proteinPercent + fatPercent;
    if (totalPercent <= 0) {
      canvas.drawArc(rectF, 0, 360, false, trackPaint);
      return;
    }

    float carbsAngle = (carbsPercent / totalPercent) * 360;
    float proteinAngle = (proteinPercent / totalPercent) * 360;
    float fatAngle = (fatPercent / totalPercent) * 360;

    float currentAngle = startAngle;

    if (carbsPercent > 0) {
      canvas.drawArc(rectF, currentAngle, carbsAngle, false, carbsPaint);
      currentAngle += carbsAngle;
    }

    if (proteinPercent > 0) {
      canvas.drawArc(rectF, currentAngle, proteinAngle, false, proteinPaint);
      currentAngle += proteinAngle;
    }

    if (fatPercent > 0) {
      canvas.drawArc(rectF, currentAngle, fatAngle, false, fatPaint);
    }

    float remainingAngle = 360 - (carbsAngle + proteinAngle + fatAngle);
    if (remainingAngle > 0) {
      canvas.drawArc(rectF, currentAngle + fatAngle, remainingAngle, false, trackPaint);
    }
  }
}

