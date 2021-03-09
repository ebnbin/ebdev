package dev.ebnbin.ebdev

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import cat.ereza.customactivityoncrash.config.CaocConfig

internal class EBDevInitializer : ContentProvider() {
    override fun onCreate(): Boolean {
        initCrash()
        return true
    }

    private fun initCrash() {
        CaocConfig.Builder.create()
            .errorActivity(CrashActivity::class.java)
            .apply()
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }
}
