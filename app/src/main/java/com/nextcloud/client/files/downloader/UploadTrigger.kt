/*
 * Nextcloud Android client application
 *
 * @author Chris Narkiewicz
 * Copyright (C) 2021 Chris Narkiewicz <hello@ezaquarii.com>
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.nextcloud.client.files.downloader

import com.owncloud.gshare.operations.UploadFileOperation

/**
 * Upload transfer trigger.
 */
enum class UploadTrigger(val value: Int) {

    /**
     * Transfer triggered manually by the user.
     */
    USER(com.owncloud.gshare.operations.UploadFileOperation.CREATED_BY_USER),

    /**
     * Transfer triggered automatically by taking a photo.
     */
    PHOTO(com.owncloud.gshare.operations.UploadFileOperation.CREATED_AS_INSTANT_PICTURE),

    /**
     * Transfer triggered automatically by making a video.
     */
    VIDEO(com.owncloud.gshare.operations.UploadFileOperation.CREATED_AS_INSTANT_VIDEO);

    companion object {
        @JvmStatic
        fun fromValue(value: Int) = when (value) {
            com.owncloud.gshare.operations.UploadFileOperation.CREATED_BY_USER -> USER
            com.owncloud.gshare.operations.UploadFileOperation.CREATED_AS_INSTANT_PICTURE -> PHOTO
            com.owncloud.gshare.operations.UploadFileOperation.CREATED_AS_INSTANT_VIDEO -> VIDEO
            else -> USER
        }
    }
}
