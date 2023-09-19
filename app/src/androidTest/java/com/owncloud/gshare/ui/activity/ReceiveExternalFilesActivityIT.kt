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
package com.owncloud.gshare.ui.activity

import android.app.Activity
import androidx.test.espresso.intent.rule.IntentsTestRule
import org.junit.Rule
import org.junit.Test

class ReceiveExternalFilesActivityIT : com.owncloud.gshare.AbstractIT() {
    @get:Rule
    val activityRule = IntentsTestRule(com.owncloud.gshare.ui.activity.ReceiveExternalFilesActivity::class.java, true, false)

    @Test
    @com.owncloud.gshare.utils.ScreenshotTest
    fun open() {
        val sut: Activity = activityRule.launchActivity(null)
        screenshot(sut)
    }
}
