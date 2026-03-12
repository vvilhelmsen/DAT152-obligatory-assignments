package com.example.oblig1.provider

import android.content.*
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import com.example.oblig1.data.PhotoDatabase

/**
 * Exposes the photo entries to other apps (and adb) via a standard ContentProvider.
 * Authority: com.example.oblig1.provider
 * URI:       content://com.example.oblig1.provider/entries
 * Columns:   "name", "URI"
 */
class PhotoEntryProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.example.oblig1.provider"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/entries")
        const val COL_NAME = "name"
        const val COL_URI = "URI"
    }

    override fun onCreate(): Boolean = true

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        val dao = PhotoDatabase.getInstance(context!!).photoDao()
        val entries = dao.getAllSync()
        val cursor = MatrixCursor(arrayOf(COL_NAME, COL_URI))
        entries.forEach { entry ->
            cursor.addRow(arrayOf(entry.name, entry.imageUri))
        }
        return cursor
    }

    override fun getType(uri: Uri): String = "vnd.android.cursor.dir/vnd.$AUTHORITY.entries"

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0
}
