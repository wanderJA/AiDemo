package com.wander.aidemo

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_scrolling.*
import kotlinx.android.synthetic.main.content_scrolling.*
import org.json.JSONException
import org.json.JSONObject


class ScrollingActivity : AppCompatActivity(), IVoiceAsrCallback {
    override fun onReadyForSpeech(var1: Bundle?) {
        appendLog("语音就绪")
    }

    override fun onBeginningOfSpeech() {
        appendLog("检测到语音")
    }

    override fun onRmsChanged(var1: Float) {
    }

    override fun onBufferReceived(var1: ByteArray?) {
    }

    override fun onEndOfSpeech() {
        appendLog("检测到语音结束")
    }

    override fun onError(var1: Int) {
        appendLog("发生错误:$var1")
    }

    override fun onResults(bundle: Bundle?) {
        try {
            val asrResult = bundle?.getStringArrayList(IVoiceAsrCallback.RESULTS_RECOGNITION)!![0]
            appendLog("最终结果： $asrResult")
        } catch (e: Throwable) {
        }

    }

    override fun onPartialResults(bundle: Bundle?) {
        try {
            val asrResult = bundle?.getStringArrayList(IVoiceAsrCallback.RESULTS_RECOGNITION)!![0]
            appendLog("中间结果： $asrResult")
        } catch (e: Throwable) {
        }

    }

    override fun onIntent(command: String?) {
        try {
            val json = JSONObject(command)
            val type = json.optInt("type", 0)
            var cmdType = ""
            when (type) {
                1 -> cmdType = "控制"
                2 -> cmdType = "播放"
                3 -> cmdType = "搜索结果"
                4 -> cmdType = "网页展示"
                5 -> cmdType = "导航"
                6 -> cmdType = "度秘技能"
                7 -> cmdType = "自定义技能"
                8 -> cmdType = "只看他"
                9 -> cmdType = "场景注册"
                10 -> cmdType = "生活信息"
                11 -> cmdType = "截图识别"
                12 -> cmdType = "截图识别结果"
                13 -> cmdType = "情节跳转"
                14 -> cmdType = "频道节目单"
                15 -> cmdType = "纯搜索意图"
                16 -> cmdType = "明星信息"
                else -> cmdType = "未知指令$type"
            }
            appendLog("指令结果: $cmdType")
        } catch (e: JSONException) {
            appendLog("解析指令失败")
        }

    }

    private val tag = "ScrollingActivity"
    private fun appendLog(line: String) {
        Log.d(tag,line)
        runOnUiThread { des.append(line+"\n") }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        initAi()
    }

    private fun initAi() {

        ReaderAi.initAi(applicationContext)
        fabOpen.setOnClickListener {    ReaderAi.voiceRecognition(applicationContext,this,false) }
        fab.setOnClickListener {
            releaseSearchAsr()
        }
    }

    private fun releaseSearchAsr() {
        Log.d(tag, "leave search")
        des.text = ""
        ReaderAi.releaseRecognizer()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
