/*
 *
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
package com.owncloud.gshare.ui.trashbin

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Intent
import androidx.test.espresso.intent.rule.IntentsTestRule
import com.owncloud.gshare.AbstractIT
import com.owncloud.gshare.MainApp
import com.owncloud.android.lib.common.accounts.AccountUtils
import com.owncloud.gshare.utils.ScreenshotTest
import org.junit.Rule
import org.junit.Test

class TrashbinActivityIT : com.owncloud.gshare.AbstractIT() {
    enum class TestCase {
        ERROR, EMPTY, FILES
    }

    @get:Rule
    var activityRule = IntentsTestRule(com.owncloud.gshare.ui.trashbin.TrashbinActivity::class.java, true, false)

    @Test
    @com.owncloud.gshare.utils.ScreenshotTest
    fun error() {
        val sut: com.owncloud.gshare.ui.trashbin.TrashbinActivity = activityRule.launchActivity(null)

        val trashbinRepository = TrashbinLocalRepository(TestCase.ERROR)

        sut.trashbinPresenter = com.owncloud.gshare.ui.trashbin.TrashbinPresenter(trashbinRepository, sut)

        sut.runOnUiThread { sut.loadFolder() }

        shortSleep()

        screenshot(sut)
    }

    @Test
    @com.owncloud.gshare.utils.ScreenshotTest
    fun files() {
        val sut: com.owncloud.gshare.ui.trashbin.TrashbinActivity = activityRule.launchActivity(null)

        val trashbinRepository = TrashbinLocalRepository(TestCase.FILES)

        sut.trashbinPresenter = com.owncloud.gshare.ui.trashbin.TrashbinPresenter(trashbinRepository, sut)

        sut.runOnUiThread { sut.loadFolder() }

        waitForIdleSync()
        shortSleep()
        shortSleep()

        screenshot(sut)
    }

    @Test
    @com.owncloud.gshare.utils.ScreenshotTest
    fun empty() {
        val sut: com.owncloud.gshare.ui.trashbin.TrashbinActivity = activityRule.launchActivity(null)

        val trashbinRepository = TrashbinLocalRepository(TestCase.EMPTY)

        sut.trashbinPresenter = com.owncloud.gshare.ui.trashbin.TrashbinPresenter(trashbinRepository, sut)

        sut.runOnUiThread { sut.loadFolder() }

        shortSleep()
        shortSleep()
        waitForIdleSync()

        screenshot(sut)
    }

    @Test
    @com.owncloud.gshare.utils.ScreenshotTest
    fun loading() {
        val sut: com.owncloud.gshare.ui.trashbin.TrashbinActivity = activityRule.launchActivity(null)

        val trashbinRepository = TrashbinLocalRepository(TestCase.EMPTY)

        sut.trashbinPresenter = com.owncloud.gshare.ui.trashbin.TrashbinPresenter(trashbinRepository, sut)

        sut.runOnUiThread { sut.showInitialLoading() }

        shortSleep()

        screenshot(sut)
    }

    @Test
    fun normalUser() {
        val sut: com.owncloud.gshare.ui.trashbin.TrashbinActivity = activityRule.launchActivity(null)

        val trashbinRepository = TrashbinLocalRepository(TestCase.EMPTY)

        sut.trashbinPresenter = com.owncloud.gshare.ui.trashbin.TrashbinPresenter(trashbinRepository, sut)

        sut.runOnUiThread { sut.showUser() }

        shortSleep()

        screenshot(sut)
    }

    @Test
    fun differentUser() {
        val temp = Account("differentUser@https://nextcloud.localhost", com.owncloud.gshare.MainApp.getAccountType(targetContext))

        val platformAccountManager = AccountManager.get(targetContext)
        platformAccountManager.addAccountExplicitly(temp, "password", null)
        platformAccountManager.setUserData(temp, AccountUtils.Constants.KEY_OC_BASE_URL, "https://nextcloud.localhost")
        platformAccountManager.setUserData(temp, AccountUtils.Constants.KEY_USER_ID, "differentUser")

        val intent = Intent()
        intent.putExtra(Intent.EXTRA_USER, "differentUser@https://nextcloud.localhost")
        val sut: com.owncloud.gshare.ui.trashbin.TrashbinActivity = activityRule.launchActivity(intent)

        val trashbinRepository = TrashbinLocalRepository(TestCase.EMPTY)

        sut.trashbinPresenter = com.owncloud.gshare.ui.trashbin.TrashbinPresenter(trashbinRepository, sut)

        sut.runOnUiThread { sut.showUser() }

        shortSleep()

        screenshot(sut)
    }
}
