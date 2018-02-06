/*
 *     Copyright 2018 IBM Corp.
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package engage.com.applaunchsample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ibm.mobile.applaunch.android.AppLaunchFailResponse;
import com.ibm.mobile.applaunch.android.AppLaunchResponse;
import com.ibm.mobile.applaunch.android.api.AppLaunch;
import com.ibm.mobile.applaunch.android.api.AppLaunchConfig;
import com.ibm.mobile.applaunch.android.api.AppLaunchListener;
import com.ibm.mobile.applaunch.android.api.AppLaunchUser;
import com.ibm.mobile.applaunch.android.api.ICRegion;
import com.ibm.mobile.applaunch.android.api.RefreshPolicy;

public class HomeActivity extends AppCompatActivity implements AppLaunchListener {

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

        AppLaunchConfig appLaunchConfig = new AppLaunchConfig.Builder().eventFlushInterval(10).cacheExpiration(1).fetchPolicy(RefreshPolicy.BACKGROUND_REFRESH).deviceId("f88ky8u").build();
        AppLaunchUser appLaunchUser = new AppLaunchUser.Builder().userId("norton").custom("test","newtest").build();
    //    AppLaunchUser appLaunchUser = new AppLaunchUser.Builder().userId("norton").build();
      // AppLaunch.getInstance().init(getApplication(), ICRegion.US_SOUTH_STAGING,"852301c1-128e-4b11-80f5-9d113cdb976f","ecf53cdf-40ca-4239-9c49-15cdd88a36e7",appLaunchConfig,appLaunchUser,this);
        AppLaunch.getInstance().init(getApplication(), ICRegion.US_SOUTH,"d28481fc-948b-44ff-9e75-e637fa3fbaf3","6e113b79-cbfe-458d-8fd8-4840547b3241",appLaunchConfig,appLaunchUser,this);




      //  AppLaunchSDK.getInstance().initApp(getApplication(), BMSClient.REGION_US_SOUTH,"4d370cdc-bfbb-4512-bb7b-1c37b512fe39","18bbdb7b-910b-4364-8372-83611e1970ae");

//        AppLaunchParameters appLaunchParameters = new AppLaunchParameters();
//        appLaunchParameters.put("key","value");
//        appLaunchParameters.put("something",false);,
//        appLaunchParameters.put("another",1);

       // AppLaunch.getInstance().registerUser("norton");



//        AppLaunchSDK.getInstance().registerUser("norton-android", new AppLaunchResponseListener() {
//            @Override
//            public void onSuccess(AppLaunchResponse appLaunchResponse) {
//                Log.d("HomeActivity","Init Successful - "+ appLaunchResponse.getResponseText());
//            }
//
//            @Override
//            public void onFailure(AppLaunchFailResponse appLaunchFailResponse) {
//                Log.d("HomeActivity","Init Failed - "+ appLaunchFailResponse.getErrorMsg());
//            }
//        });
         }

    @Override
    public void onSuccess(AppLaunchResponse response) {
        Log.i("onSuccess",response.getResponseJSON().toString());
      //  AppLaunch.getInstance().displayInAppMessages(HomeActivity.this);
        AppLaunch.getInstance().destroy(this);
    }

    @Override
    public void onFailure(AppLaunchFailResponse failResponse) {
        Log.i("Error",failResponse.getErrorMsg()+failResponse.getErrorCode());
    }
}
