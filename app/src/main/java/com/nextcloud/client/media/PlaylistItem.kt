package com.nextcloud.client.media

import com.nextcloud.client.account.User
import com.owncloud.gshare.datamodel.OCFile

data class PlaylistItem(val file: com.owncloud.gshare.datamodel.OCFile, val startPositionMs: Long, val autoPlay: Boolean, val user: User)
