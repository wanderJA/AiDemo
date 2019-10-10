package com.wander.aidemo

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.webkit.ValueCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import com.iqiyi.homeai.core.HomeAIEnv
import com.iqiyi.homeai.core.HomeAISdk
import com.iqiyi.homeai.core.HomeAISdkClient
import com.iqiyi.homeai.core.player.IPlayerStateListener
import com.iqiyi.homeai.core.player.IScreen
import com.iqiyi.homeai.core.player.ItemDetail
import com.iqiyi.homeai.core.player.ItemList
import kotlinx.android.synthetic.main.activity_scrolling.*
import java.util.*


class ScrollingActivity : AppCompatActivity(), HomeAISdkClient, IScreen {
    override fun previous() {
    }

    override fun isOn(): Boolean {
        return true
    }

    override fun seek(p0: Boolean, p1: Int) {
    }

    override fun next() {
    }

    override fun selectItem(p0: Int): Boolean {
        return false
    }

    override fun resume() {
    }

    override fun setResolution(p0: Boolean, p1: Int) {
    }

    override fun onVoiceWakeup(p0: ValueCallback<Boolean>?) {
    }

    override fun navigateBackforward(p0: Int): Boolean {
        return false
    }

    override fun isPlaying(): Boolean {
        return false
    }

    override fun setPlayerStateListener(p0: IPlayerStateListener?) {
    }

    override fun playVideo(
        p0: ItemDetail?,
        p1: String?,
        p2: String?,
        p3: String?,
        p4: MutableList<String>?
    ) {
    }

    override fun showPage(p0: String?) {
    }

    override fun pause() {
    }

    override fun destroy() {
    }

    override fun volumnAdjust(p0: Boolean, p1: Float) {
    }

    override fun mute(p0: Boolean) {
    }

    override fun showSearchResult(p0: ItemList?, p1: Int, p2: Int) {
    }

    override fun switchPage(p0: Boolean, p1: Int) {
    }

    override fun onVoiceSleep(p0: ValueCallback<Boolean>?) {
    }

    override fun stop() {
    }

    override fun playEpisode(p0: Int) {
    }

    override fun showNotification(p0: String?) {
    }

    override fun skipEd() {
    }

    override fun skipOp() {
    }

    override fun onAnswerToUnknownIntent(p0: String?): Boolean {
        return true
    }

    override fun onCustomSkill(p0: String?, p1: String?, p2: HashMap<String, String>?) {
    }

    override fun onDuerSkill(p0: String?, p1: String?, p2: HashMap<String, String>?) {
    }

    override fun onLackScreen() {
    }

    override fun onMicOpenFailed() {
    }

    override fun onASRInitError() {
    }

    override fun onASRResult(p0: Boolean, p1: String?) {
    }

    override fun onStateUserSpeaking() {
    }

    override fun onNetworkUnstable(p0: Int, p1: Int, p2: String?) {
    }

    override fun onWakeupCommand(p0: String?) {
    }

    override fun showStateHint(p0: String?) {
    }

    override fun showVerboseHint(p0: String?) {
    }

    override fun onStateNeedWakeup() {
    }

    override fun onVoiceInputVolume(p0: Int) {
    }

    override fun showResultHint(p0: String?) {
    }

    override fun onStateSpeakFinished() {
    }

    override fun onMessageNotHandled(p0: String?, p1: Int) {
    }

    override fun onStateWaitingInput() {
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

        HomeAIEnv.setAppAuth("demo", "chdemo_appsecret")
        val speech = HomeAIEnv.ApiAuth()
        speech.appId = "dmD1EE939C30061699"
        speech.apiKey = "1BBF8CE2EA16B751B2D4C7FF56BF9003"
        speech.secretKey = "baidu_speech_secretkey"
        val tts = HomeAIEnv.ApiAuth()
        tts.appId = "baidu_speech_appid"
        tts.apiKey = "baidu_speech_appkey"
        tts.secretKey = "baidu_speech_secretkey"
        val duer = HomeAIEnv.ApiAuth()
        duer.appId = "duer_appid"
        duer.apiKey = "duer_appkey"
        duer.secretKey = ""
        HomeAIEnv.setBaiduAuth(speech, tts, duer)
        HomeAIEnv.setAudioPlayerEnabled(true)

        HomeAIEnv.addWakeupWord("小鹿小鹿") // 方法一
        val homeAISdk = HomeAISdk.getInstance(this)
        homeAISdk.setScreen(this)
        homeAISdk.setClient(this)
        homeAISdk.start()
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
