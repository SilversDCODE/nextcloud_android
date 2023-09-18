/*
 *
 * Nextcloud Android client application
 *
 * @author Tobias Kaminsky
 * Copyright (C) 2022 Tobias Kaminsky
 * Copyright (C) 2022 Nextcloud GmbH
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

package com.owncloud.gshare.ui.adapter

import com.nextcloud.client.account.User
import com.owncloud.gshare.datamodel.FileDataStorageManager
import com.owncloud.gshare.datamodel.OCFile
import com.owncloud.android.utils.FileSortOrder

interface CommonOCFileListAdapterInterface {
    fun isMultiSelect(): Boolean
    fun cancelAllPendingTasks()
    fun getItemPosition(file: _root_ide_package_.com.owncloud.gshare.datamodel.OCFile): Int
    fun swapDirectory(
        user: User,
        directory: _root_ide_package_.com.owncloud.gshare.datamodel.OCFile,
        storageManager: _root_ide_package_.com.owncloud.gshare.datamodel.FileDataStorageManager,
        onlyOnDevice: Boolean,
        mLimitToMimeType: String
    )

    fun setHighlightedItem(file: _root_ide_package_.com.owncloud.gshare.datamodel.OCFile)
    fun setSortOrder(mFile: _root_ide_package_.com.owncloud.gshare.datamodel.OCFile, sortOrder: FileSortOrder)
    fun addCheckedFile(file: _root_ide_package_.com.owncloud.gshare.datamodel.OCFile)
    fun isCheckedFile(file: _root_ide_package_.com.owncloud.gshare.datamodel.OCFile): Boolean
    fun getCheckedItems(): Set<_root_ide_package_.com.owncloud.gshare.datamodel.OCFile>
    fun removeCheckedFile(file: _root_ide_package_.com.owncloud.gshare.datamodel.OCFile)
    fun notifyItemChanged(file: _root_ide_package_.com.owncloud.gshare.datamodel.OCFile)
    fun getFilesCount(): Int
    fun setMultiSelect(boolean: Boolean)
    fun clearCheckedItems()
}
