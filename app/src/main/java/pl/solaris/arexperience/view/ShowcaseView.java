package pl.solaris.arexperience.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import pl.solaris.arexperience.R;

/**
 * Created by pbednarz on 2015-02-16.
 */
public class ShowcaseView extends RelativeLayout {
    private float centerX;
    private float centerY;
    private float radius;
    private Paint paintShadow;

    public ShowcaseView(Context context) {
        this(context, null);
    }

    public ShowcaseView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.scf_style);
    }

    public ShowcaseView(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, 0);
    }

    public ShowcaseView(Context context, AttributeSet attrs, int defStyle, int defStyleRes) {
        super(context, attrs, defStyle);
        setClickable(true);
        paintShadow = new Paint();
        paintShadow.setAntiAlias(true);
        paintShadow.setStyle(Paint.Style.FILL);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShowcaseAttrs, defStyle, defStyleRes);
        paintShadow.setColor(a.getColor(R.styleable.ShowcaseAttrs_scv_circle_color, Color.WHITE));
        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (radius > 0) {
            canvas.drawCircle(centerX, centerY, radius, paintShadow);
        }
        super.onDraw(canvas);
    }

    public void setDrawCircle(View showcaseTarget) {
        centerX = showcaseTarget.getLeft() + showcaseTarget.getWidth() * 0.5f;
        centerY = showcaseTarget.getTop() + showcaseTarget.getHeight() * 0.5f;
        radius = showcaseTarget.getWidth();
        postInvalidate();
    }
}
