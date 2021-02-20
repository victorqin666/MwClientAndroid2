package com.work.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;


@SuppressLint("NewApi")
public class SharedUtils {
	private static SharedPreferences sp;
	private static SharedPreferences.Editor editor;

	public static void init(Context context) {
		sp = context.getSharedPreferences("share_data",Context.MODE_PRIVATE);
		editor = sp.edit();
	}
	/**
	 * 添加数据
	 * @param key
	 * @param val
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean putData(String key,Object val){
		try {
			if(val instanceof Integer){
				editor.putInt(key,(Integer) val);
			}else if(val instanceof Long){
				editor.putLong(key, (Long) val);
			}else if(val instanceof Boolean){
				editor.putBoolean(key, (Boolean) val);
			}else if(val instanceof String){
				editor.putString(key, (String) val);
			}else if(val instanceof Float){
				editor.putFloat(key, (Float) val);
			}else if(val instanceof Set){
				editor.putStringSet(key, (Set<String>) val);
			}
			editor.commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	};
	/**
	 * 获取数据
	 */
	public static boolean getBoolean(String key){
        return getBoolean(key, false);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }
	public static int getInt(String key){
		return getInt(key,-1);
	}
	public static int getInt(String key,int defaultVal){
		return sp.getInt(key,defaultVal);
	}
	public static String getString(String key){
		return sp.getString(key,null);
	}
	public static long getLong(String key){
		return getLong(key,-1);
	}
	public static long getLong(String key,long defaultValue){
		return sp.getLong(key,defaultValue);
	}
	public static float getFloat(String key){
		return sp.getFloat(key,-1f);
	}
	/**
	 * 清除指定内容
	 */
	public static void removeData(String key){
		editor.remove(key);
		editor.commit();
	}
	/**
	 * 清除所有
	 */
	public static void removeAll(){
		editor.clear();
		editor.commit();
	}
}
