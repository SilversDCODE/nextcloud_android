/*
 *
 * Nextcloud Android client application
 *
 * @author Tobias Kaminsky
 * Copyright (C) 2021 Tobias Kaminsky
 * Copyright (C) 2021 Nextcloud GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.owncloud.gshare.providers

import android.content.ContentValues
import com.owncloud.gshare.db.ProviderMeta
import com.owncloud.gshare.utils.MimeTypeUtil
import org.junit.Test
import java.lang.IllegalArgumentException

@Suppress("FunctionNaming")
class FileContentProviderVerificationIT {

    companion object {
        private const val INVALID_COLUMN = "Invalid column"
        private const val FILE_LENGTH = 120
    }

    @Test(expected = IllegalArgumentException::class)
    fun verifyColumnName_Exception() {
        com.owncloud.gshare.providers.FileContentProvider.VerificationUtils.verifyColumnName(INVALID_COLUMN)
    }

    @Test
    fun verifyColumnName_OK() {
        com.owncloud.gshare.providers.FileContentProvider.VerificationUtils.verifyColumnName(com.owncloud.gshare.db.ProviderMeta.ProviderTableMeta.FILE_NAME)
    }

    @Test
    fun verifyColumn_ContentValues_OK() {
        // with valid columns
        val contentValues = ContentValues()
        contentValues.put(com.owncloud.gshare.db.ProviderMeta.ProviderTableMeta.FILE_CONTENT_LENGTH, FILE_LENGTH)
        contentValues.put(com.owncloud.gshare.db.ProviderMeta.ProviderTableMeta.FILE_CONTENT_TYPE, com.owncloud.gshare.utils.MimeTypeUtil.MIMETYPE_TEXT_MARKDOWN)
        com.owncloud.gshare.providers.FileContentProvider.VerificationUtils.verifyColumns(contentValues)

        // empty
        com.owncloud.gshare.providers.FileContentProvider.VerificationUtils.verifyColumns(ContentValues())
    }

    @Test(expected = IllegalArgumentException::class)
    fun verifyColumn_ContentValues_invalidColumn() {
        // with invalid columns
        val contentValues = ContentValues()
        contentValues.put(INVALID_COLUMN, FILE_LENGTH)
        contentValues.put(com.owncloud.gshare.db.ProviderMeta.ProviderTableMeta.FILE_CONTENT_TYPE, com.owncloud.gshare.utils.MimeTypeUtil.MIMETYPE_TEXT_MARKDOWN)
        com.owncloud.gshare.providers.FileContentProvider.VerificationUtils.verifyColumns(contentValues)
    }

    @Test
    fun verifySortOrder_OK() {
        // null
        com.owncloud.gshare.providers.FileContentProvider.VerificationUtils.verifySortOrder(null)

        // empty
        com.owncloud.gshare.providers.FileContentProvider.VerificationUtils.verifySortOrder("")

        // valid sort
        com.owncloud.gshare.providers.FileContentProvider.VerificationUtils.verifySortOrder(com.owncloud.gshare.db.ProviderMeta.ProviderTableMeta.FILE_DEFAULT_SORT_ORDER)
    }

    @Test(expected = IllegalArgumentException::class)
    fun verifySortOrder_InvalidColumn() {
        // with invalid column
        com.owncloud.gshare.providers.FileContentProvider.VerificationUtils.verifySortOrder("$INVALID_COLUMN desc")
    }

    @Test(expected = IllegalArgumentException::class)
    fun verifySortOrder_InvalidGrammar() {
        // with invalid grammar
        com.owncloud.gshare.providers.FileContentProvider.VerificationUtils.verifySortOrder("${com.owncloud.gshare.db.ProviderMeta.ProviderTableMeta._ID} ;--foo")
    }

    @Test
    fun verifyWhere_OK() {
        com.owncloud.gshare.providers.FileContentProvider.VerificationUtils.verifyWhere(null)
        com.owncloud.gshare.providers.FileContentProvider.VerificationUtils.verifyWhere(
            "${com.owncloud.gshare.db.ProviderMeta.ProviderTableMeta._ID}=? AND ${com.owncloud.gshare.db.ProviderMeta.ProviderTableMeta.FILE_ACCOUNT_OWNER}=?"
        )
        com.owncloud.gshare.providers.FileContentProvider.VerificationUtils.verifyWhere(
            "${com.owncloud.gshare.db.ProviderMeta.ProviderTableMeta._ID} = 1" +
                " AND (1 = 1)" +
                " AND ${com.owncloud.gshare.db.ProviderMeta.ProviderTableMeta.FILE_ACCOUNT_OWNER} LIKE ?"
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun verifyWhere_InvalidColumnName() {
        com.owncloud.gshare.providers.FileContentProvider.VerificationUtils.verifyWhere("$INVALID_COLUMN= ?")
    }

    @Test(expected = IllegalArgumentException::class)
    fun verifyWhere_InvalidGrammar() {
        com.owncloud.gshare.providers.FileContentProvider.VerificationUtils.verifyWhere("1=1 -- SELECT * FROM")
    }
}
