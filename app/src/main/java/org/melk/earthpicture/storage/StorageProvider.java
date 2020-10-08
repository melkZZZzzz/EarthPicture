package org.melk.earthpicture.storage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class StorageProvider {

	public static String srcName = "source.png";
	public static String backgroundName = "background.png";

	private Context context = null;

	public StorageProvider(Context context) {
		this.context = context;
	}

	protected Date Date(String filename) {
		File file = new File( this.context.getFilesDir(), filename );
		return file.exists() ? new Date(file.lastModified()) : null;
	}
	protected Bitmap Bitmap(String filename) {
		Bitmap bitmap = null;

		try {
			File file = new File( this.context.getFilesDir(), filename );
			InputStream in = new FileInputStream(file);
			bitmap = BitmapFactory.decodeStream(in);
			in.close();
		} catch (NullPointerException e) {
			//Toast.makeText( context, "Can't find file " + filename, Toast.LENGTH_SHORT).show();
			Log.e("Error", "file name must be provided: " + e.getMessage());
		} catch (IOException e) {
			//Toast.makeText( context, "Error accessing " + filename, Toast.LENGTH_SHORT).show();
			Log.e("Error", "Error accessing " + filename +": " + e.getMessage());
		}

		return bitmap;
	}
	protected boolean Save(String filename, Bitmap bmp, Date date) {
		boolean bSaved = false;
		File file = new File( this.context.getFilesDir(), filename );

		try {
			file.createNewFile();

			FileOutputStream out = new FileOutputStream( file );
			bmp.compress( Bitmap.CompressFormat.PNG, 90, out );
			out.close();
			if ( null != date ) {
				file.setLastModified(date.getTime());
			}
			bSaved = true;
		} catch (IOException e) {
			//Toast.makeText( context, "Error saving " + filename, Toast.LENGTH_SHORT).show();
			Log.e("Error", "Error saving " + filename +": " + e.getMessage());
		}

		return bSaved;
	}
	protected boolean Save(String filename, Bitmap bmp) {
		return Save(filename, bmp, null);
	}
	protected boolean Delete(String filename) {
		File file = new File( this.context.getFilesDir(), filename );
		return file.exists() && file.delete();
	}


	public Date SrcDate() {
		return this.Date( this.srcName );
	}
	public Bitmap SrcBitmap() {
		return this.Bitmap( this.srcName );
	}
	public boolean SrcSave(Bitmap bmp, Date date) {
		return this.Save( this.srcName, bmp, date );
	}
	public boolean SrcSave(Bitmap bmp) {
		return this.Save( this.srcName, bmp );
	}
	public boolean SrcDelete() {
		return this.Delete( this.srcName );
	}

	public Date BackgroundDate() {
		return this.Date( this.backgroundName );
	}
	public Bitmap BackgroundBitmap() {
		return this.Bitmap( this.backgroundName );
	}
	public boolean BackgroundSave(Bitmap bmp, Date date) {
		return this.Save( this.backgroundName, bmp, date );
	}
	public boolean BackgroundSave(Bitmap bmp) {
		return this.Save( this.backgroundName, bmp );
	}
	public boolean BackgroundDelete() {
		return this.Delete( this.backgroundName );
	}

}
