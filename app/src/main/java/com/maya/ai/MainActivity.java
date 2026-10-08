package com.maya.ai;

import android.app.*;
import android.os.*;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.view.*;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {
    TextView chat;
    EditText input;
    TextToSpeech tts;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24,24,24,24);

        TextView title = new TextView(this);
        title.setText("🌸 Maya");
        title.setTextSize(28);
        title.setTextColor(Color.rgb(110,60,180));
        root.addView(title);

        chat = new TextView(this);
        chat.setText("Maya: হাই! আমি মায়া। 😊\nতোমার সাথে কথা বলতে আমার ভালো লাগছে।\n\n");
        chat.setTextSize(18);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(chat);
        root.addView(scroll, new LinearLayout.LayoutParams(-1,0,1));

        LinearLayout bar = new LinearLayout(this);
        input = new EditText(this);
        input.setHint("কিছু লিখো...");
        Button send = new Button(this);
        send.setText("পাঠাও");
        bar.addView(input, new LinearLayout.LayoutParams(0,-2,1));
        bar.addView(send);
        root.addView(bar);

        setContentView(root);

        tts = new TextToSpeech(this, s -> {
            if (s == TextToSpeech.SUCCESS)
                tts.setLanguage(new Locale("bn","IN"));
        });

        send.setOnClickListener(v -> reply());
    }

    boolean isInternetAvailable() {
    ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
    if (cm == null) return false;
    android.net.Network network = cm.getActiveNetwork();
    if (network == null) return false;
    NetworkCapabilities nc = cm.getNetworkCapabilities(network);
    return nc != null && nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
}

void reply() {
        String q = input.getText().toString().trim();
        if(q.isEmpty()) return;
        String a;
        boolean online = isInternetAvailable();
        String x=q.toLowerCase();

        if(x.contains("দুঃখ") || x.contains("মন খারাপ"))
            a="আহা... মন খারাপ কোরো না। আমি আছি তোমার সাথে। ❤️";
        else if(x.contains("হাস") || x.contains("মজা"))
            a="হাহা! 😄 তোমাকে হাসাতে পারলে আমারও ভালো লাগে!";
        else if(x.contains("রাগ") || x.contains("বকা"))
            a="আচ্ছা আচ্ছা... রাগ কোরো না। 🥺 আমি শান্ত হয়ে গেলাম।";
        else if(x.contains("হাই") || x.contains("হ্যালো"))
            a="হ্যালো! 😊 আজ কেমন আছো?";
        else if(x.contains("চুপ"))
            a="তুমি চুপ করে আছো কেন? সব ঠিক আছে তো?";
        else
            a = online ? "আমি ইন্টারনেটে সংযুক্ত আছি 🌐😊। এখনো আমার AI brain/API যোগ করা হয়নি, সেটা পরে বসাব।" : "এই মুহূর্তে ইন্টারনেট সংযোগ নেই 📡।";

        chat.append("তুমি: "+q+"\nMaya: "+a+"\n\n");
        input.setText("");
        tts.speak(a, TextToSpeech.QUEUE_FLUSH, null, "maya");
    }

    @Override protected void onDestroy() {
        if(tts!=null) tts.shutdown();
        super.onDestroy();
    }
}
