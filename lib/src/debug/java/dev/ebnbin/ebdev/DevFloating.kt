package dev.ebnbin.ebdev

import android.app.Activity
import android.app.Application
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatDelegate
import dev.ebnbin.ebui.EBUIPrefs
import dev.ebnbin.ebui.requireActivity
import java.lang.ref.WeakReference

/**
 * Dev 悬浮按钮.
 */
internal object DevFloating : Application.ActivityLifecycleCallbacks, DevFloatingView.Listener {
    private var currentActivityRef: WeakReference<Activity>? = null

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivityRef = WeakReference(activity)
        show(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        hide(activity)
        currentActivityRef = null
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    var isEnabled: Boolean = true
        set(value) {
            if (field == value) return
            field = value
            currentActivityRef?.get()?.let {
                if (value) {
                    show(it)
                } else {
                    hide(it, force = true)
                }
            }
        }

    private fun checkEnabled(activity: Activity): Boolean {
        return isEnabled && !EBDev.config.devFloatingExceptActivityNameSet().contains(activity::class.java.name)
    }

    private fun show(activity: Activity) {
        if (!checkEnabled(activity)) return
        activity.window.decorView.post {
            internalHide(activity)
            internalShow(activity)
        }
    }

    fun update(activity: Activity, x: Int, y: Int) {
        if (!checkEnabled(activity)) return
        EBDevPrefs.devFloatingX.value = x
        EBDevPrefs.devFloatingY.value = y
        activity.window.decorView.post {
            internalUpdate(activity, x, y)
        }
    }

    private fun hide(activity: Activity, force: Boolean = false) {
        if (!force && !checkEnabled(activity)) return
        activity.window.decorView.post {
            internalHide(activity)
        }
    }

    private val TAG_POPUP_WINDOW = "popupWindow".hashCode()

    private fun internalShow(activity: Activity) {
        val popupWindow = PopupWindow().also {
            val devFloatingView = DevFloatingView(activity)
            devFloatingView.listener = this
            it.contentView = devFloatingView
            it.width = ViewGroup.LayoutParams.WRAP_CONTENT
            it.height = ViewGroup.LayoutParams.WRAP_CONTENT
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val x = EBDevPrefs.devFloatingX.value
            val y = EBDevPrefs.devFloatingY.value
            it.showAtLocation(activity.window.decorView, Gravity.NO_GRAVITY, x, y)
        }
        activity.window.decorView.setTag(TAG_POPUP_WINDOW, popupWindow)
    }

    private fun internalUpdate(activity: Activity, x: Int, y: Int) {
        val popupWindow = activity.window.decorView.getTag(TAG_POPUP_WINDOW) as PopupWindow? ?: return
        popupWindow.update(x, y, popupWindow.width, popupWindow.height)
    }

    private fun internalHide(activity: Activity) {
        val popupWindow = activity.window.decorView.getTag(TAG_POPUP_WINDOW) as PopupWindow? ?: return
        popupWindow.dismiss()
        activity.window.decorView.setTag(TAG_POPUP_WINDOW, null)
    }

    override fun onScroll(view: View, x: Int, y: Int) {
        val activity = view.context.requireActivity()
        update(activity, x, y)
    }

    override fun onDoubleTap(view: View): Boolean {
        val activity = view.context.requireActivity()
        if (!checkEnabled(activity)) return false
        activity.openDev()
        return true
    }

    override fun onLongPress(view: View): Boolean {
        EBUIPrefs.nightMode.value = when (EBUIPrefs.nightMode.value) {
            AppCompatDelegate.MODE_NIGHT_YES -> AppCompatDelegate.MODE_NIGHT_NO
            else -> AppCompatDelegate.MODE_NIGHT_YES
        }
        return true
    }
}
