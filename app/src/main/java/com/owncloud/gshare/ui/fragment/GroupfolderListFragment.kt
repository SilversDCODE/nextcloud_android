/*
 * Nextcloud Android client application
 *
 * @author Tobias Kaminsky
 * Copyright (C) 2023 Tobias Kaminsky
 * Copyright (C) 2023 Nextcloud GmbH
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

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.nextcloud.android.lib.resources.groupfolders.Groupfolder
import com.nextcloud.client.di.Injectable
import com.nextcloud.client.logger.Logger
import com.owncloud.gshare.MainApp
import com.owncloud.android.R
import com.owncloud.gshare.datamodel.OCFile
import com.owncloud.android.lib.resources.files.ReadFileRemoteOperation
import com.owncloud.android.lib.resources.files.model.RemoteFile
import com.owncloud.gshare.ui.EmptyRecyclerView
import com.owncloud.gshare.ui.activity.FileDisplayActivity
import com.owncloud.android.ui.adapter.GroupfolderListAdapter
import com.owncloud.android.ui.asynctasks.GroupfoldersSearchTask
import com.owncloud.android.ui.interfaces.GroupfolderListInterface
import com.owncloud.gshare.utils.DisplayUtils
import com.owncloud.gshare.utils.FileStorageUtils
import com.owncloud.android.utils.theme.ViewThemeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * A Fragment that lists groupfolders
 */
class GroupfolderListFragment : _root_ide_package_.com.owncloud.gshare.ui.fragment.OCFileListFragment(), Injectable, GroupfolderListInterface {

    lateinit var adapter: GroupfolderListAdapter

    @Inject
    lateinit var logger: Logger

    @Inject
    lateinit var viewThemeUtils: ViewThemeUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchFragment = true
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        currentSearchType = SearchType.GROUPFOLDER
        menuItemAddRemoveValue = MenuItemAddRemove.REMOVE_GRID_AND_SORT
        requireActivity().invalidateOptionsMenu()

        search()
    }

    public override fun setAdapter(args: Bundle?) {
        adapter = GroupfolderListAdapter(requireContext(), viewThemeUtils, this)
        setRecyclerViewAdapter(adapter)

        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        (recyclerView as _root_ide_package_.com.owncloud.gshare.ui.EmptyRecyclerView).setHasFooter(false)
    }

    private fun search() {
        GroupfoldersSearchTask(
            this,
            accountManager.user,
            mContainerActivity.storageManager
        ).execute()
    }

    override fun onResume() {
        super.onResume()
        Handler().post {
            if (activity is _root_ide_package_.com.owncloud.gshare.ui.activity.FileDisplayActivity) {
                val fileDisplayActivity = activity as _root_ide_package_.com.owncloud.gshare.ui.activity.FileDisplayActivity
                fileDisplayActivity.updateActionBarTitleAndHomeButtonByString(
                    getString(R.string.drawer_item_groupfolders)
                )
                fileDisplayActivity.setMainFabVisible(false)
            }
        }
    }

    override fun onRefresh() {
        super.onRefresh()

        search()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(result: Map<String, Groupfolder>) {
        adapter.setData(result)
        adapter.notifyDataSetChanged()
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

    companion object {
        private val SHARED_TAG = GroupfolderListFragment::class.java.simpleName
    }

    override fun onFolderClick(path: String) {
        _root_ide_package_.com.owncloud.gshare.MainApp.showOnlyFilesOnDevice(false)
        Intent(
            context,
            _root_ide_package_.com.owncloud.gshare.ui.activity.FileDisplayActivity::class.java
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            action = ACTION_VIEW
            putExtra(_root_ide_package_.com.owncloud.gshare.ui.activity.FileDisplayActivity.KEY_FILE_PATH, path)
            startActivity(this)
        }
    }
}
