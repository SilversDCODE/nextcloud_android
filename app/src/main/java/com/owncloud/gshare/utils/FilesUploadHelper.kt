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

package com.owncloud.gshare.utils

import com.nextcloud.client.account.User
import com.nextcloud.client.jobs.BackgroundJobManager
import com.owncloud.gshare.MainApp
import com.owncloud.gshare.datamodel.OCFile
import com.owncloud.gshare.datamodel.UploadsStorageManager
import com.owncloud.gshare.db.OCUpload
import com.owncloud.gshare.files.services.NameCollisionPolicy
import com.owncloud.android.lib.common.utils.Log_OC
import javax.inject.Inject

class FilesUploadHelper {
    @Inject
    lateinit var backgroundJobManager: BackgroundJobManager

    @Inject
    lateinit var uploadsStorageManager: _root_ide_package_.com.owncloud.gshare.datamodel.UploadsStorageManager

    init {
        _root_ide_package_.com.owncloud.gshare.MainApp.getAppComponent().inject(this)
    }

    @Suppress("LongParameterList")
    fun uploadNewFiles(
        user: User,
        localPaths: Array<String>,
        remotePaths: Array<String>,
        createRemoteFolder: Boolean,
        createdBy: Int,
        requiresWifi: Boolean,
        requiresCharging: Boolean,
        nameCollisionPolicy: _root_ide_package_.com.owncloud.gshare.files.services.NameCollisionPolicy,
        localBehavior: Int
    ) {
        val uploads = localPaths.mapIndexed { index, localPath ->
            _root_ide_package_.com.owncloud.gshare.db.OCUpload(localPath, remotePaths[index], user.accountName).apply {
                this.nameCollisionPolicy = nameCollisionPolicy
                isUseWifiOnly = requiresWifi
                isWhileChargingOnly = requiresCharging
                uploadStatus = _root_ide_package_.com.owncloud.gshare.datamodel.UploadsStorageManager.UploadStatus.UPLOAD_IN_PROGRESS
                this.createdBy = createdBy
                isCreateRemoteFolder = createRemoteFolder
                localAction = localBehavior
            }
        }
        uploadsStorageManager.storeUploads(uploads)
        backgroundJobManager.startFilesUploadJob(user)
    }

    fun uploadUpdatedFile(
        user: User,
        existingFiles: Array<_root_ide_package_.com.owncloud.gshare.datamodel.OCFile>,
        behaviour: Int,
        nameCollisionPolicy: _root_ide_package_.com.owncloud.gshare.files.services.NameCollisionPolicy
    ) {
        Log_OC.d(this, "upload updated file")

        val uploads = existingFiles.map { file ->
            _root_ide_package_.com.owncloud.gshare.db.OCUpload(file, user).apply {
                fileSize = file.fileLength
                this.nameCollisionPolicy = nameCollisionPolicy
                isCreateRemoteFolder = true
                this.localAction = behaviour
                isUseWifiOnly = false
                isWhileChargingOnly = false
                uploadStatus = _root_ide_package_.com.owncloud.gshare.datamodel.UploadsStorageManager.UploadStatus.UPLOAD_IN_PROGRESS
            }
        }
        uploadsStorageManager.storeUploads(uploads)
        backgroundJobManager.startFilesUploadJob(user)
    }

    fun retryUpload(upload: _root_ide_package_.com.owncloud.gshare.db.OCUpload, user: User) {
        Log_OC.d(this, "retry upload")

        upload.uploadStatus = _root_ide_package_.com.owncloud.gshare.datamodel.UploadsStorageManager.UploadStatus.UPLOAD_IN_PROGRESS
        uploadsStorageManager.updateUpload(upload)

        backgroundJobManager.startFilesUploadJob(user)
    }
}
