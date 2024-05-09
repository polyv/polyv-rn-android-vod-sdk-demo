package com.polyvrnvoddemo.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easefun.polyvsdk.PolyvSDKClient;
import com.easefun.polyvsdk.PolyvSDKUtil;
import com.easefun.polyvsdk.R;
import com.easefun.polyvsdk.marquee.PolyvMarqueeItem;
import com.easefun.polyvsdk.marquee.PolyvMarqueeView;
import com.easefun.polyvsdk.player.PolyvPlayerAnswerView;
import com.easefun.polyvsdk.player.PolyvPlayerAudioCoverView;
import com.easefun.polyvsdk.player.PolyvPlayerAuditionView;
import com.easefun.polyvsdk.player.PolyvPlayerAuxiliaryView;
import com.easefun.polyvsdk.player.PolyvPlayerLightView;
import com.easefun.polyvsdk.player.PolyvPlayerMediaController;
import com.easefun.polyvsdk.player.PolyvPlayerPreviewView;
import com.easefun.polyvsdk.player.PolyvPlayerProgressView;
import com.easefun.polyvsdk.player.PolyvPlayerVolumeView;
//import com.easefun.polyvsdk.screencast.utils.PolyvToastUtil;
import com.easefun.polyvsdk.srt.PolyvSRTItemVO;
import com.easefun.polyvsdk.util.PolyvErrorMessageUtils;
import com.easefun.polyvsdk.util.PolyvScreenUtils;
import com.easefun.polyvsdk.video.IPolyvMediaPlayerControl;
import com.easefun.polyvsdk.video.PolyvMediaInfoType;
import com.easefun.polyvsdk.video.PolyvPlayErrorReason;
import com.easefun.polyvsdk.video.PolyvVideoView;
import com.easefun.polyvsdk.video.auxiliary.PolyvAuxiliaryVideoView;
import com.easefun.polyvsdk.video.listener.IPolyvOnAdvertisementCountDownListener;
import com.easefun.polyvsdk.video.listener.IPolyvOnAdvertisementEventListener2;
import com.easefun.polyvsdk.video.listener.IPolyvOnAdvertisementOutListener2;
import com.easefun.polyvsdk.video.listener.IPolyvOnChangeModeListener;
import com.easefun.polyvsdk.video.listener.IPolyvOnCompletionListener2;
import com.easefun.polyvsdk.video.listener.IPolyvOnErrorListener2;
import com.easefun.polyvsdk.video.listener.IPolyvOnGestureClickListener;
import com.easefun.polyvsdk.video.listener.IPolyvOnGestureDoubleClickListener;
import com.easefun.polyvsdk.video.listener.IPolyvOnGestureLeftDownListener;
import com.easefun.polyvsdk.video.listener.IPolyvOnGestureLeftUpListener;
import com.easefun.polyvsdk.video.listener.IPolyvOnGestureLongTouchListener;
import com.easefun.polyvsdk.video.listener.IPolyvOnGestureRightDownListener;
import com.easefun.polyvsdk.video.listener.IPolyvOnGestureRightUpListener;
import com.easefun.polyvsdk.video.listener.IPolyvOnGestureSwipeLeftListener;
import com.easefun.polyvsdk.video.listener.IPolyvOnGestureSwipeRightListener;
import com.easefun.polyvsdk.video.listener.IPolyvOnInfoListener2;
import com.easefun.polyvsdk.video.listener.IPolyvOnPlayPauseListener;
import com.easefun.polyvsdk.video.listener.IPolyvOnPreloadPlayListener;
import com.easefun.polyvsdk.video.listener.IPolyvOnPreparedListener2;
import com.easefun.polyvsdk.video.listener.IPolyvOnQuestionOutListener2;
import com.easefun.polyvsdk.video.listener.IPolyvOnTeaserCountDownListener;
import com.easefun.polyvsdk.video.listener.IPolyvOnTeaserOutListener;
import com.easefun.polyvsdk.video.listener.IPolyvOnVideoPlayErrorListener2;
import com.easefun.polyvsdk.video.listener.IPolyvOnVideoSRTListener;
import com.easefun.polyvsdk.video.listener.IPolyvOnVideoStatusListener;
import com.easefun.polyvsdk.view.PolyvTouchSpeedLayout;
import com.easefun.polyvsdk.vo.PolyvADMatterVO;
import com.easefun.polyvsdk.vo.PolyvQuestionVO;
import com.easefun.polyvsdk.vo.PolyvVideoVO;
import com.facebook.react.bridge.ReadableMap;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author df
 * @create 2019/2/20
 * @Describe
 */
public class PolyvRNVodPlayer extends FrameLayout  implements IPolyvMediaPlayerControl {
    private static final String TAG = "PolyvRNVodPlayer";

    //    private PolyvPlayerDanmuLayout danmuFragment;
    private ImageView iv_vlms_cover;
    /**
     * 视频控制栏
     */
    private PolyvPlayerMediaController mediaController = null;
    /**
     * 播放器的parentView
     */
    private RelativeLayout viewLayout = null;

    /**
     * 播放主视频播放器
     */
    private PolyvVideoView videoView = null;
    /**
     * 图片广告界面
     */
    private PolyvPlayerAuxiliaryView auxiliaryView = null;
    /**
     * 跑马灯控件
     */
    private PolyvMarqueeView marqueeView = null;
    private PolyvMarqueeItem marqueeItem = null;
    /**
     * 字幕文本视图
     */
    private TextView srtTextView = null;
    /**
     * 普通问答界面
     */
    private PolyvPlayerAnswerView questionView = null;
    /**
     * 语音问答界面
     */
    private PolyvPlayerAuditionView auditionView = null;
    /**
     * 用于播放广告片头的播放器
     */
    private PolyvAuxiliaryVideoView auxiliaryVideoView = null;
    /**
     * 视频广告，视频片头加载缓冲视图
     */
    private ProgressBar auxiliaryLoadingProgress = null;
    /**
     * 广告倒计时
     */
    private TextView advertCountDown = null;
    /**
     * 缩略图界面
     */
    private PolyvPlayerPreviewView firstStartView = null;
    /**
     * 手势出现的亮度界面
     */
    private PolyvPlayerLightView lightView = null;
    /**
     * 手势出现的音量界面
     */
    private PolyvPlayerVolumeView volumeView = null;
    /**
     * 手势出现的进度界面
     */
    private PolyvPlayerProgressView progressView = null;
    /**
     * 手势出现的快进界面
     */
    private PolyvTouchSpeedLayout touchSpeedLayout = null;
    /**
     * 音频模式下的封面
     */
    private PolyvPlayerAudioCoverView coverView = null;
    /**
     * 视频加载缓冲视图
     */
    private ProgressBar loadingProgress = null;

    private int fastForwardPos = 0;
    private boolean isPlay = false;


    private LinearLayout videoErrorLayout;
    private TextView videoErrorContent, videoErrorRetry;
    private String vid;
    private int bitrate;
    private boolean isMustFromLocal;

    private boolean isCanDrag = true;
    private float beforeTouchSpeed;

    public PolyvRNVodPlayer(Context context) {
        this(context,null);
    }

    public PolyvRNVodPlayer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PolyvRNVodPlayer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setVid(String vid) {
        videoView.setVid(vid);
    }

    public void onDestory(){
        videoView.release();
    }


    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.polyv_rn_vod_player, this);
        PolyvScreenUtils.generateHeight16_9((Activity) getContext());
        findIdAndNew();
        initView();
    }

    private void findIdAndNew() {
        videoErrorLayout = (LinearLayout) findViewById(R.id.video_error_layout);
        videoErrorContent = (TextView) findViewById(R.id.video_error_content);
        videoErrorRetry = (TextView) findViewById(R.id.video_error_retry);

        viewLayout = (RelativeLayout) findViewById(R.id.view_layout);
        videoView = (PolyvVideoView) findViewById(R.id.polyv_video_view);
        marqueeView = (PolyvMarqueeView) findViewById(R.id.polyv_marquee_view);
        mediaController =  findViewById(R.id.polyv_player_media_controller);
        mediaController.setMediaPlayer(videoView);
        srtTextView = (TextView) findViewById(R.id.srt);
        questionView = (PolyvPlayerAnswerView) findViewById(R.id.polyv_player_question_view);
        auditionView = (PolyvPlayerAuditionView) findViewById(R.id.polyv_player_audition_view);
        auxiliaryVideoView = (PolyvAuxiliaryVideoView) findViewById(R.id.polyv_auxiliary_video_view);
        auxiliaryLoadingProgress = (ProgressBar) findViewById(R.id.auxiliary_loading_progress);
        auxiliaryView =  findViewById(R.id.polyv_player_auxiliary_view);
        advertCountDown = (TextView) findViewById(R.id.count_down);
        firstStartView = (PolyvPlayerPreviewView) findViewById(R.id.polyv_player_first_start_view);
        lightView = (PolyvPlayerLightView) findViewById(R.id.polyv_player_light_view);
        volumeView = (PolyvPlayerVolumeView) findViewById(R.id.polyv_player_volume_view);
        progressView = (PolyvPlayerProgressView) findViewById(R.id.polyv_player_progress_view);
        loadingProgress = (ProgressBar) findViewById(R.id.loading_progress);
        coverView = (PolyvPlayerAudioCoverView) findViewById(R.id.polyv_cover_view);
//        danmuFragment = (PolyvPlayerDanmuLayout) findViewById(R.id.fl_danmu);
        iv_vlms_cover = (ImageView) findViewById(R.id.iv_vlms_cover);
        touchSpeedLayout = (PolyvTouchSpeedLayout) findViewById(R.id.polyv_player_touch_speed_layout);
        mediaController.initConfig(viewLayout);
        mediaController.setAudioCoverView(coverView);
//        mediaController.setDanmuFragment(danmuFragment);
        questionView.setPolyvVideoView(videoView);
        auditionView.setPolyvVideoView(videoView);
        auxiliaryVideoView.setPlayerBufferingIndicator(auxiliaryLoadingProgress);
//        auxiliaryView.setPolyvVideoView(videoView);
//        auxiliaryView.setDanmakuFragment(danmuFragment);

        videoView.setAuxiliaryVideoView(auxiliaryVideoView);
        videoView.setPlayerBufferingIndicator(loadingProgress);
        // 设置跑马灯
//        videoView.setMarqueeView(marqueeView, marqueeItem = new PolyvMarqueeItem()
//                .setStyle(PolyvMarqueeItem.STYLE_ROLL_FLICK) //样式
//                .setDuration(10000) //时长
//                .setText("") //文本
//                .setSize(16) //字体大小
//                .setColor(Color.YELLOW) //字体颜色
//                .setTextAlpha(70) //字体透明度
//                .setInterval(1000) //隐藏时间
//                .setLifeTime(1000) //显示时间
//                .setTweenTime(1000) //渐隐渐现时间
//                .setHasStroke(true) //是否有描边
//                .setBlurStroke(true) //是否模糊描边
//                .setStrokeWidth(3) //描边宽度
//                .setStrokeColor(Color.MAGENTA) //描边颜色
//                .setStrokeAlpha(70)); //描边透明度


        //投屏功能默认隐藏
        mediaController.findViewById(R.id.iv_screencast_search).setVisibility(View.INVISIBLE);
        mediaController.findViewById(R.id.iv_screencast_search_land).setVisibility(View.INVISIBLE);

        //关闭画中画
        mediaController.findViewById(R.id.iv_pip).setVisibility(View.GONE);
        mediaController.findViewById(R.id.iv_pip_portrait).setVisibility(View.GONE);

        //避免空指针而初始化，但是并不使用
//        mediaController.networkDetection = new PolyvNetworkDetection(getContext());

    }

    private void initView() {
        videoView.setOpenAd(true);
        videoView.setOpenTeaser(true);
        videoView.setOpenQuestion(true);
        videoView.setOpenSRT(true);
        videoView.setOpenPreload(true, 2);
        videoView.setOpenMarquee(true);
        videoView.setAutoContinue(false);
        videoView.setNeedGestureDetector(true);

        videoView.setOnPreparedListener(new IPolyvOnPreparedListener2() {
            @Override
            public void onPrepared() {
                mediaController.preparedView();
                progressView.setViewMaxValue(videoView.getDuration());
                // 没开预加载在这里开始弹幕
                // danmuFragment.start();

            }
        });

        videoView.setOnPreloadPlayListener(new IPolyvOnPreloadPlayListener() {
            @Override
            public void onPlay() {
                // 开启预加载在这里开始弹幕
//                danmuFragment.start();
            }
        });

        videoView.setOnInfoListener(new IPolyvOnInfoListener2() {
            @Override
            public boolean onInfo(int what, int extra) {
                switch (what) {
                    case PolyvMediaInfoType.MEDIA_INFO_BUFFERING_START:
//                        danmuFragment.pause(false);
                        touchSpeedLayout.updateStatus(true);
                        break;
                    case PolyvMediaInfoType.MEDIA_INFO_BUFFERING_END:
//                        danmuFragment.resume(false);
                        touchSpeedLayout.updateStatus(false);
                        break;
                }

                return true;
            }
        });

        videoView.setOnPlayPauseListener(new IPolyvOnPlayPauseListener() {
            @Override
            public void onPause() {
                coverView.stopAnimation();
            }

            @Override
            public void onPlay() {
                coverView.startAnimation();
            }

            @Override
            public void onCompletion() {
                coverView.stopAnimation();
            }
        });

        videoView.setOnChangeModeListener(new IPolyvOnChangeModeListener() {
            @Override
            public void onChangeMode(String changedMode) {
                coverView.changeModeFitCover(videoView, changedMode);
            }
        });

        videoView.setOnVideoStatusListener(new IPolyvOnVideoStatusListener() {
            @Override
            public void onStatus(int status) {
                if (status < 60) {
                    Toast.makeText(getContext(), "状态错误 " + status, Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, String.format("状态正常 %d", status));
                }
            }
        });

        videoView.setOnVideoPlayErrorListener(new IPolyvOnVideoPlayErrorListener2() {
            @Override
            public boolean onVideoPlayError(@PolyvPlayErrorReason.PlayErrorReason int playErrorReason) {
                String message = PolyvErrorMessageUtils.getPlayErrorMessage(playErrorReason);
                message += "(error code " + playErrorReason + ")";

                showErrorView(message);
                return true;
            }
        });

        videoView.setOnErrorListener(new IPolyvOnErrorListener2() {
            @Override
            public boolean onError() {
                String message = "当前视频无法播放，请尝试切换网络重新播放或者向管理员反馈(error code " + PolyvPlayErrorReason.VIDEO_ERROR + ")";
                showErrorView(message);
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        videoView.setOnAdvertisementOutListener(new IPolyvOnAdvertisementOutListener2() {
            @Override
            public void onOut(@NonNull PolyvADMatterVO adMatter) {
//                auxiliaryView.show(adMatter);
            }
        });

        videoView.setOnAdvertisementCountDownListener(new IPolyvOnAdvertisementCountDownListener() {
            @Override
            public void onCountDown(int num) {
                advertCountDown.setText("广告也精彩：" + num + "秒");
                advertCountDown.setVisibility(View.VISIBLE);
            }

            @Override
            public void onEnd() {
                advertCountDown.setVisibility(View.GONE);
                auxiliaryView.hide();
            }
        });

        videoView.setOnAdvertisementEventListener(new IPolyvOnAdvertisementEventListener2() {
            @Override
            public void onShow(PolyvADMatterVO adMatter) {
                Log.i(TAG, "开始播放视频广告");
            }

            @Override
            public void onClick(PolyvADMatterVO adMatter) {
                if (!TextUtils.isEmpty(adMatter.getAddrUrl())) {
                    try {
                        new URL(adMatter.getAddrUrl());
                    } catch (MalformedURLException e1) {
                        Log.e(TAG, PolyvSDKUtil.getExceptionFullMessage(e1, -1));
                        return;
                    }

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(adMatter.getAddrUrl()));
                    getContext().startActivity(intent);
                }
            }
        });

        videoView.setOnQuestionOutListener(new IPolyvOnQuestionOutListener2() {
            @Override
            public void onOut(@NonNull PolyvQuestionVO questionVO) {
                videoView.start();
                switch (questionVO.getType()) {
                    case PolyvQuestionVO.TYPE_QUESTION:
//                        questionView.showAnswerContent(questionVO);
                        break;

                    case PolyvQuestionVO.TYPE_AUDITION:
//                        auditionView.show(questionVO);
                        break;
                }
            }
        });

        videoView.setOnTeaserOutListener(new IPolyvOnTeaserOutListener() {
            @Override
            public void onOut(@NonNull String url) {
//                auxiliaryView.show(url);
            }
        });

        videoView.setOnTeaserCountDownListener(new IPolyvOnTeaserCountDownListener() {
            @Override
            public void onEnd() {
                auxiliaryView.hide();
            }
        });

        videoView.setOnCompletionListener(new IPolyvOnCompletionListener2() {
            @Override
            public void onCompletion() {
//                danmuFragment.pause();
            }
        });

        videoView.setOnVideoSRTListener(new IPolyvOnVideoSRTListener() {
            @Override
            public void onVideoSRT(@Nullable PolyvSRTItemVO subTitleItem) {
                if (subTitleItem == null) {
                    srtTextView.setText("");
                } else {
                    srtTextView.setText(subTitleItem.getSubTitle());
                }

                srtTextView.setVisibility(View.VISIBLE);
            }
        });

        videoView.setOnGestureLeftUpListener(new IPolyvOnGestureLeftUpListener() {

            @Override
            public void callback(boolean start, boolean end) {
                Log.d(TAG, String.format("LeftUp %b %b brightness %d", start, end, videoView.getBrightness((Activity) getContext())));
                int brightness = videoView.getBrightness((Activity) getContext()) + 5;
                if (brightness > 100) {
                    brightness = 100;
                }

                videoView.setBrightness((Activity) getContext(), brightness);
                lightView.setViewLightValue(brightness, end);
            }
        });

        videoView.setOnGestureLeftDownListener(new IPolyvOnGestureLeftDownListener() {

            @Override
            public void callback(boolean start, boolean end) {
                Log.d(TAG, String.format("LeftDown %b %b brightness %d", start, end, videoView.getBrightness((Activity) getContext())));
                int brightness = videoView.getBrightness((Activity) getContext()) - 5;
                if (brightness < 0) {
                    brightness = 0;
                }

                videoView.setBrightness((Activity) getContext(), brightness);
                lightView.setViewLightValue(brightness, end);
            }
        });

        videoView.setOnGestureRightUpListener(new IPolyvOnGestureRightUpListener() {

            @Override
            public void callback(boolean start, boolean end) {
                Log.d(TAG, String.format("RightUp %b %b volume %d", start, end, videoView.getVolume()));
                // 加减单位最小为10，否则无效果
                int volume = videoView.getVolume() + 10;
                if (volume > 100) {
                    volume = 100;
                }

                videoView.setVolume(volume);
                volumeView.setViewVolumeValue(volume, end);
            }
        });

        videoView.setOnGestureRightDownListener(new IPolyvOnGestureRightDownListener() {

            @Override
            public void callback(boolean start, boolean end) {
                Log.d(TAG, String.format("RightDown %b %b volume %d", start, end, videoView.getVolume()));
                // 加减单位最小为10，否则无效果
                int volume = videoView.getVolume() - 10;
                if (volume < 0) {
                    volume = 0;
                }

                videoView.setVolume(volume);
                volumeView.setViewVolumeValue(volume, end);
            }
        });

        videoView.setOnGestureSwipeLeftListener(new IPolyvOnGestureSwipeLeftListener() {

            @Override
            public void callback(boolean start, boolean end) {
                if (!isCanDrag)
                    return;
                // 左滑事件
                Log.d(TAG, String.format("SwipeLeft %b %b", start, end));
                mediaController.hideTickTips();
                if (fastForwardPos == 0) {
                    fastForwardPos = videoView.getCurrentPosition();
                }

                if (end) {
                    if (fastForwardPos < 0)
                        fastForwardPos = 1000;
                    videoView.seekTo(fastForwardPos);
//                    danmuFragment.seekTo();
                    if (videoView.isCompletedState()) {
                        videoView.start();
//                        danmuFragment.resume();
                    }
                    fastForwardPos = 0;
                } else {
                    fastForwardPos -= 10000;
                    if (fastForwardPos <= 0)
                        fastForwardPos = -1;
                }
                progressView.setViewProgressValue(fastForwardPos, videoView.getDuration(), end, false);
            }
        });

        videoView.setOnGestureSwipeRightListener(new IPolyvOnGestureSwipeRightListener() {

            @Override
            public void callback(boolean start, boolean end) {
                if (!isCanDrag)
                    return;
                // 右滑事件
                Log.d(TAG, String.format("SwipeRight %b %b", start, end));
                mediaController.hideTickTips();
                if (fastForwardPos == 0) {
                    fastForwardPos = videoView.getCurrentPosition();
                }

                if (end) {
                    if (fastForwardPos > videoView.getDuration())
                        fastForwardPos = videoView.getDuration();
                    if (!videoView.isCompletedState()) {
                        videoView.seekTo(fastForwardPos);
//                        danmuFragment.seekTo();
                    } else if (videoView.isCompletedState() && fastForwardPos != videoView.getDuration()) {
                        videoView.seekTo(fastForwardPos);
//                        danmuFragment.seekTo();
                        videoView.start();
//                        danmuFragment.resume();
                    }
                    fastForwardPos = 0;
                } else {
                    fastForwardPos += 10000;
                    if (fastForwardPos > videoView.getDuration())
                        fastForwardPos = videoView.getDuration();
                }
                progressView.setViewProgressValue(fastForwardPos, videoView.getDuration(), end, true);
            }
        });

        videoView.setOnGestureClickListener(new IPolyvOnGestureClickListener() {
            @Override
            public void callback(boolean start, boolean end) {
                if (videoView.isInPlaybackState() || videoView.isExceptionCompleted() && mediaController != null)
                    if (mediaController.isShowing())
                        mediaController.hide();
                    else
                        mediaController.show();
            }
        });

        videoView.setOnGestureDoubleClickListener(new IPolyvOnGestureDoubleClickListener() {
            @Override
            public void callback() {
                if ((videoView.isInPlaybackState() || videoView.isExceptionCompleted()) && mediaController != null && !mediaController.isLocked())
                    mediaController.playOrPause();
            }
        });

        videoView.setOnGestureLongTouchListener(new IPolyvOnGestureLongTouchListener() {
            @Override
            public void callback(boolean isTouchLeft, boolean start, boolean end) {
                if (start) {
                    beforeTouchSpeed = videoView.getSpeed();
                    if (beforeTouchSpeed < 2 && videoView.isPlaying() && !mediaController.isLocked()) {
                        videoView.setSpeed(2);
                        touchSpeedLayout.show();
                    }
                } else {
                    videoView.setSpeed(beforeTouchSpeed);
                    mediaController.initSpeedView((int) (beforeTouchSpeed * 10));
                    touchSpeedLayout.hide();
                }
            }
        });

        videoErrorRetry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                videoErrorLayout.setVisibility(View.GONE);
                //调用setVid方法视频会自动播放
                play(vid, bitrate, true, isMustFromLocal);
            }
        });
    }

    private void showErrorView(String message) {
        videoErrorLayout.setVisibility(View.VISIBLE);
        videoErrorContent.setText(message);
    }

    private void clearGestureInfo() {
        videoView.clearGestureInfo();
        progressView.hide();
        volumeView.hide();
        lightView.hide();
    }

    /**
     * RN为了性能重写空实现了requestLayout，所有元素都不能正常调用
     * 同时动态addView的控件会因此宽高为0
     * 视频控件里面的renderview会因此黑屏没有视频画面，所以在这里主动measure+layout，确定宽高
     * 不知道为什么，在原生模块那里重写并没有生效。
     */
    @Override
    public void requestLayout() {
        super.requestLayout();
        post(measureAndLayout);
    }

    private final Runnable measureAndLayout = new Runnable() {
        @Override
        public void run() {
            measure(
                    MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY));
            layout(getLeft(), getTop(), getRight(), getBottom());
        }
    };

    private void resetMarquee() {
        if (marqueeView != null) {
            marqueeView.removeAllItem();
            marqueeView.addItem(marqueeItem);
        }
    }


    // <editor-fold defaultstate="collapsed" desc="生命周期函数">

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        post(new Runnable() {
            @Override
            public void run() {
                resetMarquee();
            }
        });
    }

    public void onResume() {
        //回来后继续播放
        if (isPlay) {
            videoView.onActivityResume();
//            danmuFragment.resume();
//            if (auxiliaryView.isPauseAdvert()) {
//                auxiliaryView.hide();
//            }
        }
        mediaController.resume();
    }

    public void onPause() {
        clearGestureInfo();
        mediaController.pause();
    }

    public void onStop() {
        //弹出去暂停
        isPlay = videoView.onActivityStop();
//        danmuFragment.pause();
    }

    public boolean onBackPressed() {
        if (mediaController != null && mediaController.isFullScreen()) {
            mediaController.changeToSmallScreen();
            return true;
        }
        return false;
    }

    public void onDestroy() {
        videoView.destroy();
        questionView.hide();
        auditionView.hide();
        auxiliaryView.hide();
        firstStartView.hide();
        coverView.hide();
        mediaController.disable();
    }
// </editor-fold>

   // <editor-fold defaultstate="collapsed" desc="播放器相关公开接口函数">

    /**
     * 播放视频
     *
     * @param vid             视频id
     * @param bitrate         码率（清晰度）
     * @param startNow        是否现在开始播放视频
     * @param isMustFromLocal 是否必须从本地（本地缓存的视频）播放
     */
    public void play(final String vid, final int bitrate, boolean startNow, final boolean isMustFromLocal) {
        if (TextUtils.isEmpty(vid)) return;
        if (iv_vlms_cover != null && iv_vlms_cover.getVisibility() == View.VISIBLE) {
            iv_vlms_cover.setVisibility(View.GONE);
        }

//        videoView.release();
        srtTextView.setVisibility(View.INVISIBLE);
//        mediaController.hide();
        mediaController.show();
        loadingProgress.setVisibility(View.GONE);
        questionView.hide();
        auditionView.hide();
        auxiliaryVideoView.hide();
        auxiliaryLoadingProgress.setVisibility(View.GONE);
        auxiliaryView.hide();
        advertCountDown.setVisibility(View.GONE);
        firstStartView.hide();
        progressView.resetMaxValue();

//        danmuFragment.setVid(vid, videoView);
        if (startNow) {
            //调用setVid方法视频会自动播放
            videoView.setVid(vid, bitrate, isMustFromLocal);
        } else {
            //视频不播放，先显示一张缩略图
            firstStartView.setCallback(new PolyvPlayerPreviewView.Callback() {

                @Override
                public void onClickStart() {
                    /**
                     * 调用setVid方法视频会自动播放
                     * 如果是有学员登陆的播放，可以在登陆的时候通过
                     * {@link com.easefun.polyvsdk.PolyvSDKClient.getinstance().setViewerId()}设置学员id
                     * 或者调用{@link videoView.setVidWithStudentId}传入学员id进行播放
                     */
                    videoView.setVidWithViewerId(vid, bitrate, isMustFromLocal, PolyvSDKClient.getInstance().getViewerId());

                }
            });

            firstStartView.show(vid);
        }
        if (PolyvVideoVO.MODE_VIDEO.equals(videoView.getPriorityMode())) {
            coverView.hide();
        }
    }

    public void updateVid(String vid) {
        this.vid = vid;
    }


    @Override
    public void start() {
        mediaController.playOrPause();
    }

    @Override
    public void pause() {
        mediaController.playOrPause();
    }

    @Override
    public int getDuration() {
        return videoView.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return videoView.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {

        videoView.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return videoView.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return videoView.getBufferPercentage();
    }

    @Override
    public boolean canPause() {
        return videoView.canPause();
    }

    @Override
    public boolean canSeekBackward() {
        return videoView.canSeekBackward();
    }

    @Override
    public boolean canSeekForward() {
        return videoView.canSeekForward();
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    public void setPlayerFullScreen(boolean fullScreen){
        if(fullScreen){
            if(!PolyvScreenUtils.isLandscape(getContext())){
                //设置横屏策略为FULLSCREEN_PORTRAIT
                //因为iOS的竖屏全屏需要设置一个展位图View，没有则为横屏全屏
                mediaController.changeToFullScreen();
            }
        }else {
            if(!PolyvScreenUtils.isPortrait(getContext())){
                mediaController.changeToSmallScreen();
            }
        }
    }

    // 设置跑马灯
    public void playMarquee(ReadableMap marquee) {
        if(!marquee.hasKey("content")){
//            PolyvToastUtil.show(getContext(),"未设置跑马灯内容");
            Toast.makeText(getContext(),"未设置跑马灯内容",Toast.LENGTH_SHORT).show();

            return;
        }
        int displayDuration = 8*1000,font = 20,alpha = 255,maxRollInterval=1*1000,color=0xffffff,reappearTime=3*1000;
        if(marquee.hasKey("displayDuration")){
            displayDuration = marquee.getInt("displayDuration")*1000;
        }
        if(marquee.hasKey("font")){
            font = marquee.getInt("font");
        }

        if(marquee.hasKey("alpha")){
            alpha = (int) (255*marquee.getDouble("alpha"));
        }
        if(marquee.hasKey("maxRollInterval")){
            maxRollInterval = marquee.getInt("maxRollInterval")*1000;
        }

        if(marquee.hasKey("color")){
            color = Color.parseColor(marquee.getString("color"));
        }
        if(marquee.hasKey("reappearTime")){
            reappearTime = marquee.getInt("reappearTime")*1000;
        }

        videoView.setMarqueeView(marqueeView, marqueeItem = new PolyvMarqueeItem()
                .setStyle(PolyvMarqueeItem.STYLE_ROLL) //样式
                .setDuration(displayDuration) //时长
                .setText(marquee.getString("content")) //文本
                .setSize(font) //字体大小
                .setColor(color) //字体颜色
                .setTextAlpha(alpha) //字体透明度
                .setLifeTime(maxRollInterval)//显示时间
                .setReappearTime(reappearTime)) // 设置跑马灯再次出现的间隔
        ;
    }
    // </editor-fold>

}
