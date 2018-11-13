package com.example.opilane.primenumbers;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText txtFrom, txtTo;
    private RadioButton rbtnPrim, rbtnSec1, rbtnSec2;
    private ListView lstPrime;
    private ArrayAdapter<Long> adapter;
    private List<Long> primes = new ArrayList<>();


    private Handler messageHandler;
    private Handler primeHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtFrom = findViewById(R.id.etFrom);
        txtTo = findViewById(R.id.etTo);
        rbtnPrim = findViewById(R.id.rbtnPrime);
        rbtnPrim = findViewById(R.id.rbtnSec1);
        rbtnPrim = findViewById(R.id.rbtnSec2);
        lstPrime = findViewById(R.id.lstPrimes);

        lstPrime.setAdapter(adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1));

        messageHandler = new MessageHandler(this);
        primeHandler = new PrimeHandler(this, adapter);
    }

    public void onClear(View view) {
        adapter.clear();
    }

    public void onCalculate(View view) {
        try {
            long from = Long.parseLong(txtFrom.getText().toString());
            long to = Long.parseLong(txtTo.getText().toString());
            if (from > 0 && to > from && to - from <= 1000)
            {
                if (rbtnPrim.isChecked()) calc1(from,to);
                else if (rbtnSec1.isChecked()) calc2(from,to);
                else calce3(from,to);
                return;
            }
        }
        catch (Exception ex) {
            Toast.makeText(this, "Illegal values", Toast.LENGTH_LONG).show();
        }
    }

    private void calce3(long from, long to) {
        final long a = from;
        final long b = to;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (long t = a; t <= b; ++t) if (isPrime(t))
                {
                    Message msg = primeHandler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putLong("prime", t);
                    msg.setData(bundle);
                    primeHandler.sendMessage(msg);
                }
                messageHandler.sendEmptyMessage(0);
            }
        };
        (new Thread(runnable)).start();
    }

    private void calc2(long from, long to) {
        final long a = from;
        final long b = to;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (long t = a; t <= b; ++t) if (isPrime(t)) {
                    adapter.add(t);
                }
            }
        };
        (new Thread(runnable)).start();
    }

    private void calc1(long a, long b) {
        for (long t = a; t <= b; ++t) if (isPrime(t))
        {adapter.add(t);}
    }

    //Testib kas number on algarv. Klass defineerib mitte vähem kui 10;
    private  boolean isPrime(long n) {
        if (n == 2 || n == 3 || n == 5 || n == 7) return true;
        if (n < 11 || n % 2 == 0) return false;
        for (long t = 3, m = (long)Math.sqrt(n) + 1; t <= m; t += 2) if (n % t == 0) return false;
        return true;
    }
}
class MessageHandler extends Handler {
    private Context context;

    public MessageHandler(Context context) {
        this.context = context;
    }

    @Override
    public void handleMessage(Message msg) {
        Toast.makeText(context, "Primes generated", Toast.LENGTH_LONG).show();
    }
}
class PrimeHandler extends Handler {
    private Context context;
    private ArrayAdapter<Long> adapter;

    public PrimeHandler(Context context, ArrayAdapter<Long> adapter) {
        this.context = context;
        this.adapter = adapter;
    }

    @Override
    public void handleMessage(Message msg) {
        Bundle bundle = msg.getData();
        Long prime = bundle.getLong("prime");
        adapter.add(prime);
    }
}