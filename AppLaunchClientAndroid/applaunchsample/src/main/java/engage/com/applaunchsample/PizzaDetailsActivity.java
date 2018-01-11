package engage.com.applaunchsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.ibm.mobile.applaunch.android.api.AppLaunchSDK;

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
                AppLaunchSDK.getInstance().sendMetrics(metrics);
            }
        });

        try{
            if(AppLaunchSDK.getInstance().isFeatureEnabled("_j98xjn74z")){
                String value = AppLaunchSDK.getInstance().getPropertyOfFeature("_j98xjn74z","_1pgqug4y5");
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
            if(AppLaunchSDK.getInstance().isFeatureEnabled("_j98xjn74z")){
                String value = AppLaunchSDK.getInstance().getPropertyOfFeature("_j98xjn74z","_1pgqug4y5");
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
