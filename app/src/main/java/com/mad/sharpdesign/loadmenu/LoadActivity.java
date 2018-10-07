package com.mad.sharpdesign.loadmenu;

import android.Manifest;
import android.app.ActionBar;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


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

public class LoadActivity extends AppCompatActivity implements LoadContract {
    private static final String[] STORAGE_PERMISSIONS = { Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final String[] CAMERA_PERMISSIONS = { Manifest.permission.CAMERA};
    private Button mButtonURL, mButtonCancelURL, mButtonCancelCamera, mButtonURLPaste, mButtonLoadURL, mButtonLoadCamera, mButtonFile, mButtonCamera, mButtonCameraRetake;
    private File mImageFile;
    private ImageLoader mImageLoader;
    private Uri mPath;
    private ImageView mCameraImageView, mURLImageView;
    private int mDialogType;
    private LinearLayout mURLLinearLayout;
    private RelativeLayout mURLRelativeLayout, mCameraRelativeLayout;
    private EditText mEditTextURL;
    private View mLoadCameraDialogView, mLoadURLDialogView;
    private LoadPresenter mPresenter;
    private Target mPasteTarget, mLoadTarget, mLoadPreviewTarget;
    private AlertDialog.Builder mURLDialogBuilder, mCameraDialogBuilder, mGalleryDialogBuilder;
    private ViewGroup mContent;
    private int mDialogDismissKey;
    private String capturedImagePath;
    private Bitmap mLoadedBitmap;
    private Intent mEditIntent;
    private AlertDialog mLoadImageURL, mLoadImageCamera;
    private static final int CAMERA_DIALOG_KEY = 1;
    private static final int URL_DIALOG_KEY = 2;
    private static final int CAMERA_REQUEST = 1;
    private static final int GALLERY_REQUEST = 2;
    private static final int CAMERA_PERMISSION = 100;
    private static final String IMAGE_PATH_KEY = "ImagePathKey";
    private static final String IMAGE_KEY = "ImageKey";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_menu);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
        ImageRoomDatabase db = ImageRoomDatabase.getDatabase(getApplication());
        mPresenter = new LoadPresenter(this, db.imageDao());
        InitCameraDialog();
        InitURLDialog();
        mButtonURL = (Button) findViewById(R.id.btn_loadURL);
        mButtonFile = (Button)findViewById(R.id.btn_loadFile);
        mButtonCamera = (Button)findViewById(R.id.btn_takePicture);

        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/SharpDesign/");
        Log.d("MAD", "Root folder is: "+folder.getAbsolutePath());
        folder.mkdirs();
        verifyFilePermissions();

        mButtonFile.setOnClickListener((View v) -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, GALLERY_REQUEST);
        });
        mButtonCamera.setOnClickListener((View v) -> {
            mDialogType = 1;
            verifyCameraPermissions();
            takePictureIntent();



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

    }
    private File createTempImage() throws IOException {
        String fileName = "/temp.jpg";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile("temp", ".jpg", storageDir);
        Log.d("MAD", "my file path for temp image: " + image.getAbsolutePath());
        capturedImagePath = image.getAbsolutePath();
        return image;

    }
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

    private void InitURLDialog() {
        mContent = (ViewGroup) findViewById(android.R.id.content);
        //Build the Load URL Dialog
        mURLDialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
        mLoadURLDialogView= LayoutInflater.from(this).inflate(R.layout.load_image, (ViewGroup) findViewById(android.R.id.content), false);
        mButtonCancelURL = (Button)mLoadURLDialogView.findViewById(R.id.btn_cancel);
        mButtonURLPaste = (Button)mLoadURLDialogView.findViewById(R.id.btn_paste);
        mButtonLoadURL = (Button)mLoadURLDialogView.findViewById(R.id.btn_load);
        mEditTextURL = (EditText)mLoadURLDialogView.findViewById(R.id.input_url);
        mURLRelativeLayout = (RelativeLayout) mLoadURLDialogView.findViewById(R.id.layout_URL);
        mURLImageView = (ImageView) mLoadURLDialogView.findViewById(R.id.imageView_main);
        mURLDialogBuilder.setView(mLoadURLDialogView);
        mLoadImageURL = mURLDialogBuilder.create();
    }
    private void InitCameraDialog() {
        mContent = (ViewGroup) findViewById(android.R.id.content);
        //Build the Load from Camera Dialog
        mCameraDialogBuilder = new AlertDialog.Builder(this);
        mLoadCameraDialogView = LayoutInflater.from(this).inflate(R.layout.load_image_camera, (ViewGroup) findViewById(android.R.id.content), false);
        mButtonLoadCamera = (Button)mLoadCameraDialogView.findViewById(R.id.btn_loadCamera_load);
        mButtonCancelCamera = (Button)mLoadCameraDialogView.findViewById(R.id.btn_loadCamera_cancel);
        mButtonCameraRetake = (Button)mLoadCameraDialogView.findViewById(R.id.btn_loadCamera_retake);
        mCameraImageView = (ImageView)mLoadCameraDialogView.findViewById(R.id.imageView_loadCamera);
        mCameraRelativeLayout = (RelativeLayout)mLoadCameraDialogView.findViewById(R.id.layout_camera);
        mCameraDialogBuilder.setView(mLoadCameraDialogView);
        mLoadImageCamera = mCameraDialogBuilder.create();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }
    public void loadImageIntoPreview(int dialogType, Uri path) {
        ImageView img = new ImageView(this);
        mLoadPreviewTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.d("MAD","Bitmap was loaded");
                switch(dialogType) {
                    case CAMERA_DIALOG_KEY:
                        Picasso.get().load(path).into(mCameraImageView);
                        break;
                    case URL_DIALOG_KEY:
                        Picasso.get().load(path).into(mURLImageView);
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        Picasso.get().load(path).into(mLoadPreviewTarget);
        Picasso.get().load(path).transform(new HeaderImageBlur(this, 25)).into(img, new Callback() {
            @Override
            public void onSuccess() {
                switch(dialogType) {
                    case CAMERA_DIALOG_KEY:
                        mCameraRelativeLayout.setBackground(img.getDrawable());
                        break;
                    case URL_DIALOG_KEY:
                        mURLRelativeLayout.setBackground(img.getDrawable());
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

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
                        mEditIntent.putExtra(IMAGE_PATH_KEY, mPath);
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
                e.printStackTrace();

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        Picasso.get().load(mPath).into(mLoadTarget);



    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case CAMERA_REQUEST:
                mLoadImageCamera.show();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                Bitmap bitmap = BitmapFactory.decodeFile(capturedImagePath, options);
                ImageRotate rotate = new ImageRotate(capturedImagePath, bitmap);
                bitmap = rotate.getRotatedBitmap();
                loadImageIntoPreview(CAMERA_DIALOG_KEY, getImageUri(this, bitmap));
                mPath = getImageUri(this, bitmap);
                Log.d("MAD", "My mpath is :" + mPath);
                break;
            default:
                break;

        }
    }

    public Uri getImageUri(Context context, Bitmap image) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), image, "temp", null);
        return Uri.parse(path);
    }
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
        //Add some input validation to check if url is valid later
        mPath = Uri.parse(textToPaste.toString());
        ImageView img = new ImageView(this);
        Log.d("MAD","Loading image now");
        mPasteTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.d("MAD","Bitmap was loaded");
                Picasso.get().load(mPath).into(mURLImageView);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        Picasso.get().load(mPath).into(mPasteTarget);


        Picasso.get().load(mPath).transform(new HeaderImageBlur(this, 25)).into(img, new Callback() {
            @Override
            public void onSuccess() {
                mURLRelativeLayout.setBackground(img.getDrawable());
            }

            @Override
            public void onError(Exception e) {

            }
        });
        //mImageLoader.displayImage(textToPaste.toString(), mImageView);

        //mPresenter.saveImage(textToPaste.toString());
    }
    public void verifyCameraPermissions() {
        int permissionCamera = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, CAMERA_PERMISSIONS, 1 );
        }
    }
    public boolean verifyFilePermissions() {
        // This will return the current Status
        int permissionExternalMemory = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permissionExternalMemory != PackageManager.PERMISSION_GRANTED)
        {
            // If permission not granted then ask for permission real time.
            ActivityCompat.requestPermissions(this,STORAGE_PERMISSIONS,1);
            return true;
        }
        return true;
    }

}
