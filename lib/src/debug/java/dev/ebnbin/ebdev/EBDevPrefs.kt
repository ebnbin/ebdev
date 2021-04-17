package dev.ebnbin.ebdev

import dev.ebnbin.eb.Pref
import dev.ebnbin.eb.Prefs
import dev.ebnbin.eb.appId

object EBDevPrefs : Prefs() {
    override val prefName: String = "$appId.ebdev"

    val devPage: Pref<Int> = createPref("dev_page", 0)
    val devFloatingX: Pref<Int> = createPref("dev_floating_x", 0)
    val devFloatingY: Pref<Int> = createPref("dev_floating_y", 0)
}
