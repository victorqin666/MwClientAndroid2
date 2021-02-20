package com.work.util;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileUtils {
	/**
	 * sd卡的根目录
	 */
	private static String mSdRootPath = Environment.getExternalStorageDirectory().getPath();
	/**
	 * 手机的缓存根目录
	 */
	private static String mDataRootPath = null;
	/**
	 * 保存Image的目录名
	 */
    public final static String FOLDER_NAME = "/yzmw";

	public final static String IMAGE_NAME = "/cache";

	public FileUtils(Context context){
		mDataRootPath = context.getCacheDir().getPath();
		makeAppDir();
	}
	private String makeAppDir(){
		String path = getStorageDirectory();
		File folderFile = new File(path);
		if(!folderFile.exists()){
			folderFile.mkdir();
		}
		path = path + IMAGE_NAME;
		folderFile = new File(path);
		if(!folderFile.exists()){
			folderFile.mkdir();
		}
		return path;
	}
	public boolean copyFile(String oldPath$Name, String newPath$Name) {
		try {
			File oldFile = new File(oldPath$Name);
			if (!oldFile.exists()) {
				SLog.e("copyFile:  oldFile not exist.");
				return false;
			} else if (!oldFile.isFile()) {
				SLog.e("copyFile:  oldFile not file.");
				return false;
			} else if (!oldFile.canRead()) {
				SLog.e( "copyFile:  oldFile cannot read.");
				return false;
			}

        /* 如果不需要打log，可以使用下面的语句
        if (!oldFile.exists() || !oldFile.isFile() || !oldFile.canRead()) {
            return false;
        }
        */

			FileInputStream fileInputStream = new FileInputStream(oldPath$Name);    //读入原文件
			FileOutputStream fileOutputStream = new FileOutputStream(newPath$Name);
			byte[] buffer = new byte[1024];
			int byteRead;
			while ((byteRead = fileInputStream.read(buffer)) != -1) {
				fileOutputStream.write(buffer, 0, byteRead);
			}
			fileInputStream.close();
			fileOutputStream.flush();
			fileOutputStream.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 获取储存Image的目录
	 * @return
	 */
	public String getStorageDirectory(){
		String localPath = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
				mSdRootPath + FOLDER_NAME : mDataRootPath + FOLDER_NAME;
		File folderFile = new File(localPath);
		if(!folderFile.exists()){
			folderFile.mkdir();
		}
		return localPath;
	}
	/**
	 * 保存Image的方法，有sd卡存储到sd卡，没有就存储到手机目录
	 * @param fileName 
	 * @param bitmap   
	 * @throws IOException
	 */
	public String saveBitmap(String fileName, Bitmap bitmap){
		if(bitmap == null){
			return null;
		}
		String path = getStorageDirectory();
		File folderFile = new File(path);
		if(!folderFile.exists()){
			folderFile.mkdir();
		}
		path = path + IMAGE_NAME;
		folderFile = new File(path);
		if(!folderFile.exists()){
			folderFile.mkdir();
		}
		String filePath = path + File.separator + fileName;
		File file = new File(filePath);
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			bitmap.compress(CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
			return filePath;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


	public String saveCameraBitmap(String imageName,Bitmap bitmap){
		if(bitmap == null){
			return null;
		}
		String imagePath = getCameraFilePath()+"/"+imageName;
		File file = new File(imagePath);
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			bitmap.compress(CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
			return imagePath;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String saveIclauncher(String icName,Bitmap bitmap){
		if(bitmap == null){
			return null;
		}
		if(isFileExists(icName)){
			return getFilePath(icName);
		}
		File file = new File(getFilePath(icName));
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			bitmap.compress(CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
			return file.getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	};

	public String saveFile(String fileName,byte[] bytes){
		return saveFile(getStorageDirectory(),fileName,bytes);
	}

	public String saveImage(String imageName,InputStream inputStream) {
		String imagePath = getCameraFilePath()+"/"+imageName;
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(imagePath);
			byte[] buffer = new byte[1024 * 8];
			int tem;
			while((tem = inputStream.read(buffer))!=-1){
				fileOutputStream.write(buffer,0,tem);
			}
			fileOutputStream.flush();
			return imagePath;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(fileOutputStream!=null){
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(inputStream!=null){
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	};

	public static String getCameraFilePath(){
		String imagePath = (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?mSdRootPath:mDataRootPath)+"/DCIM";
		File file = new File(imagePath);
		if(!file.exists()){
			file.mkdir();
		}
		imagePath += "/Camera";
		file = new File(imagePath);
		if(!file.exists()){
			file.mkdir();
		}
		return imagePath;
	}

	public String saveFile(String dir,String fileName,byte[] bytes){
		BufferedOutputStream bufferedOutputStream = null;
		FileOutputStream outputStream = null;
		File file = new File(dir+"/"+fileName);
		try {
			outputStream = new FileOutputStream(file);
			bufferedOutputStream = new BufferedOutputStream(outputStream);
			bufferedOutputStream.write(bytes);
			bufferedOutputStream.flush();
			return file.getAbsolutePath();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(bufferedOutputStream != null){
				try {
					bufferedOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(outputStream != null){
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	/**
	 * 判断文件是否存在
	 * @param fileName
	 * @return
	 */
	public boolean isFileExists(String fileName){
		return new File(getFilePath(fileName)).exists();
	}

	/**
	 * 获取文件路径
	 */
	public String getFilePath(String fileName){
		return getStorageDirectory() + File.separator + fileName;
	}
	
	/**
	 * 获取文件的大小
	 * @param fileName
	 * @return
	 */
	public long getFileSize(String fileName) {
		return new File(getStorageDirectory() + File.separator + fileName).length();
	}
	
	
	/**
	 * 删除SD卡或者手机的缓存图片和目录
	 */
	public void deleteFile() {
		File dirFile = new File(getStorageDirectory());
		if(! dirFile.exists()){
			return;
		}
		if (dirFile.isDirectory()) {
			String[] children = dirFile.list();
			for (int i = 0; i < children.length; i++) {
				new File(dirFile, children[i]).delete();
			}
		}
		dirFile.delete();
	}

	/**
	 * 删除文件
	 */
	public void deleteFile(String deletePath,String videoPath) {
		File file = new File(deletePath);
		if (file.exists()) {
			File[] files = file.listFiles();
			for (File f : files) {
				if(f.isDirectory()){
					if(f.listFiles().length==0){
						f.delete();
					}else{
						deleteFile(f.getAbsolutePath(),videoPath);
					}
				}else if(!f.getAbsolutePath().equals(videoPath)){
					f.delete();
				}
			}
		}
	}

	/**
	 * Returns the remainder of 'reader' as a string, closing it when done.
	 */
	public static String readFully(Reader reader) throws IOException {
		try {
			StringWriter writer = new StringWriter();
			char[] buffer = new char[1024];
			int count;
			while ((count = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, count);
			}
			return writer.toString();
		} finally {
			reader.close();
		}
	}

	/**
	 * Returns the ASCII characters up to but not including the next "\r\n", or
	 * "\n".
	 *
	 * @throws EOFException if the stream is exhausted before the next newline
	 *                              character.
	 */
	public static String readAsciiLine(InputStream in) throws IOException {
		StringBuilder result = new StringBuilder(80);
		while (true) {
			int c = in.read();
			if (c == -1) {
				throw new EOFException();
			} else if (c == '\n') {
				break;
			}

			result.append((char) c);
		}
		int length = result.length();
		if (length > 0 && result.charAt(length - 1) == '\r') {
			result.setLength(length - 1);
		}
		return result.toString();
	}

	/**
	 * Closes 'closeable', ignoring any checked exceptions. Does nothing if 'closeable' is null.
	 */
	public static void closeQuietly(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (RuntimeException rethrown) {
				throw rethrown;
			} catch (Exception ignored) {
			}
		}
	}

	/**
	 * Try to delete directory in a fast way.
	 */
	public static void deleteDirectoryQuickly(File dir) throws IOException {

		if (!dir.exists()) {
			return;
		}
		final File to = new File(dir.getAbsolutePath() + System.currentTimeMillis());
		dir.renameTo(to);
		if (!dir.exists()) {
			// rebuild
			dir.mkdirs();
		}

		// try to run "rm -r" to remove the whole directory
		if (to.exists()) {
			String deleteCmd = "rm -r " + to;
			Runtime runtime = Runtime.getRuntime();
			try {
				Process process = runtime.exec(deleteCmd);
				process.waitFor();
			} catch (IOException e) {

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (!to.exists()) {
			return;
		}
		deleteDirectoryRecursively(to);
		if (to.exists()) {
			to.delete();
		}
	}

	public static void chmod(String mode, String path) {
		try {
			String command = "chmod " + mode + " " + path;
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec(command);
			process.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	/**
	 * recursively delete
	 *
	 * @param dir
	 * @throws IOException
	 */
	public static void deleteDirectoryRecursively(File dir) throws IOException {
		File[] files = dir.listFiles();
		if (files == null) {
			throw new IllegalArgumentException("not a directory: " + dir);
		}
		for (File file : files) {
			if (file.isDirectory()) {
				deleteDirectoryRecursively(file);
			}
			if (!file.delete()) {
				throw new IOException("failed to delete file: " + file);
			}
		}
	}

	public static void deleteIfExists(File file) throws IOException {
		if (file.exists() && !file.delete()) {
			throw new IOException();
		}
	}

	public static boolean writeString(String filePath, String content) {
		File file = new File(filePath);
		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();

		FileWriter writer = null;
		try {

			writer = new FileWriter(file);
			writer.write(content);

		} catch (IOException e) {
		} finally {
			try {
				if (writer != null) {

					writer.close();
					return true;
				}
			} catch (IOException e) {
			}
		}
		return false;
	}

	public static String readString(String filePath) {
		File file = new File(filePath);
		if (!file.exists())
			return null;

		FileInputStream fileInput = null;
		FileChannel channel = null;
		try {
			fileInput = new FileInputStream(filePath);
			channel = fileInput.getChannel();
			ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());
			channel.read(buffer);

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			byteArrayOutputStream.write(buffer.array());
			return byteArrayOutputStream.toString();
		} catch (Exception e) {
		} finally {
			if (fileInput != null) {
				try {
					fileInput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (channel != null) {
				try {
					channel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	@SuppressLint("NewApi")
	public static String getUriPath(final Context context, final Uri uri){
		if(uri==null){
			return "";
		}
		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return "";
	}
	private static String getDataColumn(Context context, Uri uri, String selection,
										String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				final int column_index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(column_index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}
	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	private static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	private static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	private static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	public static String getString(Context context,int id){
		return context.getResources().getString(id);
	}
	public static long getUsableSpace(File path) {
		if (path == null) {
			return -1;
		}
		return path.getUsableSpace();
	}
	/**
	 * external: "/storage/emulated/0/Android/data/in.srain.sample/files"
	 * internal: "/data/data/in.srain.sample/files"
	 */
	public static String wantFilesPath(Context context, boolean externalStorageFirst) {
		String path;
		if (externalStorageFirst && isSDCardEnable()) {
			path = Environment.getExternalStorageDirectory().getAbsolutePath();
		} else {
			path = context.getFilesDir().getAbsolutePath();
		}
		return path;
	}
	/**
	 * 获取设备SD卡是否可用
	 *
	 * @return true : 可用<br>false : 不可用
	 */
	public static boolean isSDCardEnable() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}
}
