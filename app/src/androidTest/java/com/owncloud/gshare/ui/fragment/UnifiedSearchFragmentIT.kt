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
package com.owncloud.gshare.ui.fragment

import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import com.nextcloud.test.TestActivity
import com.owncloud.android.lib.common.SearchResultEntry
import com.owncloud.gshare.ui.unifiedsearch.UnifiedSearchSection
import com.owncloud.gshare.ui.unifiedsearch.UnifiedSearchViewModel
import org.junit.Rule
import org.junit.Test
import java.io.File

class UnifiedSearchFragmentIT : com.owncloud.gshare.AbstractIT() {
    @get:Rule
    val testActivityRule = IntentsTestRule(TestActivity::class.java, true, false)

    @Test
    fun showSearchResult() {
        val activity = testActivityRule.launchActivity(null)
        val sut = UnifiedSearchFragment.newInstance(null)

        activity.addFragment(sut)

        shortSleep()

        UiThreadStatement.runOnUiThread {
            sut.onSearchResultChanged(
                listOf(
                    UnifiedSearchSection(
                        providerID = "files",
                        name = "Files",
                        entries = listOf(
                            SearchResultEntry(
                                "thumbnailUrl",
                                "Test",
                                "in Files",
                                "http://localhost/nc/index.php/apps/files/?dir=/Files&scrollto=Test",
                                "icon",
                                false
                            )
                        ),
                        hasMoreResults = false
                    )
                )
            )
        }
        shortSleep()
    }

    @Test
    fun search() {
        val activity = testActivityRule.launchActivity(null) as TestActivity
        val sut = UnifiedSearchFragment.newInstance(null)
        val testViewModel = UnifiedSearchViewModel(activity.application)
        testViewModel.setConnectivityService(activity.connectivityServiceMock)
        val localRepository = UnifiedSearchFakeRepository()
        testViewModel.setRepository(localRepository)

        val ocFile = com.owncloud.gshare.datamodel.OCFile("/folder/test1.txt").apply {
            storagePath = "/sdcard/1.txt"
            storageManager.saveFile(this)
        }

        File(ocFile.storagePath).createNewFile()

        activity.addFragment(sut)

        shortSleep()

        UiThreadStatement.runOnUiThread {
            sut.setViewModel(testViewModel)
            sut.vm.setQuery("test")
            sut.vm.initialQuery()
        }
        shortSleep()
    }
}
