package engage.com.applaunchsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.applaunch.api.AppLaunch;
import com.applaunch.api.AppLaunchActions;

import java.util.ArrayList;

public class PizzaDetailsActivity extends AppCompatActivity implements AppLaunchActions{

    Button button=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pizza_details);
         button = (Button) findViewById(R.id.touchid_checkout);
        AppLaunch.getInstance().getActions(PizzaDetailsActivity.this);
        button.setVisibility(View.GONE);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> metrics = new ArrayList<String>();
                metrics.add("_w7xos3fqh");
                AppLaunch.getInstance().sendMetrics(metrics);
            }
        });


    }

    @Override
    public void onFeaturesReceived(String features) {
        try{
            if(AppLaunch.getInstance().isFeatureEnabled("_intics0gg")){
                String value = AppLaunch.getInstance().getVariableForFeature("_intics0gg","_4tganfvfg");
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
