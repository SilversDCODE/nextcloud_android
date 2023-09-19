/*
 * Nextcloud Android client application
 *
 * @author Tobias Kaminsky
 * Copyright (C) 2021 Tobias Kaminsky
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

import android.Manifest
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.rule.GrantPermissionRule
import com.owncloud.gshare.R
import org.junit.Rule
import org.junit.Test

class BackupListFragmentIT : com.owncloud.gshare.AbstractIT() {
    @get:Rule
    val testActivityRule = IntentsTestRule(com.owncloud.gshare.ui.activity.ContactsPreferenceActivity::class.java, true, false)

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.READ_CALENDAR)

    @Test
    @com.owncloud.gshare.utils.ScreenshotTest
    fun showLoading() {
        val sut = testActivityRule.launchActivity(null)
        val file = com.owncloud.gshare.datamodel.OCFile("/")
        val transaction = sut.supportFragmentManager.beginTransaction()

        transaction.replace(R.id.frame_container, com.owncloud.gshare.ui.fragment.contactsbackup.BackupListFragment.newInstance(file, user))
        transaction.commit()

        waitForIdleSync()
        screenshot(sut)
    }

    @Test
    @com.owncloud.gshare.utils.ScreenshotTest
    fun showContactList() {
        val sut = testActivityRule.launchActivity(null)
        val transaction = sut.supportFragmentManager.beginTransaction()
        val file = getFile("vcard.vcf")
        val ocFile = com.owncloud.gshare.datamodel.OCFile("/vcard.vcf")
        ocFile.storagePath = file.absolutePath
        ocFile.mimeType = "text/vcard"

        transaction.replace(R.id.frame_container, com.owncloud.gshare.ui.fragment.contactsbackup.BackupListFragment.newInstance(ocFile, user))
        transaction.commit()

        waitForIdleSync()
        shortSleep()
        screenshot(sut)
    }

    @Test
    @com.owncloud.gshare.utils.ScreenshotTest
    fun showCalendarList() {
        val sut = testActivityRule.launchActivity(null)
        val transaction = sut.supportFragmentManager.beginTransaction()
        val file = getFile("calendar.ics")
        val ocFile = com.owncloud.gshare.datamodel.OCFile("/Private calender_2020-09-01_10-45-20.ics.ics")
        ocFile.storagePath = file.absolutePath
        ocFile.mimeType = "text/calendar"

        transaction.replace(R.id.frame_container, com.owncloud.gshare.ui.fragment.contactsbackup.BackupListFragment.newInstance(ocFile, user))
        transaction.commit()

        waitForIdleSync()
        screenshot(sut)
    }

    @Test
    @com.owncloud.gshare.utils.ScreenshotTest
    fun showCalendarAndContactsList() {
        val sut = testActivityRule.launchActivity(null)
        val transaction = sut.supportFragmentManager.beginTransaction()

        val calendarFile = getFile("calendar.ics")
        val calendarOcFile = com.owncloud.gshare.datamodel.OCFile("/Private calender_2020-09-01_10-45-20.ics")
        calendarOcFile.storagePath = calendarFile.absolutePath
        calendarOcFile.mimeType = "text/calendar"

        val contactFile = getFile("vcard.vcf")
        val contactOcFile = com.owncloud.gshare.datamodel.OCFile("/vcard.vcf")
        contactOcFile.storagePath = contactFile.absolutePath
        contactOcFile.mimeType = "text/vcard"

        val files = arrayOf(calendarOcFile, contactOcFile)
        transaction.replace(R.id.frame_container, com.owncloud.gshare.ui.fragment.contactsbackup.BackupListFragment.newInstance(files, user))
        transaction.commit()

        waitForIdleSync()
        screenshot(sut)
    }
}
