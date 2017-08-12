#ifndef SUBTITLE_SERVICE_UTILS_H
#define SUBTITLE_SERVICE_UTILS_H

#ifdef  __cplusplus
extern "C" {
#endif

    void subtitleShow();
    void subtitleOpen(char* path, void *pthis);
    void subtitleOpenIdx(int idx);
    void subtitleClose();
    int subtitleGetTotal();
    void subtitleNext();
    void subtitlePrevious();
    void subtitleShowSub(int pos);
    void subtitleOption();
    int subtitleGetType();
    char* subtitleGetTypeStr();
    int subtitleGetTypeDetial();
    void subtitleSetTextColor(int color);
    void subtitleSetTextSize(int size);
    void subtitleSetGravity(int gravity);
    void subtitleSetTextStyle(int style);
    void subtitleSetPosHeight(int height);
    void subtitleSetImgRatio(float ratioW, float ratioH, int maxW, int maxH);
    void subtitleClear();
    void subtitleResetForSeek();
    void subtitleHide();
    void subtitleDisplay();
    char* subtitleGetCurName();
    char* subtitleGetName(int idx);
    char* subtitleGetLanguage(int idx);
    void subtitleLoad(char* path);
    void subtitleSetSurfaceViewParam(int x, int y, int w, int h);
#ifdef  __cplusplus
}
#endif

#endif
