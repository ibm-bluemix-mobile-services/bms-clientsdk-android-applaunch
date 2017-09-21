package engage.com.applaunchsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.engage.EngageFailResponse;
import com.engage.EngageResponse;
import com.engage.EngageResponseListener;
import com.engage.api.EngageClient;

import java.util.ArrayList;

public class PizzaDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pizza_details);
        final Button button = (Button) findViewById(R.id.touchid_checkout);
        button.setVisibility(View.GONE);
        EngageClient.getInstance().isFeatureEnabled("_fagwqc8so", new EngageResponseListener() {
            @Override
            public void onSuccess(EngageResponse engageResponse) {

                final String text = EngageClient.getInstance().getVariableForFeature("_fagwqc8so","_x7t4re1pq");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(null!=text){
                            button.setVisibility(View.VISIBLE);
                            button.setText(text);
                        }
                    }
                });

            }

            @Override
            public void onFailure(EngageFailResponse engageFailResponse) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        button.setVisibility(View.GONE);
                    }
                });
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> metrics = new ArrayList<String>();
                metrics.add("_w7xos3fqh");
                EngageClient.getInstance().sendMetrics(metrics);
            }
        });


    }
}
