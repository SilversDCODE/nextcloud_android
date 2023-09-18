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
package com.owncloud.gshare.ui.activity

import android.content.Intent
import androidx.test.espresso.intent.rule.IntentsTestRule
import com.owncloud.gshare.AbstractIT
import com.owncloud.gshare.datamodel.OCFile
import com.owncloud.gshare.utils.ScreenshotTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ContactsPreferenceActivityIT : com.owncloud.gshare.AbstractIT() {
    @get:Rule
    var activityRule = IntentsTestRule(com.owncloud.gshare.ui.activity.ContactsPreferenceActivity::class.java, true, false)

    @Test
    @com.owncloud.gshare.utils.ScreenshotTest
    fun openVCF() {
        val file = getFile("vcard.vcf")
        val vcfFile = com.owncloud.gshare.datamodel.OCFile("/contacts.vcf")
        vcfFile.storagePath = file.absolutePath

        assertTrue(vcfFile.isDown)

        val intent = Intent()
        intent.putExtra(com.owncloud.gshare.ui.activity.ContactsPreferenceActivity.EXTRA_FILE, vcfFile)
        intent.putExtra(com.owncloud.gshare.ui.activity.ContactsPreferenceActivity.EXTRA_USER, user)
        val sut = activityRule.launchActivity(intent)

        shortSleep()

        screenshot(sut)
    }

    @Test
    @com.owncloud.gshare.utils.ScreenshotTest
    fun openContactsPreference() {
        val sut = activityRule.launchActivity(null)

        shortSleep()

        screenshot(sut)
    }
}
