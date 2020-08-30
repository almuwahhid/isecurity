package chat.rocket.android.call.GTRTC


import android.content.Context
import android.hardware.camera2.CameraDevice
import android.media.MediaRecorder
import android.opengl.EGLContext
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager
import com.almuwahhid.isecurityrtctest.Candidate
import com.almuwahhid.isecurityrtctest.Payload
import com.google.gson.Gson
import com.mobile.isecurity.core.rtc.GTPeerConnectionParameters
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.*
import java.lang.Exception
import java.util.*


class GTRTCCLient(ctx: Context, peerParam: GTPeerConnectionParameters, rtcListener: RTCListener, mEGLcontext: EGLContext?) :
    SurfaceHolder.Callback {


    private val TAG = ".iSecurityRTCClient"

    private val pcConstraints = MediaConstraints()
    private var localMS: MediaStream? = null
    private var remoteMS: MediaStream? = null
    private var factory: PeerConnectionFactory? = null
    private val iceServers = LinkedList<PeerConnection.IceServer>()
    private var videoSource: VideoSource? = null
    private var rtc: IceCandidate? = null
    private var sdp: SessionDescription? = null
    private var pr: PeerConnection? = null
    private var commandMap: HashMap<String, Command>? = null
    private var context: Context? = null
    private var rtccLient: RTCListener? = null
    private var params: GTPeerConnectionParameters? = null
    private var peer : Peer? = null
    private var videoCapturer : VideoCapturer? = null
    private var videoConstraints : MediaConstraints? = null

    private var offerCommand = CreateOfferCommand()
    private var answerCommand = CreateAnswerCommand()
    private var remoteSDPCommand = SetRemoteSDPCommand()
    private var iceCandidateCommand = AddIceCandidateCommand()
    internal var gson: Gson? = null

    private var surfaceView: SurfaceView? = null
    private var mCamera: CameraDevice? = null
    private var windowManager: WindowManager? = null
    var mediaRecorder: MediaRecorder? = null
    var layoutParams: WindowManager.LayoutParams? = null
    var isFront = true
    var videoTrack : VideoTrack?? = null

    fun checkIsFront(): Boolean{
        return isFront
    }


    private fun initRTC(){
        iceServers.add(PeerConnection.IceServer("stun:stun-cjt.kemlu.go.id:5349"))
//        iceServers.add(PeerConnection.IceServer("stun:stun.stunprotocol.org:3478"))
//        iceServers.add(PeerConnection.IceServer("stun:stun.l.google.com:19302"))
        pcConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "false"))
        pcConstraints.mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        pcConstraints.optional.add(MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"))
    }

    init {
        this.context = ctx
        this.rtccLient = rtcListener
        this.params = peerParam

        /*windowManager = context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        surfaceView = SurfaceView(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
//                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
            )
        } else {
            layoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
            )
        }

        layoutParams!!.gravity = Gravity.LEFT or Gravity.TOP
        windowManager!!.addView(surfaceView, layoutParams)
        surfaceView!!.holder.addCallback(this)*/

//        var glview_call = GLSurfaceView(context)
//        glview_call.setPreserveEGLContextOnPause(true)
//        glview_call.setKeepScreenOn(true)
//
//        VideoRendererGui.setView(glview_call){
//
//        }
//
//        val displaySize = Point()
//        windowManager!!.defaultDisplay.getSize(displaySize)

//        PeerConnectionFactory.initializeAndroidGlobals(rtccLient, true, true,
//            params!!.videoCodecHwAcceleration, true)
        PeerConnectionFactory.initializeAndroidGlobals(rtccLient, true, true,
            params!!.videoCodecHwAcceleration, mEGLcontext)
//        PeerConnectionFactory.initializeAndroidGlobals(rtccLient, true, true,
//            params!!.videoCodecHwAcceleration, pcConstraints)
        factory = PeerConnectionFactory()
        gson = Gson()
        initRTC()

    }

    public fun answerCandidate(candidate: Candidate){
        iceCandidateCommand.execute(JSONObject().put("candidate", candidate.ice.candidate).put("label", candidate.ice.sdpMLineIndex).put("id", candidate.ice.sdpMid))
    }

    public fun answerOffer(payload : Payload){
        answerCommand.execute(JSONObject().put("sdp", payload.sdp.sdp).put("type", payload.sdp.type))
    }

    fun offers(){
        offerCommand.execute(JSONObject())
    }

    public fun answerAnswer(payload : Payload){
        remoteSDPCommand.execute(JSONObject().put("sdp", payload.sdp.sdp).put("type", payload.sdp.type))
    }


    public interface RTCListener{
        fun onCallReady(type: String, sdp: String)

        fun onCandidateCall(label: Int, id: String, candidate: String)

        fun onStatusChanged(newStatus: String)

        fun onLocalStream(localStream: MediaStream)

        fun onAddRemoteStream(remoteStream: MediaStream)

        fun onRemoveRemoteStream()

    }
    private interface Command {
        @Throws(JSONException::class)
        fun execute(payload: JSONObject)

    }
    private inner class CreateOfferCommand : Command {
        @Throws(JSONException::class)
        override fun execute(payload: JSONObject) {
            Log.d(TAG, "CreateOfferCommand")
            if(peer!= null){
                peer!!.pc!!.createOffer(peer, pcConstraints)
//                rtccLient!!.onStatusChanged("Menghubungkan")
            }
        }

    }

    private inner class CreateAnswerCommand : Command {
        @Throws(JSONException::class)
        override fun execute(payload: JSONObject) {
            Log.d(TAG, "CreateAnswerCommand")
            val sdp = SessionDescription(
//                    SessionDescription.Type.fromCanonicalForm(payload.getString("type")),
                    SessionDescription.Type.fromCanonicalForm("offer"),
                    payload.getString("sdp")
            )
            peer!!.pc!!.setRemoteDescription(peer, sdp)
            peer!!.pc!!.createAnswer(peer, pcConstraints)
            rtccLient!!.onStatusChanged("Menghubungkan")
        }
    }

    private inner class SetRemoteSDPCommand : Command {
        @Throws(JSONException::class)
        override fun execute(payload: JSONObject) {
            Log.d(TAG, "SetRemoteSDPCommand")
            val sdp = SessionDescription(
//                    SessionDescription.Type.fromCanonicalForm(payload.getString("type")),
                    SessionDescription.Type.fromCanonicalForm("answer"),
                    payload.getString("sdp")
            )
            peer!!.pc!!.setRemoteDescription(peer, sdp)
        }
    }

    private inner class AddIceCandidateCommand : Command {
        @Throws(JSONException::class)
        override fun execute(payload: JSONObject) {
            Log.d(TAG, "AddIceCandidateCommand")
            if (peer!!.pc!!.getRemoteDescription() != null) {
                val candidate = IceCandidate(
                        payload.getString("id"),
                        payload.getInt("label"),
                        payload.getString("candidate")
                )
                peer!!.pc!!.addIceCandidate(candidate)
            }
        }
    }

    fun initPeer(isTrue: Boolean){
        setCamera(isTrue)
        peer = Peer()
        offerCommand.execute(JSONObject())
    }

//    public fun initStream(model: GTCallModel){
    fun initStream(){
        if(peer == null){
            setCamera(true)
            peer = Peer()
//            peer!!.pc!!.addStream(localMS)
        }
    }

    fun setCamera(isFront : Boolean) {
        localMS = factory!!.createLocalMediaStream("ARDAMS")
        Log.d(TAG, "setCamera: " + localMS!!.label())
        if (params!!.videoCallEnabled) {

        }

        videoConstraints = MediaConstraints()
        videoConstraints!!.mandatory.add(MediaConstraints.KeyValuePair("maxHeight", Integer.toString(params!!.videoHeight)))
        videoConstraints!!.mandatory.add(MediaConstraints.KeyValuePair("maxWidth", Integer.toString(params!!.videoWidth)))
        videoConstraints!!.mandatory.add(MediaConstraints.KeyValuePair("maxFrameRate", Integer.toString(params!!.videoFps)))
        videoConstraints!!.mandatory.add(MediaConstraints.KeyValuePair("minFrameRate", Integer.toString(params!!.videoFps)))

        try {
            videoSource = factory!!.createVideoSource(getVideoCapturer(true), videoConstraints)
            if (videoSource != null) {
                videoSource!!.dispose()
            }
        } catch (e : Exception){
            e.printStackTrace()
        }

        try {
            videoSource = factory!!.createVideoSource(getVideoCapturer(false), videoConstraints)
            if (videoSource != null) {
                videoSource!!.dispose()
            }
        } catch (e : Exception){
            e.printStackTrace()
        }

        videoSource = factory!!.createVideoSource(getVideoCapturer(isFront), videoConstraints)
        videoTrack = factory!!.createVideoTrack("ARDAMSv0", videoSource)
        localMS!!.addTrack(videoTrack!!)
        val audioSource = factory!!.createAudioSource(MediaConstraints())
//        localMS!!.addTrack(factory!!.createAudioTrack("ARDAMSa0", audioSource))
        rtccLient!!.onLocalStream(localMS!!)
    }

    fun getVideoCapturer(isFront : Boolean): VideoCapturer {
        this.isFront = isFront
        //            TODO : here
//        TODO("Not yet implemented")
        Log.d("isFront", "+ "+isFront)
//        val frontCameraDeviceName = VideoCapturerAndroid.getNameOfBackFacingDevice()
        var frontCameraDeviceName = ""
        if(isFront){
            frontCameraDeviceName = VideoCapturerAndroid.getNameOfFrontFacingDevice()
//            frontCameraDeviceName = VideoCapturerAndroid.getNameOfBackFacingDevice()
        } else {
            frontCameraDeviceName = VideoCapturerAndroid.getNameOfBackFacingDevice()
        }
        videoCapturer = VideoCapturerAndroid.create(frontCameraDeviceName)
        return videoCapturer!!
    }

    fun switchCamera(isFront: Boolean){
        peer!!.pc!!.removeStream(localMS)
        localMS!!.removeTrack(videoTrack!!)

        videoConstraints = MediaConstraints()
        videoConstraints!!.mandatory.add(MediaConstraints.KeyValuePair("maxHeight", Integer.toString(params!!.videoHeight)))
        videoConstraints!!.mandatory.add(MediaConstraints.KeyValuePair("maxWidth", Integer.toString(params!!.videoWidth)))
        videoConstraints!!.mandatory.add(MediaConstraints.KeyValuePair("maxFrameRate", Integer.toString(params!!.videoFps)))
        videoConstraints!!.mandatory.add(MediaConstraints.KeyValuePair("minFrameRate", Integer.toString(params!!.videoFps)))

        videoSource = factory!!.createVideoSource(getVideoCapturer(isFront), videoConstraints)
        videoTrack = factory!!.createVideoTrack("ARDAMSv0", videoSource)
        localMS!!.addTrack(videoTrack)
        rtccLient!!.onLocalStream(localMS!!)
        peer!!.pc!!.addStream(localMS)
    }


    private inner class Peer() : SdpObserver, PeerConnection.Observer {
        val pc: PeerConnection?

        init {
            this.pc = factory!!.createPeerConnection(iceServers, pcConstraints, this)
            pc!!.addStream(localMS) //, new MediaConstraints()

            Log.d(TAG, "Menghubungkan Peer:")
        }

        override fun onCreateSuccess(sdp: SessionDescription) {
            // TODO: modify sdp to use pcParams prefered codecs
            try {
                val payload = JSONObject()
                payload.put("type", sdp.type.canonicalForm())
                payload.put("sdp", sdp.description)
                Log.d(TAG, "oncreateSccess "+sdp.type.canonicalForm()+" "+sdp.description)
//                context!!.sendBroadcast(Intent("iSecurity").putExtra("data", "oncreateSccess "+sdp.type.canonicalForm()+" "+sdp.description))
                rtccLient!!.onCallReady(sdp.type.canonicalForm(), sdp.description)

                pc!!.setLocalDescription(this@Peer, sdp)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }

        override fun onSetSuccess() {}

        override fun onCreateFailure(s: String) {}

        override fun onSetFailure(s: String) {}

        override fun onSignalingChange(signalingState: PeerConnection.SignalingState) {}

        override fun onIceConnectionChange(iceConnectionState: PeerConnection.IceConnectionState) {
            if (iceConnectionState == PeerConnection.IceConnectionState.DISCONNECTED) {
                removePeer()
//                mListener.onStatusChanged("Panggilan berakhir")
            }
        }

        override fun onIceGatheringChange(iceGatheringState: PeerConnection.IceGatheringState) {}

        override fun onIceCandidate(candidate: IceCandidate) {
            try {
                rtccLient!!.onCandidateCall(candidate.sdpMLineIndex, candidate.sdpMid, candidate.sdp)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }

        override fun onAddStream(mediaStream: MediaStream) {
            Log.d(TAG, "onAddStream " + mediaStream.label())
//            remoteMS = mediaStream
//            peer!!.pc!!.addStream(remoteMS)
//            rtccLient!!.onAddRemoteStream(mediaStream)
        }

        override fun onRemoveStream(mediaStream: MediaStream) {
            Log.d(TAG, "onRemoveStream " + mediaStream.label())
            removePeer()
        }

        override fun onDataChannel(dataChannel: DataChannel) {}

        override fun onRenegotiationNeeded() {

        }

    }

    private fun removePeer() {
//        mListener.onRemoveRemoteStream(peer.endPoint)
        if(peer!=null){
            peer!!.pc!!.close()
        }
    }

    public fun stopPeer(){
        peer!!.pc!!.dispose()
        if (videoSource != null) {
//            videoSource!!.stop()
            videoSource!!.dispose()
        }
    }

    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {

    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {

    }

    override fun surfaceCreated(p0: SurfaceHolder?) {
        mediaRecorder = MediaRecorder()

        PeerConnectionFactory.initializeAndroidGlobals(rtccLient, true, true,
            params!!.videoCodecHwAcceleration, true)
        factory = PeerConnectionFactory()
        gson = Gson()

    }
}