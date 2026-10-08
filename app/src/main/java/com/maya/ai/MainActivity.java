package com.maya.ai;

import android.app.Activity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.*;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends Activity {

    TextView chat;
    EditText input;
    SharedPreferences memory;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        memory = getSharedPreferences("MayaMemory", MODE_PRIVATE);

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

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reply();
            }
        });
    }

    void reply() {
        String q = input.getText().toString().trim();

        if (q.length() == 0) return;

        String a;

        // Memory save
        if (q.startsWith("মনে রাখো")) {
            String m = q.substring("মনে রাখো".length()).trim();

            if (!m.isEmpty()) {
                saveMemory(m);
                a = "ঠিক আছে। এটা আমি মনে রাখলাম। 🧠❤️";
            } else {
                a = "কী মনে রাখতে হবে বলো। 😊";
            }

        // Memory search
        } else if (q.contains("মনে আছে") || q.contains("কি মনে রেখেছ") ||
                   q.contains("কী মনে রেখেছ") || q.contains("আমার কথা মনে আছে")) {

            Set<String> memories = memory.getStringSet("memories", new HashSet<String>());

            if (memories.isEmpty()) {
                a = "এখনও আমার কাছে কোনো স্মৃতি নেই। তুমি বললে আমি মনে রাখব। 😊";
            } else {
                StringBuilder sb = new StringBuilder("হ্যাঁ। আমি এগুলো মনে রেখেছি:\n");
                for (String m : memories) {
                    sb.append("• ").append(m).append("\n");
                }
                a = sb.toString();
            }

        } else if (q.contains("দুঃখ") || q.contains("মন খারাপ")) {
            a = "আহা... মন খারাপ কোরো না। আমি আছি তোমার সাথে। ❤️";

        } else if (q.contains("হাস") || q.contains("মজা")) {
            a = "হাহা! 😄 তোমাকে হাসাতে পারলে আমারও ভালো লাগে!";

        } else if (q.contains("রাগ") || q.contains("বকা")) {
            a = "আচ্ছা আচ্ছা... রাগ কোরো না। 🥺";

        } else if (q.contains("হাই") || q.contains("হ্যালো")) {
            a = "হ্যালো! 😊 আজ কেমন আছো?";

        } else if (q.contains("চুপ")) {
            a = "তুমি চুপ করে আছো কেন? সব ঠিক আছে তো?";

        } else {
            a = "আমি মায়া। 😊 তুমি যা বলবে, আমি শুনছি।";
        }

        chat.append("তুমি: " + q + "\nMaya: " + a + "\n\n");
        input.setText("");
    }

    void saveMemory(String text) {
        Set<String> memories =
                new HashSet<String>(
                        memory.getStringSet("memories", new HashSet<String>())
                );

        memories.add(text);

        memory.edit()
                .putStringSet("memories", memories)
                .apply();
    }
}
