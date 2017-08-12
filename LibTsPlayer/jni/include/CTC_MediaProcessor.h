/*
 * author: bo.cao@amlogic.com
 * date: 2012-07-20
 * wrap original source code for CTC usage
 */

#ifndef _CTC_MEDIAPROCESSOR_H_
#define _CTC_MEDIAPROCESSOR_H_
#include "CTsPlayer.h"

class CTC_MediaProcessor:public CTsPlayer
{

};

// 获取CTC_MediaProcessor 派生类的实例对象。在CTC_MediaProcessor () 这个接口的实现中，需要创建一个
// CTC_MediaProcessor 派生类的实例，然后返回这个实例的指针
ITsPlayer* GetMediaProcessor();  // { return NULL; }
#ifdef USE_OPTEEOS
ITsPlayer* GetMediaProcessor(bool DRMMode);
#endif

// 获取底层模块实现的接口版本号。将来如果有多个底层接口定义，使得上层与底层之间能够匹配。本版本定义
// 返回为1
int GetMediaProcessorVersion();  //  { return 0; }

#endif  // _CTC_MEDIAPROCESSOR_H_