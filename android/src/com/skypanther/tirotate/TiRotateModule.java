/**
 * Android rotation module for Titanium
 * Copyright © 2017, Tim Poulsen, Skypanther Studios Inc.
 * MIT license, see the LICENSE file for full license text
 */
package com.skypanther.tirotate;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiConfig;

import org.appcelerator.titanium.TiApplication;

import java.io.*;
import android.media.ExifInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

@Kroll.module(name="TiRotate", id="com.skypanther.tirotate")
public class TiRotateModule extends KrollModule
{

	// Standard Debugging variables
	private static final String LCAT = "TiRotateModule";
	private static final boolean DBG = TiConfig.LOGD;

	// Modules constants
	@Kroll.constant public static final int DEGREES_PORTRAIT = 0;
	@Kroll.constant public static final int DEGREES_LANDSCAPE_RIGHT = 90;
	@Kroll.constant public static final int DEGREES_UPSIDE_PORTRAIT = 180;
	@Kroll.constant public static final int DEGREES_LANDSCAPE_LEFT = 270;

	@Kroll.constant public static final String PORTRAIT = "portrait";
	@Kroll.constant public static final String LANDSCAPE_RIGHT = "landscape_right";
	@Kroll.constant public static final String UPSIDE_PORTRAIT = "upside_portrait";
	@Kroll.constant public static final String LANDSCAPE_LEFT = "landscape_left";

	@Kroll.constant public static final int ORIENTATION_NORMAL = ExifInterface.ORIENTATION_NORMAL;
	@Kroll.constant public static final int ORIENTATION_ROTATE_90 = ExifInterface.ORIENTATION_ROTATE_90;
	@Kroll.constant public static final int ORIENTATION_ROTATE_180 = ExifInterface.ORIENTATION_ROTATE_180;
	@Kroll.constant public static final int ORIENTATION_ROTATE_270 = ExifInterface.ORIENTATION_ROTATE_270;
	@Kroll.constant public static final int ORIENTATION_UNDEFINED = ExifInterface.ORIENTATION_UNDEFINED;

	public TiRotateModule()
	{
		super();
	}

	@Kroll.onAppCreate
	public static void onAppCreate(TiApplication app)
	{
		Log.d(LCAT, "inside onAppCreate");
		// put module init code that needs to run when the application is created
	}

	/**
	 * Return the EXIF orientation tag value, in degrees
	 *
	 * @param path (String) -- file path in Titanium file://... format
	 * @return (int) Degrees of rotation
	 */
	@Kroll.method
	public int getExifOrientationDegrees(String path) {
		ExifInterface exif;
		try {
			exif = new ExifInterface(convertPath(path));
		} catch (IOException e) {
			Log.e(LCAT, e.getMessage());
			return DEGREES_PORTRAIT;
		}

		int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);

		switch (exifOrientation) {
			case ExifInterface.ORIENTATION_NORMAL:
				return DEGREES_PORTRAIT;
			case ExifInterface.ORIENTATION_ROTATE_90:
				return DEGREES_LANDSCAPE_RIGHT;
			case ExifInterface.ORIENTATION_ROTATE_180:
				return DEGREES_UPSIDE_PORTRAIT;
			case ExifInterface.ORIENTATION_ROTATE_270:
				return DEGREES_LANDSCAPE_LEFT;
			default:
				return DEGREES_PORTRAIT;
		}
	}

	/**
	 * Return the EXIF orientation tag value
	 *
	 * @param path (String) -- file path in Titanium file://... format
	 * @return (String) - EXIF orientation as a plain English string (e.g. "portrait")
	 */
	@Kroll.method
	public String getExifOrientation(String path) {
		ExifInterface exif;
		try {
			exif = new ExifInterface(convertPath(path));
		} catch (IOException e) {
			Log.e(LCAT, e.getMessage());
			return PORTRAIT;
		}

		int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);

		switch (exifOrientation) {
			case ExifInterface.ORIENTATION_NORMAL:
				return PORTRAIT;
			case ExifInterface.ORIENTATION_ROTATE_90:
				return LANDSCAPE_RIGHT;
			case ExifInterface.ORIENTATION_ROTATE_180:
				return UPSIDE_PORTRAIT;
			case ExifInterface.ORIENTATION_ROTATE_270:
				return LANDSCAPE_LEFT;
			default:
				return PORTRAIT;
		}
	}

	/**
	 * Set the image's EXIF orientation tag
	 *
	 * @param path (String) -- file path in Titanium file://... format
	 * @param targetOrientation (Object) -- desired orientation as either a string or int, e.g. moduleInstance.PORTRAIT
	 */
	@Kroll.method
	public void setExifOrientation(String path, Object targetOrientation) {
		int orientation = getOrientationIntValue(targetOrientation);
		String filePath = convertPath(path);
		try {
			ExifInterface exif = new ExifInterface(filePath);
			exif.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(orientation));
			exif.saveAttributes();
		} catch (IOException e) {
			Log.e(LCAT, e.getMessage());
		}
	}

	/**
	 * Rotate the image to specified orientation and update its exif tag
	 *
	 * @param path (String) -- file path in Titanium file://... format
	 * @param targetOrientation (String) -- desired orientation, e.g. moduleInstance.PORTRAIT
	 * @return true on success
	 */
	@Kroll.method
	public Boolean rotate(String path, String targetOrientation) {
		try
		{
			String filePath = convertPath(path);
			// Determine the orientation
			int detectedOrientation = getExifOrientationDegrees(filePath);
			Log.i(LCAT, "DETECTED ORIENTATION IS: " + String.valueOf(detectedOrientation));
			Log.i(LCAT, "TARGET ORIENTATION IS: >" + targetOrientation + "<");

			int rotation = 0;
			int desiredOrientation = getOrientationIntValue(targetOrientation);

			//TODO: Rework this as a switch of the desiredOrientation int value

			switch (desiredOrientation) {
				case ORIENTATION_NORMAL:
					if (detectedOrientation == 0) {
						rotation = 0;
					} else if (detectedOrientation == 90) {
						rotation = 90;
					} else if (detectedOrientation == 180) {
						rotation = 180;
					} else if (detectedOrientation == 270) {
						rotation = 270;
					}
					break;
				case ORIENTATION_ROTATE_90:
					if (detectedOrientation == 0) {
						rotation = 90;
					} else if (detectedOrientation == 90) {
						rotation = 0;
					} else if (detectedOrientation == 180) {
						rotation = 270;
					} else if (detectedOrientation == 270) {
						rotation = 180;
					}
					break;
				case ORIENTATION_ROTATE_180:
					if (detectedOrientation == 90) {
						rotation = 270;
					} else {
						rotation = 180 - detectedOrientation;
					}
					break;
				case ORIENTATION_ROTATE_270:
					rotation = 270 - detectedOrientation;
					break;
				default:
					rotation = 0;
					break;
			}
			if (rotation == 0 || rotation == 360) {
				// no sense rotating, just return
				Log.i(LCAT, "ALREADY IN CORRECT ORIENTATION, RETURNING...");
				return true;
			}
			if (rotation > 360) {
				rotation = rotation - 360;
			}
			Log.i(LCAT, "SPECIFIED ROTATION IS: " + String.valueOf(rotation));
			doRotate(filePath, rotation);
			setExifOrientation(filePath, targetOrientation);
			return true;
		} catch (Exception e) {
			Log.e(LCAT, e.getMessage());
			for (StackTraceElement elm : e.getStackTrace()) {
				Log.e(LCAT, elm.toString());
			}
			return false;
		}
	}

	// returns the orientation integer value based on the string or int value supplied
	private int getOrientationIntValue(Object targetOrientation) {
		int orientation = 0;
		if (targetOrientation instanceof String) {
			if (targetOrientation.equals(PORTRAIT)) {
				orientation = ExifInterface.ORIENTATION_NORMAL;
			} else if (targetOrientation.equals(LANDSCAPE_RIGHT)) {
				orientation = ExifInterface.ORIENTATION_ROTATE_90;
			} else if (targetOrientation.equals(UPSIDE_PORTRAIT)) {
				orientation = ExifInterface.ORIENTATION_ROTATE_180;
			} else if (targetOrientation.equals(LANDSCAPE_LEFT)) {
				orientation = ExifInterface.ORIENTATION_ROTATE_270;
			} else {
				orientation = ExifInterface.ORIENTATION_NORMAL;
			}
		} else if (targetOrientation instanceof Integer) {
			int target = (Integer) targetOrientation;
			switch (target) {
				case ORIENTATION_NORMAL:
				case DEGREES_PORTRAIT:
					orientation = ExifInterface.ORIENTATION_NORMAL;
					break;
				case ORIENTATION_ROTATE_90:
				case DEGREES_LANDSCAPE_RIGHT:
					orientation = ExifInterface.ORIENTATION_ROTATE_90;
					break;
				case ORIENTATION_ROTATE_180:
				case DEGREES_UPSIDE_PORTRAIT:
					orientation = ExifInterface.ORIENTATION_ROTATE_180;
					break;
				case ORIENTATION_ROTATE_270:
				case DEGREES_LANDSCAPE_LEFT:
					orientation = ExifInterface.ORIENTATION_ROTATE_270;
					break;
				default:
					orientation = ExifInterface.ORIENTATION_NORMAL;
					break;
			}
		}
		return orientation;
	}

	// does the actual rotation, called by rotate()
	private void doRotate (String filePath, int rotation) throws IOException {
		try {
			// Read the file path into a File
			File imageFile = new File(filePath);

			// Decode once to determine size
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = false;

			Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(imageFile), null, opts);

			// Create the appropriate Matrix to resize and rotate
			Matrix matrix = new Matrix();
			matrix.postRotate(rotation);

			Bitmap image = Bitmap.createBitmap(bitmap, 0, 0, (int) bitmap.getWidth(), (int) bitmap.getHeight(), matrix, true);

			// Delete old image
			imageFile.delete();

			// Create new image
			imageFile.createNewFile();

			FileOutputStream fOut = new FileOutputStream(imageFile);
			//// TODO: 12/20/16 remove hard-coded JPG quality of 80%
			image.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
			fOut.flush();
			fOut.close();
		} catch (Exception e) {
			Log.e(LCAT, e.getMessage());
		}
	}

	// convert a Titanium format file path (which contains file:// and perhaps other stuff) to
	// something usuable in this native module
	private String convertPath(String path) {
		Log.i(LCAT, "Open FileInputStream for path: " + path);

		if (path.startsWith("file:/") || path.startsWith("content://") || path.startsWith("appdata://") || path.startsWith("appdata-private://")) {
			path = path.replaceAll("file://", "");
			path = path.replaceAll("file:/", "");
			path = path.replaceAll("content://", "");
			path = path.replaceAll("appdata:///?", "/mnt/sdcard/" + TiApplication.getInstance().getPackageName() + "/");
			path = path.replaceAll("appdata-private:///?", "/data/data/" + TiApplication.getInstance().getPackageName() + "/app_appdata/");
			path = path.replaceAll("//", "/");
			Log.i(LCAT, "Converted path to: " + path);
		}
		return path;
	}

}

