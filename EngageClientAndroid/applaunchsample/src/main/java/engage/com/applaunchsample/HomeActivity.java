package engage.com.applaunchsample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.applaunch.AppLaunchFailResponse;
import com.applaunch.AppLaunchResponse;
import com.applaunch.AppLaunchResponseListener;
import com.applaunch.api.AppLaunch;
import com.applaunch.api.AppLaunchParameters;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.root_layout);
       // showSnackBar("This is super cool i think", linearLayout);
        inializeAppLaunch();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(HomeActivity.this,PizzaDetailsActivity.class);
                startActivity(intent);
            }
        });
    }



    private void inializeAppLaunch(){
     //   f31df428-59a1-4418-8ae6-f886ce50c502
      //  EngageConfig engageConfig = new EngageConfig(getApplication(), BMSClient.REGION_US_SOUTH,"ef3c5a4f-6547-429d-90d5-d49cdffd71c6","4a17f904-8e87-4d92-b7a0-342974955710","norton");
      //  AppLaunchConfig appLaunchConfig = new AppLaunchConfig(getApplication(), BMSClient.REGION_US_SOUTH,"f31df428-59a1-4418-8ae6-f886ce50c502","f8bbc9c2-17c2-4a1f-a21b-50c753e3d9e1","norton");
     //   EngageConfig engageConfig = new EngageConfig(getApplication(), BMSClient.REGION_US_SOUTH,"8f6a7c1a-18f6-431c-8159-58396b46c160","6ceeffae-ace2-43c7-b070-6c9fd0bf3ffb","norton-new");
     //  AppLaunch.getInstance().initApp(getApplication(), BMSClient.REGION_US_SOUTH,"85720163-c9ca-4969-953e-9ef998464082","a9e5ad89-776d-4038-9c8a-ffd8734dcd15");
      //  AppLaunch.getInstance().initApp(getApplication(), BMSClient.REGION_US_SOUTH,"e134b9c0-7349-4a5f-b27a-278efa5c58d7","27a69ad4-bec9-4141-85c8-a4b37c0ef905");
        AppLaunch.getInstance().initApp(getApplication(), BMSClient.REGION_US_SOUTH,"4d370cdc-bfbb-4512-bb7b-1c37b512fe39","18bbdb7b-910b-4364-8372-83611e1970ae");

        AppLaunchParameters appLaunchParameters = new AppLaunchParameters();
        appLaunchParameters.put("key","value");
        appLaunchParameters.put("something",false);
        appLaunchParameters.put("another",1);

       // AppLaunch.getInstance().registerUser("norton");



        AppLaunch.getInstance().registerUser("norton-android", new AppLaunchResponseListener() {
            @Override
            public void onSuccess(AppLaunchResponse appLaunchResponse) {
                Log.d("HomeActivity","Init Successful - "+ appLaunchResponse.getResponseText());

            }

            @Override
            public void onFailure(AppLaunchFailResponse appLaunchFailResponse) {
                Log.d("HomeActivity","Init Failed - "+ appLaunchFailResponse.getErrorMsg());
            }
        });
         }

}
