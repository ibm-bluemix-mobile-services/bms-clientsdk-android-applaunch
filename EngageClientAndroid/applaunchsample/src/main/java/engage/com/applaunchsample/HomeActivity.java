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
import com.applaunch.api.AppLaunchActions;
import com.applaunch.api.AppLaunch;
import com.applaunch.api.AppLaunchConfig;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;

public class HomeActivity extends AppCompatActivity implements AppLaunchActions {

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



    private void inializeEngage(){

      //  EngageConfig engageConfig = new EngageConfig(getApplication(), BMSClient.REGION_US_SOUTH,"ef3c5a4f-6547-429d-90d5-d49cdffd71c6","4a17f904-8e87-4d92-b7a0-342974955710","norton");
        AppLaunchConfig appLaunchConfig = new AppLaunchConfig(getApplication(), BMSClient.REGION_US_SOUTH,"f31df428-59a1-4418-8ae6-f886ce50c502","f8bbc9c2-17c2-4a1f-a21b-50c753e3d9e1","norton");
     //   EngageConfig engageConfig = new EngageConfig(getApplication(), BMSClient.REGION_US_SOUTH,"8f6a7c1a-18f6-431c-8159-58396b46c160","6ceeffae-ace2-43c7-b070-6c9fd0bf3ffb","norton-new");
        AppLaunch.getInstance().registerUser(appLaunchConfig, new AppLaunchResponseListener() {
            @Override
            public void onSuccess(AppLaunchResponse appLaunchResponse) {
                Log.d("HomeActivity","Init Successful - "+ appLaunchResponse.getResponseText());
                AppLaunch.getInstance().getActions(HomeActivity.this);
            }

            @Override
            public void onFailure(AppLaunchFailResponse appLaunchFailResponse) {
                Log.d("HomeActivity","Init Failed - "+ appLaunchFailResponse.getErrorMsg());
            }
        });
    }

    @Override
    public void onFeaturesReceived(String features) {

    }
}
