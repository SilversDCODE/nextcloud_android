/*
 * Nextcloud Android client application
 *
 * @author Andy Scherzinger
 * Copyright (C) 2020 Andy Scherzinger
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
package com.owncloud.gshare.utils

import com.owncloud.gshare.AbstractIT
import org.junit.Assert
import org.junit.Test
import java.io.File

class FileUtilTest : com.owncloud.gshare.AbstractIT() {
    @Test
    fun assertNullInput() {
        Assert.assertEquals("", com.owncloud.gshare.utils.FileUtil.getFilenameFromPathString(null))
    }

    @Test
    fun assertEmptyInput() {
        Assert.assertEquals("", com.owncloud.gshare.utils.FileUtil.getFilenameFromPathString(""))
    }

    @Test
    fun assertFileInput() {
        val file = getDummyFile("empty.txt")
        Assert.assertEquals("empty.txt", com.owncloud.gshare.utils.FileUtil.getFilenameFromPathString(file.absolutePath))
    }

    @Test
    fun assertSlashInput() {
        val tempPath = File(com.owncloud.gshare.utils.FileStorageUtils.getTemporalPath(account.name) + File.pathSeparator + "folder")
        if (!tempPath.exists()) {
            Assert.assertTrue(tempPath.mkdirs())
        }
        Assert.assertEquals("", com.owncloud.gshare.utils.FileUtil.getFilenameFromPathString(tempPath.absolutePath))
    }

    @Test
    fun assertDotFileInput() {
        val file = getDummyFile(".dotfile.ext")
        Assert.assertEquals(".dotfile.ext", com.owncloud.gshare.utils.FileUtil.getFilenameFromPathString(file.absolutePath))
    }

    @Test
    fun assertFolderInput() {
        val tempPath = File(com.owncloud.gshare.utils.FileStorageUtils.getTemporalPath(account.name))
        if (!tempPath.exists()) {
            Assert.assertTrue(tempPath.mkdirs())
        }

        Assert.assertEquals("", com.owncloud.gshare.utils.FileUtil.getFilenameFromPathString(tempPath.absolutePath))
    }

    @Test
    fun assertNoFileExtensionInput() {
        val file = getDummyFile("file")
        Assert.assertEquals("file", com.owncloud.gshare.utils.FileUtil.getFilenameFromPathString(file.absolutePath))
    }
}
