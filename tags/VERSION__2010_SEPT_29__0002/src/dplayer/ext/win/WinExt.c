#include <windows.h>
#include <shellapi.h>
#include <stdio.h>
#include <jni.h>

#define JNIENV(func) ((*env)->func)
#define JNIDECL(func) _ ## func

#define WM_APPCOMMAND 0x319u
#define APPCOMMAND_VOLUME_MUTE 0x80000u
#define APPCOMMAND_VOLUME_DOWN 0x90000u
#define APPCOMMAND_VOLUME_UP   0xA0000u

HWND hwnd=0;

JNIEXPORT void JNICALL JNIDECL( Java_dplayer_ext_win_WinExt_nativeSetHWND )(JNIEnv *env, jobject object, HWND hwnd_) {
  hwnd=hwnd_;
}

HWND getWindowHandle() {
    return hwnd;
}

HWND findWindowHandle(const wchar_t* windowName) {
    return FindWindowW(L"SWT_Window0", windowName);
}

JNIEXPORT jboolean JNICALL JNIDECL( Java_dplayer_ext_win_WinExt_nativeIsRunning ) (JNIEnv *env, jobject object, jstring name) {
	const wchar_t *cWndName = (const wchar_t *)JNIENV(GetStringChars)(env, name, 0);
    jboolean ret=findWindowHandle(cWndName) != NULL;
	JNIENV(ReleaseStringChars)(env, name, (const jchar *)cWndName);
	return ret;
}

JNIEXPORT void JNICALL JNIDECL( Java_dplayer_ext_win_WinExt_nativeVolumeMute )(JNIEnv *env, jobject object) {
	 HWND mHwnd = getWindowHandle();
	 if (mHwnd != NULL) {
        SendMessage(mHwnd, WM_APPCOMMAND, (WPARAM) mHwnd, APPCOMMAND_VOLUME_MUTE);
    }
}

JNIEXPORT void JNICALL JNIDECL( Java_dplayer_ext_win_WinExt_nativeVolumeUp) (JNIEnv *env, jobject object) {
	 HWND mHwnd = getWindowHandle();
	 if (mHwnd != NULL) {
        SendMessage(mHwnd, WM_APPCOMMAND, (WPARAM) mHwnd, APPCOMMAND_VOLUME_UP);
    }
}

JNIEXPORT void JNICALL JNIDECL( Java_dplayer_ext_win_WinExt_nativeVolumeDown)(JNIEnv *env, jobject object) {
	 HWND mHwnd = getWindowHandle();
	 if (mHwnd != NULL) {
        SendMessage(mHwnd, WM_APPCOMMAND, (WPARAM) mHwnd, APPCOMMAND_VOLUME_DOWN);
    }
}

JNIEXPORT jint JNICALL JNIDECL( Java_dplayer_ext_win_WinExt_getLeftVolume)(JNIEnv *env, jobject object) {
    UINT uRetVal;
    DWORD volume;
    long lLeftVol, lRightVol;
    WAVEOUTCAPS waveCaps;

    // Make sure there is at least one wave output device to work with.
    if (waveOutGetNumDevs() < 1) {
        // No wave devices.
        return -1;
    }

    // This sample uses a hard-coded 0 as the device ID, but retail
    // applications should loop on devices 0 through N-1, where N is the
    // number of devices returned by waveOutGetNumDevs().
    if (!waveOutGetDevCaps(0, (LPWAVEOUTCAPS) &waveCaps, sizeof(WAVEOUTCAPS))) {
        // Verify the device supports volume changes.
        if (waveCaps.dwSupport & WAVECAPS_VOLUME) {
            uRetVal = waveOutGetVolume(0, (LPDWORD) &volume);
            // The low word is the left volume, the high word is the right.
            lLeftVol = (long) LOWORD(volume);
            lRightVol = (long) HIWORD(volume);
            return lLeftVol;
        }
    }

    return -1;
}

JNIEXPORT jint JNICALL JNIDECL( Java_dplayer_ext_win_WinExt_getRightVolume )(JNIEnv *env, jobject object) {
    UINT uRetVal;
    DWORD volume;
    long lLeftVol, lRightVol;
    WAVEOUTCAPS waveCaps;

    // Make sure there is at least one wave output device to work with.
    if (waveOutGetNumDevs() < 1) {
        // No wave devices.
        return -1;
    }

    if (!waveOutGetDevCaps(0, (LPWAVEOUTCAPS) &waveCaps, sizeof(WAVEOUTCAPS))) {
        // Verify the device supports volume changes.
        if (waveCaps.dwSupport & WAVECAPS_VOLUME) {
            uRetVal = waveOutGetVolume(0, (LPDWORD) &volume);
            // The low word is the left volume, the high word is the right.
            lLeftVol = (long) LOWORD(volume);
            lRightVol = (long) HIWORD(volume);
            return lRightVol;
        }
    }

    return -1;
}

JNIEXPORT void JNICALL JNIDECL( Java_dplayer_ext_win_WinExt_setLeftVolume )(JNIEnv *env, jobject object, jint vol) {
    UINT uRetVal;
    DWORD volume, nvolume;
    WAVEOUTCAPS waveCaps;

    // Make sure there is at least one wave output device to work with.
    if (waveOutGetNumDevs() < 1) {
        // No wave devices.
        return;
    }

    if (!waveOutGetDevCaps(0, (LPWAVEOUTCAPS) &waveCaps, sizeof(WAVEOUTCAPS))) {
        // Verify the device supports volume changes.
        if (waveCaps.dwSupport & WAVECAPS_VOLUME) {
            uRetVal = waveOutGetVolume(0, (LPDWORD) &volume);
            // The low word is the left volume, the high word is the right.
            nvolume = volume & (DWORD)0xFFFF0000UL;
            nvolume |= (long) vol;
            waveOutSetVolume(0, nvolume);
        }
    }
}

JNIEXPORT void JNICALL JNIDECL( Java_dplayer_ext_win_WinExt_setRightVolume)(JNIEnv *env, jobject object, jint vol) {
    UINT uRetVal;
    DWORD volume, nvolume;
    WAVEOUTCAPS waveCaps;

    // Make sure there is at least one wave output device to work with.
    if (waveOutGetNumDevs() < 1) {
        // No wave devices.
        return;
    }

    if (!waveOutGetDevCaps(0, (LPWAVEOUTCAPS) &waveCaps, sizeof(WAVEOUTCAPS))) {
        // Verify the device supports volume changes.
        if (waveCaps.dwSupport & WAVECAPS_VOLUME) {
            uRetVal = waveOutGetVolume(0, (LPDWORD) &volume);
            // The low word is the left volume, the high word is the right.
            nvolume = volume & (DWORD)0x0000FFFFUL;
            nvolume |= ((long) vol) << 16;
            waveOutSetVolume(0, nvolume);
        }
    }
}

static void
throwByName(JNIEnv *env, const char *name, const char *msg)
{
  jclass cls = JNIENV(FindClass)(env, name);

  if (cls != 0) /* Otherwise an exception has already been thrown */
      JNIENV(ThrowNew)(env, cls, msg);

  /* It's a good practice to clean up the local references. */
  JNIENV(DeleteLocalRef)(env, cls);
}

void throwEx(JNIEnv *env, char* error){
  throwByName(env,"java/lang/Exception",error);
}

JNIEXPORT void JNICALL JNIDECL( Java_dplayer_ext_win_WinExt_launchDefaultBrowser)(JNIEnv *env, jobject object, jstring url) {
  char err[256];
  const wchar_t *url_ = (const wchar_t *)JNIENV(GetStringChars)(env, url, 0);
  int ret=(int)ShellExecuteW(0, L"open", url_, 0, 0, SW_SHOWNORMAL);
  JNIENV(ReleaseStringChars)(env, url, (const jchar *)url_);
  if(ret>32)return;
  switch(ret){
    case 0: throwEx(env,"The operating system is out of memory or resources.");break;
    case ERROR_FILE_NOT_FOUND: throwEx(env,"The specified file was not found.");break;
    case ERROR_PATH_NOT_FOUND: throwEx(env,"The specified path was not found.");break;
    case ERROR_BAD_FORMAT: throwEx(env,"The .exe file is invalid (non-Microsoft Win32 .exe or error in .exe image).");break;
    case SE_ERR_ACCESSDENIED: throwEx(env,"The operating system denied access to the specified file."); break;
    case SE_ERR_ASSOCINCOMPLETE: throwEx(env,"The file name association is incomplete or invalid.");break;
    case SE_ERR_DDEBUSY: throwEx(env,"The Dynamic Data Exchange (DDE) transaction could not be completed because other DDE transactions were being processed.");break;
    case SE_ERR_DDEFAIL: throwEx(env,"The DDE transaction failed.");break;
    case SE_ERR_DDETIMEOUT: throwEx(env,"The DDE transaction could not be completed because the request timed out.");break;
    case SE_ERR_DLLNOTFOUND: throwEx(env,"The specified dynamic-link library (DLL) was not found.");break;
    case SE_ERR_NOASSOC: throwEx(env,"There is no application associated with the given file name extension. This error will also be returned if you attempt to print a file that is not printable.");break;
    case SE_ERR_OOM: throwEx(env,"There was not enough memory to complete the operation.");break;
    case SE_ERR_SHARE: throwEx(env,"A sharing violation occurred.");break;
    default:
      snprintf((char*)err,255,"Unknown error: %d.",ret);throwEx(env,err);
      break;
  }
}
