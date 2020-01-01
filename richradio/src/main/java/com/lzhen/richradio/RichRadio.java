package com.lzhen.richradio;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 富单选框
 */
public class RichRadio extends FrameLayout implements View.OnClickListener {

    private static final int POSITION_LEFT = 0;

    private static final int POSITION_TOP = 1;

    private static final int POSITION_RIGHT = 2;

    private static final int POSITION_BOTTOM = 3;

    private static final int POSITION_LEFT_TOP = 4;

    private static final int POSITION_RIGHT_TOP = 5;
    private static final int POSITION_LEFT_BOTTOM = 6;
    private static final int POSITION_RIGHT_BOTTOM = 7;


    private static final int DEFAULT_COLOR = android.R.color.background_dark;
    private static final int LINE_WIDTH = 0;

    private static final float SIZE = 15;

    private TextView txt;

    private ImageView img;

    private boolean checked;

    private int unCheckedColor;

    private int checkedColor;

    private int unableColor;

    private int iconPosition = 0;

    private int strokeWidth = LINE_WIDTH;

    private float textSize = SIZE;

    private float radius = LINE_WIDTH;

    private boolean mBroadcasting;

    private Bitmap iconbitmap = null;

    private OnCheckedChangeListener mOnCheckedChangeListener;
    private OnCheckedChangeListener mOnCheckedChangeWidgetListener;

    /**
     * 构造器
     *
     * @param context context
     */
    public RichRadio(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    /**
     * 构造器
     *
     * @param context context
     * @param attrs   attrs
     */
    public RichRadio(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * 構造器
     *
     * @param context      context
     * @param attrs        attrs
     * @param defStyleAttr defStyleAttr
     */
    public RichRadio(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = View.inflate(context, R.layout.layout_rich_radio, this);
        txt = view.findViewById(R.id.txt);
        img = view.findViewById(R.id.img);
        unCheckedColor = getResources().getColor(DEFAULT_COLOR);
        checkedColor = getResources().getColor(DEFAULT_COLOR);
        unableColor = getResources().getColor(android.R.color.darker_gray);

        if (attrs != null) {
            TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.RichRadio);
            checked = arr.getBoolean(R.styleable.RichRadio_checked, false);
            unCheckedColor = arr.getColor(R.styleable.RichRadio_unchecked_color, getResources().getColor(DEFAULT_COLOR));
            checkedColor = arr.getColor(R.styleable.RichRadio_checked_color, getResources().getColor(DEFAULT_COLOR));
            iconPosition = arr.getInt(R.styleable.RichRadio_icon_check_position, 0);
            radius = arr.getDimension(R.styleable.RichRadio_radius, LINE_WIDTH);
            strokeWidth = arr.getInt(R.styleable.RichRadio_stroke_width, LINE_WIDTH);
            textSize = arr.getDimension(R.styleable.RichRadio_text_size, SIZE);
            String str = arr.getString(R.styleable.RichRadio_text);
            txt.setText(str);
            int iconId = arr.getResourceId(R.styleable.RichRadio_checkedIcon, R.drawable.icon_checked);
            iconbitmap = BitmapFactory.decodeResource(getResources(), iconId);
            arr.recycle();
        }
        setCheckedIconPosition(iconPosition);
        setTxtMargin();
        changeDrawable(checked);
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (isEnabled() && !checked) {
            setChecked(true);
        }
    }

    public boolean isChecked() {
        return checked;
    }

    public void setCheckedIconPosition(int position) {
        iconPosition = position;
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = getGravity();
        img.setLayoutParams(params);
    }

    public void setTxtMargin() {
        if (iconbitmap != null) {
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            params.setMargins(iconbitmap.getWidth(), iconbitmap.getHeight(), iconbitmap.getWidth(), iconbitmap.getHeight());
            params.gravity = Gravity.CENTER;
            txt.setLayoutParams(params);
        }
    }

    /**
     * 设置是否选中
     *
     * @param state state
     */
    public void setChecked(boolean state) {
        if (checked != state) {
            checked = state;
            changeDrawable(state);
            refreshDrawableState();
            // Avoid infinite recursions if setChecked() is called from a listener
            if (mBroadcasting) {
                return;
            }
            mBroadcasting = true;
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(this, checked);
            }
            if (mOnCheckedChangeWidgetListener != null) {
                mOnCheckedChangeWidgetListener.onCheckedChanged(this, checked);
            }
            mBroadcasting = false;
        }
    }

    private void changeDrawable(boolean state) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        if (isEnabled()) {
            drawable.setStroke(strokeWidth, state ? checkedColor : unCheckedColor);
        }
        drawable.setColor(Color.TRANSPARENT);
        drawable.setCornerRadius(strokeWidth > 0 ? radius : 0);
        if (!isEnabled()) {
            txt.setTextColor(unableColor);
            img.setImageBitmap(null);
            return;
        }
        txt.setTextColor(state ? checkedColor : unCheckedColor);
        img.setImageBitmap(state ? iconbitmap : null);
        setBackground(drawable);
    }

    private int getGravity() {
        switch (iconPosition) {
            case POSITION_TOP:
                return Gravity.CENTER_HORIZONTAL | Gravity.TOP;
            case POSITION_RIGHT:
                return Gravity.CENTER_VERTICAL | Gravity.RIGHT;
            case POSITION_BOTTOM:
                return Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
            case POSITION_LEFT_TOP:
                return Gravity.LEFT | Gravity.TOP;
            case POSITION_RIGHT_TOP:
                return Gravity.RIGHT | Gravity.TOP;
            case POSITION_LEFT_BOTTOM:
                return Gravity.LEFT | Gravity.BOTTOM;
            case POSITION_RIGHT_BOTTOM:
                return Gravity.RIGHT | Gravity.BOTTOM;
            default:
                return Gravity.CENTER_VERTICAL | Gravity.LEFT;
        }
    }

    /**
     * Interface definition for a callback to be invoked when the checked state
     * of a compound button changed.
     */
    public static interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param buttonView The compound button view whose state has changed.
         * @param isChecked  The new checked state of buttonView.
         */
        void onCheckedChanged(RichRadio buttonView, boolean isChecked);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener mOnCheckedChangeListener) {
        this.mOnCheckedChangeListener = mOnCheckedChangeListener;
    }

    public void setOnCheckedChangeWidgetListener(OnCheckedChangeListener mOnCheckedChangeWidgetListener) {
        this.mOnCheckedChangeWidgetListener = mOnCheckedChangeWidgetListener;
    }
}
