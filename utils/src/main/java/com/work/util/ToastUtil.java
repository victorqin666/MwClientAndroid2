package com.work.util;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;

import androidx.annotation.CheckResult;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtil {
	@ColorInt
	private static int DEFAULT_TEXT_COLOR = Color.parseColor("#FFFFFF");
	@ColorInt
	private static int ERROR_COLOR = Color.parseColor("#D50000");
	@ColorInt
	private static int INFO_COLOR = Color.parseColor("#3F51B5");
	@ColorInt
	private static int SUCCESS_COLOR = Color.parseColor("#388E3C");
	@ColorInt
	private static int WARNING_COLOR = Color.parseColor("#FFA900");
	@ColorInt
	private static int NORMAL_COLOR = Color.parseColor("#353A3E");

	private static final Typeface LOADED_TOAST_TYPEFACE = Typeface.create("sans-serif-condensed", Typeface.NORMAL);
	private static Typeface currentTypeface = LOADED_TOAST_TYPEFACE;
	private static int textSize = 16; // in SP

	private static boolean tintIcon = true;

	private ToastUtil() {
		// avoiding instantiation
	}
	//-----------------------------普通---------------------------------------
	public static void normal(@NonNull Context context, @StringRes int stringId) {
		Toast toastUtil = normal(context, context.getString(stringId), Toast.LENGTH_SHORT, null, false);
		if(toastUtil!=null){
			toastUtil.show();
		}
	}

	public static void normal(@NonNull Context context, @NonNull CharSequence message) {
		Toast toastUtil = normal(context, message, Toast.LENGTH_SHORT, null, false);
		if(toastUtil!=null){
			toastUtil.show();
		}
	}

	public static void normal(@NonNull Context context, @NonNull CharSequence message, Drawable icon) {
		Toast toastUtil = normal(context, message, Toast.LENGTH_SHORT, icon, true);
		if(toastUtil!=null){
			toastUtil.show();
		}
	}

	public static void normal(@NonNull Context context, @NonNull CharSequence message, int duration) {
		Toast toastUtil = normal(context, message, duration, null, false);
		if(toastUtil!=null){
			toastUtil.show();
		}
	}

	public static void normal(@NonNull Context context, @NonNull CharSequence message, int duration,
							   Drawable icon) {
		Toast toastUtil = normal(context, message, duration, icon, true);
		if(toastUtil!=null){
			toastUtil.show();
		}
	}

	@CheckResult
	public static Toast normal(@NonNull Context context, @NonNull CharSequence message, int duration,
							   Drawable icon, boolean withIcon) {
		return custom(context, message, icon, NORMAL_COLOR, duration, withIcon, true);
	}

	//-----------------------------警告---------------------------------------
	public static void warning(@NonNull Context context, @StringRes int stringId) {
		Toast toastUtil = warning(context, context.getString(stringId), Toast.LENGTH_SHORT, true);
		if(toastUtil!=null){
			toastUtil.show();
		}
	}

	public static void warning(@NonNull Context context, @NonNull CharSequence message) {
		Toast toastUtil = warning(context, message, Toast.LENGTH_SHORT, true);
		if(toastUtil!=null){
			toastUtil.show();
		}
	}

	public static void warning(@NonNull Context context, @NonNull CharSequence message, int duration) {
		Toast toastUtil = warning(context, message, duration, true);
		if(toastUtil!=null){
			toastUtil.show();
		}
	}

	@CheckResult
	public static Toast warning(@NonNull Context context, @NonNull CharSequence message, int duration, boolean withIcon) {
		return custom(context, message, getDrawable(context, R.drawable.ic_error_outline_white_48dp),
				WARNING_COLOR, duration, withIcon, true);
	}

	//-----------------------------提示---------------------------------------
	public static void info(@NonNull Context context, @StringRes int stringId) {
		Toast toastUtil = info(context, context.getString(stringId), Toast.LENGTH_SHORT, true);
		if(toastUtil!=null){
			toastUtil.show();
		}
	}

	public static void info(@NonNull Context context, @StringRes int stringId,int length) {
		Toast toastUtil = info(context, context.getString(stringId),length, true);
		if(toastUtil!=null){
			toastUtil.show();
		}
	}

	public static void info(@NonNull Context context, @NonNull CharSequence message) {
		Toast toastUtil = info(context, message, Toast.LENGTH_SHORT, true);
		if(toastUtil!=null){
			toastUtil.show();
		}
	}

	public static void info(@NonNull Context context, @NonNull CharSequence message, int duration) {
		Toast toastUtil = info(context, message, duration, true);
		if(toastUtil!=null){
			toastUtil.show();
		}
	}

	@CheckResult
	public static Toast info(@NonNull Context context, @NonNull CharSequence message, int duration, boolean withIcon) {
		return custom(context, message, getDrawable(context, R.drawable.ic_info_outline_white_48dp),
				INFO_COLOR, duration, withIcon, true);
	}

	//-----------------------------成功---------------------------------------
	public static void success(@NonNull Context context, @StringRes int stringId) {
		Toast toastUtil = success(context, context.getString(stringId), Toast.LENGTH_SHORT, true);
		if(toastUtil!=null){
			toastUtil.show();
		}
	}

	public static void success(@NonNull Context context, @NonNull CharSequence message) {
		Toast toastUtil = success(context, message, Toast.LENGTH_SHORT, true);
		if(toastUtil!=null){
			toastUtil.show();
		}
	}

	public static void success(@NonNull Context context, @NonNull CharSequence message, int duration) {
		Toast toastUtil = success(context, message, duration, true);
		if(toastUtil!=null){
			toastUtil.show();
		}
	}

	@CheckResult
	public static Toast success(@NonNull Context context, @NonNull CharSequence message, int duration, boolean withIcon) {
		return custom(context, message, getDrawable(context, R.drawable.ic_check_white_48dp),
				SUCCESS_COLOR, duration, withIcon, true);
	}

	//-----------------------------错误---------------------------------------
	public static void error(@NonNull Context context, @StringRes int stringId) {
		Toast toastUtil = error(context, context.getString(stringId), Toast.LENGTH_SHORT, true);
		if(toastUtil!=null){
			toastUtil.show();
		}
	}

	public static void error(@NonNull Context context, @NonNull CharSequence message) {
		Toast toastUtil = error(context, message, Toast.LENGTH_SHORT, true);
		if(toastUtil!=null){
			toastUtil.show();
		}
	}

	public static void error(@NonNull Context context, @NonNull CharSequence message, int duration) {
		Toast toastUtil = error(context, message, duration, true);
		if(toastUtil!=null){
			toastUtil.show();
		}
	}

	public static Toast error(@NonNull Context context, @NonNull CharSequence message, int duration, boolean withIcon) {
		return custom(context, message, getDrawable(context, R.drawable.ic_clear_white_48dp),
				ERROR_COLOR, duration, withIcon, true);
	}

	@CheckResult
	public static Toast custom(@NonNull Context context, @NonNull CharSequence message, Drawable icon,
							   int duration, boolean withIcon) {
		return custom(context, message, icon, -1, duration, withIcon, false);
	}

	@CheckResult
	public static Toast custom(@NonNull Context context, @NonNull CharSequence message, @DrawableRes int iconRes,
							   @ColorInt int tintColor, int duration,
							   boolean withIcon, boolean shouldTint) {
		return custom(context, message, getDrawable(context, iconRes),
				tintColor, duration, withIcon, shouldTint);
	}

	@SuppressLint("ShowToast")
	@CheckResult
	public static Toast custom(@NonNull Context context, @NonNull CharSequence message, Drawable icon,
							   @ColorInt int tintColor, int duration,
							   boolean withIcon, boolean shouldTint) {
		if(TextUtils.isEmpty(message)){
			return null;
		}
		final Toast currentToast = Toast.makeText(context, "", duration);
		final View toastLayout = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.toast_layout, null);
		final ImageView toastIcon = toastLayout.findViewById(R.id.toast_icon);
		final TextView toastTextView = toastLayout.findViewById(R.id.toast_text);
		Drawable drawableFrame;

		if (shouldTint)
			drawableFrame = tint9PatchDrawableFrame(context, tintColor);
		else
			drawableFrame = getDrawable(context, R.drawable.toast_frame);
		setBackground(toastLayout, drawableFrame);

		if (withIcon) {
			if (icon == null)
				throw new IllegalArgumentException("Avoid passing 'icon' as null if 'withIcon' is set to true");
			if (tintIcon)
				icon = tintIcon(icon, DEFAULT_TEXT_COLOR);
			setBackground(toastIcon, icon);
		} else {
			toastIcon.setVisibility(View.GONE);
		}

		toastTextView.setText(message);
		toastTextView.setTextColor(DEFAULT_TEXT_COLOR);
		toastTextView.setTypeface(currentTypeface);
		toastTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);

		currentToast.setView(toastLayout);
		return currentToast;
	}

	public static class Config {
		@ColorInt
		private int DEFAULT_TEXT_COLOR = ToastUtil.DEFAULT_TEXT_COLOR;
		@ColorInt
		private int ERROR_COLOR = ToastUtil.ERROR_COLOR;
		@ColorInt
		private int INFO_COLOR = ToastUtil.INFO_COLOR;
		@ColorInt
		private int SUCCESS_COLOR = ToastUtil.SUCCESS_COLOR;
		@ColorInt
		private int WARNING_COLOR = ToastUtil.WARNING_COLOR;

		private Typeface typeface = ToastUtil.currentTypeface;
		private int textSize = ToastUtil.textSize;

		private boolean tintIcon = ToastUtil.tintIcon;

		private Config() {
			// avoiding instantiation
		}

		@CheckResult
		public static Config getInstance() {
			return new Config();
		}

		public static void reset() {
			ToastUtil.DEFAULT_TEXT_COLOR = Color.parseColor("#FFFFFF");
			ToastUtil.ERROR_COLOR = Color.parseColor("#D50000");
			ToastUtil.INFO_COLOR = Color.parseColor("#3F51B5");
			ToastUtil.SUCCESS_COLOR = Color.parseColor("#388E3C");
			ToastUtil.WARNING_COLOR = Color.parseColor("#FFA900");
			ToastUtil.currentTypeface = LOADED_TOAST_TYPEFACE;
			ToastUtil.textSize = 16;
			ToastUtil.tintIcon = true;
		}

		@CheckResult
		public Config setTextColor(@ColorInt int textColor) {
			DEFAULT_TEXT_COLOR = textColor;
			return this;
		}

		@CheckResult
		public Config setErrorColor(@ColorInt int errorColor) {
			ERROR_COLOR = errorColor;
			return this;
		}

		@CheckResult
		public Config setInfoColor(@ColorInt int infoColor) {
			INFO_COLOR = infoColor;
			return this;
		}

		@CheckResult
		public Config setSuccessColor(@ColorInt int successColor) {
			SUCCESS_COLOR = successColor;
			return this;
		}

		@CheckResult
		public Config setWarningColor(@ColorInt int warningColor) {
			WARNING_COLOR = warningColor;
			return this;
		}

		@CheckResult
		public Config setToastTypeface(@NonNull Typeface typeface) {
			this.typeface = typeface;
			return this;
		}

		@CheckResult
		public Config setTextSize(int sizeInSp) {
			this.textSize = sizeInSp;
			return this;
		}

		@CheckResult
		public Config tintIcon(boolean tintIcon) {
			this.tintIcon = tintIcon;
			return this;
		}

		public void apply() {
			ToastUtil.DEFAULT_TEXT_COLOR = DEFAULT_TEXT_COLOR;
			ToastUtil.ERROR_COLOR = ERROR_COLOR;
			ToastUtil.INFO_COLOR = INFO_COLOR;
			ToastUtil.SUCCESS_COLOR = SUCCESS_COLOR;
			ToastUtil.WARNING_COLOR = WARNING_COLOR;
			ToastUtil.currentTypeface = typeface;
			ToastUtil.textSize = textSize;
			ToastUtil.tintIcon = tintIcon;
		}
	}

	static Drawable tintIcon(@NonNull Drawable drawable, @ColorInt int tintColor) {
		drawable.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN);
		return drawable;
	}

	static Drawable tint9PatchDrawableFrame(@NonNull Context context, @ColorInt int tintColor) {
		final NinePatchDrawable toastDrawable = (NinePatchDrawable) getDrawable(context, R.drawable.toast_frame);
		return tintIcon(toastDrawable, tintColor);
	}

	static void setBackground(@NonNull View view, Drawable drawable) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
			view.setBackground(drawable);
		else
			view.setBackgroundDrawable(drawable);
	}

	static Drawable getDrawable(@NonNull Context context, @DrawableRes int id) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
			return context.getDrawable(id);
		else
			return context.getResources().getDrawable(id);
	}
}
