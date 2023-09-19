/*
 * Nextcloud Android client application
 *
 * @author Tobias Kaminsky
 * Copyright (C) 2019 Tobias Kaminsky
 * Copyright (C) 2019 Nextcloud GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.owncloud.gshare.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.nextcloud.client.account.User
import com.nextcloud.client.di.Injectable
import com.nextcloud.client.logger.Logger
import com.owncloud.gshare.R
import com.owncloud.android.lib.common.operations.RemoteOperation
import com.owncloud.android.lib.resources.files.ReadFileRemoteOperation
import com.owncloud.android.lib.resources.files.SearchRemoteOperation
import com.owncloud.android.lib.resources.files.model.RemoteFile
import com.owncloud.android.lib.resources.shares.GetSharesRemoteOperation
import com.owncloud.gshare.ui.events.SearchEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * A Fragment that lists folders shared by the user
 */
@Suppress("TooManyFunctions")
class SharedListFragment : _root_ide_package_.com.owncloud.gshare.ui.fragment.OCFileListFragment(), Injectable {

    @Inject
    lateinit var logger: Logger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchFragment = true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter.setShowMetadata(false)
        currentSearchType = SearchType.SHARED_FILTER
        searchEvent = SearchEvent("", SearchRemoteOperation.SearchType.SHARED_FILTER)
        menuItemAddRemoveValue = MenuItemAddRemove.REMOVE_GRID_AND_SORT
        requireActivity().invalidateOptionsMenu()
    }

    override fun onResume() {
        super.onResume()
        Handler().post {
            if (activity is _root_ide_package_.com.owncloud.gshare.ui.activity.FileDisplayActivity) {
                val fileDisplayActivity = activity as _root_ide_package_.com.owncloud.gshare.ui.activity.FileDisplayActivity
                fileDisplayActivity.updateActionBarTitleAndHomeButtonByString(getString(R.string.drawer_item_shared))
                fileDisplayActivity.setMainFabVisible(false)
            }
        }
    }

    override fun getSearchRemoteOperation(currentUser: User?, event: SearchEvent?): RemoteOperation<*> {
        return GetSharesRemoteOperation()
    }

    private suspend fun fetchFileData(partialFile: _root_ide_package_.com.owncloud.gshare.datamodel.OCFile): _root_ide_package_.com.owncloud.gshare.datamodel.OCFile? {
        return withContext(Dispatchers.IO) {
            val user = accountManager.user
            val fetchResult = ReadFileRemoteOperation(partialFile.remotePath).execute(user, context)
            if (!fetchResult.isSuccess) {
                logger.e(SHARED_TAG, "Error fetching file")
                if (fetchResult.isException) {
                    logger.e(SHARED_TAG, "exception: ", fetchResult.exception)
                }
                null
            } else {
                val remoteFile = fetchResult.data[0] as RemoteFile
                val file = _root_ide_package_.com.owncloud.gshare.utils.FileStorageUtils.fillOCFile(remoteFile)
                _root_ide_package_.com.owncloud.gshare.utils.FileStorageUtils.searchForLocalFileInDefaultPath(file, user.accountName)
                val savedFile = mContainerActivity.storageManager.saveFileWithParent(file, context)
                savedFile.apply {
                    isSharedViaLink = partialFile.isSharedViaLink
                    isSharedWithSharee = partialFile.isSharedWithSharee
                    sharees = partialFile.sharees
                }
                adapter.replaceFileByRemotePath(savedFile, false)
                savedFile
            }
        }
    }

    private fun fetchFileAndRun(partialFile: _root_ide_package_.com.owncloud.gshare.datamodel.OCFile, block: (file: _root_ide_package_.com.owncloud.gshare.datamodel.OCFile) -> Unit) {
        lifecycleScope.launch {
            isLoading = true
            val file = fetchFileData(partialFile)
            isLoading = false
            if (file != null) {
                block(file)
            } else {
                _root_ide_package_.com.owncloud.gshare.utils.DisplayUtils.showSnackMessage(requireActivity(), R.string.error_retrieving_file)
            }
        }
    }

    override fun onShareIconClick(file: _root_ide_package_.com.owncloud.gshare.datamodel.OCFile) {
        fetchFileAndRun(file) { fetched ->
            super.onShareIconClick(fetched)
        }
    }

    override fun showShareDetailView(file: _root_ide_package_.com.owncloud.gshare.datamodel.OCFile) {
        fetchFileAndRun(file) { fetched ->
            super.showShareDetailView(fetched)
        }
    }

    override fun showActivityDetailView(file: _root_ide_package_.com.owncloud.gshare.datamodel.OCFile) {
        fetchFileAndRun(file) { fetched ->
            super.showActivityDetailView(fetched)
        }
    }

    override fun onOverflowIconClicked(file: _root_ide_package_.com.owncloud.gshare.datamodel.OCFile, view: View?) {
        fetchFileAndRun(file) { fetched ->
            super.onOverflowIconClicked(fetched, view)
        }
    }

    override fun onItemClicked(file: _root_ide_package_.com.owncloud.gshare.datamodel.OCFile) {
        fetchFileAndRun(file) { fetched ->
            super.onItemClicked(fetched)
        }
    }

    override fun onLongItemClicked(file: _root_ide_package_.com.owncloud.gshare.datamodel.OCFile): Boolean {
        fetchFileAndRun(file) { fetched ->
            super.onLongItemClicked(fetched)
        }
        return true
    }

    companion object {
        private val SHARED_TAG = SharedListFragment::class.java.simpleName
    }
}
