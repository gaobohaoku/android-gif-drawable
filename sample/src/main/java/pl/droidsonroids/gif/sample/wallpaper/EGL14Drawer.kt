package pl.droidsonroids.gif.sample.wallpaper

import android.annotation.TargetApi
import android.opengl.EGL14.*
import android.opengl.EGLConfig
import android.opengl.GLUtils
import android.os.Build
import android.view.SurfaceHolder

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
class EGL14Drawer {
    private var eglDisplay = EGL_NO_DISPLAY
    private var eglSurface = EGL_NO_SURFACE
    private var eglContext = EGL_NO_CONTEXT

    fun initialize(holder: SurfaceHolder) {
        eglDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY)
        if (eglDisplay == EGL_NO_DISPLAY) {
            throw IllegalStateException("Unable to obtain EGL14 display")
        }
        if (!eglInitialize(eglDisplay, null, 0, null, 0)) {
            throw IllegalStateException("Unable to initialize EGL14: $eglError")
        }

        val eglConfigs = arrayOfNulls<EGLConfig>(1)
        val numConfigs = IntArray(1)
        val configAttributes = intArrayOf(
            EGL_RED_SIZE,
            8,
            EGL_GREEN_SIZE,
            8,
            EGL_BLUE_SIZE,
            8,
            EGL_ALPHA_SIZE,
            8,
            EGL_RENDERABLE_TYPE,
            EGL_OPENGL_ES2_BIT,
            EGL_SURFACE_TYPE,
            EGL_PBUFFER_BIT,
            EGL_NONE
        )

        if (!eglChooseConfig(eglDisplay, configAttributes, 0, eglConfigs, 0, eglConfigs.size, numConfigs, 0)) {
            throw IllegalStateException("Unable to find RGB888 ES2 EGL config: $eglError")
        }

        val contextAttributes = intArrayOf(EGL_CONTEXT_CLIENT_VERSION, 2, EGL_NONE)
        eglContext = eglCreateContext(eglDisplay, eglConfigs[0], EGL_NO_CONTEXT, contextAttributes, 0)
        if (eglContext == EGL_NO_CONTEXT) {
            throw IllegalStateException("Unable to create EGL context: $eglError")
        }
        eglSurface = eglCreateWindowSurface(eglDisplay, eglConfigs[0], holder, null, 0)
        if (!eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
            throw IllegalStateException("Unable to initialize EGL: $eglError")
        }
    }

    fun draw() {
        eglSwapBuffers(eglDisplay, eglSurface)
    }

    fun destroy() {
        eglMakeCurrent(eglDisplay, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT)
        eglDestroySurface(eglDisplay, eglSurface)
        eglSurface = EGL_NO_SURFACE
        eglDestroyContext(eglDisplay, eglContext)
        eglContext = EGL_NO_CONTEXT
        eglDisplay = EGL_NO_DISPLAY
    }

    private val eglError: String
        get() = GLUtils.getEGLErrorString(eglGetError())
}