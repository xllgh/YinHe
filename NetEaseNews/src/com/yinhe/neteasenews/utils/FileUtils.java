package com.yinhe.neteasenews.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class FileUtils {

	private Context mContext;

	public FileUtils(Context context) {
		mContext = context;
	}

	public static void copyAsset(AssetManager manager, String srcPath,
			String desPath) throws IOException {
		String[] files;
		String lastPath = null;
		byte buffer[]=new byte[1024];
		FileOutputStream fos = null ;
		InputStream in = null;
		if (srcPath == null) {
			return;
		}
        lastPath=lastPath+File.separator+srcPath;		
		File temp = new File(desPath, srcPath);
		Log.e("copyAsset", temp.getAbsolutePath());
		files = manager.list(lastPath);
		if (files.length > 0) {
			if (temp.exists()) {
				temp.delete();
			}
			temp.mkdirs();
			for (String f : files) {
				copyAsset(manager, f, temp.getAbsolutePath());
			}
		} else {
			fos=new FileOutputStream(temp);
			in=manager.open(srcPath);
			while(in.read(buffer, 0, 1024)!=-1){
				fos.write(buffer);
			}
		}
		
		if(fos!=null){
			fos.close();
		}
		if(in!=null){
			in.close();
		}

	}

	public static void copyFile(String srcPath, String desPath) {
		File srcFile = new File(srcPath);
		srcFile = new File(srcPath);
		Log.e("copy", "srcPath:" + srcFile.getAbsolutePath() + "\n desPath:"
				+ desPath);
		File temp;
		byte[] content = new byte[1024];
		FileOutputStream fos = null;
		FileInputStream fis = null;
		if (srcPath != null) {
			temp = new File(desPath, srcFile.getName());
			Log.e("copy", "createFile:" + temp.getAbsolutePath());

			if (!srcFile.isDirectory()) {
				try {
					fos = new FileOutputStream(temp);
					fis = new FileInputStream(srcFile);
					while (fis.read(content, 0, 1024) != -1) {
						fos.write(content);
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						fos.close();
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else {
				temp.mkdirs();
				desPath = desPath + File.separator + srcFile.getName();
				for (File f : srcFile.listFiles()) {
					copyFile(f.getAbsolutePath(), desPath);
				}
			}
		}
	}

	public void copyAsserts(String assertDir, String dir) {
		String files[];
		try {
			files = mContext.getResources().getAssets().list(assertDir);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		File mWorkingPath = new File(dir);
		if (!mWorkingPath.exists()) {
			if (!mWorkingPath.mkdirs()) {
				return;
			}
		}
		for (int i = 0; i < files.length; i++) {
			String fileName = files[i];
			if (!fileName.contains(".")) {
				if (0 == assertDir.length()) {
					copyAsserts(fileName, dir + "/" + fileName);
				} else {
					copyAsserts(assertDir + "/" + fileName, dir + "/"
							+ fileName);
				}
				continue;
			}
			File outFile = new File(mWorkingPath, fileName);
			if (outFile.exists()) {
				outFile.delete();
			}
			InputStream in = null;
			if (0 != assertDir.length()) {
				try {
					in = mContext.getAssets().open(assertDir + "/" + fileName);
					OutputStream out = new FileOutputStream(outFile);
					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					in.close();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
