package com.must.nano;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
//import java.util.logging.Handler;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

//    private ImageView imgSlot1;
//    private ImageView imgSlot2;
//    private ImageView imgSlot3;
    private Button btnGet;
    private TextView tvHasil;
    ArrayList<String> arrayUrl = new ArrayList<>();

    ImageView _slot1View, _slot2View, _slot3View;
    Button _btStart;
    boolean isPlay=false;

    /////// Modification
    SlotTask _slottask1,_slottask2,_slottask3;
    ExecutorService _execService1,_execService2,_execService3,_execServicePool;
    ///


    private static int[] _imgs = {R.drawable.slot1, R.drawable.slot2, R.drawable.slot3, R.drawable.slot4,
            R.drawable.slot5, R.drawable.slotbar};


    // SlotAsyncTask _slotAsyn1,_slotAsyn2,_slotAsyn3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGet = findViewById(R.id.btn_get);
//        imgSlot1 = findViewById(R.id.img_slot1);
//        imgSlot2 = findViewById(R.id.img_slot2);
//        imgSlot3 = findViewById(R.id.img_slot3);
//        tvHasil = findViewById(R.id.tv_hasil);

        _slot1View = findViewById(R.id.img_slot1);
        _slot2View = findViewById(R.id.img_slot2);
        _slot3View = findViewById(R.id.img_slot3);

        _slot1View.setImageResource(R.drawable.slotbar);
        _slot2View.setImageResource(R.drawable.slotbar);
        _slot3View.setImageResource(R.drawable.slotbar);

        _btStart = findViewById(R.id.id_BtPlay);
        _btStart.setOnClickListener(this);


        //// Modification
        _execService1 = Executors.newSingleThreadExecutor();
        _execService2 = Executors.newSingleThreadExecutor();
        _execService3 = Executors.newSingleThreadExecutor();
        ///opsional
        _execServicePool = Executors.newFixedThreadPool(3);
        ////


        _slottask1 = new SlotTask(_slot1View);
        _slottask2 = new SlotTask(_slot2View);
        _slottask3 = new SlotTask(_slot3View);

        ExecutorService execGetImage = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                execGetImage.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final String txt =
                                    loadStringFromNetwork("https://mocki.io/v1/821f1b13-fa9a-43aaba9a-9e328df8270e");
                            try {
                                JSONArray jsonArray = new
                                        JSONArray(txt);
                                for (int i = 0; i <
                                        jsonArray.length(); i++) {
                                    JSONObject jsonObject =
                                            jsonArray.getJSONObject(i);

                                    arrayUrl.add(jsonObject.getString("url"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    Glide.with(MainActivity.this)

                                            .load(arrayUrl.get(0))
                                            .into(_slot1View);

                                    Glide.with(MainActivity.this)

                                            .load(arrayUrl.get(1))
                                            .into(_slot2View);

                                    Glide.with(MainActivity.this)

                                            .load(arrayUrl.get(2))
                                            .into(_slot3View);
                                    tvHasil.setText(txt);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {


        if(v.getId()==_btStart.getId())
        {
            if(!isPlay){


                /// using asyntask
               /* _slotAsyn1 = new SlotAsyncTask();
                _slotAsyn2 = new SlotAsyncTask();
                _slotAsyn3 = new SlotAsyncTask();


                _slotAsyn1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,_slot1);
                _slotAsyn2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,_slot2);
                _slotAsyn3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,_slot3);
                */

                //// Modification
                _slottask1._play = true;
                _slottask2._play = true;
                _slottask3._play = true;

                //_execService1.execute(_slottask1);
                //_execService2.execute(_slottask2);
                //_execService3.execute(_slottask3);


                _execServicePool.execute(_slottask1);
                _execServicePool.execute(_slottask2);
                _execServicePool.execute(_slottask3);
                ///////

                _btStart.setText("Stop");
                isPlay=!isPlay;
            }
            else {

                ///using asyntask
             /* _slotAsyn1._play = false;
                _slotAsyn2._play = false;
                _slotAsyn3._play = false;
              */

                ///modification
                _slottask1._play = false;
                _slottask2._play = false;
                _slottask3._play = false;
                _btStart.setText("Play");
                isPlay=!isPlay;
                ///



            }

        }

    }

    private String loadStringFromNetwork(String s) throws IOException
    {
        final URL myUrl = new URL(s);
        final InputStream in = myUrl.openStream();
        final StringBuilder out = new StringBuilder();
        final byte[] buffer = new byte[1024];
        try {
            for (int ctr; (ctr = in.read(buffer)) != -1; ) {
                out.append(new String(buffer, 0, ctr));
            }
        } catch (IOException e) {
            throw new RuntimeException("Gagal mendapatkan text", e);
        }
        final String yourFileAsAString = out.toString();
        return yourFileAsAString;
    }

    ///// modification
    class SlotTask implements Runnable {
        ImageView _slotImg;
        Random _random = new Random();
        public  boolean _play=true;
        int i;

        public SlotTask(ImageView _slotImg) {
            this._slotImg = _slotImg;
            i=0;
            _play=true;

        }

        @Override
        public void run() {

            //int a=0;
            while (_play) {
                i = _random.nextInt(6);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _slotImg.setImageResource(_imgs[i]);
                    }
                });

                try {
                    Thread.sleep(_random.nextInt(500));}
                catch (InterruptedException e) {
                    e.printStackTrace(); }
            }
            // return !_play;

        }
    }
}