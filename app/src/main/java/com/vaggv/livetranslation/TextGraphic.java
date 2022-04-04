package com.vaggv.livetranslation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


import com.google.mlkit.vision.text.Text;

public class TextGraphic extends View {

    private final Paint rectPaint;
    private final Paint textPaint;

    private final Text.TextBlock text;

    private static final int TEXT_COLOR = Color.YELLOW;
    private static final int RECT_COLOR = Color.YELLOW;
    private static final float TEXT_SIZE = 54.0f;
    private static final float STROKE_WIDTH = 10.0f;

    private final Path path = new Path();

    public TextGraphic(Context context, Text.TextBlock text) {
        super(context);

        rectPaint = new Paint(); 
        rectPaint.setColor(RECT_COLOR);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(STROKE_WIDTH);

        textPaint = new Paint();
        textPaint.setColor(TEXT_COLOR);
        textPaint.setStrokeWidth(50f);
        textPaint.setTextSize(TEXT_SIZE);
        textPaint.setStyle(Paint.Style.FILL);

        this.text = text;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Point[] points = text.getCornerPoints();

        if (points.length == 4){
            for (int i = 0; i < points.length; i++){
                points[i].x = (int) translate((int) shiftToLeft(points[i].x));
                points[i].y = (int) translate(points[i].y);
            }
            float[] pts = {
                    points[0].x, points[0].y, points[1].x, points[1].y,
                    points[1].x, points[1].y, points[2].x, points[2].y,
                    points[2].x, points[2].y, points[3].x, points[3].y,
                    points[3].x, points[3].y, points[0].x, points[0].y
            };

            float averageHeight = (points[3].y - points[0].y + (points[2].y - points[1].y)) / 2.0f;
            float textSize = averageHeight * 0.7f;
            float offset = averageHeight / 4;


            path.moveTo(points[3].x, points[3].y - textSize - offset - 10f);
            path.lineTo(points[2].x, points[2].y - textSize - offset - 10f);

            canvas.drawLines(pts, rectPaint);
            canvas.drawTextOnPath(text.getText(), path, 0f, 0f, textPaint);
        }
    }

    private float translate(int num) {
        return num * 2.5f;
    }

    private float shiftToLeft(int num){
        return num;
    }

}
