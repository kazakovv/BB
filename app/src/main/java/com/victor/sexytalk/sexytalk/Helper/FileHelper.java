package com.victor.sexytalk.sexytalk.Helper;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.victor.sexytalk.sexytalk.Statics;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileHelper {
	
	public static final String TAG = FileHelper.class.getSimpleName();
	
	//public static final int SHORT_SIDE_TARGET = 500; //1280

	public static byte[] getByteArrayFromFile(Context context, Uri uri) {
		byte[] fileBytes = null;
        InputStream inStream = null;
        ByteArrayOutputStream outStream = null;

        if (uri.getScheme().equals("content")) {
        	try {
        		inStream = context.getContentResolver().openInputStream(uri);
        		outStream = new ByteArrayOutputStream();
            
        		byte[] bytesFromFile = new byte[1024*1024]; // buffer size (1 MB)
        		int bytesRead = inStream.read(bytesFromFile);
        		while (bytesRead != -1) {
        			outStream.write(bytesFromFile, 0, bytesRead);
        			bytesRead = inStream.read(bytesFromFile);
        		}
            
        		fileBytes = outStream.toByteArray();
        	}
	        catch (IOException e) {
	        	Log.e(TAG, e.getMessage());
	        }
	        finally {
	        	try {
	        		inStream.close();
	        		outStream.close();
	        	}
	        	catch (IOException e) { /*( Intentionally blank */ }
	        }
        }
        else {
        	try {
	        	File file = new File(uri.getPath());
	        	FileInputStream fileInput = new FileInputStream(file);
	        	fileBytes = IOUtils.toByteArray(fileInput);
        	}
        	catch (IOException e) {
        		Log.e(TAG, e.getMessage());
        	}
       	}
        
        return fileBytes;
	}
	
	public static byte[] reduceImageForUpload(byte[] imageData, int shortSideTarget) {
		Bitmap bitmap = ImageResizer.resizeImageMaintainAspectRatio(imageData, shortSideTarget);
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
		byte[] reducedData = outputStream.toByteArray();
		try {
			outputStream.close();
		}
		catch (IOException e) {
			// Intentionally blank
		}
		
		return reducedData;
	}

	public static String getFileName(Context context, Uri uri, String fileType) {
		String fileName ="";

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        //TODO: tr da se dobavi random string/number kam imeto na fila

        if (fileType.equals(Statics.TYPE_IMAGE)) {
            fileName="IMG"+timeStamp;
            fileName += ".png";
		}
		else {
			// For video, we want to get the actual file extension
			if (uri.getScheme().equals("content")) {
				// do it using the mime type
				String mimeType = context.getContentResolver().getType(uri);
				int slashIndex = mimeType.indexOf("/");
				String fileExtension = mimeType.substring(slashIndex + 1);
				fileName += "MOV_" + timeStamp + "." + fileExtension;
			}
			else {
				fileName = uri.getLastPathSegment();
			}
		}
		
		return fileName;
	}
    //ot URI vzimame agala, ako tr da se varti image. Izpolzvame agala, za da zavartim veche umalenia image
    public static Bitmap rotateImageIfNeeded(Context context, Uri mMediaUri, Bitmap image) {
        String path = getPath(context, mMediaUri);
        int rotationAngle = 0;
        Bitmap rotatedImage=null;
        try {
            ExifInterface ei = new ExifInterface(path);
            rotationAngle = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (rotationAngle) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotationAngle = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotationAngle = 180;
                    break;
            }
            //zavartame image
            if(rotationAngle !=0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(rotationAngle);
                rotatedImage = Bitmap.createBitmap(image,0,0,image.getWidth(),image.getHeight(),matrix,true);
            } else {
            //ako niama nuzda da se varti, prosto vrashtame sashtata kartinka, koito sme podali kato argument
                rotatedImage = image;
            }

        } catch(Exception e) {
            //TODO tr da napravia neshto tuk
        }



        return rotatedImage;
    }


    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

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
                final String[] selectionArgs = new String[] {
                        split[1]
                };

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

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

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
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}