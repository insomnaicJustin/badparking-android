package ua.in.badparking.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import ua.in.badparking.R;
import ua.in.badparking.data.TrespassController;
import ua.in.badparking.ui.MainActivity;

/**
 * Created by Dima Kovalenko on 8/12/15.
 */
public class StartFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_IMAGE_CAPTURE = 356;
    private static final int PICK_IMAGE = 357;

    private ImageView firstImageView;
    private ImageView secondImageView;

    private View firstHolder;
    private View secondHolder;
    private View takePhotoButton;
    private EditText platesEditText;

    private boolean isFirstHasImage;
    private boolean isSecondHasImage;

    private File firstImage;
    private File secondImage;
    private Spinner spinner; // TODO make parking on trotuar first

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static StartFragment newInstance() {
        return new StartFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_start, container, false);

        Resources res = getResources();
        String[] trespassTypes = res.getStringArray(R.array.trespass_types);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Arrays.asList(trespassTypes));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner = (Spinner)rootView.findViewById(R.id.trespassSpinner);
        spinner.setAdapter(adapter);

        ImageView closeFirst = (ImageView)rootView.findViewById(R.id.close_first);
        ImageView closeSecond = (ImageView)rootView.findViewById(R.id.close_second);
        firstImageView = (ImageView)rootView.findViewById(R.id.first_image);
        secondImageView = (ImageView)rootView.findViewById(R.id.second_image);
        firstHolder = rootView.findViewById(R.id.first_image_holder);
        secondHolder = rootView.findViewById(R.id.second_image_holder);
        takePhotoButton = rootView.findViewById(R.id.takePhotoButton);
        platesEditText = (EditText)rootView.findViewById(R.id.plates);

        rootView.findViewById(R.id.next).setOnClickListener(this);
        firstImageView.setOnClickListener(this);
        secondImageView.setOnClickListener(this);
        closeFirst.setOnClickListener(this);
        closeSecond.setOnClickListener(this);
        takePhotoButton.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            if (!isFirstHasImage) {
                if (isSecondHasImage) {
                    takePhotoButton.setVisibility(View.GONE);
                }
                firstHolder.setVisibility(View.VISIBLE);
                setPic(firstImageView, firstImage.getPath());
                isFirstHasImage = true;
            } else {
                takePhotoButton.setVisibility(View.GONE);
                secondHolder.setVisibility(View.VISIBLE);
                setPic(secondImageView, secondImage.getPath());
                isSecondHasImage = true;
            }
        } else if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (getPathFromUri(data.getData()) != null) {
                File file = new File(getPathFromUri(data.getData()));
                if (!isFirstHasImage) {
                    firstHolder.setVisibility(View.VISIBLE);
                    firstImage = file;
                    setPic(firstImageView, firstImage.getPath());
                    isFirstHasImage = true;
                    if (isSecondHasImage) {
                        takePhotoButton.setVisibility(View.GONE);
                    }
                } else {
                    takePhotoButton.setVisibility(View.GONE);
                    secondHolder.setVisibility(View.VISIBLE);
                    secondImage = file;
                    setPic(secondImageView, secondImage.getPath());
                    isSecondHasImage = true;
                }
            } else
                Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseAll();
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close_first:
                releaseFirstImage();
                break;
            case R.id.close_second:
                releaseSecondImage();
                break;
            case R.id.takePhotoButton:
                new AlertDialog.Builder(getActivity()).setMessage(getString(R.string.choose_photo_mode))
                        .setNegativeButton(getString(R.string.make_phonot), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                openCamera();
                            }
                        })
                        .setPositiveButton(getString(R.string.from_gallery), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                openGallery();
                            }
                        }).show();
                break;
            case R.id.next:
                hideKeyboard(getActivity());
                final String platesText = platesEditText.getText().toString();
                if (platesText.length() == 0) {
                    Toast.makeText(getActivity(), "Введiть номернi знаки", Toast.LENGTH_LONG).show();
                } else if (!isFirstHasImage && !isSecondHasImage) {
                    Toast.makeText(getActivity(), "Додайте хоча б одне фото", Toast.LENGTH_LONG).show();
                } else {
                    TrespassController.INST.getTrespass().clearPhotos();
                    if (isFirstHasImage) {
                        TrespassController.INST.getTrespass().addPhoto(firstImage);
                    }
                    if (isSecondHasImage) {
                        TrespassController.INST.getTrespass().addPhoto(secondImage);
                    }
                    TrespassController.INST.getTrespass().setCaseTypeId(spinner.getSelectedItemId() + "");
                    ((MainActivity)getActivity()).scrollToPlace();
                }
                break;
        }
    }

    // Photo stuff

    private void openCamera() {
        if (!isFirstHasImage || !isSecondHasImage) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                try {
                    if (!isFirstHasImage) {
                        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                        firstImage = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".jpg", storageDir);
                    } else {
                        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                        secondImage = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".jpg", storageDir);
                    }
                } catch (IOException ignored) {
                }
                if (firstImage != null || secondImage != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(!isFirstHasImage ? firstImage : secondImage));
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        } else {
            Toast.makeText(getActivity(), "You can send only 2 pics", Toast.LENGTH_LONG).show();
        }
    }

    private void openGallery() {
        if (!isFirstHasImage || !isSecondHasImage) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, PICK_IMAGE);
        } else {
            Toast.makeText(getActivity(), getString(R.string.only_2_pics), Toast.LENGTH_LONG).show();
        }
    }

    private void releaseFirstImage() {
        firstImageView.setImageBitmap(null);
        firstImage = null;
        isFirstHasImage = false;
        firstHolder.setVisibility(View.GONE);
        takePhotoButton.setVisibility(View.VISIBLE);
    }

    private void releaseSecondImage() {
        secondImageView.setImageBitmap(null);
        secondImage = null;
        isSecondHasImage = false;
        secondHolder.setVisibility(View.GONE);
        takePhotoButton.setVisibility(View.VISIBLE);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void releaseAll() {
        if (firstImage != null) firstImage.delete();
        if (secondImage != null) secondImage.delete();
        firstImage = null;
        secondImage = null;
        isFirstHasImage = false;
        isSecondHasImage = false;
    }

    private void setPic(ImageView view, String currentPhotoPath) {
        int targetW = getResources().getDimensionPixelSize(R.dimen.photo_side);
        int targetH = getResources().getDimensionPixelSize(R.dimen.photo_side);

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        //noinspection deprecation
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        view.setImageBitmap(bitmap);
    }

    private String getPathFromUri(Uri uri) {
        String selected;
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        selected = cursor.getString(columnIndex);
        cursor.close();
        return selected;
    }
}