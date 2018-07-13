package com.actiknow.fileuploaddemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import com.actiknow.fileuploaddemo.utils.AppConfigTags;
import com.actiknow.fileuploaddemo.utils.AppConfigURL;
import com.actiknow.fileuploaddemo.utils.Constants;
import com.actiknow.fileuploaddemo.utils.NetworkConnection;
import com.actiknow.fileuploaddemo.utils.Utils;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.myhexaville.smartimagepicker.ImagePicker;
import com.myhexaville.smartimagepicker.OnImagePickedListener;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropFragment;
import com.yalantis.ucrop.UCropFragmentCallback;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements UCropFragmentCallback {
    
    TextView tvUploadImage;
    TextView tvUploadVideo;
    TextView tvUploadFile;
    
    Uri sourceUri, destinationUri;
    
    public ImagePicker imagePicker;
    
    ProgressDialog progressDialog;
    
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        initView ();
        initData ();
        initListener ();
    }
    
    private void initView () {
        tvUploadImage = (TextView) findViewById (R.id.tvUploadImage);
        tvUploadVideo = (TextView) findViewById (R.id.tvUploadVideo);
        tvUploadFile = (TextView) findViewById (R.id.tvUploadFile);
    }
    
    private void initData () {
        progressDialog = new ProgressDialog (this);
    }
    
    private void initListener () {
        tvUploadImage.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                imagePicker = new ImagePicker (MainActivity.this,
                        null,
                        new OnImagePickedListener () {
                            @Override
                            public void onImagePicked (Uri imageUri) {
                                startCrop (imageUri);
                            }
                        });
                imagePicker.choosePicture (true);
            }
        });
        tvUploadVideo.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
//                Pix.start (MainActivity.this, 1212);
            }
        });
        tvUploadFile.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                DialogProperties properties = new DialogProperties ();
                properties.selection_mode = DialogConfigs.SINGLE_MODE;
                properties.selection_type = DialogConfigs.FILE_SELECT;
                properties.root = new File (DialogConfigs.DEFAULT_DIR);
                properties.error_dir = new File (DialogConfigs.DEFAULT_DIR);
                properties.offset = new File (DialogConfigs.DEFAULT_DIR);
                properties.extensions = null;
                
                final FilePickerDialog dialog = new FilePickerDialog (MainActivity.this, properties);
                dialog.setTitle ("Select a File");
                
                dialog.setDialogSelectionListener (new DialogSelectionListener () {
                    @Override
                    public void onSelectedFilePaths (String[] files) {
                        for (int i = 0; i < files.length; i++) {
                            try {
                                
                                File file = new File (files[i]);
                                Uri uri = Uri.fromFile (file);
                                
                                Log.e ("karman", "Filepath : " + files[i]);
                                Log.e ("karman", "Filename : " + file.getName ());
                                Log.e ("karman", "Extension : " + MimeTypeMap.getFileExtensionFromUrl (uri.toString ()));
                                
                                
                                FileInputStream fis = new FileInputStream (file);
                                ByteArrayOutputStream bos = new ByteArrayOutputStream ();
                                byte[] buf = new byte[1024];
                                try {
                                    for (int readNum; (readNum = fis.read (buf)) != - 1; ) {
                                        bos.write (buf, 0, readNum);
                                    }
                                } catch (IOException ex) {
                                    ex.printStackTrace ();
                                }
                                byte[] bytes = bos.toByteArray ();
                                String encodedImage = Base64.encodeToString (bytes, Base64.DEFAULT);
                                uploadFile (encodedImage, MimeTypeMap.getFileExtensionFromUrl (uri.toString ()));

                            } catch (Exception e) {
                                e.printStackTrace ();
                            }
                        }
                    }
                });
                dialog.show ();
            }
        });
    }
    
    private void startCrop (@NonNull Uri uri) {
        String destinationFileName = "CroppedImage.jpg";
        
        UCrop uCrop = UCrop.of (uri, Uri.fromFile (new File (getCacheDir (), destinationFileName)));
        uCrop = uCrop.withMaxResultSize (600, 800);
        uCrop = advancedConfig (uCrop);
        
        uCrop.start (MainActivity.this);
    }
    
    private UCrop advancedConfig (@NonNull UCrop uCrop) {
        UCrop.Options options = new UCrop.Options ();
        
        options.setCompressionFormat (Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality (60);
        options.setHideBottomControls (false);
        options.setFreeStyleCropEnabled (true);

        /*
        If you want to configure how gestures work for all UCropActivity tabs

        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
        * */

        /*
        This sets max size for bitmap that will be decoded from source Uri.
        More size - more memory allocation, default implementation uses screen diagonal.

        options.setMaxBitmapSize(640);
        * */


       /*

        Tune everything (ﾉ◕ヮ◕)ﾉ*:･ﾟ✧

        options.setMaxScaleMultiplier(5);
        options.setImageToCropBoundsAnimDuration(666);
        options.setDimmedLayerColor(Color.CYAN);
        options.setCircleDimmedLayer(true);
        options.setShowCropFrame(false);
        options.setCropGridStrokeWidth(20);
        options.setCropGridColor(Color.GREEN);
        options.setCropGridColumnCount(2);
        options.setCropGridRowCount(1);
        options.setToolbarCropDrawable(R.drawable.your_crop_icon);
        options.setToolbarCancelDrawable(R.drawable.your_cancel_icon);

        // Color palette
        options.setToolbarColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.your_color_res));
        options.setRootViewBackgroundColor(ContextCompat.getColor(this, R.color.your_color_res));

        // Aspect ratio options
        options.setAspectRatioOptions(1,
            new AspectRatio("WOW", 1, 2),
            new AspectRatio("MUCH", 3, 4),
            new AspectRatio("RATIO", CropImageView.DEFAULT_ASPECT_RATIO, CropImageView.DEFAULT_ASPECT_RATIO),
            new AspectRatio("SO", 16, 9),
            new AspectRatio("ASPECT", 1, 1));

       */
        
        return uCrop.withOptions (options);
//        return null;
    }
    
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        imagePicker.handleActivityResult (resultCode, requestCode, data);
        
        if (resultCode == RESULT_OK) {
            if (requestCode == UCrop.REQUEST_CROP) {
                handleCropResult (data);
            }
        }
        if (resultCode == UCrop.RESULT_ERROR) {
            handleCropError (data);
        }
    }
    
    @Override
    public void loadingProgress (boolean showLoader) {
    }
    
    @Override
    public void onCropFinish (UCropFragment.UCropResult result) {
        switch (result.mResultCode) {
            case RESULT_OK:
                handleCropResult (result.mResultData);
                break;
            case UCrop.RESULT_ERROR:
                handleCropError (result.mResultData);
                break;
        }
    }
    
    private void handleCropResult (@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput (result);
        if (resultUri != null) {
            Log.e ("karman", "cropped image : " + resultUri);
            
            try {
                File file = new File (resultUri.getPath ());
                Log.e ("karman", "Filepath : " + file.getPath ());
                Log.e ("karman", "Filename : " + file.getName ());
                Log.e ("karman", "Extension : " + MimeTypeMap.getFileExtensionFromUrl (resultUri.toString ()));
                
                FileInputStream fis = new FileInputStream (file);
                ByteArrayOutputStream bos = new ByteArrayOutputStream ();
                byte[] buf = new byte[1024];
                try {
                    for (int readNum; (readNum = fis.read (buf)) != - 1; ) {
                        bos.write (buf, 0, readNum);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace ();
                }
                
                byte[] bytes = bos.toByteArray ();
                String encodedImage = Base64.encodeToString (bytes, Base64.DEFAULT);
                
                uploadFile (encodedImage, MimeTypeMap.getFileExtensionFromUrl (resultUri.toString ()));
//                Log.e ("karman", "Base 64 : " + encodedImage);
            } catch (Exception e) {
                e.printStackTrace ();
            }
            
        } else {
            Log.e ("karman", "cropped image : error");
        }
    }
    
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void handleCropError (@NonNull Intent result) {
        final Throwable cropError = UCrop.getError (result);
        if (cropError != null) {
            Log.e ("karman", "handleCropError: ", cropError);
        } else {
        }
    }
    
    
    public void uploadFile (final String file, final String extension) {
        if (NetworkConnection.isNetworkAvailable (MainActivity.this)) {
            Utils.showProgressDialog (MainActivity.this, progressDialog, "Loading...", true);
            Utils.showLog (Log.INFO, AppConfigTags.URL, AppConfigURL.UPLOAD_URL, true);
            StringRequest strRequest = new StringRequest (Request.Method.POST, AppConfigURL.UPLOAD_URL,
                    new com.android.volley.Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject (response);
                                    boolean is_error = jsonObj.getBoolean (AppConfigTags.ERROR);
                                    String message = jsonObj.getString (AppConfigTags.MESSAGE);
                                    if (! is_error) {
                                    } else {
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace ();
                                }
                            } else {
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                            progressDialog.dismiss ();
                        }
                    },
                    new com.android.volley.Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                            progressDialog.dismiss ();
                            NetworkResponse response = error.networkResponse;
                            if (response != null && response.data != null) {
                                Utils.showLog (Log.ERROR, AppConfigTags.ERROR, new String (response.data), true);
                            }
                        }
                    }) {
                
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String> ();
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    params.put ("file", file);
                    params.put ("extension", extension);
                    return params;
                }
                
                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<> ();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
                
            };
            Utils.sendRequest (strRequest, 30);
        } else {
        }
    }
}