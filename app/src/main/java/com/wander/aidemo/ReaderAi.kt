package com.wander.aidemo

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.webkit.ValueCallback
import com.iqiyi.homeai.core.HomeAIEnv
import com.iqiyi.homeai.core.HomeAISdk
import com.iqiyi.homeai.core.HomeAISdkClient
import com.iqiyi.homeai.core.player.*
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

object ReaderAi {
    private const val TAG = "ReaderAi"
    private val useQYWakeup = false

    @SuppressLint("StaticFieldLeak")
    private  var mHomeAI: HomeAISdk? = null
    private var mCurrentWakeupCallback: IVoiceWakeupCallback? = null
    private var mCurrentAsrCallback: IVoiceAsrCallback? = null


    // IQYSearchApi impls

    fun voiceWakeUp(context: Context, callback: IVoiceWakeupCallback) {
        mCurrentWakeupCallback = callback
        mHomeAI?.setWakeupEnabled(true)
    }

    fun stopWakeUp() {
        mCurrentWakeupCallback = null
        mHomeAI?.setWakeupEnabled(false)
    }

    fun voiceRecognition(context: Context, callback: IVoiceAsrCallback, longSpeech: Boolean) {
        Log.d(TAG, "call homeai wakeup")
        mHomeAI?.wakeUp(!longSpeech)
        mCurrentAsrCallback = callback
    }

    fun stopListening() {
        mHomeAI?.process()
    }

    fun cancelRecognition() {
        if (mHomeAI == null) {
            Log.e(TAG, "cancel asr before homeai instanced")
            return
        }
        mHomeAI?.sleep()
    }

    fun setOnlyASR(onlyAsr: Boolean) {
        HomeAIEnv.setOnlyASR(onlyAsr)
    }

    fun releaseRecognizer() {
        if (mHomeAI == null) {
            Log.e(TAG, "release before homeai instanced")
            return
        }
        // TODO: always set only asr when release asr recognizer
        HomeAIEnv.setOnlyASR(true)
        // TODO: 调用过destroy后，HomeAISdk的实例不再能重用，所以这里要置空
        mHomeAI?.onDestroy()
        mHomeAI = null
    }


    fun initAi(application: Context) {
        // HomeAI分配给移动端的业务标识和token
        HomeAIEnv.setAppAuth("demo", "chdemo_appsecret")
        // tts现在都不需要了，可以随意填
        val tts = HomeAIEnv.ApiAuth()
        tts.appId = "dmD1EE939C30061699"
        tts.apiKey = "1BBF8CE2EA16B751B2D4C7FF56BF9003"
        tts.secretKey = "baidu_speech_secretkey"
        // TODO: speech的参数，如果使用度秘的唤醒是需要填原来用的基线使用的百度语音的appId等数值，如果使用自研的唤醒可以随便填
        //       使用自研的话， HomeAIEnv.setUseQyWakeup传true，且依赖中要包含opt-qyasr-libs.
        val speech = HomeAIEnv.ApiAuth()
        speech.appId = "baidu_speech_appid"
        speech.apiKey = "baidu_speech_appkey"
        speech.secretKey = "baidu_speech_secretkey"
        HomeAIEnv.setUseQyWakeup(useQYWakeup)
        // TODO: 度秘的相关参数目前还是需要的，下面的是给移动端申请的，直接用这份代码的值即可
        val duer = HomeAIEnv.ApiAuth()
        duer.appId = "dm8CD782DF3A096B21"//tvguoapp
        duer.apiKey = "A8A63528FAC0BD5192AEF947C4BE7BFF"
        duer.secretKey = ""
        HomeAIEnv.setBaiduAuth(speech, tts, duer)
        HomeAIEnv.setParseCommand(false)
        HomeAIEnv.setOnlyASR(true)

        // TODO: 由于基线使用过程中，唤醒完后业务方会再次主动调用voiceRecognition开始ASR，因此设置唤醒词时，使用HomeAI的WakeUpCommand
        //       HomeAI的普通唤醒词在唤醒后会自动进入语音识别状态
        Log.e(TAG, "add wake up word")
        if (useQYWakeup)
            HomeAIEnv.addWakeupAsCommand("x iao3 ii i4 x iao3 ii i4")
        else
            HomeAIEnv.addWakeupAsCommand("小艺小艺")


        // TODO: 可以通过这个方法打开HomeAI的日志
        HomeAIEnv.setVerboseOutputEnabled(true)
        // TODO: 可以通过这个方法切换HomeAI的后台环境
        HomeAIEnv.setEnvironmentType(HomeAIEnv.TYPE_ENV_DEV)
        HomeAIEnv.setHomeAiDevServer("http://10.5.178.127:20086/")
        init(application)

    }


    // 在上一次使用完release后，调用此方法继续后续使用
    private fun init(application: Context) {
        if (mHomeAI != null)
            return
        mHomeAI = HomeAISdk.getInstance(application)
        mHomeAI?.setWakeupEnabled(false)
        mHomeAI?.start()
        // 设置回调
        mHomeAI?.setClient(object : HomeAISdkClient {
            override fun getPlayingAlbum(): String {
                return ""
            }

            override fun getSceneRegisters(): HashMap<String, String>? {
                return null
            }

            override fun getCustomConfigs(): HashMap<String, String>? {
                return null
            }

            override fun getSearchingKeyword(): String? {
                return null
            }

            override fun getShowingGuides(): MutableList<String>? {
                return null
            }


            override fun showResultHint(s: String) {

            }

            override fun showStateHint(s: String) {

            }

            override fun showVerboseHint(s: String) {

            }

            override fun onASRResult(isFinal: Boolean, s: String) {
                val data = ArrayList<String>()
                data.add(s)
                val bundle = Bundle()
                bundle.putStringArrayList(IVoiceAsrCallback.RESULTS_RECOGNITION, data)
                    if (isFinal)
                        mCurrentAsrCallback?.onResults(bundle)
                    else
                        mCurrentAsrCallback?.onPartialResults(bundle)
            }

            override fun onStateNeedWakeup() {

            }

            override fun onStateUserSpeaking() {
                    mCurrentAsrCallback?.onBeginningOfSpeech()
            }

            override fun onStateWaitingInput() {
                    mCurrentAsrCallback?.onReadyForSpeech(Bundle())
            }

            override fun onStateSpeakFinished() {
                    mCurrentAsrCallback?.onEndOfSpeech()
            }

            override fun onVoiceInputVolume(v: Double) {
                    mCurrentAsrCallback?.onRmsChanged(v.toFloat())
            }

            override fun onLackScreen() {

            }

            override fun onNoScreenShot() {

            }

            override fun onAnswerToUnknownIntent(s: String): Boolean {
                return false
            }

            override fun onMessageNotHandled(s: String, i: Int) {

            }

            override fun onWakeupCommand(s: String) {
                    mCurrentWakeupCallback?.onWakedUp(s)
            }

            override fun onDuerSkill(s: String, s1: String, hashMap: HashMap<String, String>) {

            }

            override fun onCustomSkill(s: String, s1: String, hashMap: HashMap<String, String>) {

            }

            override fun getPlayingVideo(longs: LongArray): String? {
                return null
            }

            override fun onSceneCommand(s: String) {

            }

            override fun onScreenshotUsed(bitmap: Bitmap) {

            }

            override fun onGoHome() {

            }

            override fun onHomeAIMessage(s: String, s1: String) {

            }

            // following are error related callbacks.
            override fun onNetworkUnstable(i: Int, i1: Int, s: String) {
                    mCurrentAsrCallback?.onError(IVoiceAsrCallback.ERROR_NETWORK)
            }

            override fun onASRNoResult() {
                    mCurrentAsrCallback?.onError(IVoiceAsrCallback.ERROR_NO_RESULT)
            }

            override fun onMicOpenFailed() {
                    mCurrentAsrCallback?.onError(IVoiceAsrCallback.ERROR_MIC)
            }

            override fun onASRInitError() {
                    mCurrentAsrCallback?.onError(IVoiceAsrCallback.ERROR_INIT)
            }

            override fun onVoiceInputTimeout() {
                if (mCurrentAsrCallback != null)
                    mCurrentAsrCallback?.onError(IVoiceAsrCallback.ERROR_TIMEOUT)
            }

            override fun onWritePCMStreamFailed() {
                    mCurrentAsrCallback?.onError(IVoiceAsrCallback.ERROR_BUSY)
            }

            override fun onRawCommand(cmdString: String) {
                    mCurrentAsrCallback?.onIntent(cmdString)
            }
        })
        mHomeAI?.setScreen(object : IScreen {
            override fun showPersonResult(p0: PersonDetail?, p1: ItemList?, p2: Int, p3: Int) {
            }

            override fun getPlayingTVInfo(): TVPlayingInfo? {
                return null
            }

            override fun showImageRecognitionResult(
                p0: Bitmap?,
                p1: MutableList<ImageRecogResult>?
            ) {
            }

            override fun getShowingList(): HashMap<String, String>? {
                return null
            }

            fun showSearchResult(p0: ItemList?, p1: Int, p2: Int) {
            }


            override fun isOn(): Boolean {
                return true
            }

            override fun showNotification(s: String) {

            }

            override fun showPage(s: String) {

            }

            override fun showSearchResult(itemList: ItemList, i: Int, i1: Int, s: String) {

            }


            override fun playVideo(
                itemDetail: ItemDetail,
                s: String,
                s1: String,
                s2: String,
                list: List<String>
            ) {

            }

            override fun onlyWatchHim(s: String, list: List<String>, b: Boolean, s1: String) {

            }

            override fun seekToPlot(s: String, l: Long, s1: String, s2: String) {

            }

            override fun playEpisode(i: Int) {

            }

            override fun setResolution(b: Boolean, i: Int) {

            }

            override fun navigateBackforward(i: Int): Boolean {
                return false
            }

            override fun selectItem(i: Int): Boolean {
                return false
            }

            override fun selectItem2D(i: Int, i1: Int): Boolean {
                return false
            }

            override fun switchPage(b: Boolean, i: Int) {

            }

            override fun skipOp() {

            }

            override fun skipEd() {

            }

            override fun selectItemById(s: String): Boolean {
                return false
            }

            override fun onLifeInfo(s: String, i: Int, jsonObject: JSONObject) {

            }

            override fun getLastScreenshot(longs: LongArray): ByteArray {
                return ByteArray(0)
            }


            override fun nextChannel() {

            }

            override fun previousChannel() {

            }

            override fun playChannel(i: Int) {

            }

            override fun showChannelCard(s: String, s1: String, s2: String) {

            }

            override fun saveOnlyWatchHimList(
                s: String,
                s1: String,
                s2: String,
                longs: LongArray,
                longs1: LongArray
            ) {

            }

            override fun setPlayerStateListener(iPlayerStateListener: IPlayerStateListener) {

            }

            override fun resume() {

            }

            override fun pause() {

            }

            // TODO: 下面两个回调必须要至少调用一下callback，不能啥也不干，否则会卡死
            override fun onVoiceSleep(valueCallback: ValueCallback<Boolean>) {
                valueCallback.onReceiveValue(true)
            }

            override fun onVoiceWakeup(valueCallback: ValueCallback<Boolean>) {
                valueCallback.onReceiveValue(true)
            }

            override fun stop() {

            }

            override fun isPlaying(): Boolean {
                return false
            }

            override fun seek(b: Boolean, i: Int) {

            }

            override fun volumnAdjust(b: Boolean, v: Float) {

            }

            override fun mute(b: Boolean) {

            }

            override fun next() {

            }

            override fun previous() {

            }

            override fun destroy() {

            }

            override fun shutdown(b: Boolean) {

            }
        })
    }
}