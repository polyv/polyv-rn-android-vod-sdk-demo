//
//  PolyvVodPlayerWrapperView.m
//  PolyvVodRnDemo
//
//  Created by 李长杰 on 2019/2/18.
//  Copyright © 2019 Facebook. All rights reserved.
//

#import "PolyvVodPlayerWrapperView.h"
#import <PLVVodSDK/PLVVodSDK.h>
#import "PLVVodSkinPlayerController.h"

@interface PolyvVodPlayerWrapperView ()

@property (nonatomic, strong) NSString *vid;
@property (nonatomic, strong) PLVVodSkinPlayerController *player;
@property (nonatomic, assign) CGRect fullScreenRect;

@property CGRect orginRect;

@end

@implementation PolyvVodPlayerWrapperView

// 初始化播放器
- (void)setup {
  self.player = [[PLVVodSkinPlayerController alloc] initWithNibName:nil bundle:nil];
//  self.player.rememberLastPosition = YES;
  self.player.enableBackgroundPlayback = YES;
  self.player.enableTeaser = YES;
  [self addSubview:self.player.view];
  
  CGRect frame = self.player.view.frame;
  frame.size = self.frame.size;
  self.player.view.frame = frame;
  self.player.view.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
  
  self.orginRect = CGRectZero;
  
  self.player.wrapperView = self;
  
  //根据进入时屏幕横竖屏判断
  UIInterfaceOrientation orientation = [UIApplication sharedApplication].statusBarOrientation;
  if (orientation == UIInterfaceOrientationUnknown ||
         orientation == UIInterfaceOrientationPortrait) {
         //横屏
     } else {
       [self setFullScreen];
     }
}

- (void)setPlay_parameters:(NSDictionary *)play_parameters {
  BOOL autoplay = ((NSNumber *)[play_parameters objectForKey:@"is_auto_start"]).boolValue;
  self.player.autoplay = autoplay;
  
  NSString *vid = [play_parameters objectForKey:@"vid"];
  [self updateVid:vid];
  
  BOOL fullScreen = [[play_parameters objectForKey:@"full_screen"] boolValue];
  if (fullScreen) {
    [self performSelector:@selector(setFullScreen) withObject:nil afterDelay:0.1];
  }
  
  NSDictionary *marqueeDic = [play_parameters objectForKey:@"marquee"];
  if (marqueeDic) {
    int displayDuration = [[marqueeDic objectForKey:@"displayDuration"] intValue];
    int maxRollInterval = [[marqueeDic objectForKey:@"maxRollInterval"] intValue];
    NSString *content = [marqueeDic objectForKey:@"content"];
    NSString *colorStr = [marqueeDic objectForKey:@"color"];
    NSNumber *alpha = [marqueeDic objectForKey:@"alpha"];
    NSNumber *font= [marqueeDic objectForKey:@"font"];
    
    if (content.length != 0) {
      if (displayDuration == 0) {
        displayDuration = 8;
      }
      if (colorStr.length == 0) {
        colorStr = @"#FFFFFF";
      }
      if (!alpha) {
        alpha = @(1.0);
      }
      if (!font) {
        font = @(20);
      }
      
      PLVMarqueeModel *marqueeModel = [[PLVMarqueeModel alloc]init];
      marqueeModel.style = PLVMarqueeModelStyleRoll;
      marqueeModel.speed = displayDuration;
      marqueeModel.interval = maxRollInterval;
      marqueeModel.content = content;
      marqueeModel.fontColor = colorStr;
      marqueeModel.alpha = [alpha floatValue];
      marqueeModel.fontSize = [font integerValue];
      self.player.marqueeView.hidden = NO;
      [self.player.marqueeView setPLVMarqueeModel:marqueeModel];
      [self.player.marqueeView start];
    } else {
      self.player.marqueeView.hidden = YES;
      [self.player.marqueeView stop];
    }
  }
}

// 全屏-半屏切换
- (void)switchToFullScreen:(BOOL)fullScreen {
  NSLog(@"switchToFullScreen - %@", self);
  if (CGRectEqualToRect(self.orginRect, CGRectZero)) {
    self.orginRect = self.bounds;
  }
  
  if (fullScreen) {
    NSLog(@"switch to fullScreen");
    self.player.view.frame = self.fullScreenRect;
    
    [self.player.view removeFromSuperview];
    [[UIApplication sharedApplication].keyWindow addSubview:self.player.view];
  } else {
    NSLog(@"switch to halfScreen");
    if (!CGRectEqualToRect(self.orginRect, CGRectZero)) {
      self.player.view.frame = self.orginRect;
      
      [self.player.view removeFromSuperview];
      [self addSubview:self.player.view];
    }
  }
}

// 切换vid
- (void)updateVid:(NSString *)vid {
  if (vid.length == 0 || [_vid isEqualToString:vid]) {
    return;
  }
  
  _vid = vid;
  
  // 无网络情况下，优先检测本地视频文件
  PLVVodLocalVideo *local = [PLVVodLocalVideo localVideoWithVid:vid dir:[PLVVodDownloadManager sharedManager].downloadDir];
  if (local && local.path){
    self.player.video = local;
  } else {
    // 有网情况下，也可以调用此接口，只要存在本地视频，都会优先播放本地视频
    __weak typeof(self) weakSelf = self;
    [PLVVodVideo requestVideoWithVid:vid completion:^(PLVVodVideo *video, NSError *error) {
      if (!video.available) {
        return;
      }
      weakSelf.player.video = video;
    }];
  }
}

// 开始/停止
- (void)startOrPause {
  if (self.player.playbackState == PLVVodPlaybackStatePlaying) {
    dispatch_async(dispatch_get_main_queue(), ^{
      [self.player pause];
    });
  } else if (self.player.playbackState == PLVVodPlaybackStatePaused || self.player.playbackState == PLVVodPlaybackStateStopped) {
     dispatch_async(dispatch_get_main_queue(), ^{
       [self.player play];
     });
  }
}

// 开始
- (void)start {
  dispatch_async(dispatch_get_main_queue(), ^{
    [self.player play];
  });
}

// 停止
- (void)pause {
  dispatch_async(dispatch_get_main_queue(), ^{
    [self.player pause];
  });
}

// 销毁
- (void)destroyPlayer {
  dispatch_async(dispatch_get_main_queue(), ^{
    [self.player destroyPlayer];
  });
}

// 横竖屏切换
- (void)setFullScreen {
  [self setFullScreen:YES];
}

- (void)setFullScreen:(BOOL)fullScreen {
  dispatch_async(dispatch_get_main_queue(), ^{
      if (CGRectEqualToRect(self.player.view.frame, self.fullScreenRect)) {
        if (!fullScreen) {
          [self.player.playerControl.fullShrinkscreenButton sendActionsForControlEvents:UIControlEventTouchUpInside];
        }
      } else {
        if (fullScreen) {
          [self.player.playerControl.fullShrinkscreenButton sendActionsForControlEvents:UIControlEventTouchUpInside];
          [self switchToFullScreen:YES];
        }
      }
  });
}

#pragma mark -- private method
- (UIColor *)colorWithHexStringRGB:(NSString *)hexColor {
  
  if(!hexColor || [hexColor isKindOfClass:[NSNull class]]){
    return [UIColor blackColor];
  }
  
  hexColor = [[hexColor stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]] uppercaseString];
  
  // String should be 6 or 7 characters if it includes '#'
  if ([hexColor length] < 6)
    return [UIColor blackColor];
  
  // strip # if it appears
  if ([hexColor hasPrefix:@"#"])
    hexColor = [hexColor substringFromIndex:1];
  
  // if the value isn't 6 characters at this point return
  // the color black
  if ([hexColor length] != 6)
    return [UIColor blackColor];
  
  // Separate into r, g, b substrings
  NSRange range;
  range.location = 0;
  range.length = 2;
  
  NSString *rString = [hexColor substringWithRange:range];
  
  range.location = 2;
  NSString *gString = [hexColor substringWithRange:range];
  
  range.location = 4;
  NSString *bString = [hexColor substringWithRange:range];
  
  // Scan values
  unsigned int r, g, b;
  [[NSScanner scannerWithString:rString] scanHexInt:&r];
  [[NSScanner scannerWithString:gString] scanHexInt:&g];
  [[NSScanner scannerWithString:bString] scanHexInt:&b];
  
  return [UIColor colorWithRed:((float) r / 255.0f)
                         green:((float) g / 255.0f)
                          blue:((float) b / 255.0f)
                         alpha:1.0f];
}

#pragma mark -- getter
- (CGRect)fullScreenRect {
  CGRect screenBounds = [UIScreen mainScreen].bounds;
  CGFloat screenWidth = screenBounds.size.width;
  CGFloat screenHeight = screenBounds.size.height;
  if (screenHeight > screenWidth) {
    return CGRectMake(0, 0, screenHeight, screenWidth);
  }
  return CGRectMake(0, 0, screenWidth, screenHeight);
}

@end
