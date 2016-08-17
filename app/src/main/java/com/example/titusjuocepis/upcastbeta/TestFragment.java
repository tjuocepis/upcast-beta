package com.example.titusjuocepis.upcastbeta;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.sip.SipAudioCall;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by titusjuocepis on 6/6/16.
 */
public class TestFragment extends Fragment {

    Button button;
    ImageView imageView;
    private static TestFragment mFragment;

    private final String FIREBASE_URL = "https://upcast-beta.firebaseio.com";

    public static TestFragment newInstance() {

        if (mFragment != null)
            return mFragment;

        mFragment = new TestFragment();

        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);

        button = (Button) view.findViewById(R.id.capture_button);
        imageView = (ImageView) view.findViewById(R.id.image_view);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA);
                dispatchTakePictureIntent();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /** Create a file Uri for saving an image or video */
    private Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
/*
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
*/
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = null;
        if (type == MEDIA_TYPE_IMAGE){
            try {
                mediaFile = File.createTempFile("IMG_"+ timeStamp, ".jpg", getContext().getExternalFilesDir(null));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(type == MEDIA_TYPE_VIDEO) {
            //mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            //        "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        Log.d("[CREATE FILE] - ", mediaFile.getAbsolutePath());

        return mediaFile;
    }

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;

    private void dispatchTakePictureIntent() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {

            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image

            Log.d("[FILE URI] - ", fileUri.toString());

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

            //getActivity().setResult(Activity.RESULT_OK, intent);
            // start the image capture Intent
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    private static Bitmap image = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("[ADD BITMAP] - ", fileUri.toString());

        try {
            image = getThumbnail(fileUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (image == null)
            Log.d("[IMAGE] - ", "IS NULL!!!");

        imageView.setImageBitmap(image);

        PutCastToBucket castToBucketTask = new PutCastToBucket();
        castToBucketTask.execute(fileUri);
    }

    private int THUMBNAIL_SIZE = 900;

    public Bitmap getThumbnail(Uri uri) throws IOException{
        InputStream input = getContext().getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither=true;//optional
        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();

        Log.d("[THUMBNAIL] - ", uri.toString());

        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
            return null;

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither=true;//optional
        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        input = getContext().getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int)Math.floor(ratio));
        if(k==0) return 1;
        else return k;
    }

    /*
    *
    *
     */

    private class PutCastToBucket extends AsyncTask<Uri, Void, Void> implements TaskStatusListener {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://upcast-beta.appspot.com");
        StorageReference imageRef = storageRef.child("casts/"+fileUri.getLastPathSegment());

        TaskStatusListener taskListener = this;

        @Override
        protected Void doInBackground(Uri... params) {

            UploadTask uploadTask = imageRef.putFile(params[0]);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String msg = taskSnapshot.getDownloadUrl().toString();
                    taskListener.taskDone(msg);
                }
            });

            return null;
        }

        @Override
        public void taskDone(String msg) {
            Log.d("[TASK DONE] - ", msg);

            Firebase channelCastUrlsRef = new Firebase(FIREBASE_URL + "/channel_cast_urls/");
        }
    }

    private interface TaskStatusListener {

        public void taskDone(String msg);
    }
}
