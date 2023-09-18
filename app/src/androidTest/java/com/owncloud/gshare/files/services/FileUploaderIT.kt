/*
 *
 * Nextcloud Android client application
 *
 * @author Tobias Kaminsky
 * Copyright (C) 2020 Tobias Kaminsky
 * Copyright (C) 2020 Nextcloud GmbH
 * Copyright (C) 2020 Chris Narkiewicz <hello@ezaquarii.com>
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
package com.owncloud.gshare.files.services

import com.nextcloud.client.account.UserAccountManager
import com.nextcloud.client.account.UserAccountManagerImpl
import com.nextcloud.client.device.BatteryStatus
import com.nextcloud.client.device.PowerManagementService
import com.nextcloud.client.network.Connectivity
import com.nextcloud.client.network.ConnectivityService
import com.owncloud.gshare.AbstractOnServerIT
import com.owncloud.gshare.datamodel.OCFile
import com.owncloud.gshare.datamodel.UploadsStorageManager
import com.owncloud.gshare.db.OCUpload
import com.owncloud.android.lib.common.operations.OperationCancelledException
import com.owncloud.android.lib.resources.files.ReadFileRemoteOperation
import com.owncloud.android.lib.resources.files.model.RemoteFile
import com.owncloud.gshare.operations.UploadFileOperation
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test

abstract class FileUploaderIT : com.owncloud.gshare.AbstractOnServerIT() {
    var uploadsStorageManager: com.owncloud.gshare.datamodel.UploadsStorageManager? = null

    val connectivityServiceMock: ConnectivityService = object : ConnectivityService {
        override fun isInternetWalled(): Boolean = false
        override fun getConnectivity(): Connectivity = Connectivity.CONNECTED_WIFI
    }

    private val powerManagementServiceMock: PowerManagementService = object : PowerManagementService {
        override val isPowerSavingEnabled: Boolean
            get() = false

        override val isPowerSavingExclusionAvailable: Boolean
            get() = false

        override val battery: BatteryStatus
            get() = BatteryStatus()
    }

    @Before
    fun setUp() {
        val contentResolver = targetContext.contentResolver
        val accountManager: UserAccountManager = UserAccountManagerImpl.fromContext(targetContext)
        uploadsStorageManager = com.owncloud.gshare.datamodel.UploadsStorageManager(accountManager, contentResolver)
    }

    /**
     * uploads a file, overwrites it with an empty one, check if overwritten
     */
    // disabled, flaky test
    // @Test
    // fun testKeepLocalAndOverwriteRemote() {
    //     val file = getDummyFile("chunkedFile.txt")
    //     val ocUpload = OCUpload(file.absolutePath, "/testFile.txt", account.name)
    //
    //     assertTrue(
    //         UploadFileOperation(
    //             uploadsStorageManager,
    //             connectivityServiceMock,
    //             powerManagementServiceMock,
    //             user,
    //             null,
    //             ocUpload,
    //             FileUploader.NameCollisionPolicy.DEFAULT,
    //             FileUploader.LOCAL_BEHAVIOUR_COPY,
    //             targetContext,
    //             false,
    //             false
    //         )
    //             .setRemoteFolderToBeCreated()
    //             .execute(client, storageManager)
    //             .isSuccess
    //     )
    //
    //     val result = ReadFileRemoteOperation("/testFile.txt").execute(client)
    //     assertTrue(result.isSuccess)
    //
    //     assertEquals(file.length(), (result.data[0] as RemoteFile).length)
    //
    //     val ocUpload2 = OCUpload(getDummyFile("empty.txt").absolutePath, "/testFile.txt", account.name)
    //
    //     assertTrue(
    //         UploadFileOperation(
    //             uploadsStorageManager,
    //             connectivityServiceMock,
    //             powerManagementServiceMock,
    //             user,
    //             null,
    //             ocUpload2,
    //             FileUploader.NameCollisionPolicy.OVERWRITE,
    //             FileUploader.LOCAL_BEHAVIOUR_COPY,
    //             targetContext,
    //             false,
    //             false
    //         )
    //             .execute(client, storageManager)
    //             .isSuccess
    //     )
    //
    //     val result2 = ReadFileRemoteOperation("/testFile.txt").execute(client)
    //     assertTrue(result2.isSuccess)
    //
    //     assertEquals(0, (result2.data[0] as RemoteFile).length)
    // }

    /**
     * uploads a file, overwrites it with an empty one, check if overwritten
     */
    @Test
    fun testKeepLocalAndOverwriteRemoteStatic() {
        val file = getDummyFile("chunkedFile.txt")

        com.owncloud.gshare.files.services.FileUploader.uploadNewFile(
            targetContext,
            user,
            file.absolutePath,
            "/testFile.txt",
            com.owncloud.gshare.files.services.FileUploader.LOCAL_BEHAVIOUR_COPY,
            null,
            true,
            com.owncloud.gshare.operations.UploadFileOperation.CREATED_BY_USER,
            false,
            false,
            com.owncloud.gshare.files.services.NameCollisionPolicy.DEFAULT
        )

        longSleep()

        val result = ReadFileRemoteOperation("/testFile.txt").execute(client)
        assertTrue(result.isSuccess)

        assertEquals(file.length(), (result.data[0] as RemoteFile).length)

        val ocFile2 = com.owncloud.gshare.datamodel.OCFile("/testFile.txt")
        ocFile2.storagePath = getDummyFile("empty.txt").absolutePath

        com.owncloud.gshare.files.services.FileUploader.uploadUpdateFile(
            targetContext,
            user,
            ocFile2,
            com.owncloud.gshare.files.services.FileUploader.LOCAL_BEHAVIOUR_COPY,
            com.owncloud.gshare.files.services.NameCollisionPolicy.OVERWRITE
        )

        shortSleep()

        val result2 = ReadFileRemoteOperation("/testFile.txt").execute(client)
        assertTrue(result2.isSuccess)

        assertEquals(0, (result2.data[0] as RemoteFile).length)
    }

    /**
     * uploads a file, uploads another one with automatically (2) added, check
     */
    @Test
    fun testKeepBoth() {
        var renameListenerWasTriggered = false

        val file = getDummyFile("chunkedFile.txt")
        val ocUpload = com.owncloud.gshare.db.OCUpload(file.absolutePath, "/testFile.txt", account.name)

        assertTrue(
            com.owncloud.gshare.operations.UploadFileOperation(
                uploadsStorageManager,
                connectivityServiceMock,
                powerManagementServiceMock,
                user,
                null,
                ocUpload,
                com.owncloud.gshare.files.services.NameCollisionPolicy.DEFAULT,
                com.owncloud.gshare.files.services.FileUploader.LOCAL_BEHAVIOUR_COPY,
                targetContext,
                false,
                false,
                storageManager
            )
                .setRemoteFolderToBeCreated()
                .execute(client)
                .isSuccess
        )

        val result = ReadFileRemoteOperation("/testFile.txt").execute(client)
        assertTrue(result.isSuccess)

        assertEquals(file.length(), (result.data[0] as RemoteFile).length)

        val file2 = getDummyFile("empty.txt")
        val ocUpload2 = com.owncloud.gshare.db.OCUpload(file2.absolutePath, "/testFile.txt", account.name)

        assertTrue(
            com.owncloud.gshare.operations.UploadFileOperation(
                uploadsStorageManager,
                connectivityServiceMock,
                powerManagementServiceMock,
                user,
                null,
                ocUpload2,
                com.owncloud.gshare.files.services.NameCollisionPolicy.RENAME,
                com.owncloud.gshare.files.services.FileUploader.LOCAL_BEHAVIOUR_COPY,
                targetContext,
                false,
                false,
                storageManager
            )
                .addRenameUploadListener {
                    renameListenerWasTriggered = true
                }
                .execute(client)
                .isSuccess
        )

        val result2 = ReadFileRemoteOperation("/testFile.txt").execute(client)
        assertTrue(result2.isSuccess)

        assertEquals(file.length(), (result2.data[0] as RemoteFile).length)

        val result3 = ReadFileRemoteOperation("/testFile (2).txt").execute(client)
        assertTrue(result3.isSuccess)

        assertEquals(file2.length(), (result3.data[0] as RemoteFile).length)
        assertTrue(renameListenerWasTriggered)
    }

    /**
     * uploads a file, uploads another one with automatically (2) added, check
     */
    @Test
    fun testKeepBothStatic() {
        val file = getDummyFile("nonEmpty.txt")

        com.owncloud.gshare.files.services.FileUploader.uploadNewFile(
            targetContext,
            user,
            file.absolutePath,
            "/testFile.txt",
            com.owncloud.gshare.files.services.FileUploader.LOCAL_BEHAVIOUR_COPY,
            null,
            true,
            com.owncloud.gshare.operations.UploadFileOperation.CREATED_BY_USER,
            false,
            false,
            com.owncloud.gshare.files.services.NameCollisionPolicy.DEFAULT
        )

        longSleep()

        val result = ReadFileRemoteOperation("/testFile.txt").execute(client)
        assertTrue(result.isSuccess)

        assertEquals(file.length(), (result.data[0] as RemoteFile).length)

        val ocFile2 = com.owncloud.gshare.datamodel.OCFile("/testFile.txt")
        ocFile2.storagePath = getDummyFile("empty.txt").absolutePath

        com.owncloud.gshare.files.services.FileUploader.uploadUpdateFile(
            targetContext,
            user,
            ocFile2,
            com.owncloud.gshare.files.services.FileUploader.LOCAL_BEHAVIOUR_COPY,
            com.owncloud.gshare.files.services.NameCollisionPolicy.RENAME
        )

        shortSleep()

        val result2 = ReadFileRemoteOperation("/testFile.txt").execute(client)
        assertTrue(result2.isSuccess)

        assertEquals(file.length(), (result2.data[0] as RemoteFile).length)

        val result3 = ReadFileRemoteOperation("/testFile (2).txt").execute(client)
        assertTrue(result3.isSuccess)

        assertEquals(ocFile2.fileLength, (result3.data[0] as RemoteFile).length)
    }

    /**
     * uploads a file with "keep server" option set, so do nothing
     */
    @Test
    fun testKeepServer() {
        val file = getDummyFile("chunkedFile.txt")
        val ocUpload = com.owncloud.gshare.db.OCUpload(file.absolutePath, "/testFile.txt", account.name)

        assertTrue(
            com.owncloud.gshare.operations.UploadFileOperation(
                uploadsStorageManager,
                connectivityServiceMock,
                powerManagementServiceMock,
                user,
                null,
                ocUpload,
                com.owncloud.gshare.files.services.NameCollisionPolicy.DEFAULT,
                com.owncloud.gshare.files.services.FileUploader.LOCAL_BEHAVIOUR_COPY,
                targetContext,
                false,
                false,
                storageManager
            )
                .setRemoteFolderToBeCreated()
                .execute(client)
                .isSuccess
        )

        val result = ReadFileRemoteOperation("/testFile.txt").execute(client)
        assertTrue(result.isSuccess)

        assertEquals(file.length(), (result.data[0] as RemoteFile).length)

        val ocUpload2 =
            com.owncloud.gshare.db.OCUpload(getDummyFile("empty.txt").absolutePath, "/testFile.txt", account.name)

        assertFalse(
            com.owncloud.gshare.operations.UploadFileOperation(
                uploadsStorageManager,
                connectivityServiceMock,
                powerManagementServiceMock,
                user,
                null,
                ocUpload2,
                com.owncloud.gshare.files.services.NameCollisionPolicy.CANCEL,
                com.owncloud.gshare.files.services.FileUploader.LOCAL_BEHAVIOUR_COPY,
                targetContext,
                false,
                false,
                storageManager
            )
                .execute(client).isSuccess
        )

        val result2 = ReadFileRemoteOperation("/testFile.txt").execute(client)
        assertTrue(result2.isSuccess)

        assertEquals(file.length(), (result2.data[0] as RemoteFile).length)
    }

    /**
     * uploads a file with "keep server" option set, so do nothing
     */
    @Test
    fun testKeepServerStatic() {
        val file = getDummyFile("chunkedFile.txt")

        com.owncloud.gshare.files.services.FileUploader.uploadNewFile(
            targetContext,
            user,
            file.absolutePath,
            "/testFile.txt",
            com.owncloud.gshare.files.services.FileUploader.LOCAL_BEHAVIOUR_COPY,
            null,
            true,
            com.owncloud.gshare.operations.UploadFileOperation.CREATED_BY_USER,
            false,
            false,
            com.owncloud.gshare.files.services.NameCollisionPolicy.DEFAULT
        )

        longSleep()

        val result = ReadFileRemoteOperation("/testFile.txt").execute(client)
        assertTrue(result.isSuccess)

        assertEquals(file.length(), (result.data[0] as RemoteFile).length)

        val ocFile2 = com.owncloud.gshare.datamodel.OCFile("/testFile.txt")
        ocFile2.storagePath = getDummyFile("empty.txt").absolutePath

        com.owncloud.gshare.files.services.FileUploader.uploadUpdateFile(
            targetContext,
            user,
            ocFile2,
            com.owncloud.gshare.files.services.FileUploader.LOCAL_BEHAVIOUR_COPY,
            com.owncloud.gshare.files.services.NameCollisionPolicy.CANCEL
        )

        shortSleep()

        val result2 = ReadFileRemoteOperation("/testFile.txt").execute(client)
        assertTrue(result2.isSuccess)

        assertEquals(file.length(), (result2.data[0] as RemoteFile).length)
    }

    /**
     * uploads a file with "skip if exists" option set, so do nothing if file exists
     */
    @Test
    fun testCancelServer() {
        val file = getDummyFile("chunkedFile.txt")
        val ocUpload = com.owncloud.gshare.db.OCUpload(file.absolutePath, "/testFile.txt", account.name)

        assertTrue(
            com.owncloud.gshare.operations.UploadFileOperation(
                uploadsStorageManager,
                connectivityServiceMock,
                powerManagementServiceMock,
                user,
                null,
                ocUpload,
                com.owncloud.gshare.files.services.NameCollisionPolicy.CANCEL,
                com.owncloud.gshare.files.services.FileUploader.LOCAL_BEHAVIOUR_COPY,
                targetContext,
                false,
                false,
                storageManager
            )
                .setRemoteFolderToBeCreated()
                .execute(client)
                .isSuccess
        )

        val result = ReadFileRemoteOperation("/testFile.txt").execute(client)
        assertTrue(result.isSuccess)

        assertEquals(file.length(), (result.data[0] as RemoteFile).length)

        val ocUpload2 =
            com.owncloud.gshare.db.OCUpload(getDummyFile("empty.txt").absolutePath, "/testFile.txt", account.name)

        val uploadResult = com.owncloud.gshare.operations.UploadFileOperation(
            uploadsStorageManager,
            connectivityServiceMock,
            powerManagementServiceMock,
            user,
            null,
            ocUpload2,
            com.owncloud.gshare.files.services.NameCollisionPolicy.CANCEL,
            com.owncloud.gshare.files.services.FileUploader.LOCAL_BEHAVIOUR_COPY,
            targetContext,
            false,
            false,
            storageManager
        )
            .execute(client)

        assertFalse(uploadResult.isSuccess)
        assertTrue(uploadResult.exception is OperationCancelledException)

        val result2 = ReadFileRemoteOperation("/testFile.txt").execute(client)
        assertTrue(result2.isSuccess)

        assertEquals(file.length(), (result2.data[0] as RemoteFile).length)
    }

    /**
     * uploads a file with "skip if exists" option set, so do nothing if file exists
     */
    @Test
    fun testKeepCancelStatic() {
        val file = getDummyFile("chunkedFile.txt")

        com.owncloud.gshare.files.services.FileUploader.uploadNewFile(
            targetContext,
            user,
            file.absolutePath,
            "/testFile.txt",
            com.owncloud.gshare.files.services.FileUploader.LOCAL_BEHAVIOUR_COPY,
            null,
            true,
            com.owncloud.gshare.operations.UploadFileOperation.CREATED_BY_USER,
            false,
            false,
            com.owncloud.gshare.files.services.NameCollisionPolicy.DEFAULT
        )

        longSleep()

        val result = ReadFileRemoteOperation("/testFile.txt").execute(client)
        assertTrue(result.isSuccess)

        assertEquals(file.length(), (result.data[0] as RemoteFile).length)

        val ocFile2 = com.owncloud.gshare.datamodel.OCFile("/testFile.txt")
        ocFile2.storagePath = getDummyFile("empty.txt").absolutePath

        com.owncloud.gshare.files.services.FileUploader.uploadUpdateFile(
            targetContext,
            user,
            ocFile2,
            com.owncloud.gshare.files.services.FileUploader.LOCAL_BEHAVIOUR_COPY,
            com.owncloud.gshare.files.services.NameCollisionPolicy.CANCEL
        )

        shortSleep()

        val result2 = ReadFileRemoteOperation("/testFile.txt").execute(client)
        assertTrue(result2.isSuccess)

        assertEquals(file.length(), (result2.data[0] as RemoteFile).length)
    }
}
