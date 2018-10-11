package com.mad.sharpdesign.loadmenu;

import android.Manifest;
import android.app.ActionBar;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.mad.sharpdesign.R;
import com.mad.sharpdesign.editmenu.EditActivity;
import com.mad.sharpdesign.model.ImageRoomDatabase;
import com.mad.sharpdesign.utils.ImageRotate;
import com.mad.sharpdesign.utils.fancy.HeaderImageBlur;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.IoUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

/**
 * Activity that will present user with 3 different ways to load an image as bitmap. After loading the image, the bitmap is then sent as an intent to the actual edit activity.
 */
public class LoadActivity extends AppCompatActivity implements LoadContract {
    private static final String[] STORAGE_PERMISSIONS = { Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final String[] CAMERA_PERMISSIONS = { Manifest.permission.CAMERA};
    private Button mButtonURL, mButtonCancelURL, mButtonCancelCamera, mButtonURLPaste, mButtonLoadURL, mButtonLoadCamera, mButtonFile, mButtonCamera, mButtonCameraRetake, mButtonLoadGallery, mButtonCancelGallery, mButtonRepickGallery;
    private File mImageFile;
    private ImageLoader mImageLoader;
    private Uri mPath;
    private ImageView mCameraImageView, mURLImageView, mGalleryImageView;
    private int mDialogType;
    private LinearLayout mURLLinearLayout;
    private RelativeLayout mURLRelativeLayout, mCameraRelativeLayout;
    private EditText mEditTextURL;
    private View mLoadCameraDialogView, mLoadURLDialogView, mLoadGalleryDialogView;
    private LoadPresenter mPresenter;
    private Target mPasteTarget, mLoadTarget, mLoadPreviewTarget;
    private AlertDialog.Builder mURLDialogBuilder, mCameraDialogBuilder, mGalleryDialogBuilder;
    private ViewGroup mContent;
    private TextView mCameraErrorText, mURLErrorText, mGalleryErrorText;
    private int mDialogDismissKey;
    private String capturedImagePath;
    private Bitmap mLoadedBitmap;
    private Boolean mHasCameraPermissions;
    private Intent mEditIntent;
    private AlertDialog mLoadImageURL, mLoadImageCamera, mLoadImageGallery;
    private static final int CAMERA_DIALOG_KEY = 1;
    private static final int URL_DIALOG_KEY = 2;
    private static final int GALLERY_DIALOG_KEY = 3;
    private static final int CAMERA_REQUEST = 1;
    private static final int GALLERY_REQUEST = 2;
    private static final int CAMERA_PERMISSION = 100;
    private static final String IMAGE_PATH_KEY = "ImagePathKey";
    private static final String IMAGE_NAME = "Name";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.LoadTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_menu);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
        ImageRoomDatabase db = ImageRoomDatabase.getDatabase(getApplication());
        mPresenter = new LoadPresenter(this, db.imageDao());
        InitCameraDialog();
        InitURLDialog();
        InitGalleryDialog();
        mButtonURL = (Button) findViewById(R.id.btn_loadURL);
        mButtonFile = (Button)findViewById(R.id.btn_loadFile);
        mButtonCamera = (Button)findViewById(R.id.btn_takePicture);
        //Create
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/SharpDesign/");
        folder.mkdirs();
        verifyFilePermissions();

        mButtonFile.setOnClickListener((View v) -> {
            selectGalleryIntent();
        });
        mButtonCamera.setOnClickListener((View v) -> {
            mDialogType = 1;
            int permissionCamera = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, CAMERA_PERMISSIONS, 1 );

            } else {
                takePictureIntent();
            }

        });
        mButtonURL.setOnClickListener((View v) -> {
            mDialogType = 2;
            mLoadImageURL.show();

        });

        mButtonLoadURL.setOnClickListener((View v) -> {
            loadImage();
        });
        mButtonCancelURL.setOnClickListener((View v) -> {
            mLoadImageURL.dismiss();
        });
        mButtonURLPaste.setOnClickListener((View v) -> {
            pasteClipboard();

        });
        mButtonCameraRetake.setOnClickListener((View v) -> {
            takePictureIntent();
        });
        mButtonCancelCamera.setOnClickListener((View v) -> {
            mLoadImageCamera.dismiss();
        });
        mButtonLoadCamera.setOnClickListener((View v) -> {
            loadImage();
        });

        //Creates a target for our paste clipboard Picasso event. Includes error handling
        mPasteTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.d("MAD","Bitmap was loaded");
                mURLImageView.setVisibility(View.VISIBLE);
                mURLErrorText.setVisibility(View.GONE);
                mButtonLoadURL.setEnabled(true);
                mURLImageView.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                mURLImageView.setVisibility(View.GONE);
                mURLErrorText.setVisibility(View.VISIBLE);
                mButtonLoadURL.setEnabled(false);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        mButtonLoadGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImage();
            }
        });
        mButtonCancelGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoadImageGallery.cancel();
            }
        });
        mButtonRepickGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    /**
     * Create a temporary image for our camera intent to store
     * @return
     * @throws IOException
     */
    private File createTempImage() throws IOException {
        String fileName = "/temp.jpg";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile("temp", ".jpg", storageDir);
        Log.d("MAD", "my file path for temp image: " + image.getAbsolutePath());
        capturedImagePath = image.getAbsolutePath();
        return image;

    }

    /**
     * Helper function to take a picture intent, including an Output source from Fileprovider, so the camera saves the full image temporarily instead of a thumbnail.
     */
    private void takePictureIntent() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePicture.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createTempImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile!=null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.mad.sharpdesign", photoFile);
                Log.d("MAD", "My photo URI: " + photoURI);
                takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePicture, CAMERA_REQUEST);
            }
        }
    }

    /**
     * Start the Android Gallery Intent
     */
    private void selectGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    /**
     * Initialize our URL selection alert dialog.
     */
    private void InitURLDialog() {
        mContent = (ViewGroup) findViewById(android.R.id.content);
        //Build the Load URL Dialog
        mURLDialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
        mLoadURLDialogView= LayoutInflater.from(this).inflate(R.layout.load_image, (ViewGroup) findViewById(android.R.id.content), false);
        mButtonCancelURL = (Button)mLoadURLDialogView.findViewById(R.id.btn_cancel);
        mButtonURLPaste = (Button)mLoadURLDialogView.findViewById(R.id.btn_paste);
        mURLErrorText = (TextView)mLoadURLDialogView.findViewById(R.id.error_textView);
        mButtonLoadURL = (Button)mLoadURLDialogView.findViewById(R.id.btn_load);
        mEditTextURL = (EditText)mLoadURLDialogView.findViewById(R.id.input_url);
        mURLRelativeLayout = (RelativeLayout) mLoadURLDialogView.findViewById(R.id.layout_URL);
        mURLImageView = (ImageView) mLoadURLDialogView.findViewById(R.id.imageView_main);
        mURLDialogBuilder.setView(mLoadURLDialogView);
        mLoadImageURL = mURLDialogBuilder.create();
        mEditTextURL.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                //Input validation for URL
                if (i== EditorInfo.IME_ACTION_SEARCH || i== EditorInfo.IME_ACTION_DONE || keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (keyEvent == null || !keyEvent.isShiftPressed()) {
                        Uri imagePath = Uri.parse(mEditTextURL.getText().toString());
                        if (imagePath != null) {
                            Picasso.get().load(imagePath).into(mPasteTarget);
                        }
                    }
                }
                return false;
            }
        });
    }

    /**
     * Initialize our Camera selection alert dialog.
     */
    private void InitCameraDialog() {
        mContent = (ViewGroup) findViewById(android.R.id.content);
        //Build the Load from Camera Dialog
        mCameraDialogBuilder = new AlertDialog.Builder(this);
        mLoadCameraDialogView = LayoutInflater.from(this).inflate(R.layout.load_image_camera, (ViewGroup) findViewById(android.R.id.content), false);
        mButtonLoadCamera = (Button)mLoadCameraDialogView.findViewById(R.id.btn_loadCamera_load);
        mButtonCancelCamera = (Button)mLoadCameraDialogView.findViewById(R.id.btn_loadCamera_cancel);
        mCameraErrorText = (TextView)mLoadCameraDialogView.findViewById(R.id.error_camera_textView);
        mButtonCameraRetake = (Button)mLoadCameraDialogView.findViewById(R.id.btn_loadCamera_retake);
        mCameraImageView = (ImageView)mLoadCameraDialogView.findViewById(R.id.imageView_loadCamera);
        mCameraRelativeLayout = (RelativeLayout)mLoadCameraDialogView.findViewById(R.id.layout_camera);
        mCameraDialogBuilder.setView(mLoadCameraDialogView);
        mLoadImageCamera = mCameraDialogBuilder.create();
    }

    /**
     * Initialize our Gallery selection alert dialog
     */
    private void InitGalleryDialog() {
        mContent = (ViewGroup) findViewById(android.R.id.content);
        //Build the Load from Camera Dialog
        mGalleryDialogBuilder = new AlertDialog.Builder(this);
        mLoadGalleryDialogView = LayoutInflater.from(this).inflate(R.layout.load_image_gallery, (ViewGroup) findViewById(android.R.id.content), false);
        mButtonLoadGallery = (Button)mLoadGalleryDialogView.findViewById(R.id.btn_loadGallery_load);
        mButtonCancelGallery = (Button)mLoadGalleryDialogView.findViewById(R.id.btn_loadGallery_cancel);
        mGalleryErrorText = (TextView)mLoadGalleryDialogView.findViewById(R.id.error_gallery_textView);
        mButtonRepickGallery = (Button)mLoadGalleryDialogView.findViewById(R.id.btn_loadGallery_retake);
        mGalleryImageView = (ImageView)mLoadGalleryDialogView.findViewById(R.id.imageView_loadGallery);
        mCameraRelativeLayout = (RelativeLayout)mLoadGalleryDialogView.findViewById(R.id.layout_gallery);
        mGalleryDialogBuilder.setView(mLoadGalleryDialogView);
        mLoadImageGallery = mGalleryDialogBuilder.create();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }

    /**
     * Load our selected image into a file into our own directory, put the actual bitmap into an intent and then start the EditActivity intent.
     */
    public void loadImage() {
        Log.d("MAD", "Entered load image method");
        mEditIntent = new Intent(this, EditActivity.class);

        mLoadTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("MAD", "Entered run thread");
                        String fileName = ""+System.currentTimeMillis();

                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/SharpDesign/" + fileName + ".jpg");
                        mEditIntent.putExtra(IMAGE_PATH_KEY, Uri.fromFile(file));
                        mEditIntent.putExtra(IMAGE_NAME, fileName);
                        Log.d("MAD", "Saving file into: " + file.getAbsolutePath());
                        try {
                            file.createNewFile();
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, ostream);
                            ostream.flush();
                            ostream.close();
                        } catch (IOException e) {
                            Log.d("MAD","Failed");
                            e.printStackTrace();
                        }
                        startActivity(mEditIntent);
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {


            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        Picasso.get().load(mPath).into(mLoadTarget);
    }

    @Override
    protected void onStop() {
        if (mLoadImageCamera.isShowing()) {
            mLoadImageCamera.dismiss();
        }
        if (mLoadImageGallery.isShowing()) {
            mLoadImageGallery.dismiss();
        }
        if (mLoadImageURL.isShowing()) {
            mLoadImageURL.dismiss();
        }
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case CAMERA_REQUEST:
                if (resultCode == RESULT_OK) {
                    mLoadImageCamera.show();
                    //Create a bitmap from the URI of our image from our camera intent.
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(capturedImagePath, options);
                    //Rotate it using our helper class in case the camera captures a rotated image.
                    ImageRotate rotate = new ImageRotate(capturedImagePath, bitmap);
                    bitmap = rotate.getRotatedBitmap();
                    mCameraImageView.setImageBitmap(bitmap);
                    mPath = getImageUri(this, bitmap);
                    Log.d("MAD", "My mpath is :" + mPath);
                } else {
                    mLoadImageCamera.cancel();
                }
                break;
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    mLoadImageGallery.show();
                    //Load the Gallery URI into a bitmap
                    Uri pickedImageURI = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(pickedImageURI, filePath, null, null, null);
                    cursor.moveToFirst();
                    String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
                    mGalleryImageView.setImageBitmap(bitmap);
                    mPath = getImageUri(this, bitmap);
                } else {
                    mLoadImageGallery.cancel();
                }
            default:
                break;

        }
    }

    /**
     * Helper function to get a ephemeral image (Like a URL loaded image) Uri by first saving it into a temporary location, then returning the Uri of that file.
     * @param context
     * @param image
     * @return
     */
    public Uri getImageUri(Context context, Bitmap image) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), image, "temp", null);
        final Uri pathURI = Uri.parse(path);
        return pathURI;
    }

    /**
     * Get our current clipboard contents and paste them into the edit text, and show an instant image preview.
     */
    public void pasteClipboard() {
        mImageLoader = ImageLoader.getInstance();
        ClipboardManager clipboardManager = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = clipboardManager.getPrimaryClip();
        if (clip==null) return;
        ClipData.Item item = clip.getItemAt(0);
        if (item==null) return;
        CharSequence textToPaste = item.getText();
        if (textToPaste == null) return;
        mEditTextURL.setText(textToPaste);
        mPath = Uri.parse(textToPaste.toString());
        ImageView img = new ImageView(this);
        Log.d("MAD","Loading image now");
        Picasso.get().load(mPath).into(mPasteTarget);
    }


    /**
     * Check to see if we have Write file permissions. If not, then request them.
     * @return
     */
    public boolean verifyFilePermissions() {
        int permissionExternalMemory = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permissionExternalMemory != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,STORAGE_PERMISSIONS,1);
            return true;
        }
        return true;
    }

}
