package meui.progress.smoothprogressbar;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import com.meui.SmoothBar.*;
import android.provider.*;
import android.content.*;
import java.util.*;

/**
 * Original created by castorflex on 11/10/13.
 * MEUI version, used for MEUI ROM.
 * @modifier zhaozihanzzh
 */
final public class SmoothProgressBar extends ProgressBar {

  private static final int INTERPOLATOR_ACCELERATE = 0;
  private static final int INTERPOLATOR_LINEAR = 1;
  private static final int INTERPOLATOR_ACCELERATEDECELERATE = 2;
  private static final int INTERPOLATOR_DECELERATE = 3;

  public SmoothProgressBar(Context context) {
    this(context, null);
  }

  public SmoothProgressBar(Context context, AttributeSet attrs) {
    this(context, attrs, R.attr.spbStyle);
  }

  public SmoothProgressBar(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    if (isInEditMode()) {
      setIndeterminateDrawable(new SmoothProgressDrawable.Builder(context, true).build());
      return;
    }

    // Resources res = context.getResources();
    // TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SmoothProgressBar, defStyle, 0);

    final ContentResolver RESOLVER=context.getContentResolver();
    final int color = Settings.System.getInt(RESOLVER,"spb_color",0xff009688);
        // a.getColor(R.styleable.SmoothProgressBar_spb_color, res.getColor(R.color.spb_default_color));
    final int sectionsCount = Settings.System.getInt(RESOLVER,"spb_sections_count",4);
        // a.getInteger(R.styleable.SmoothProgressBar_spb_sections_count, res.getInteger(R.integer.spb_default_sections_count));
    final int separatorLength = Settings.System.getInt(RESOLVER,"spb_stroke_separator_length",4);
        // a.getDimensionPixelSize(R.styleable.SmoothProgressBar_spb_stroke_separator_length, res.getDimensionPixelSize(R.dimen.spb_default_stroke_separator_length));
    final float strokeWidth = Settings.System.getFloat(RESOLVER,"spb_stroke_width",4);
        // a.getDimension(R.styleable.SmoothProgressBar_spb_stroke_width, res.getDimension(R.dimen.spb_default_stroke_width));
    final float speed = Settings.System.getFloat(RESOLVER,"spb_speed",1F);
        // a.getFloat(R.styleable.SmoothProgressBar_spb_speed, Float.parseFloat(res.getString(R.string.spb_default_speed)));
    final float speedProgressiveStart = Settings.System.getFloat(RESOLVER,"spb_start_speed",speed);
        // a.getFloat(R.styleable.SmoothProgressBar_spb_progressiveStart_speed, speed);
    final float speedProgressiveStop = Settings.System.getFloat(RESOLVER,"spb_stop_speed",speed);
        // a.getFloat(R.styleable.SmoothProgressBar_spb_progressiveStop_speed, speed);
    final int iInterpolator = Settings.System.getInt(RESOLVER,"spb_interpolator",-1);
        // a.getInteger(R.styleable.SmoothProgressBar_spb_interpolator, -1);
    final boolean reversed = getBoolean(RESOLVER,"spb_reversed");
        //a.getBoolean(R.styleable.SmoothProgressBar_spb_reversed, res.getBoolean(R.bool.spb_default_reversed));
    final boolean mirrorMode = getBoolean(RESOLVER, "spb_mirror");
        // a.getBoolean(R.styleable.SmoothProgressBar_spb_mirror_mode, res.getBoolean(R.bool.spb_default_mirror_mode));
    // final int colorsId = a.getResourceId(R.styleable.SmoothProgressBar_spb_colors, 0);
        // We can't use this way in order to get user's settings.
    final boolean progressiveStartActivated = true; //当仅有这一个ProgressBar时设定为false可能会造成屏幕闪烁。
        // getBoolean(RESOLVER, "spb_progressive_start_activated");
        // a.getBoolean(R.styleable.SmoothProgressBar_spb_progressiveStart_activated, res.getBoolean(R.bool.spb_default_progressiveStart_activated));
    // final Drawable backgroundDrawable = a.getDrawable(R.styleable.SmoothProgressBar_spb_background);
    final boolean generateBackgroundWithColors = true; //当仅有这一个ProgressBar时设定为false可能会造成屏幕闪烁。
        // a.getBoolean(R.styleable.SmoothProgressBar_spb_generate_background_with_colors, false);
    final boolean gradients = getBoolean(RESOLVER,"spb_gradients");
        // a.getBoolean(R.styleable.SmoothProgressBar_spb_gradients, false);
    // a.recycle();
    /*final*/ boolean multiColor = getBoolean(RESOLVER,"spb_color_multi");

    //interpolator
    Interpolator interpolator = null;
    if (iInterpolator == -1) {
      interpolator = getInterpolator();
    }
    if (interpolator == null) {
      switch (iInterpolator) {
        case INTERPOLATOR_ACCELERATEDECELERATE:
          interpolator = new AccelerateDecelerateInterpolator();
          break;
        case INTERPOLATOR_DECELERATE:
          interpolator = new DecelerateInterpolator();
          break;
        case INTERPOLATOR_LINEAR:
          interpolator = new LinearInterpolator();
          break;
        case INTERPOLATOR_ACCELERATE:
        default:
          interpolator = new AccelerateInterpolator();
      }
    }

    //// Use for DEBUG:
    // multiColor=true;
    ////
    ArrayList<Integer> colors = new ArrayList<>();
    //colors
    if(multiColor){
        String colorString = Settings.System.getString(RESOLVER,"spb_color_string");
        
        // colorString="(16777215)(-16738680)";
        ArrayList<Integer> lefts= new ArrayList<>();
        ArrayList<Integer> rights= new ArrayList<>();
        if(colorString != null && !colorString.isEmpty()){
            for(int now=0; now < colorString.length(); now++){
                if(colorString.subSequence(now,now+1).toString().equals("(")){
                    lefts.add(now);
                    continue;
                } 
                if(colorString.subSequence(now,now+1).toString().equals(")")){
                    rights.add(now);
                }
            }
        }
        if(!lefts.isEmpty()){
            for(int now = 0;now < lefts.size();now++){
                colors.add(Integer.parseInt(colorString.subSequence(lefts.get(now)+1,rights.get(now)).toString()));
            }
            
        }
    }
    /* if (colorsId != 0) {
      colors = res.getIntArray(colorsId);
    }*/

    SmoothProgressDrawable.Builder builder = new SmoothProgressDrawable.Builder(context)
        .speed(speed)
        .progressiveStartSpeed(speedProgressiveStart)
        .progressiveStopSpeed(speedProgressiveStop)
        .interpolator(interpolator)
        .sectionsCount(sectionsCount)
        .separatorLength(separatorLength)
        .strokeWidth(strokeWidth)
        .reversed(reversed)
        .mirrorMode(mirrorMode)
        .progressiveStart(progressiveStartActivated)
        .gradients(gradients);

    /* if (backgroundDrawable != null) {
      builder.backgroundDrawable(backgroundDrawable);
    }*/ // 我认为此功能在ROM中不是那么必要。

    if (generateBackgroundWithColors) {
      builder.generateBackgroundUsingColors();
    }
    
    
    if (colors != null && colors.size() > 0){
        //Integer[] colors2=(Integer[]) colors.toArray();
        int[] finalColors=new int[colors.size()];
        for(int now=0; now < colors.size(); now++){
            finalColors[now] = colors.get(now);
        }
        builder.colors(finalColors);
    } else
      builder.color(color);

    SmoothProgressDrawable d = builder.build();
    setIndeterminateDrawable(d);
  }

  /*private void applyStyle(int styleResId) {
    TypedArray a = getContext().obtainStyledAttributes(null, R.styleable.SmoothProgressBar, 0, styleResId);

    if (a.hasValue(R.styleable.SmoothProgressBar_spb_color)) {
      setSmoothProgressDrawableColor(a.getColor(R.styleable.SmoothProgressBar_spb_color, 0));
    }
    if (a.hasValue(R.styleable.SmoothProgressBar_spb_colors)) {
      int colorsId = a.getResourceId(R.styleable.SmoothProgressBar_spb_colors, 0);
      if (colorsId != 0) {
        int[] colors = getResources().getIntArray(colorsId);
        if (colors != null && colors.length > 0)
          setSmoothProgressDrawableColors(colors);
      }
    }
    if (a.hasValue(R.styleable.SmoothProgressBar_spb_sections_count)) {
      setSmoothProgressDrawableSectionsCount(a.getInteger(R.styleable.SmoothProgressBar_spb_sections_count, 0));
    }
    if (a.hasValue(R.styleable.SmoothProgressBar_spb_stroke_separator_length)) {
      setSmoothProgressDrawableSeparatorLength(a.getDimensionPixelSize(R.styleable.SmoothProgressBar_spb_stroke_separator_length, 0));
    }
    if (a.hasValue(R.styleable.SmoothProgressBar_spb_stroke_width)) {
      setSmoothProgressDrawableStrokeWidth(a.getDimension(R.styleable.SmoothProgressBar_spb_stroke_width, 0));
    }
    if (a.hasValue(R.styleable.SmoothProgressBar_spb_speed)) {
      setSmoothProgressDrawableSpeed(a.getFloat(R.styleable.SmoothProgressBar_spb_speed, 0));
    }
    if (a.hasValue(R.styleable.SmoothProgressBar_spb_progressiveStart_speed)) {
      setSmoothProgressDrawableProgressiveStartSpeed(a.getFloat(R.styleable.SmoothProgressBar_spb_progressiveStart_speed, 0));
    }
    if (a.hasValue(R.styleable.SmoothProgressBar_spb_progressiveStop_speed)) {
      setSmoothProgressDrawableProgressiveStopSpeed(a.getFloat(R.styleable.SmoothProgressBar_spb_progressiveStop_speed, 0));
    }
    if (a.hasValue(R.styleable.SmoothProgressBar_spb_reversed)) {
      setSmoothProgressDrawableReversed(a.getBoolean(R.styleable.SmoothProgressBar_spb_reversed, false));
    }
    if (a.hasValue(R.styleable.SmoothProgressBar_spb_mirror_mode)) {
      setSmoothProgressDrawableMirrorMode(a.getBoolean(R.styleable.SmoothProgressBar_spb_mirror_mode, false));
    }
    if (a.hasValue(R.styleable.SmoothProgressBar_spb_progressiveStart_activated)) {
      setProgressiveStartActivated(a.getBoolean(R.styleable.SmoothProgressBar_spb_progressiveStart_activated, false));
    }
    if (a.hasValue(R.styleable.SmoothProgressBar_spb_progressiveStart_activated)) {
      setProgressiveStartActivated(a.getBoolean(R.styleable.SmoothProgressBar_spb_progressiveStart_activated, false));
    }
    if (a.hasValue(R.styleable.SmoothProgressBar_spb_gradients)) {
      setSmoothProgressDrawableUseGradients(a.getBoolean(R.styleable.SmoothProgressBar_spb_gradients, false));
    }
    if (a.hasValue(R.styleable.SmoothProgressBar_spb_generate_background_with_colors)) {
      if (a.getBoolean(R.styleable.SmoothProgressBar_spb_generate_background_with_colors, false)) {
        setSmoothProgressDrawableBackgroundDrawable(
            SmoothProgressBarUtils.generateDrawableWithColors(checkIndeterminateDrawable().getColors(), checkIndeterminateDrawable().getStrokeWidth()));
      }
    }
    if (a.hasValue(R.styleable.SmoothProgressBar_spb_interpolator)) {
      int iInterpolator = a.getInteger(R.styleable.SmoothProgressBar_spb_interpolator, -1);
      Interpolator interpolator;
      switch (iInterpolator) {
        case INTERPOLATOR_ACCELERATEDECELERATE:
          interpolator = new AccelerateDecelerateInterpolator();
          break;
        case INTERPOLATOR_DECELERATE:
          interpolator = new DecelerateInterpolator();
          break;
        case INTERPOLATOR_LINEAR:
          interpolator = new LinearInterpolator();
          break;
        case INTERPOLATOR_ACCELERATE:
          interpolator = new AccelerateInterpolator();
          break;
        default:
          interpolator = null;
      }
      if (interpolator != null) {
        setInterpolator(interpolator);
      }
    }
    a.recycle();
  }*/

  @Override
  protected synchronized void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (isIndeterminate() && getIndeterminateDrawable() instanceof SmoothProgressDrawable &&
        !((SmoothProgressDrawable) getIndeterminateDrawable()).isRunning()) {
      getIndeterminateDrawable().draw(canvas);
    }
  }

  /* private SmoothProgressDrawable checkIndeterminateDrawable() {
    Drawable ret = getIndeterminateDrawable();
    if (ret == null || !(ret instanceof SmoothProgressDrawable))
      throw new RuntimeException("The drawable is not a SmoothProgressDrawable");
    return (SmoothProgressDrawable) ret;
  }*/

  @Override
  public void setInterpolator(Interpolator interpolator) {
    super.setInterpolator(interpolator);
    Drawable ret = getIndeterminateDrawable();
    if (ret != null && (ret instanceof SmoothProgressDrawable))
      ((SmoothProgressDrawable) ret).setInterpolator(interpolator);
  }
  
  public static boolean getBoolean(ContentResolver RESOLVER, String name){
      return Settings.System.getInt(RESOLVER,name,0)==1;
  }
/*
  private void setSmoothProgressDrawableInterpolator(Interpolator interpolator) {
    checkIndeterminateDrawable().setInterpolator(interpolator);
  }

  private void setSmoothProgressDrawableColors(int[] colors) {
    checkIndeterminateDrawable().setColors(colors);
  }

  private void setSmoothProgressDrawableColor(int color) {
    checkIndeterminateDrawable().setColor(color);
  }

  private void setSmoothProgressDrawableSpeed(float speed) {
    checkIndeterminateDrawable().setSpeed(speed);
  }

  private void setSmoothProgressDrawableProgressiveStartSpeed(float speed) {
    checkIndeterminateDrawable().setProgressiveStartSpeed(speed);
  }

  private void setSmoothProgressDrawableProgressiveStopSpeed(float speed) {
    checkIndeterminateDrawable().setProgressiveStopSpeed(speed);
  }

  private void setSmoothProgressDrawableSectionsCount(int sectionsCount) {
    checkIndeterminateDrawable().setSectionsCount(sectionsCount);
  }

  private void setSmoothProgressDrawableSeparatorLength(int separatorLength) {
    checkIndeterminateDrawable().setSeparatorLength(separatorLength);
  }

  private void setSmoothProgressDrawableStrokeWidth(float strokeWidth) {
    checkIndeterminateDrawable().setStrokeWidth(strokeWidth);
  }

  private void setSmoothProgressDrawableReversed(boolean reversed) {
    checkIndeterminateDrawable().setReversed(reversed);
  }

  private void setSmoothProgressDrawableMirrorMode(boolean mirrorMode) {
    checkIndeterminateDrawable().setMirrorMode(mirrorMode);
  }

  private void setProgressiveStartActivated(boolean progressiveStartActivated) {
    checkIndeterminateDrawable().setProgressiveStartActivated(progressiveStartActivated);
  }

  private void setSmoothProgressDrawableCallbacks(SmoothProgressDrawable.Callbacks listener) {
    checkIndeterminateDrawable().setCallbacks(listener);
  }

  private void setSmoothProgressDrawableBackgroundDrawable(Drawable drawable) {
    checkIndeterminateDrawable().setBackgroundDrawable(drawable);
  }

  private void setSmoothProgressDrawableUseGradients(boolean useGradients) {
    checkIndeterminateDrawable().setUseGradients(useGradients);
  }

  private void progressiveStart() {
    checkIndeterminateDrawable().progressiveStart();
  }

  private void progressiveStop() {
    checkIndeterminateDrawable().progressiveStop();
  }*/
}
