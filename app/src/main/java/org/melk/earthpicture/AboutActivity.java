package org.melk.earthpicture;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import org.melk.earthpicture.helpers.SharedPreferencesHelpers;
import org.melk.earthpicture.storage.StorageProvider;
import org.melk.earthpicture.work.EarthPictureWorkManager;
import org.melk.earthpicture.worker.LiveWallpaperContinuationWorker;
import org.melk.earthpicture.worker.WallpaperContinuationWorker;

import java.util.concurrent.TimeUnit;

public class AboutActivity extends AppCompatActivity {

	private static final String help_html =	"" +
										"<h1 id=\"earth-picture\">Earth Picture</h1>\n" +
										"<blockquote>\n" +
										"<p><em>Users can find earth pictures <strong>useful</strong>, or just <strong>beautiful</strong></em>.</p>\n" +
										"</blockquote>\n" +
										"<hr>\n" +
										"<h2 id=\"description\">Description</h2>\n" +
										"<p>Earth Picture is a java application for android powered devices.</p>\n" +
										"<p>The application provide pictures from <a href=\"https://www.eumetsat.int\" title=\"EUMETSAT website\">EUropean METeo SATellites</a>.\n" +
										"The application will download the most recent picture from the selected satellite source, allowing to:</p>\n" +
										"<ul>\n" +
										"<li>display the picture in a ImageView component,</li>\n" +
										"<li>zoom in/out the picture,</li>\n" +
										"<li>crop the picture,</li>\n" +
										"<li>apply the picture as device wallpaper.</li>\n" +
										"</ul>\n" +
										"<hr>\n" +
										"<h2 id=\"characteristics\">Characteristics</h2>\n" +
										"<h3 id=\"image-preview\">Image preview</h3>\n" +
										"<h4 id=\"actions-menu\">Actions menu</h4>\n" +
										"<ul>\n" +
										"<li><strong>Sync</strong>: Download the latest image from the selected source.</li>\n" +
										"<li><strong>Crop</strong>: Crop the displayed image on ImageView borders.</li>\n" +
										"<li><strong>Set as Wallpaper</strong>: Apply the image as device wallpaper.</li>\n" +
										"<li><strong>Set as live Wallpaper</strong>:  Apply the image source as device live wallpaper.</li>\n" +
										"</ul>\n" +
										"<h4 id=\"image-preview\">Image preview</h4>\n" +
										"<p>Once an image source is selected, the ImageView component displays the most up to date picture from <a href=\"https://www.eumetsat.int\" title=\"EUMETSAT website\">EUropean METeo SATellites</a>.\n" +
										"The image can be scrolled and pinched to zoom on users prefered area.</p>\n" +
										"<h3 id=\"settings\">Settings</h3>\n" +
										"<h4 id=\"-a-name-source_selection-a-source-selection\"><a name=\"Source_selection\"></a>Source selection</h4>\n" +
										"<p>Two choices are to be made when selecting a source:</p>\n" +
										"<ol>\n" +
										"<li><p>Satellite location</p>\n" +
										"<p>Pictures are all taken from 0° latitude (above earth's equator). </p>\n" +
										"<p>0° or 45° longitudes can be selected in order to get pictures respectively centered above metropolitan France or above La Réunion.</p>\n" +
										"</li>\n" +
										"<li><p>Image processing type</p>\n" +
										"<p>EUMETSAT provide several sensors processing traditionally used for meteorological data analysis and previsions.</p>\n" +
										"<p>Users select one resulting image as a source:</p>\n" +
										"<ul>\n" +
										"<li>Atmospheric Motion Vectors,</li>\n" +
										"<li>Fire,</li>\n" +
										"<li>Precipitation rate at ground,</li>\n" +
										"<li>Infrared,</li>\n" +
										"<li>Multi-Sensor Precipitation Estimate,</li>\n" +
										"<li>Airmass,</li>\n" +
										"<li>Ash,</li>\n" +
										"<li>Convection,</li>\n" +
										"<li>Dust,</li>\n" +
										"<li>EView,</li>\n" +
										"<li>Fog,</li>\n" +
										"<li>Microphysics,</li>\n" +
										"<li>Natural Colour,</li>\n" +
										"<li>Solar Day,</li>\n" +
										"<li>Tropical Airmass,</li>\n" +
										"<li>Water Vapor.</li>\n" +
										"</ul>\n" +
										"</li>\n" +
										"</ol>\n" +
										"<h4 id=\"-a-name-synchronization_mode-a-synchronization-mode\"><a name=\"Synchronization_mode\"></a>Synchronization mode</h4>\n" +
										"<p>This option controls wallpaper refresh mode.</p>\n" +
										"<table>\n" +
										"<thead>\n" +
										"<tr>\n" +
										"<th style=\"text-align:center\"><em>Mode</em></th>\n" +
										"<th><em>Description</em></th>\n" +
										"</tr>\n" +
										"</thead>\n" +
										"<tbody>\n" +
										"<tr>\n" +
										"<td style=\"text-align:center\"><strong>Off</strong></td>\n" +
										"<td>User can apply the previewed image as wallpaper without further synchronization.</td>\n" +
										"</tr>\n" +
										"<tr>\n" +
										"<td style=\"text-align:center\"><strong>Wallpaper</strong></td>\n" +
										"<td>The most up to date source is applied as basic wallpaper. This mode uses background processing and may interfere with android power management and battery saving.</td>\n" +
										"</tr>\n" +
										"<tr>\n" +
										"<td style=\"text-align:center\"><strong>Live Wallpaper</strong></td>\n" +
										"<td>The most up to date source is applied as live wallpaper.</td>\n" +
										"</tr>\n" +
										"</tbody>\n" +
										"</table>\n" +
										"<h4 id=\"-a-name-refresh_rate-a-refresh-rate\"><a name=\"Refresh_rate\"></a>Refresh rate</h4>\n" +
										"<p>This option controls the wallpaper synchronization period.\n" +
										"Users can choose between 15mn, 30mn, 1h, 6h, 12h or 24h.</p>\n" +
										"<h4 id=\"-a-name-remember_last_crop_area-a-remember-last-crop-area\"><a name=\"Remember_last_crop_area\"></a>Remember last crop area</h4>\n" +
										"<p>This option controls wether the wallpaper synchronization shall apply the same crop for futures wallpapers.</p>\n";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Intent intent = getIntent();
		//if (intent != null && intent.hasExtra("help") ){
		//}

		setContentView(R.layout.about_activity);
		WebView webView = (WebView) findViewById( R.id.aboutView );
		webView.loadData(help_html, "text/html; charset=UTF-8", null);

		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

}