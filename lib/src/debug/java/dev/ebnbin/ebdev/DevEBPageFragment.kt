package dev.ebnbin.ebdev

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import dev.ebnbin.eb.SDK_26_O_8
import dev.ebnbin.eb.closeApp
import dev.ebnbin.eb.e
import dev.ebnbin.eb.timestamp
import dev.ebnbin.eb.toTimeString
import dev.ebnbin.ebui.AlertDialogFragment
import dev.ebnbin.ebui.EBUIPrefs
import dev.ebnbin.ebui.openAlertDialog
import dev.ebnbin.ebui.openLogDialog
import dev.ebnbin.ebui.requireArg
import dev.ebnbin.ebui.toast
import leakcanary.LeakCanary

internal class DevEBPageFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceScreen = preferenceManager.createPreferenceScreen(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        PreferenceCategory(requireContext()).also {
            it.title = "Calling Activity"
            preferenceScreen.addPreference(it)
        }

        Preference(requireContext()).also {
            it.title = "Calling Activity"
            it.summary = requireArg<String>(KEY_CALLING_ACTIVITY)
            it.setOnPreferenceClickListener {
                childFragmentManager.openLogDialog(
                    title = "Calling Activity",
                    log = requireArg<String>(KEY_CALLING_ACTIVITY_LOG),
                )
                true
            }
            preferenceScreen.addPreference(it)
        }

        PreferenceCategory(requireContext()).also {
            it.title = "Dev"
            preferenceScreen.addPreference(it)
        }

        SwitchPreferenceCompat(requireContext()).also {
            it.title = "Dev 悬浮窗"
            it.summaryOff = "关闭"
            it.summaryOn = "开启"
            it.isChecked = DevFloating.isEnabled
            it.setOnPreferenceChangeListener { _, newValue ->
                newValue as Boolean
                DevFloating.isEnabled = newValue
                true
            }
            preferenceScreen.addPreference(it)
        }

        Preference(requireContext()).also {
            fun summary(
                floatingX: Int = EBDevPrefs.devFloatingX.value,
                floatingY: Int = EBDevPrefs.devFloatingY.value,
            ): CharSequence {
                return "$floatingX,$floatingY"
            }

            EBDevPrefs.devFloatingX.observe(viewLifecycleOwner) { value ->
                it.summary = summary(floatingX = value)
            }
            EBDevPrefs.devFloatingY.observe(viewLifecycleOwner) { value ->
                it.summary = summary(floatingY = value)
            }
            it.title = "Dev 悬浮窗位置（像素）"
            it.summary = summary()
            it.setOnPreferenceClickListener {
                childFragmentManager.openAlertDialog(
                    message = "重置 Dev 悬浮窗位置为 0,0 ？",
                    positiveText = "确定",
                    negativeText = "取消",
                    fragmentResultListener = AlertDialogFragment.ResultListener(
                        requestKey = "devFloatingLocation",
                        lifecycleOwner = viewLifecycleOwner,
                    ) { _, resultType ->
                        if (resultType == AlertDialogFragment.ResultType.POSITIVE) {
                            DevFloating.update(requireActivity(), 0, 0)
                        }
                    },
                )
                true
            }
            preferenceScreen.addPreference(it)
        }

        PreferenceCategory(requireContext()).also {
            it.title = "Theme"
            preferenceScreen.addPreference(it)
        }

        ListPreference(requireContext()).also {
            it.isPersistent = false
            it.key = EBUIPrefs.nightMode.key
            it.value = EBUIPrefs.nightMode.value.toString()
            it.title = "Night Mode"
            val nightModeMap = mapOf(
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM to "MODE_NIGHT_FOLLOW_SYSTEM",
                AppCompatDelegate.MODE_NIGHT_NO to "MODE_NIGHT_NO",
                AppCompatDelegate.MODE_NIGHT_YES to "MODE_NIGHT_YES",
                AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY to "MODE_NIGHT_AUTO_BATTERY",
            )
            it.setSummaryProvider { preference ->
                preference as ListPreference
                nightModeMap[preference.value.toInt()]
            }
            it.entryValues = nightModeMap
                .map { nightMode -> nightMode.key.toString() }
                .toTypedArray()
            it.entries = nightModeMap
                .map { nightMode -> nightMode.value }
                .toTypedArray()
            it.dialogTitle = it.title
            it.setOnPreferenceChangeListener { _, newValue ->
                newValue as String
                EBUIPrefs.nightMode.value = newValue.toInt()
                true
            }
            preferenceScreen.addPreference(it)
        }

        PreferenceCategory(requireContext()).also {
            it.title = "Dialog"
            preferenceScreen.addPreference(it)
        }

        Preference(requireContext()).also {
            it.title = "AlertDialog"
            it.setOnPreferenceClickListener {
                childFragmentManager.openAlertDialog(
                    title = "Title",
                    message = (0..999).joinToString(",") { index ->
                        "$index"
                    },
                    positiveText = "OK",
                    negativeText = "Cancel",
                    fragmentResultListener = AlertDialogFragment.ResultListener(
                        requestKey = "alertDialogIsMaterialFalse",
                        lifecycleOwner = viewLifecycleOwner,
                    ) { _, resultType ->
                        if (resultType == AlertDialogFragment.ResultType.POSITIVE) {
                            toast("OK")
                        }
                    },
                )
                true
            }
            preferenceScreen.addPreference(it)
        }

        Preference(requireContext()).also {
            it.title = "LogDialog"
            it.summary = "logHorizontalScrollable = true"
            it.setOnPreferenceClickListener {
                childFragmentManager.openLogDialog(
                    title = "Title",
                    log = (0..99).joinToString("\n") { row ->
                        "$row:" + (0..99).joinToString(",") { column ->
                            "$column"
                        }
                    },
                    logHorizontalScrollable = true,
                )
                true
            }
            preferenceScreen.addPreference(it)
        }

        Preference(requireContext()).also {
            it.title = "LogDialog"
            it.summary = "logHorizontalScrollable = false"
            it.setOnPreferenceClickListener {
                childFragmentManager.openLogDialog(
                    title = "Title",
                    log = (0..99).joinToString("\n") { row ->
                        "$row:" + (0..99).joinToString(",") { column ->
                            "$column"
                        }
                    },
                    logHorizontalScrollable = false,
                )
                true
            }
            preferenceScreen.addPreference(it)
        }

        PreferenceCategory(requireContext()).also {
            it.title = "Toast"
            preferenceScreen.addPreference(it)
        }

        Preference(requireContext()).also {
            it.title = "Toast"
            it.setOnPreferenceClickListener {
                toast(timestamp().toTimeString())
                true
            }
            preferenceScreen.addPreference(it)
        }

        PreferenceCategory(requireContext()).also {
            it.title = "Report"
            preferenceScreen.addPreference(it)
        }

        PreferenceCategory(requireContext()).also {
            it.title = "LeakCanary"
            preferenceScreen.addPreference(it)
        }

        Preference(requireContext()).also {
            it.title = "Open LeakCanary Activity"
            it.setOnPreferenceClickListener {
                startActivity(LeakCanary.newLeakDisplayActivityIntent())
                true
            }
            preferenceScreen.addPreference(it)
        }

        PreferenceCategory(requireContext()).also {
            it.title = "App"
            preferenceScreen.addPreference(it)
        }

        Preference(requireContext()).also {
            it.title = "Close App"
            it.setOnPreferenceClickListener {
                childFragmentManager.openAlertDialog(
                    title = "Close App",
                    message = "确定？应用进程会被杀死！",
                    positiveText = "确定",
                    negativeText = "取消",
                    fragmentResultListener = AlertDialogFragment.ResultListener(
                        requestKey = "closeApp",
                        lifecycleOwner = viewLifecycleOwner,
                    ) { _, resultType ->
                        if (resultType == AlertDialogFragment.ResultType.POSITIVE) {
                            closeApp()
                        }
                    }
                )
                true
            }
            preferenceScreen.addPreference(it)
        }

        Preference(requireContext()).also {
            it.title = "Restart App"
            it.setOnPreferenceClickListener {
                childFragmentManager.openAlertDialog(
                    title = "Restart App",
                    message = "确定？应用进程会被杀死！",
                    positiveText = "确定",
                    negativeText = "取消",
                    fragmentResultListener = AlertDialogFragment.ResultListener(
                        requestKey = "restartApp",
                        lifecycleOwner = viewLifecycleOwner,
                    ) { _, resultType ->
                        if (resultType == AlertDialogFragment.ResultType.POSITIVE) {
                            closeApp(restart = true)
                        }
                    }
                )
                true
            }
            preferenceScreen.addPreference(it)
        }

        PreferenceCategory(requireContext()).also {
            it.title = "Crash"
            preferenceScreen.addPreference(it)
        }

        Preference(requireContext()).also {
            it.title = "Crash"
            it.setOnPreferenceClickListener {
                childFragmentManager.openAlertDialog(
                    title = "Crash",
                    message = "确定？应用会崩溃的！",
                    positiveText = "确定",
                    negativeText = "取消",
                    fragmentResultListener = AlertDialogFragment.ResultListener(
                        requestKey = "crash",
                        lifecycleOwner = viewLifecycleOwner,
                    ) { _, resultType ->
                        if (resultType == AlertDialogFragment.ResultType.POSITIVE) {
                            e()
                        }
                    }
                )
                true
            }
            preferenceScreen.addPreference(it)
        }
    }

    companion object {
        private const val KEY_CALLING_ACTIVITY = "calling_activity"
        private const val KEY_CALLING_ACTIVITY_LOG = "calling_activity_log"

        fun createArgs(activity: Activity): Bundle {
            val callingActivity = activity::class.java.name
            val sb = StringBuilder()
                .appendLine(callingActivity)
            activity.getAllFragments().forEach {
                repeat(it.second + 1) {
                    sb.append("  ")
                }
                sb.appendLine(it.first::class.java.name)
            }
            activity.getAllLegacyFragments().forEach {
                sb.append("* ")
                repeat(it.second) {
                    sb.append("  ")
                }
                sb.appendLine(it.first::class.java.name)
            }
            return bundleOf(
                KEY_CALLING_ACTIVITY to callingActivity,
                KEY_CALLING_ACTIVITY_LOG to sb.toString(),
            )
        }
    }
}

/**
 * 获取所有 Fragment. 如果 Activity 不是 [FragmentActivity] 则返回空列表.
 */
private fun Activity.getAllFragments(): List<Pair<Fragment, Int>> {
    val list = mutableListOf<Pair<Fragment, Int>>()
    if (this is FragmentActivity) {
        supportFragmentManager.getAllFragments(list)
    }
    return list
}

/**
 * 递归获取所有 Fragment.
 *
 * @param depth 深度, 从 0 开始.
 */
private fun FragmentManager.getAllFragments(
    list: MutableList<Pair<Fragment, Int>>,
    depth: Int = 0,
) {
    fragments.forEach {
        list.add(it to depth)
        it.childFragmentManager.getAllFragments(list, depth + 1)
    }
}

@Suppress("DEPRECATION")
private fun Activity.getAllLegacyFragments(): List<Pair<android.app.Fragment, Int>> {
    val list = mutableListOf<Pair<android.app.Fragment, Int>>()
    if (Build.VERSION.SDK_INT >= SDK_26_O_8) {
        fragmentManager.getAllFragments(list)
    }
    return list
}

@Suppress("DEPRECATION")
@RequiresApi(SDK_26_O_8)
private fun android.app.FragmentManager.getAllFragments(
    list: MutableList<Pair<android.app.Fragment, Int>>,
    depth: Int = 0,
) {
    fragments.forEach {
        list.add(it to depth)
        it.childFragmentManager.getAllFragments(list, depth + 1)
    }
}
