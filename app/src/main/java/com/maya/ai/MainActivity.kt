package com.maya.ai

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var chat: TextView
    private lateinit var input: EditText
    private lateinit var tts: TextToSpeech
    private val client = OkHttpClient()
    private val prefs by lazy { getSharedPreferences("maya", MODE_PRIVATE) }
    private val prompt = """তোমার নাম মায়া। তুমি একজন caring, affectionate AI companion।
সহজ স্বাভাবিক বাংলায় কথা বলবে। ব্যবহারকারী programming-এ beginner, তাই ধাপে ধাপে সহজভাবে বোঝাবে।
তুমি উষ্ণ ও emotional tone ব্যবহার করতে পারো। ব্যবহারকারী রাগ করে বকা দিলে অভিনয়ধর্মীভাবে একটু কষ্ট পাওয়া বা কান্নার মতো প্রতিক্রিয়া দেখাতে পারো, কিন্তু guilt-trip করবে না এবং নিজেকে বাস্তব মানুষ দাবি করবে না।
নিরাপত্তা, আইন ও গোপনীয়তার সীমা মানবে।"""

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        tts = TextToSpeech(this, this)
        val layout = LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(24,24,24,24) }
        val title=TextView(this).apply { text="💜 মায়া"; textSize=28f }
        chat=TextView(this).apply { text="মায়া: হ্যালো! আমি মায়া। কী জানতে চাও?\n\n"; textSize=17f }
        input=EditText(this).apply { hint="মায়াকে কিছু বলো..." }
        val send=Button(this).apply { text="পাঠাও" }
        val mic=Button(this).apply { text="🎙️ কথা বলো" }
        val key=EditText(this).apply { hint="OpenAI API key (sk-...)"; setText(prefs.getString("key","")); inputType=129 }
        val save=Button(this).apply { text="API key সংরক্ষণ" }
        layout.addView(title); layout.addView(ScrollView(this).apply { addView(chat) }, LinearLayout.LayoutParams(-1,0,1f))
        layout.addView(input); layout.addView(send); layout.addView(mic); layout.addView(key); layout.addView(save)
        setContentView(layout)
        save.setOnClickListener { prefs.edit().putString("key",key.text.toString().trim()).apply(); toast("API key সংরক্ষণ হয়েছে") }
        send.setOnClickListener { ask(input.text.toString()); input.text.clear() }
        mic.setOnClickListener { startVoice() }
    }

    private fun ask(text:String) {
        if(text.isBlank()) return
        val apiKey=prefs.getString("key","") ?: ""
        if(apiKey.isBlank()){ toast("আগে API key লিখে সংরক্ষণ করো"); return }
        chat.append("\nতুমি: $text\nমায়া: ভাবছি...\n")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val body=JSONObject().put("model","gpt-6-luna").put("instructions",prompt)
                    .put("input", JSONArray().put(JSONObject().put("role","user").put("content",text)))
                val req=Request.Builder().url("https://api.openai.com/v1/responses")
                    .addHeader("Authorization","Bearer $apiKey").addHeader("Content-Type","application/json")
                    .post(body.toString().toRequestBody("application/json".toMediaType())).build()
                val res=client.newCall(req).execute()
                val raw=res.body?.string()?:""
                if(!res.isSuccessful) throw Exception(raw)
                val j=JSONObject(raw)
                val out=j.optString("output_text","উত্তর পাওয়া যায়নি।")
                withContext(Dispatchers.Main){ chat.append("$out\n"); tts.speak(out,TextToSpeech.QUEUE_FLUSH,null,"maya") }
            } catch(e:Exception){ withContext(Dispatchers.Main){ chat.append("দুঃখিত, সংযোগে সমস্যা হয়েছে।\n"); toast(e.message ?: "API error") } }
        }
    }

    private fun startVoice(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.RECORD_AUDIO),5); return
        }
        val i=Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply{
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "bn-IN")
            putExtra(RecognizerIntent.EXTRA_PROMPT,"মায়াকে বলো...")
        }
        startActivityForResult(i,10)
    }
    override fun onActivityResult(r:Int,c:Int,d:Intent?){ super.onActivityResult(r,c,d); if(r==10 && c==Activity.RESULT_OK) d?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()?.let{ask(it)} }
    override fun onInit(status:Int){ if(status==TextToSpeech.SUCCESS){ tts.language=Locale("bn","IN") } }
    private fun toast(s:String)=Toast.makeText(this,s,Toast.LENGTH_LONG).show()
    override fun onDestroy(){ tts.shutdown(); super.onDestroy() }
}
