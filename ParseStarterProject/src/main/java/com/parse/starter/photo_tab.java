package com.parse.starter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;

/**
 * Created by Jizhizi Li on 15/09/2015.
 */
public class photo_tab extends Fragment {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int CHOOSE_FROM_GALLERY = 2;
    static final int CROP_IMAGE_FROM_GALLERY = 3;
    static final int CROP_WIDTH=300;
    static final int CROP_HEIGHT=300;
    private Uri picUri;
    private Button takePhoto;
    private Button choose;
    private Button receiveBT;
    private Switch switchBT;
    private BluetoothAdapter btAdapter;
    private Boolean btState;

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view =inflater.inflate(R.layout.photo_tab,container,false);
        takePhoto = (Button) view.findViewById(R.id.takePhoto);



        choose = (Button) view.findViewById(R.id.choose);
        receiveBT = (Button)view.findViewById(R.id.bt);
        switchBT = (Switch)view.findViewById(R.id.switchBT);

btAdapter = BluetoothAdapter.getDefaultAdapter();
        btState = btAdapter.enable();
        switchBT.setChecked(btState);



        final Activity activity = getActivity();
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePhoto = new Intent(activity,TakePhoto.class);
                startActivity(takePhoto);
                Log.d("takePhoto","takePhoto");
            }
        });
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFromGallery(v);
            }

        });


receiveBT.setOnClickListener(new View.OnClickListener(){
    @Override
public void onClick(View v){

        Intent bluetooth = new Intent(activity,BlueTooth.class);
        startActivity(bluetooth);

    }
});



        switchBT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!btAdapter.enable()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, 1);
                    }


                } else {
                    if (btAdapter.enable()) {
                        btAdapter.disable();
                    }
                }
            }
        });



        return view;
    }
    //choose from gallery method
    public void chooseFromGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, CHOOSE_FROM_GALLERY);
    }
    @Override
    public void onActivityResult(int requestcode, int resultCode, Intent data) {

        if (requestcode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK && data != null) {

            //Take the photo by system's camera, which is not used in this project.
            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap) extras.get("data");

        } else if (requestcode == CHOOSE_FROM_GALLERY) {
            //choose from gallery
            Uri selectedImage = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor c = getActivity().getContentResolver().query(selectedImage, filePath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String picturePath = c.getString(columnIndex);
            c.close();
            Bitmap photo = (BitmapFactory.decodeFile(picturePath));

            //crop picture then display
            picUri = data.getData();
            performCrop();

        } else if (requestcode == CROP_IMAGE_FROM_GALLERY) {
            Bundle extras = data.getExtras();
            Bitmap thePic = extras.getParcelable("data");
            //photo after Cropped pass to another Intent
            Intent intent = new Intent(getActivity(), PhotoEdit.class);
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            thePic.compress(Bitmap.CompressFormat.PNG, 50, bs);
            intent.putExtra("byteArray", bs.toByteArray());
            startActivity(intent);

        }
    }
    private void performCrop() {

        try {
            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", CROP_WIDTH);
            cropIntent.putExtra("outputY", CROP_HEIGHT);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);

            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_IMAGE_FROM_GALLERY);

        } catch (ActivityNotFoundException anfe) {
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }


    }
}
