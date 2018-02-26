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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.ibm.mobile.applaunch.android.api.AppLaunch;

import java.util.ArrayList;

public class PizzaDetailsActivity extends AppCompatActivity   {

    Button button=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pizza_details);
         button = (Button) findViewById(R.id.touchid_checkout);
     //   AppLaunchSDK.getInstance().getActions(PizzaDetailsActivity.this);
        button.setVisibility(View.GONE);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> metrics = new ArrayList<String>();
                metrics.add("_7ybkgux2n");
                AppLaunch.getInstance().sendMetrics(metrics);
            }
        });

        try{
            if(AppLaunch.getInstance().isFeatureEnabled("_u9u6kkwud")){
                String value = AppLaunch.getInstance().getPropertyOfFeature("_u9u6kkwud","_efggnarpr");
                if(Boolean.valueOf(value)){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            button.setVisibility(View.VISIBLE);
                        }
                    });

                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            button.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }


    public void onFeaturesReceived(String features) {
        try{
            if(AppLaunch.getInstance().isFeatureEnabled("_u9u6kkwud")){
                String value = AppLaunch.getInstance().getPropertyOfFeature("_u9u6kkwud","_efggnarpr");
                if(Boolean.valueOf(value)){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            button.setVisibility(View.VISIBLE);
                        }
                    });

                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            button.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
