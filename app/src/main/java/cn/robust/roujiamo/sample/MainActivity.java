package cn.robust.roujiamo.sample;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.Toast;

import cn.robust.roujiamo.R;
import cn.robust.roujiamo.library.Burger;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Burger burger = (Burger) findViewById(R.id.burger);
//        burger.post(new Runnable() {
//            @Override
//            public void run() {
//                burger.setOpen(true, false);
//            }
//        });
        burger.setOnOpenListener(new Burger.OnOpenListener() {
            @Override
            public void onOpen(boolean open) {
                if(open){
                    Toast.makeText(MainActivity.this, "open", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "close", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
