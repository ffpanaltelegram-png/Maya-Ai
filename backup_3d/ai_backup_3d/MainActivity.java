package com.maya.ai;

import android.app.Activity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.view.animation.*;
import android.widget.*;
import android.graphics.drawable.Drawable;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends Activity {

    TextView chat;
    EditText input;
    ImageView maya;
    SharedPreferences memory;

    int dp(float v) {
        return (int) (v * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        memory = getSharedPreferences("MayaMemory", MODE_PRIVATE);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(12), dp(16), dp(12));

        TextView title = new TextView(this);
        title.setText("🌸 Maya");
        title.setTextSize(28);
        title.setTextColor(Color.rgb(110,60,180));
        root.addView(title);

        maya = new ImageView(this);
        maya.setImageResource(com.maya.ai.R.drawable.maya_character);
        maya.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        maya.setAdjustViewBounds(true);
        root.addView(maya, new LinearLayout.LayoutParams(-1, dp(280)));

        chat = new TextView(this);
        chat.setText("Maya: হাই! আমি মায়া। 😊\nতোমার সাথে কথা বলতে আমার ভালো লাগছে।\n\n");
        chat.setTextSize(18);

        ScrollView scroll = new ScrollView(this);
        scroll.addView(chat);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        LinearLayout bar = new LinearLayout(this);

        input = new EditText(this);
        input.setHint("কিছু লিখো...");

        Button send = new Button(this);
        send.setText("পাঠাও");

        bar.addView(input, new LinearLayout.LayoutParams(0, -2, 1));
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

    void react(String type) {
        maya.animate().cancel();
        maya.setRotation(0f);
        maya.setScaleX(1f);
        maya.setScaleY(1f);
        maya.setTranslationX(0f);
        maya.setTranslationY(0f);

        AnimatorSet set = new AnimatorSet();

        if ("happy".equals(type)) {
            ObjectAnimator x = ObjectAnimator.ofFloat(maya, "scaleX", 1f, 1.05f, 1f);
            ObjectAnimator y = ObjectAnimator.ofFloat(maya, "scaleY", 1f, 1.05f, 1f);
            set.playTogether(x, y);
            set.setDuration(500);
        } else if ("sad".equals(type)) {
            ObjectAnimator y = ObjectAnimator.ofFloat(maya, "translationY", 0f, dp(10), 0f);
            ObjectAnimator a = ObjectAnimator.ofFloat(maya, "alpha", 1f, 0.78f, 1f);
            set.playTogether(y, a);
            set.setDuration(650);
        } else if ("angry".equals(type)) {
            ObjectAnimator r = ObjectAnimator.ofFloat(maya, "rotation", 0f, -3f, 3f, -2f, 2f, 0f);
            set.playTogether(r);
            set.setDuration(500);
        } else if ("wave".equals(type)) {
            ObjectAnimator x = ObjectAnimator.ofFloat(maya, "translationX", 0f, dp(8), -dp(8), dp(6), 0f);
            set.playTogether(x);
            set.setDuration(700);
        } else {
            ObjectAnimator y = ObjectAnimator.ofFloat(maya, "translationY", 0f, -dp(5), 0f);
            set.playTogether(y);
            set.setDuration(450);
        }

        set.start();
    }

    void reply() {
        String q = input.getText().toString().trim();

        if (q.length() == 0) return;

        String a;
        String reaction = "normal";

        if (q.startsWith("মনে রাখো") ||
            q.startsWith("আমার নাম ") ||
            q.contains("মনে রেখো")) {

            String m = q;

            if (q.startsWith("মনে রাখো")) {
                m = q.substring("মনে রাখো".length()).trim();
            } else if (q.contains("মনে রেখো")) {
                m = q.substring(q.indexOf("মনে রেখো") + "মনে রেখো".length()).trim();
            }

            if (!m.isEmpty()) {
                saveMemory(m);
                a = "ঠিক আছে। এটা আমি মনে রাখলাম। 🧠❤️";
                reaction = "happy";
            } else {
                a = "কী মনে রাখতে হবে বলো। 😊";
                reaction = "wave";
            }

        } else if (q.contains("মনে আছে") || q.contains("কি মনে রেখেছ") ||
                   q.contains("কী মনে রেখেছ") || q.contains("আমার কথা মনে আছে")) {

            Set<String> memories = memory.getStringSet("memories", new HashSet<String>());

            if (memories.isEmpty()) {
                a = "এখনও আমার কাছে কোনো স্মৃতি নেই। তুমি বললে আমি মনে রাখব। 😊";
                reaction = "sad";
            } else {
                StringBuilder sb = new StringBuilder("হ্যাঁ। আমি এগুলো মনে রেখেছি:\n");
                for (String m : memories) {
                    sb.append("• ").append(m).append("\n");
                }
                a = sb.toString();
                reaction = "happy";
            }

        } else if (q.contains("দুঃখ") || q.contains("মন খারাপ")) {
            a = "আহা... মন খারাপ কোরো না। আমি আছি তোমার সাথে। ❤️";
            reaction = "sad";

        } else if (q.contains("হাস") || q.contains("মজা")) {
            a = "হাহা! 😄 তোমাকে হাসাতে পারলে আমারও ভালো লাগে!";
            reaction = "happy";

        } else if (q.contains("রাগ") || q.contains("বকা")) {
            a = "আচ্ছা আচ্ছা... রাগ কোরো না। 🥺";
            reaction = "angry";

        } else if (q.contains("হাই") || q.contains("হ্যালো")) {
            a = "হ্যালো! 😊 আজ কেমন আছো?";
            reaction = "wave";

        } else if (q.contains("চুপ")) {
            a = "তুমি চুপ করে আছো কেন? সব ঠিক আছে তো?";
            reaction = "sad";

        } else {
            a = "আমি মায়া। 😊 তুমি যা বলবে, আমি শুনছি।";
            reaction = "normal";
        }

        chat.append("তুমি: " + q + "\nMaya: " + a + "\n\n");
        input.setText("");
        react(reaction);
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
