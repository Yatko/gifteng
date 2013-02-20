package com.venefica.module.listings.post;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ViewSwitcher.ViewFactory;

import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.venefica.module.listings.GalleryImageAdapter;
import com.venefica.module.listings.ListingDetailsResultWrapper;
import com.venefica.module.listings.browse.BrowseCategoriesActivity;
import com.venefica.module.main.R;
import com.venefica.module.main.VeneficaMapActivity;
import com.venefica.module.network.WSAction;
import com.venefica.module.utils.CameraPreview;
import com.venefica.module.utils.InputFieldValidator;
import com.venefica.module.utils.Utility;
import com.venefica.services.AdDto;
import com.venefica.services.ImageDto;
import com.venefica.utils.Constants;
import com.venefica.utils.VeneficaApplication;

/**
 * @author avinash 
 * Activity to post listings/adds
 */
public class PostListingActivity extends VeneficaMapActivity implements LocationListener, OnClickListener, ViewFactory {
	/**
	 * main layout
	 */
	private LinearLayout layMain;
	/**
	 * images layout for step1 and step2
	 */
	private RelativeLayout layImagesView;
	/**
	 * Controls(buttons) layouts
	 */
	private LinearLayout layControlsStepOne, layControlsStepTwo;
	/**
	 * layout inflater
	 */
	private LayoutInflater infleter;
	/**
	 * surface view for camera in step 1
	 */
	private FrameLayout cameraPreview;
	private Camera camera;
	private CameraPreview camPreview;
	/**
	 * ImageSwitcher for preview of images in step 2
	 */
	private ImageSwitcher imgSwitcher;
	/**
	 * Gallery view
	 */
	private Gallery gallery;
	/**
	 * Images taken
	 */
	private ArrayList<Bitmap> images;
	/**
	 * Adapter for gallery
	 */
	private GalleryImageAdapter galImageAdapter;
	/**
	 * buttons for step one
	 */
	private ImageButton imgBtnPickGallery, imgBtnPickCamera, imgBtnNextToStep2;
	/**
	 * buttons for step two
	 */
	private ImageButton imgBtnBackToStepOne, imgBtnDelete, imgBtnContrast,
			imgBtnCrop, imgBtnNextToStepThree;
	/**
	 * is step two 
	 */
	private boolean isStepTwo = false;
	
	
	/**
	 * Field validator
	 */
	private InputFieldValidator vaildator;
	/**
	 * Constants
	 */
	public static final int REQ_SELECT_CATEGORY = 1001;
	private static final int REQ_GET_IMAGE = 1002;
	private static final int REQ_IMAGE_CROP = 1003;
	private static final int REQ_GET_CAMERA_IMAGE = 1004;
	/**
	 * Constants to identify dialogs
	 */
	private final int D_PROGRESS = 1, D_ERROR = 2, D_DATE = 3;	
	/**
	 * Current error code.
	 */
	private int ERROR_CODE;
	/**
	 * Activity MODE
	 */
	
	public static final int ACT_MODE_POST_LISTING = 3001;
	public static final int ACT_MODE_UPDATE_LISTING = 3002;
	public static final int ACT_MODE_GET_LISTING = 3003;
	public static final int ACT_MODE_PROCESS_BITMAP = 3004;
	private static int CURRENT_MODE = ACT_MODE_POST_LISTING;
	
	protected static final int ERROR_DATE_VALIDATION = 22;
	
	
	/**
	 * Edit fields to collect listing data
	 */
	private EditText edtTitle, edtSubTitle, edtDescription,
			edtCondition, edtPrice, edtZip, edtState, edtCounty, edtCity, edtArea,
			edtLatitude, edtLongitude;
	/**
	 * Text fields to collect listing data
	 */
	/*private TextView txtTitle, txtSubTitle, txtCategory, txtDescription,
			txtCondition, txtPrice, txtZip, txtState, txtCounty, txtCity, txtArea,
			txtLatitude, txtLongitude;*/
	private Spinner spinCurrency;
	/**
	 * Gallery to images
	 *//*
	private Gallery gallery;
	*//**
	 * Images
	 *//*
	private List<Bitmap> drawables;
	*//**
	 * Adapter for gallery
	 *//*
	private GalleryImageAdapter galImageAdapter;
	*/
	private Uri selectedImageUri;

	/**
	 * Buttons
	 */
	private Button btnSelCategory, btnAddPhotos, btnPost, btnExpiary;
	/**
	 * Map button
	 */
	private ImageButton btnLocateOnMap;
	/**
	 * Calendar for current date
	 */
	private Calendar calendar = Calendar.getInstance();
	/**
	 * Map
	 */
	private MapController mapController;
	private MapView mapView;
	private LocationManager locationManager;
	private Overlay itemizedoverlay;
	private boolean showMap = true;
	private String locProvider;
	private Location location;
	/**
	 * Selected category
	 */
	private long categoryId;
	private String categoryName;
	/**
	 * Selected listing
	 */
	private long selectedListingId;
	private WSAction wsAction;	
	private AdDto selectedListing;
	private ImageDto image;
	private List<ImageDto> imageDtos;
	
	/**
	 * Low resolution flag
	 */
	private boolean isLowResolution = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(com.actionbarsherlock.R.style.Theme_Sherlock_Light_DarkActionBar);
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setCustomView(R.layout.view_actionbar_title);
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.background_transperent_black));
		setProgressBarIndeterminateVisibility(false);
		
		setContentView(R.layout.activity_post_listing);
		//set mode
		CURRENT_MODE = getIntent().getIntExtra("act_mode", ACT_MODE_POST_LISTING);
		//location manager 
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// layout inflater
		infleter = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		// main layout
		layMain = (LinearLayout) findViewById(R.id.layPostlistingMain);
		// images layout
		layImagesView = (RelativeLayout) infleter.inflate(R.layout.view_post_listing_images, null);
		layMain.addView(layImagesView);
		// Controls layout
		layControlsStepOne = (LinearLayout) layImagesView.findViewById(R.id.layPostListingStep1Controls);
		imgBtnPickGallery = (ImageButton) layImagesView.findViewById(R.id.imgBtnPostListingPickGallery);
		imgBtnPickGallery.setOnClickListener(this);
		imgBtnPickCamera = (ImageButton) layImagesView.findViewById(R.id.imgBtnPostListingPickCamera);
		imgBtnPickCamera.setOnClickListener(this);
		imgBtnNextToStep2 = (ImageButton) layImagesView.findViewById(R.id.imgBtnPostListingNextToStep2);
		imgBtnNextToStep2.setOnClickListener(this);

		layControlsStepTwo = (LinearLayout) layImagesView.findViewById(R.id.layPostListingStep2Controls);
		imgBtnBackToStepOne = (ImageButton) layImagesView.findViewById(R.id.imgBtnPostListingBackToStep1);
		imgBtnBackToStepOne.setOnClickListener(this);
		imgBtnDelete = (ImageButton) layImagesView.findViewById(R.id.imgBtnPostListingDelete);
		imgBtnDelete.setOnClickListener(this);
		imgBtnContrast = (ImageButton) layImagesView.findViewById(R.id.imgBtnPostListingContrast);
		imgBtnContrast.setOnClickListener(this);
		imgBtnCrop = (ImageButton) layImagesView.findViewById(R.id.imgBtnPostListingCrop);
		imgBtnCrop.setOnClickListener(this);
		imgBtnNextToStepThree = (ImageButton) layImagesView.findViewById(R.id.imgBtnPostListingNextToStep3);
		imgBtnNextToStepThree.setOnClickListener(this);

		cameraPreview = (FrameLayout) layImagesView.findViewById(R.id.layViewPostListingCameraPreview);
		imgSwitcher = (ImageSwitcher) layImagesView.findViewById(R.id.imgSwitcherActPostListing);
		imgSwitcher.setFactory(this);
		gallery = (Gallery) layImagesView.findViewById(R.id.galleryActPostListing);
		gallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				if (isStepTwo && images != null && images.size() > 0) {
					Drawable dr = new BitmapDrawable(images.get(position));
					imgSwitcher.setImageDrawable(dr);
				}
			}
		});
		setStepOneUIVisiblity(ViewGroup.VISIBLE);
		
		camPreview = new CameraPreview(this);
		cameraPreview.addView(camPreview);
		
		//images
		images = new ArrayList<Bitmap>();
		galImageAdapter = new GalleryImageAdapter(this, null, images, true, true);
		gallery.setAdapter(galImageAdapter);
		/*// Gallery
		gallery = (Gallery) findViewById(R.id.galleryActPostListingPhotos);
		drawables = new ArrayList<Bitmap>();
		galImageAdapter = new GalleryImageAdapter(this, null, drawables, true, true);
		gallery.setAdapter(galImageAdapter);

		// Map
		mapView = (MapView) findViewById(R.id.mapviewActPostListingMapLocate);
		mapView.setBuiltInZoomControls(true);
		// satellite or 2d mode
		mapView.setSatellite(true);
		mapController = mapView.getController();
		mapController.setZoom(14); // Zoom 1 is world view
		btnLocateOnMap = (ImageButton) findViewById(R.id.btnActPostListingLocateAddress);
		btnLocateOnMap.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (showMap) {
					mapView.setVisibility(MapView.VISIBLE);
					showMap = false;
				} else {
					mapView.setVisibility(MapView.GONE);
					showMap = true;
				}

			}
		});
		// Add photos
		btnAddPhotos = (Button) findViewById(R.id.btnActPostListingAddPhotos);
		btnAddPhotos.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				pickImage();
			}
		});
		
		btnPost = (Button) findViewById(R.id.btnActPostListingPost);
		btnPost.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				if (validateFields()) {
					if(WSAction.isNetworkConnected(PostListingActivity.this)){
						if(CURRENT_MODE == ACT_MODE_UPDATE_LISTING){
							new PostListingTask().execute(ACT_MODE_UPDATE_LISTING);
						}else if (CURRENT_MODE == ACT_MODE_POST_LISTING) {
							new PostListingTask().execute(ACT_MODE_POST_LISTING);
						}
					} else {
						ERROR_CODE = Constants.ERROR_NETWORK_UNAVAILABLE;
						showDialog(D_ERROR);
					}
				}
			}
		});
		btnSelCategory = (Button) findViewById(R.id.btnActPostListingCategory);
		btnSelCategory.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				Intent selCatIntent = new Intent(PostListingActivity.this, BrowseCategoriesActivity.class);
				selCatIntent.putExtra("act_mode", BrowseCategoriesActivity.ACT_MODE_GET_CATEGORY);
				startActivityForResult(selCatIntent, REQ_SELECT_CATEGORY);
			}
		});
		btnExpiary = (Button) findViewById(R.id.btnActPostListingAddExpiary);
		btnExpiary.setText(Utility.convertShortDateToString(new Date()));
		btnExpiary.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				showDialog(D_DATE);
			}
		});
		//data
		edtTitle = (EditText) findViewById(R.id.edtActPostListingListTitle);
		edtSubTitle = (EditText) findViewById(R.id.edtActPostListingSubTitle);
		edtDescription = (EditText) findViewById(R.id.edtActPostListingDescription);
		edtCondition = (EditText) findViewById(R.id.edtActPostListingCondition);
		edtPrice = (EditText) findViewById(R.id.edtActPostListingPrice);
		edtZip = (EditText) findViewById(R.id.edtActPostListingZip);
		edtState = (EditText) findViewById(R.id.edtActPostListingState);
		edtCounty = (EditText) findViewById(R.id.edtActPostListingCounty);
		edtCity = (EditText) findViewById(R.id.edtActPostListingCity);
		edtArea = (EditText) findViewById(R.id.edtActPostListingArea);
		edtLatitude =  (EditText) findViewById(R.id.edtActPostListingLatitude);
		edtLongitude = (EditText) findViewById(R.id.edtActPostListingLongitude);
		
		txtTitle = (TextView) findViewById(R.id.txtActPostListingListTitle);
		txtSubTitle = (TextView) findViewById(R.id.txtActPostListingSubTitle);
		txtCategory = (TextView) findViewById(R.id.txtActPostListingCategory);
		txtDescription = (TextView) findViewById(R.id.txtActPostListingDescription);
		txtCondition = (TextView) findViewById(R.id.txtActPostListingCondition);
		txtPrice = (TextView) findViewById(R.id.txtActPostListingPrice);
		txtZip = (TextView) findViewById(R.id.txtActPostListingZip);
		txtState = (TextView) findViewById(R.id.txtActPostListingState);
		txtCounty = (TextView) findViewById(R.id.txtActPostListingCounty);
		txtCity = (TextView) findViewById(R.id.txtActPostListingCity);
		txtArea = (TextView) findViewById(R.id.txtActPostListingArea);
		txtLatitude = (TextView) findViewById(R.id.txtActPostListingLatitude);
		txtLongitude = (TextView) findViewById(R.id.txtActPostListingLongitude);
		
		if (CURRENT_MODE == ACT_MODE_UPDATE_LISTING) {
			selectedListingId = getIntent().getLongExtra("ad_id", 0);
			btnPost.setText(getResources().getString(R.string.label_update));
			new PostListingTask().execute(ACT_MODE_GET_LISTING);
		}*/
	}

	@Override
	protected void onStart() {
		super.onStart();
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setCostAllowed(false);
		locProvider = locationManager.getBestProvider(criteria, true);		

	    if (locProvider != null) {
	    	Log.d("PostListingActivity :", locProvider);
	    	location = locationManager.getLastKnownLocation(locProvider);
			onLocationChanged(location);
	    } else {
	    	ERROR_CODE = Constants.ERROR_ENABLE_LOCATION_PROVIDER;
	    	showDialog(D_ERROR);	        
	    }	    
	   
	}

	/* Request updates at startup */
	@Override
	protected void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(locProvider, 400, 1, this);
		if (Utility.checkCameraHardware(this)) {
			camera = Utility.getCameraInstance();
			camPreview.setCamera(camera);
		}
	}

	/* Remove the locationlistener updates when Activity is paused */
	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
		releaseCamera();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return true;
	}
	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.imgBtnPostListingPickGallery) {
			
		} else if (view.getId() == R.id.imgBtnPostListingPickCamera){
			camera.takePicture(null, null, pictureCallback);
		} else if (view.getId() == R.id.imgBtnPostListingNextToStep2){
			setStepOneUIVisiblity(ViewGroup.GONE);
			setStepTwoUIVisiblity(ViewGroup.VISIBLE); 
			isStepTwo = true;
		} else if (view.getId() == R.id.imgBtnPostListingBackToStep1){
			setStepOneUIVisiblity(ViewGroup.VISIBLE);
			setStepTwoUIVisiblity(ViewGroup.GONE);
			isStepTwo = false;
		} else if (view.getId() == R.id.imgBtnPostListingDelete){
			
		} else if (view.getId() == R.id.imgBtnPostListingContrast){
			
		} else if (view.getId() == R.id.imgBtnPostListingCrop){
			
		} else if (view.getId() == R.id.imgBtnPostListingNextToStep3){
			
		}
	}
	/**
	 * helper method to show hide step one UI
	 * @param visibility
	 */
	private void setStepOneUIVisiblity(int visibility) {
		layControlsStepOne.setVisibility(visibility);
		cameraPreview.setVisibility(visibility);		
	}

	/**
	 * helper method to show hide step one UI
	 * @param visibility
	 */
	private void setStepTwoUIVisiblity(int visibility) {
		layControlsStepTwo.setVisibility(visibility);
		imgSwitcher.setVisibility(visibility);
	}
	
	/**
	 * release the camera for other applications
	 */
	private void releaseCamera(){
        if (camera != null){
            camera.release();
            camera = null;
        }
    }
	
	private PictureCallback pictureCallback = new PictureCallback() {
		
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
			
			images.add(Bitmap.createScaledBitmap(image, Constants.IMAGE_THUMBNAILS_WIDTH, Constants.IMAGE_THUMBNAILS_HEIGHT, false));
			galImageAdapter.notifyDataSetChanged();
			image.recycle();
			camera.startPreview();
		}
	};	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQ_SELECT_CATEGORY) {
				 categoryId = data.getLongExtra("cat_id", -1);
				 categoryName = data.getStringExtra("category_name").trim().
						 equalsIgnoreCase("")?getResources().getString(R.string.code_category_other): data.getStringExtra("category_name");
				if (categoryName != null) {
					btnSelCategory.setText(categoryName);
				}
				
			} else if (requestCode == REQ_GET_IMAGE){
				final Bundle extras = data.getExtras();
	            if (extras != null) {            		
	    			selectedImageUri = data.getData();
	    			performCrop();
	            }			           
	        } else if (requestCode == REQ_IMAGE_CROP) {
	        	new PostListingTask().execute(ACT_MODE_PROCESS_BITMAP);
			}
		}
	}
	
	/**
	 * Method to resize bitmap with specified size
	 * @return bitmap
	 */
	private Bitmap resizeBitmap() {
		Rect rect = new Rect(0, 0, Constants.IMAGE_MAX_SIZE_X, Constants.IMAGE_MAX_SIZE_Y);
	    BitmapFactory.Options opts = new BitmapFactory.Options();
	    opts.inInputShareable = false;
	    opts.inSampleSize = 1;
	    opts.inScaled = false;
	    opts.inDither = false;
	    opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
	    Bitmap bm = null;
		try {
			bm = BitmapFactory.decodeStream(getContentResolver().openInputStream(Utility.getTempUri()));
			if (Constants.IMAGE_MAX_SIZE_X > bm.getWidth() && Constants.IMAGE_MAX_SIZE_Y > bm.getHeight()) {
				isLowResolution = true;				
				bm.recycle();
				return null;
			}else{
				isLowResolution = false;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	    int width = bm.getWidth();
	    int height = bm.getHeight();
	    float scaleWidth = ((float) Constants.IMAGE_MAX_SIZE_X) / width;
	    float scaleHeight = ((float) Constants.IMAGE_MAX_SIZE_Y) / height;

	    // create a matrix for the manipulation
	    Matrix matrix = new Matrix();

	    // resize the bit map
	    matrix.postScale(scaleWidth, scaleHeight);

	    // recreate the new Bitmap
	    return  Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
	}

	@Override
    protected Dialog onCreateDialog(int id) {
    	//Create progress dialog
    	if(id == D_PROGRESS){
    		ProgressDialog pDialog = new ProgressDialog(PostListingActivity.this);
			pDialog.setTitle(getResources().getString(R.string.app_name));
			pDialog.setMessage(getResources().getString(R.string.msg_progress));
			pDialog.setIcon(R.drawable.ic_launcher);
			pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			return pDialog;
		}
    	//Create error dialog
    	if(id == D_ERROR){
    		AlertDialog.Builder builder = new AlertDialog.Builder(PostListingActivity.this);
			builder.setTitle(R.string.app_name);
			builder.setIcon(R.drawable.ic_launcher);
			builder.setMessage("");
			builder.setCancelable(true);
			builder.setNeutralButton(R.string.label_btn_ok, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					dismissDialog(D_ERROR);
					if(ERROR_CODE == Constants.RESULT_POST_LISTING_SUCCESS 
							|| ERROR_CODE == Constants.RESULT_UPDATE_LISTING_SUCCESS
							|| ERROR_CODE == Constants.ERROR_NETWORK_UNAVAILABLE){
						finish();
					}else if (ERROR_CODE == Constants.ERROR_ENABLE_LOCATION_PROVIDER) {
						enableLocationSettings();
					}
				}
			});			
			AlertDialog aDialog = builder.create();
			return aDialog;
		}
    	if(id == D_DATE){
    		DatePickerDialog dateDg = new DatePickerDialog(PostListingActivity.this, new OnDateSetListener() {
				
				public void onDateSet(DatePicker arg0, int year, int month, int date) {
					if (year >= calendar.get(Calendar.YEAR) 
							&& (year > calendar.get(Calendar.YEAR) || month >= calendar.get(Calendar.MONTH))
							&& (year > calendar.get(Calendar.YEAR) || month > calendar.get(Calendar.MONTH) 
							|| date > calendar.get(Calendar.DAY_OF_MONTH))) {
						btnExpiary.setText((month>9? (month+1): "0"+(month+1))+"/"+(date>9? date: "0"+date)+"/"+year);
					} else {
						ERROR_CODE = ERROR_DATE_VALIDATION;
						showDialog(D_ERROR);
					}
										
				}
			}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
			return dateDg;
    	}
    	return null;
    }
	@Override
    protected void onPrepareDialog(int id, Dialog dialog) {
    	if(id == D_ERROR) {
    		String message = "";
    		//Display error message as per the error code
    		if (ERROR_CODE == Constants.ERROR_NETWORK_UNAVAILABLE) {
    			message = (String) getResources().getText(R.string.error_network_01);
			} else if(ERROR_CODE == Constants.ERROR_NETWORK_CONNECT){
				message = (String) getResources().getText(R.string.error_network_02);
			}else if(ERROR_CODE == Constants.ERROR_RESULT_POST_LISTING){
				message = (String) getResources().getText(R.string.error_postlisting);
			}else if(ERROR_CODE == Constants.RESULT_POST_LISTING_SUCCESS){
				message = (String) getResources().getText(R.string.msg_postlisting_success);
			}else if(ERROR_CODE == Constants.ERROR_RESULT_GET_LOCATION){
				message = (String) getResources().getText(R.string.error_postlisting_get_location);
			}else if(ERROR_CODE == Constants.ERROR_ENABLE_LOCATION_PROVIDER){
				message = (String) getResources().getText(R.string.msg_postlisting_enable_provider);
			}else if(ERROR_CODE == ERROR_DATE_VALIDATION){
				message = (String) getResources().getText(R.string.msg_validation_date_higher);
			}else if(ERROR_CODE == Constants.ERROR_RESULT_UPDATE_LISTING){
				message = (String) getResources().getText(R.string.error_update_listing);
			}else if(ERROR_CODE == Constants.RESULT_UPDATE_LISTING_SUCCESS){
				message = (String) getResources().getText(R.string.msg_postlisting_update_success);
			}else if(ERROR_CODE == Constants.ERROR_LOW_RESOLUTION_CROP){
				message = (String) getResources().getText(R.string.msg_postlisting_low_resolution);
			}
    		((AlertDialog) dialog).setMessage(message);
		}    	
    }
	/**
	 * 
	 * @author avinash
	 * Class to handle post listing server communication
	 */
	class PostListingTask extends AsyncTask<Integer, Integer, PostListingResultWrapper>{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
//			showDialog(D_PROGRESS);
			setSupportProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected PostListingResultWrapper doInBackground(Integer... params) {
			PostListingResultWrapper wrapper = new PostListingResultWrapper();
			try{
				if(wsAction == null ){
					wsAction = new WSAction();
				}
				if(params[0].equals(ACT_MODE_PROCESS_BITMAP)){
					wrapper.image = resizeBitmap();
				} else if (params[0].equals(ACT_MODE_POST_LISTING)) {
					wrapper = wsAction.postListing(((VeneficaApplication)getApplication()).getAuthToken(), getListingDetails(null));
				}else if (params[0].equals(ACT_MODE_GET_LISTING)) {
					ListingDetailsResultWrapper detailsWrapper = wsAction.getListingById(((VeneficaApplication)getApplication()).getAuthToken()
							, selectedListingId);
					wrapper.listing = detailsWrapper.listing;
					wrapper.result = detailsWrapper.result;
				}else if (params[0].equals(ACT_MODE_UPDATE_LISTING)) {
					wrapper = wsAction.updateListing(((VeneficaApplication)getApplication()).getAuthToken(), getListingDetails(selectedListing));
				}
			}catch (IOException e) {
				Log.e("PostListingTask::doInBackground :", e.toString());
				wrapper.result = Constants.ERROR_NETWORK_CONNECT;
			} catch (XmlPullParserException e) {
				Log.e("PostListingTask::doInBackground :", e.toString());
			}
			return wrapper;
		}
		
		@Override
		protected void onPostExecute(PostListingResultWrapper result) {
			super.onPostExecute(result);
//			dismissDialog(D_PROGRESS);
			setSupportProgressBarIndeterminateVisibility(false);
			if (isLowResolution && result.image == null) {
				ERROR_CODE = Constants.ERROR_LOW_RESOLUTION_CROP;
				showDialog(D_ERROR);
			} else if(result.image != null){
				if (imageDtos == null) {
					imageDtos = new ArrayList<ImageDto>();
				}
				image = new ImageDto(result.image);
				imageDtos.add(image);
								
//                drawables.add(result.image);
                galImageAdapter.notifyDataSetChanged();
			} else if(result.data == null && result.result == -1 && result.listing == null){
				ERROR_CODE = Constants.ERROR_NETWORK_CONNECT;
				showDialog(D_ERROR);
			}else if (result.result == Constants.RESULT_GET_LISTING_DETAILS_SUCCESS && result.listing != null) {
				setListingDetails(result.listing);
			}else if (result.result != -1) {
				ERROR_CODE = result.result;
				showDialog(D_ERROR);				
			}
		}
	}

	/**
	 * Method to get listing data from edit fields 
	 * @return AdDto
	 */
	private AdDto getListingDetails(AdDto listing) {
		if (listing == null) {
			listing = new AdDto();
		}		
		listing.setTitle(edtTitle.getText().toString());
		listing.setCategory(categoryName);
		listing.setDescription(edtDescription.getText().toString());
		listing.setCategoryId(categoryId);
		listing.setPrice(new BigDecimal(edtPrice.getText().toString()));
		listing.setLatitude(Double.parseDouble(edtLatitude.getText().toString()));
		listing.setLongitude(Double.parseDouble(edtLongitude.getText().toString()));
		listing.setCanMarkAsSpam(true);
		listing.setCanRate(true);
//		listing.setCreatedAt(Utility.converDateToString(new Date()));
		listing.setExpired(false);
		listing.setExpiresAt(new Date(btnExpiary.getText().toString()));
		listing.setInBookmars(false);
		listing.setNumAvailProlongations(0);
		listing.setOwner(true);
		listing.setWanted(false);
		listing.setNumViews(0L);
		listing.setRating(1.0f);
		if (image != null) {
			listing.setImage(image);
		}
		if (images != null) {
//			listing.setImages(images);
		}
		return listing;
	}

	public void onLocationChanged(Location location) {
		if (location != null) {
//			edtLatitude.setText(location.getLatitude() + "");
//			edtLongitude.setText(location.getLongitude() + "");
		}
	}

	public void onProviderDisabled(String provider) {
		Utility.showLongToast(this, provider + getResources().getString(R.string.msg_postlisting_provider_disabled));
	}

	public void onProviderEnabled(String provider) {
		Utility.showLongToast(this, provider + getResources().getString(R.string.msg_postlisting_provider_selected));
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * Method to show options to enable location provider
	 */
	private void enableLocationSettings() {
	    Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	    startActivity(settingsIntent);
	}
	
	/**
     * Method to validate input fields
     * @return result of validation
     */
    private boolean validateFields(){
    	boolean result = true;
    	StringBuffer message = new StringBuffer();
    	if(vaildator == null){
    		vaildator = new InputFieldValidator();    		
    	}
    	
    	if(!vaildator.validateField(edtTitle, Pattern.compile(InputFieldValidator.countyCityAreaPatternRegx))){
    		result = false;
    		message.append(getResources().getString(R.string.label_postlisting_title).toString());
    		message.append("- ");
    		message.append(getResources().getString(R.string.msg_validation_county_city_area));
    		message.append("\n");
    	}
    	/*if(!vaildator.validateField(edtSubTitle, Pattern.compile(InputFieldValidator.countyCityAreaPatternRegx))){
    		result = false;
    		message.append(txtSubTitle.getText().toString());
    		message.append("- ");
    		message.append(getResources().getString(R.string.msg_validation_county_city_area));
    		message.append("\n");
    	}*/
    	if(categoryId == 0L){
    		result = false;
    		message.append(getResources().getString(R.string.label_postlisting_category).toString());
    		message.append("- ");
    		message.append(getResources().getString(R.string.msg_validation_category));
    		message.append("\n");
    	}
    	if(!vaildator.validateField(edtDescription, Pattern.compile(InputFieldValidator.countyCityAreaPatternRegx))){
    		result = false;
    		message.append(getResources().getString(R.string.label_postlisting_description).toString());
    		message.append("- ");
    		message.append(getResources().getString(R.string.msg_validation_county_city_area));
    		message.append("\n");
    	}
    	/*if(!vaildator.validateField(edtCondition, Pattern.compile(InputFieldValidator.countyCityAreaPatternRegx))){
    		result = false;
    		message.append(txtCondition.getText().toString());
    		message.append("- ");
    		message.append(getResources().getString(R.string.msg_validation_county_city_area));
    		message.append("\n");
    	}*/
    	if(!vaildator.validateField(edtPrice, Pattern.compile(InputFieldValidator.phonePatternRegx))){
    		result = false;
    		message.append(getResources().getString(R.string.label_postlisting_price).toString());
    		message.append("- ");
    		message.append(getResources().getString(R.string.msg_validation_phone));
    		message.append("\n");
    	}
    	/*if(!vaildator.validateField(edtLatitude, Pattern.compile(InputFieldValidator.userNamePatternRegx))){
    		result = false;
    		message.append(txtLatitude.getText().toString());
    		message.append("- ");
    		message.append(getResources().getString(R.string.msg_validation_fname_lname));
    		message.append("\n");
    	}
    	if(!vaildator.validateField(edtLongitude, Pattern.compile(InputFieldValidator.userNamePatternRegx))){
    		result = false;
    		message.append(txtLongitude.getText().toString());
    		message.append("- ");
    		message.append(getResources().getString(R.string.msg_validation_fname_lname));
    		message.append("\n");
    	}*/
    	/*if(!vaildator.validateField(edtZip, Pattern.compile(InputFieldValidator.zipCodePatternRegx))){
    		result = false;
    		message.append(txtZip.getText().toString());
    		message.append("- ");
    		message.append(getResources().getString(R.string.msg_validation_zipcode));
    		message.append("\n");
    	}
    	if(!vaildator.validateField(edtState, Pattern.compile(InputFieldValidator.countyCityAreaPatternRegx))){
    		result = false;
    		message.append(txtState.getText().toString());
    		message.append("- ");
    		message.append(getResources().getString(R.string.msg_validation_county_city_area));
    		message.append("\n");
    	}
    	if(!vaildator.validateField(edtCounty, Pattern.compile(InputFieldValidator.countyCityAreaPatternRegx))){
    		result = false;
    		message.append(txtCounty.getText().toString());
    		message.append("- ");
    		message.append(getResources().getString(R.string.msg_validation_county_city_area));
    		message.append("\n");
    	}
    	if(!vaildator.validateField(edtCity, Pattern.compile(InputFieldValidator.countyCityAreaPatternRegx))){
    		result = false;
    		message.append(txtCity.getText().toString());
    		message.append("- ");
    		message.append(getResources().getString(R.string.msg_validation_county_city_area));
    		message.append("\n");
    	}
    	if(!vaildator.validateField(edtArea, Pattern.compile(InputFieldValidator.countyCityAreaPatternRegx))){
    		result = false;
    		message.append(txtArea.getText().toString());
    		message.append("- ");
    		message.append(getResources().getString(R.string.msg_validation_county_city_area));
    		message.append("\n");
    	}*/
    	if (!result) {
			Utility.showLongToast(this, message.toString());
		}/*else{
			getListingDetails();
		}*/
		return result;    	
    }
    /**
     * Get image
     */
    private void pickImage() {
    	// Camera
    	final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 
        cameraIntents.add(captureIntent);
        // Gallery.
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent
        		, getResources().getString(R.string.label_chooser));
        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
        startActivityForResult(chooserIntent, REQ_GET_IMAGE);
    }
    
    /**
     * Set listing details for editing
     * @param listing
     */
    private void setListingDetails(AdDto listing) {
    	selectedListing = listing;
		edtTitle.setText(listing.getTitle());
		btnSelCategory.setText(listing.getCategory());
		edtDescription.setText(listing.getDescription());
		edtPrice.setText(listing.getPrice().toString());
		edtLatitude.setText(listing.getLatitude()+"");
		edtLongitude.setText(listing.getLongitude()+"");
		btnExpiary.setText(Utility.convertShortDateToString(listing.getExpiresAt()));
		categoryName = listing.getCategory();
		categoryId = listing.getCategoryId();
	}
    
    /**
     * Helper method to carry out crop operation
     */
    private void performCrop(){
    	//take care of exceptions
    	try {
    		//call the standard crop action intent (the user device may not support it)
	    	Intent cropIntent = new Intent("com.android.camera.action.CROP");
	    	//indicate image type and Uri
	    	cropIntent.setDataAndType(selectedImageUri, "image/*");
	    	//set crop properties
	    	cropIntent.putExtra("crop", "true");
	    	//indicate aspect of desired crop
	    	cropIntent.putExtra("aspectX", Constants.IMAGE_ASPECT_X);
	    	cropIntent.putExtra("aspectY", Constants.IMAGE_ASPECT_Y);
	    	//retrieve data on return
	    	cropIntent.putExtra("return-data", false);
	    	cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, Utility.getTempUri());
	    	cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
	    	cropIntent.putExtra("noFaceDetection",true);
	    	//start the activity - we handle returning in onActivityResult
	        startActivityForResult(cropIntent, REQ_IMAGE_CROP);  
    	}
    	//respond to users whose devices do not support the crop action
    	catch(ActivityNotFoundException anfe){
    		Log.d("PostListingActivity::performCrop: ", anfe.toString());
    	}
    }

	@Override
	public View makeView() {
		ImageView img = new ImageView(this);
		img.setBackgroundColor(0xFF000000);
		img.setScaleType(ImageView.ScaleType.CENTER_CROP);
		img.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        return img;
	}
}