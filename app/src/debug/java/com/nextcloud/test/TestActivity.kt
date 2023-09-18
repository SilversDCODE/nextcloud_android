/*
 * Nextcloud Android client application
 *
 * @author Tobias Kaminsky
 * Copyright (C) 2020 Tobias Kaminsky
 * Copyright (C) 2020 Nextcloud GmbH
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

package com.nextcloud.test

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.nextcloud.client.network.Connectivity
import com.nextcloud.client.network.ConnectivityService
import com.nextcloud.utils.EditorUtils
import com.owncloud.android.R
import com.owncloud.android.databinding.TestLayoutBinding
import com.owncloud.gshare.datamodel.ArbitraryDataProviderImpl
import com.owncloud.gshare.datamodel.FileDataStorageManager
import com.owncloud.gshare.datamodel.OCFile
import com.owncloud.gshare.files.services.FileDownloader
import com.owncloud.gshare.files.services.FileUploader
import com.owncloud.android.lib.resources.status.OCCapability
import com.owncloud.android.lib.resources.status.OwnCloudVersion
import com.owncloud.gshare.services.OperationsService
import com.owncloud.gshare.ui.activity.FileActivity
import com.owncloud.gshare.ui.activity.OnEnforceableRefreshListener
import com.owncloud.gshare.ui.fragment.FileFragment
import com.owncloud.gshare.ui.helpers.FileOperationsHelper

class TestActivity :
    com.owncloud.gshare.ui.activity.FileActivity(),
    com.owncloud.gshare.ui.fragment.FileFragment.ContainerActivity,
    SwipeRefreshLayout.OnRefreshListener,
    com.owncloud.gshare.ui.activity.OnEnforceableRefreshListener {
    lateinit var fragment: Fragment
    lateinit var secondaryFragment: Fragment

    private lateinit var storage: com.owncloud.gshare.datamodel.FileDataStorageManager
    private lateinit var fileOperation: com.owncloud.gshare.ui.helpers.FileOperationsHelper
    private lateinit var binding: TestLayoutBinding

    val connectivityServiceMock: ConnectivityService = object : ConnectivityService {
        override fun isInternetWalled(): Boolean {
            return false
        }

        override fun getConnectivity(): Connectivity {
            return Connectivity.CONNECTED_WIFI
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = TestLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun addFragment(fragment: Fragment) {
        this.fragment = fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_fragment, fragment)
        transaction.commit()
    }

    /**
     * Adds a secondary fragment to the activity with the given tag.
     *
     * If you have to use this, your Fragments are coupled, and you should feel bad.
     */
    fun addSecondaryFragment(fragment: Fragment, tag: String) {
        this.secondaryFragment = fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.secondary_fragment, fragment, tag)
        transaction.commit()
    }

    /**
     * Adds a View to the activity.
     *
     * If you have to use this, your Fragment is coupled to your Activity and you should feel bad.
     */
    fun addView(view: View) {
        handler.post {
            binding.rootLayout.addView(view)
        }
    }

    override fun onBrowsedDownTo(folder: com.owncloud.gshare.datamodel.OCFile?) {
        TODO("Not yet implemented")
    }

    override fun getOperationsServiceBinder(): com.owncloud.gshare.services.OperationsService.OperationsServiceBinder? {
        return null
    }

    override fun showSortListGroup(show: Boolean) {
        // not needed
    }

    override fun showDetails(file: com.owncloud.gshare.datamodel.OCFile?) {
        TODO("Not yet implemented")
    }

    override fun showDetails(file: com.owncloud.gshare.datamodel.OCFile?, activeTab: Int) {
        TODO("Not yet implemented")
    }

    override fun getFileUploaderBinder(): com.owncloud.gshare.files.services.FileUploader.FileUploaderBinder? {
        return null
    }

    override fun getFileDownloaderBinder(): com.owncloud.gshare.files.services.FileDownloader.FileDownloaderBinder? {
        return null
    }

    override fun getStorageManager(): com.owncloud.gshare.datamodel.FileDataStorageManager {
        if (!this::storage.isInitialized) {
            storage = com.owncloud.gshare.datamodel.FileDataStorageManager(user.get(), contentResolver)

            if (!storage.capabilityExistsForAccount(account.name)) {
                val ocCapability = OCCapability()
                ocCapability.versionMayor = OwnCloudVersion.nextcloud_20.majorVersionNumber
                storage.saveCapabilities(ocCapability)
            }
        }

        return storage
    }

    override fun getFileOperationsHelper(): com.owncloud.gshare.ui.helpers.FileOperationsHelper {
        if (!this::fileOperation.isInitialized) {
            fileOperation = com.owncloud.gshare.ui.helpers.FileOperationsHelper(
                this,
                userAccountManager,
                connectivityServiceMock,
                EditorUtils(
                    com.owncloud.gshare.datamodel.ArbitraryDataProviderImpl(baseContext)
                )
            )
        }

        return fileOperation
    }

    override fun onTransferStateChanged(file: com.owncloud.gshare.datamodel.OCFile?, downloading: Boolean, uploading: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onRefresh(enforced: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onRefresh() {
        TODO("Not yet implemented")
    }
}
