package west.code.recyclerparsequeryadapterexample.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import west.code.recyclerparsequeryadapterexample.R;
import west.code.recyclerparsequeryadapterexample.fragments.MainFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.container,new MainFragment()).commit();
    }
}
