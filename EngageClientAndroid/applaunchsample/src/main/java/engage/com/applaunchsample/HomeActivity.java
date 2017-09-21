package engage.com.applaunchsample;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.engage.EngageFailResponse;
import com.engage.EngageResponse;
import com.engage.EngageResponseListener;
import com.engage.api.EngageClient;
import com.engage.api.EngageConfig;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;

public class HomeActivity extends AppCompatActivity {

    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.root_layout);
       // showSnackBar("This is super cool i think", linearLayout);
        inializeEngage();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(HomeActivity.this,PizzaDetailsActivity.class);
                startActivity(intent);
            }
        });
    }



//    private void displayBannerDialog(){
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setPositiveButton("Get Pro", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//            }
//        }).setNegativeButton("No thanks", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//            }
//        });
//        final AlertDialog dialog = builder.create();
//        LayoutInflater inflater = getLayoutInflater();
//        View dialogLayout = inflater.inflate(R.layout.go_pro_dialog_layout, null);
//        dialog.setView(dialogLayout);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        ImageView image = (ImageView) dialogLayout.findViewById(R.id.goProDialogImage);
//        Picasso.with(HomeActivity.this).load("https://goo.gl/bktLai").placeholder(R.drawable.placeholder_image).into(image);
//        Bitmap icon=null;
////        try {
////       //     http://www.pngmart.com/files/4/Android-PNG-Image.png
////        //    https://goo.gl/bktLai
////            icon = new GetBitmapImage().execute("http://www.pngmart.com/files/4/Android-PNG-Image.png").get();
////            if(icon!=null){
//////                        Bitmap icon = BitmapFactory.decodeResource(HomeActivity.this.getResources(),
//////                                R.drawable.android_oreo);
////                float imageWidthInPX = (float)image.getWidth();
////
////                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Math.round(imageWidthInPX),
////                        Math.round(imageWidthInPX * (float)icon.getHeight() / (float)icon.getWidth()));
////                image.setLayoutParams(layoutParams);
////            }
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        } catch (ExecutionException e) {
////            e.printStackTrace();
////        }
//
//        dialog.show();
//
//        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface d) {
//                ImageView image = (ImageView) dialog.findViewById(R.id.goProDialogImage);
//                Bitmap icon=null;
//                try {
//                    icon = new GetBitmapImage().execute("https://goo.gl/bktLai").get();
//                    if(icon!=null){
////                        Bitmap icon = BitmapFactory.decodeResource(HomeActivity.this.getResources(),
////                                R.drawable.android_oreo);
//                        float imageWidthInPX = (float)image.getWidth();
//
//                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Math.round(imageWidthInPX),
//                                Math.round(imageWidthInPX * (float)icon.getHeight() / (float)icon.getWidth()));
//                        image.setLayoutParams(layoutParams);
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
//
//
////                Bitmap icon = BitmapFactory.decodeResource(HomeActivity.this.getResources(),
////                        R.drawable.android_oreo);
////                float imageWidthInPX = (float)image.getWidth();
////
////                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Math.round(imageWidthInPX),
////                        Math.round(imageWidthInPX * (float)icon.getHeight() / (float)icon.getWidth()));
////                image.setLayoutParams(layoutParams);
//
//
//            }
//        });
//    }




    public void showSnackBar(String string, LinearLayout linearLayout)
    {
       snackbar = Snackbar.make(linearLayout, string,Snackbar.LENGTH_INDEFINITE).
                setAction("Ok", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        snackbar.dismiss();
                    }
                });
        View snackbarView = snackbar.getView();
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(50);//this is your max line as your want
        snackbar.show();
    }

    private void inializeEngage(){

      //  EngageConfig engageConfig = new EngageConfig(getApplication(), BMSClient.REGION_US_SOUTH,"ef3c5a4f-6547-429d-90d5-d49cdffd71c6","4a17f904-8e87-4d92-b7a0-342974955710","norton");
        EngageConfig engageConfig = new EngageConfig(getApplication(), BMSClient.REGION_US_SOUTH,"e63d4d97-5ce6-459e-b0fc-f5fa032e07e6","4a17f904-8e87-4d92-b7a0-342974955710","norton");
     //   EngageConfig engageConfig = new EngageConfig(getApplication(), BMSClient.REGION_US_SOUTH,"8f6a7c1a-18f6-431c-8159-58396b46c160","6ceeffae-ace2-43c7-b070-6c9fd0bf3ffb","norton-new");
        EngageClient.getInstance().initialize(engageConfig, new EngageResponseListener() {
            @Override
            public void onSuccess(EngageResponse engageResponse) {
                Log.d("HomeActivity","Init Successful - "+engageResponse.getResponseText());
                EngageClient.getInstance().getMessages(HomeActivity.this, new EngageResponseListener() {
                    @Override
                    public void onSuccess(EngageResponse engageResponse) {
                        Log.d("getMessages", engageResponse.getResponseText());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                       //         displayBannerDialog();
                            }
                        });
                    }

                    @Override
                    public void onFailure(EngageFailResponse engageFailResponse) {
                        Log.d("getMessages onFailure", engageFailResponse.getErrorMsg());
                    }
                });
            }

            @Override
            public void onFailure(EngageFailResponse engageFailResponse) {
                Log.d("HomeActivity","Init Failed - "+engageFailResponse.getErrorMsg());
            }
        });
    }
}
