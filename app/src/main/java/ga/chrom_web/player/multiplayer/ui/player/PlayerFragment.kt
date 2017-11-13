package ga.chrom_web.player.multiplayer.ui.player

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import ga.chrom_web.player.multiplayer.R
import ga.chrom_web.player.multiplayer.Utils
import ga.chrom_web.player.multiplayer.data.ChatItem
import ga.chrom_web.player.multiplayer.databinding.FragmentPlayerBinding
import ga.chrom_web.player.multiplayer.ui.SmilesAdapter
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import java.util.*

class PlayerFragment : Fragment() {

    companion object {
        private const val SMILES_IN_ROW_PORTRAIT = 7
        private const val SMILES_IN_ROW_LANDSCAPE = 4
        private const val KEYBOARD_HEIGHT_IN_DP = 263

        // keys for savedInstanceState
        private const val MESSAGES = "messages"
        private const val IS_FULLSCREEN = "isFullScreen"
    }

    private lateinit var youtubePlayerFragment: CustomYoutubePlayerFragment
    private lateinit var mViewModel: PlayerViewModel
    private lateinit var mBinding: FragmentPlayerBinding
    private var mChatAdapter: ChatAdapter? = null
    //    private var mIsOrientationChangedByButton = false
    private var mIsSmilesLayoutVisible = false
    private var mHandler: Handler = Handler(Looper.getMainLooper())
    private var isFullScreen = false
    private var isLandscape = false
    private var orientationEventListener: OrientationEventListener? = null

    private val screenOrientation: Int
        get() = resources.configuration.orientation


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Utils.debugLog("Starting room fragment...")
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_player, container, false)

        youtubePlayerFragment = CustomYoutubePlayerFragment()
        fragmentManager.beginTransaction()
                .replace(R.id.youtubeContainer, youtubePlayerFragment)
                .commit()

        youtubePlayerFragment.playerListener = object : CustomYoutubePlayerFragment.PlayerListener {
            override fun onClickFullscreen() {
                if (!isFullScreen) {
                    Utils.debugLog("not full screen")
//                    isFullScreen = true
                    if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                        toggleLandscapeAndFullscreen()
                    } else {
                        isFullScreen = true
                        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    }
                } else {
                    Utils.debugLog("full screen " + screenOrientation)
                    if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
                        toggleLandscapeAndFullscreen()
                    }
                    /*  else {
                          Utils.debugLog("just ")
                          isFullScreen = false
                      }*/
                }
            }

            override fun onClickUpload() {
                createUploadDialog()
//                toggleLandscapeAndFullscreen()
            }

            override fun onRewind(timeInMillis: Int) {
                mViewModel.rewindTo(timeInMillis)
            }

            override fun onClickPause() {
                mViewModel.pause()
            }

            override fun onClickPlay() {
                mViewModel.play()
            }

            override fun onPlayerInitialized() {
                mViewModel.playerInitialized()
            }
        }

        initChat()

        savedInstanceState?.let { inState ->
            val messages = inState.getSerializable(MESSAGES) as ArrayList<ChatItem>
            isFullScreen = inState.getBoolean(IS_FULLSCREEN)
            Utils.debugLog("getting previous fullscreen " + isFullScreen)
            mChatAdapter!!.addItems(messages)
        }

        if (isFullScreen) {
            val layoutParams = mBinding.youtubeContainer.layoutParams
            if (layoutParams is LinearLayout.LayoutParams) {
                layoutParams.weight = 0f
            }
        }

        orientationEventListener = object : OrientationEventListener(activity) {
            override fun onOrientationChanged(orientation: Int) {
                val epsilon = 10
                val leftLandscape = 90
                val rightLandscape = 270
                isLandscape = epsilonCheck(orientation, leftLandscape, epsilon) || epsilonCheck(orientation, rightLandscape, epsilon)
                if (!isFullScreen) {
                    return
                }
                if (isLandscape) {
                    isFullScreen = false
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
                }
            }

            private fun epsilonCheck(a: Int, b: Int, epsilon: Int): Boolean {
                return a > b - epsilon && a < b + epsilon
            }
        }
        orientationEventListener?.enable()
        return mBinding.root
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // there's not fullscreen in portrait orientation
        Utils.debugLog("whose first")
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            isFullScreen = false
        }
    }

    private fun toggleLandscapeAndFullscreen() {
        Utils.debugLog("toggle " + isFullScreen)
        if (isFullScreen) {
            (mBinding.youtubeContainer.layoutParams as LinearLayout.LayoutParams).weight = 0.3f
        } else {
            (mBinding.youtubeContainer.layoutParams as LinearLayout.LayoutParams).weight = 0f
        }
        isFullScreen = !isFullScreen
        mBinding.youtubeContainer.requestLayout()
        if (isLandscape) {
            mHandler.post({
                youtubePlayerFragment.updateControlsPosition(mBinding.youtubeContainer.width)
            })
        }
    }

    /**
     *  Hides smile keyboard
     *  On portrait mode will change size of keyboard to 0
     *  On landscape mode only change visibility to View.GONE
     */
    private fun hideSmilesKeyboard(withRequestLayout: Boolean) {
        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
            mBinding.emotesContainer.layoutParams.height = 0
            if (withRequestLayout) {
                mBinding.smilesKeyboard.requestLayout()
            }
        }
        mIsSmilesLayoutVisible = false
        mBinding.isSmileKeyboardOpen = false

    }

    /**
     *  Shows smile keyboard
     *  On portrait mode will change size of keyboard to KEYBOARD_HEIGHT_IN_DP
     *  On landscape mode only change visibility to View.VISIBLE
     */
    private fun showSmilesKeyboard(withRequestLayout: Boolean) {
        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
            val isKeyboardVisible = KeyboardVisibilityEvent.isKeyboardVisible(activity)
            if (isKeyboardVisible) {
                Utils.hideKeyboard(activity)
            }
            mBinding.emotesContainer.layoutParams.height = Utils.dpToPx(activity, KEYBOARD_HEIGHT_IN_DP).toInt()
            if (withRequestLayout || !isKeyboardVisible) {
                mBinding.smilesKeyboard.requestLayout()
            }
        }
        mIsSmilesLayoutVisible = true
        mBinding.isSmileKeyboardOpen = true
    }

    private fun initSmiles(smilePaths: LinkedHashMap<String, String>) {

        KeyboardVisibilityEvent.setEventListener(activity) { isOpen ->
            if (isOpen) {
                hideSmilesKeyboard(true)
            }
        }

        mBinding.etMessage?.setOnClickListener {
            hideSmilesKeyboard(false)
        }

        mBinding.imgSmiles.setOnClickListener {
            if (mIsSmilesLayoutVisible) {
                hideSmilesKeyboard(false)

                // code below will work only in portrait mode
                mBinding.etMessage?.apply {
                    requestFocus()
                    Utils.showKeyboard(mBinding.etMessage)
                }
            } else {
                showSmilesKeyboard(false)
            }
        }

        // only in portrait mode
        mBinding.eraseSmile?.setOnClickListener {
            eraseSmileOrLastLetter()
        }

        val smilesAdapter = SmilesAdapter(convertSmilesPaths(smilePaths))
        smilesAdapter.mIsBigSmiles = mBinding.smilesKeyboardSwitch.isChecked
        toggleSmileHeader(mBinding.smilesKeyboardSwitch.isChecked)
        val smilesInRow: Int

        if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
            smilesInRow = SMILES_IN_ROW_PORTRAIT
            smilesAdapter.onSmileClickListener = { smile ->
                mBinding.etMessage?.append(smile)
            }
        } else { // landscape
            smilesInRow = SMILES_IN_ROW_LANDSCAPE
            smilesAdapter.onSmileClickListener = { smile ->
                mViewModel.sendSmile(smile)
                hideSmilesKeyboard(true)
            }
        }

        mBinding.smilesKeyboardSwitch.setOnCheckedChangeListener { _, isChecked ->
            toggleSmileHeader(isChecked)
            smilesAdapter.mIsBigSmiles = isChecked
        }

        val layoutManager = GridLayoutManager(activity, smilesInRow)
        mBinding.smilesKeyboard.layoutManager = layoutManager
        mBinding.smilesKeyboard.adapter = smilesAdapter
    }

    private fun toggleSmileHeader(isChecked: Boolean) {
        if (isChecked) {
            mBinding.tvSmileHeader.text = getString(R.string.emote_stickers)
        } else {
            mBinding.tvSmileHeader.text = getString(R.string.emote_smiles)
        }
    }

    private fun eraseSmileOrLastLetter() {
        // TODO: make long click listener
        mBinding.etMessage?.let { et ->
            val smiles = mViewModel.smilesPaths.value
            if (et.length() < 1 || smiles == null) {
                return
            }

            val textLength: Int = et.length()
            // erase smile
            smiles.forEach {
                val smile = it.key
                val smilePosition = textLength - smile.length
                if (textLength >= smile.length
                        && et.text.substring(smilePosition).equals(smile)) {
                    et.text.delete(smilePosition, textLength)
                    return
                }
            }
            // if there's no smile remove last letter
            et.text.delete(textLength - 1, textLength)
        }
    }

    /**
     * Converts LinkedHashMap to ArrayList<Pair> to have access by position inside adapter
     */
    private fun convertSmilesPaths(smilePaths: LinkedHashMap<String, String>): ArrayList<Pair<String, String>> {
        val smilesArray = ArrayList<Pair<String, String>>()
        for (key in smilePaths.keys) {
            val pair = Pair(key, smilePaths.get(key)!!)
            smilesArray.add(pair)
        }
        return smilesArray
    }

    private fun initChat() {

        mChatAdapter = ChatAdapter()

        val layoutManager = LinearLayoutManager(activity)
        mBinding.rvChat.layoutManager = layoutManager
        mBinding.rvChat.setHasFixedSize(true)
        mBinding.rvChat.adapter = mChatAdapter
        layoutManager.stackFromEnd = true
    }

    private fun createUploadDialog(link: String = "") {
        val edittext = EditText(activity)
//        edittext.setText("https://www.youtube.com/watch?v=XGmFF82PE50")
        edittext.setText(link)
        val adb = AlertDialog.Builder(activity)
        adb.setTitle(getString(R.string.dialog_upload_title))
        // TODO: validate link for only youtube videos
        adb.setView(edittext)

        adb.setPositiveButton(android.R.string.ok) { _, _ -> mViewModel.loadVideo(edittext.text.toString()) }
        adb.setNegativeButton(android.R.string.cancel, null)
        adb.show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(PlayerViewModel::class.java)

        mViewModel.videoLink.observe(this, Observer { videoLink ->
            videoLink?.let {
                youtubePlayerFragment.loadVideo(videoLink)
            }
        })
        mViewModel.videoTime.observe(this, Observer { timeInMillis ->
            timeInMillis?.let {
                youtubePlayerFragment.seekToMillis(timeInMillis)
            }
        })
        mViewModel.shouldPlay.observe(this, Observer { shouldPlay ->
            shouldPlay?.let {
                playOrPause(shouldPlay)
            }
        })
        mViewModel.playerData.observe(this, Observer { playerData ->
            playerData?.let {
                youtubePlayerFragment.loadVideo(playerData.getVideo(),
                        playerData.getTimeInMilli(), playerData.isPlaying())
                // if there's share link from other app open upload dialog
                arguments?.getString(PlayerActivity.SHARE_LINK)?.let { shareLink ->
                    createUploadDialog(shareLink)
                }
            }
        })
        mViewModel.message.observe(this, Observer { message ->
            if (message == null) {
                return@Observer
            }
            mChatAdapter!!.addItem(message)
            // after adding item scroll to the very bottom
            mBinding.rvChat.post { mBinding.rvChat.smoothScrollToPosition(mChatAdapter!!.itemCount - 1) }
        })
        mViewModel.smilesPaths.observe(this, Observer { smilePaths ->
            mHandler.post {
                smilePaths?.let {
                    initSmiles(smilePaths)
                }
            }
        })
        mViewModel.withBigSmilesPaths.observe(this, Observer { smiles ->
            mChatAdapter?.setSmilePaths(smiles!!)
        })
        mBinding.playerViewModel = mViewModel
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // save chat messages
        mChatAdapter?.let { adapter ->
            // make last message null to not post it on fragment retain
            mViewModel.message.value = null
            outState.putSerializable(MESSAGES, adapter.items)
        }
        Utils.debugLog("saving fullscreen " + isFullScreen)
        outState.putBoolean(IS_FULLSCREEN, isFullScreen)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        youtubePlayerFragment.onParentViewDestroy()
        orientationEventListener?.disable()
        // TODO: make it better
        // if it is configuration then save time to retain it later
        val currentTimeMillis = youtubePlayerFragment.getCurrentTimeMillis()
        currentTimeMillis?.let { time ->
            mViewModel.setCurrentTime(time / 1000)
        }
    }


    private fun playOrPause(shouldPlay: Boolean) {
        if (shouldPlay) {
            youtubePlayerFragment.play()
        } else {
            youtubePlayerFragment.pause()
        }
    }

    /**
     * Method should be called by activity when back button was pressed.
     *
     * @return boolean true if handled
     */
    fun onBackPressed(): Boolean {
        if (mIsSmilesLayoutVisible) {
            hideSmilesKeyboard(true)
            return true
        }
        return false
    }

    /**
     *  Receives intent share from other app
     *  Method will be called if fragment is in RESUME mode
     */
    fun receiveShare(link: String) {
        createUploadDialog(link)
    }

}

