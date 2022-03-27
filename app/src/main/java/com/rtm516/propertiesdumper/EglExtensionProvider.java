package com.rtm516.propertiesdumper;

import android.opengl.GLES10;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

// From https://gitlab.com/AuroraOSS/AuroraStore/-/blob/b34e14e13a8b4dbbe9f1303b166e3a6b65e85e97/app/src/main/java/com/aurora/store/data/providers/EglExtensionProvider.kt
public class EglExtensionProvider {
    public static List<String> eglExtensions;

    static {
        Set<String> glExtensions = new HashSet<>();
        EGL10 egl10 = (EGL10) EGLContext.getEGL();
        EGLDisplay display = egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        egl10.eglInitialize(display, new int[2]);
        int[] cf = new int[1];
        if (egl10.eglGetConfigs(display, null, 0, cf)) {
            EGLConfig[] configs = new EGLConfig[cf[0]];
            if (egl10.eglGetConfigs(display, configs, cf[0], cf)) {
                int[] a1 = new int[]{
                    EGL10.EGL_WIDTH,
                    EGL10.EGL_PBUFFER_BIT,
                    EGL10.EGL_HEIGHT,
                    EGL10.EGL_PBUFFER_BIT,
                    EGL10.EGL_NONE
                };
                int[] a2 = new int[]{12440, EGL10.EGL_PIXMAP_BIT, EGL10.EGL_NONE};
                int[] a3 = new int[1];
                for (int i = 0; i < cf[0]; i++) {
                    egl10.eglGetConfigAttrib(display, configs[i], EGL10.EGL_CONFIG_CAVEAT, a3);
                    if (a3[0] != EGL10.EGL_SLOW_CONFIG) {
                        egl10.eglGetConfigAttrib(
                            display,
                            configs[i],
                            EGL10.EGL_SURFACE_TYPE,
                            a3
                        );
                        if ((1 & a3[0]) != 0) {
                            egl10.eglGetConfigAttrib(
                                display,
                                configs[i],
                                EGL10.EGL_RENDERABLE_TYPE,
                                a3
                            );
                            if ((1 & a3[0]) != 0) {
                                addExtensionsForConfig(
                                    egl10,
                                    display,
                                    configs[i],
                                    a1,
                                    null,
                                    glExtensions
                                );
                            }
                            if ((4 & a3[0]) != 0) {
                                addExtensionsForConfig(
                                    egl10,
                                    display,
                                    configs[i],
                                    a1,
                                    a2,
                                    glExtensions
                                );
                            }
                        }
                    }
                }
            }
        }
        egl10.eglTerminate(display);
        List<String> sorted = new ArrayList<>(glExtensions);
        Collections.sort(sorted);
        eglExtensions = sorted;
    }

    private static void addExtensionsForConfig(
        EGL10 egl10,
        EGLDisplay eglDisplay,
        EGLConfig eglConfig,
        int[] ai,
        int[] ai1,
        Set<String> set
    ) {
        EGLContext eglContext = egl10.eglCreateContext(eglDisplay, eglConfig, EGL10.EGL_NO_CONTEXT, ai1);
        if (eglContext == EGL10.EGL_NO_CONTEXT) {
            return;
        }
        EGLSurface eglSurface = egl10.eglCreatePbufferSurface(eglDisplay, eglConfig, ai);
        if (eglSurface == EGL10.EGL_NO_SURFACE) {
            egl10.eglDestroyContext(eglDisplay, eglContext);
        } else {
            egl10.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext);
            String s = GLES10.glGetString(7939);
            if (!TextUtils.isEmpty(s)) {
                String[] as = s.split(" ");
                int i = as.length;
                set.addAll(Arrays.asList(as).subList(0, i));
            }
            egl10.eglMakeCurrent(
                eglDisplay,
                EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_CONTEXT
            );
            egl10.eglDestroySurface(eglDisplay, eglSurface);
            egl10.eglDestroyContext(eglDisplay, eglContext);
        }
    }

}
