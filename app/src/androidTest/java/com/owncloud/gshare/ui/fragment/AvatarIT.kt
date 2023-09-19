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

import android.graphics.BitmapFactory
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.nextcloud.test.TestActivity
import com.owncloud.gshare.R
import com.owncloud.android.lib.resources.users.StatusType
import org.junit.Rule
import org.junit.Test

class AvatarIT : com.owncloud.gshare.AbstractIT() {
    @get:Rule
    val testActivityRule = IntentsTestRule(TestActivity::class.java, true, false)

    @Test
    @com.owncloud.gshare.utils.ScreenshotTest
    fun showAvatars() {
        val avatarRadius = targetContext.resources.getDimension(R.dimen.list_item_avatar_icon_radius)
        val width = com.owncloud.gshare.utils.DisplayUtils.convertDpToPixel(2 * avatarRadius, targetContext)
        val sut = testActivityRule.launchActivity(null)
        val fragment = AvatarTestFragment()

        sut.addFragment(fragment)

        runOnUiThread {
            fragment.addAvatar("Admin", avatarRadius, width, targetContext)
            fragment.addAvatar("Test Server Admin", avatarRadius, width, targetContext)
            fragment.addAvatar("Cormier Paulette", avatarRadius, width, targetContext)
            fragment.addAvatar("winston brent", avatarRadius, width, targetContext)
            fragment.addAvatar("Baker James Lorena", avatarRadius, width, targetContext)
            fragment.addAvatar("Baker  James   Lorena", avatarRadius, width, targetContext)
            fragment.addAvatar("email@nextcloud.localhost", avatarRadius, width, targetContext)
        }

        shortSleep()
        waitForIdleSync()
        screenshot(sut)
    }

    @Test
    @com.owncloud.gshare.utils.ScreenshotTest
    fun showAvatarsWithStatus() {
        val avatarRadius = targetContext.resources.getDimension(R.dimen.list_item_avatar_icon_radius)
        val width = com.owncloud.gshare.utils.DisplayUtils.convertDpToPixel(2 * avatarRadius, targetContext)
        val sut = testActivityRule.launchActivity(null)
        val fragment = AvatarTestFragment()

        val paulette = BitmapFactory.decodeFile(getFile("paulette.jpg").absolutePath)
        val christine = BitmapFactory.decodeFile(getFile("christine.jpg").absolutePath)
        val textBitmap = com.owncloud.gshare.utils.BitmapUtils.drawableToBitmap(com.owncloud.gshare.ui.TextDrawable.createNamedAvatar("Admin", avatarRadius))

        sut.addFragment(fragment)

        runOnUiThread {
            fragment.addBitmap(
                com.owncloud.gshare.utils.BitmapUtils.createAvatarWithStatus(paulette, StatusType.ONLINE, "😘", targetContext),
                width * 2,
                1,
                targetContext
            )

            fragment.addBitmap(
                com.owncloud.gshare.utils.BitmapUtils.createAvatarWithStatus(christine, StatusType.ONLINE, "☁️", targetContext),
                width * 2,
                1,
                targetContext
            )

            fragment.addBitmap(
                com.owncloud.gshare.utils.BitmapUtils.createAvatarWithStatus(christine, StatusType.ONLINE, "🌴️", targetContext),
                width * 2,
                1,
                targetContext
            )

            fragment.addBitmap(
                com.owncloud.gshare.utils.BitmapUtils.createAvatarWithStatus(christine, StatusType.ONLINE, "", targetContext),
                width * 2,
                1,
                targetContext
            )

            fragment.addBitmap(
                com.owncloud.gshare.utils.BitmapUtils.createAvatarWithStatus(paulette, StatusType.DND, "", targetContext),
                width * 2,
                1,
                targetContext
            )

            fragment.addBitmap(
                com.owncloud.gshare.utils.BitmapUtils.createAvatarWithStatus(christine, StatusType.AWAY, "", targetContext),
                width * 2,
                1,
                targetContext
            )

            fragment.addBitmap(
                com.owncloud.gshare.utils.BitmapUtils.createAvatarWithStatus(paulette, StatusType.OFFLINE, "", targetContext),
                width * 2,
                1,
                targetContext
            )

            fragment.addBitmap(
                com.owncloud.gshare.utils.BitmapUtils.createAvatarWithStatus(textBitmap, StatusType.ONLINE, "😘", targetContext),
                width,
                2,
                targetContext
            )

            fragment.addBitmap(
                com.owncloud.gshare.utils.BitmapUtils.createAvatarWithStatus(textBitmap, StatusType.ONLINE, "☁️", targetContext),
                width,
                2,
                targetContext
            )

            fragment.addBitmap(
                com.owncloud.gshare.utils.BitmapUtils.createAvatarWithStatus(textBitmap, StatusType.ONLINE, "🌴️", targetContext),
                width,
                2,
                targetContext
            )

            fragment.addBitmap(
                com.owncloud.gshare.utils.BitmapUtils.createAvatarWithStatus(textBitmap, StatusType.ONLINE, "", targetContext),
                width,
                2,
                targetContext
            )

            fragment.addBitmap(
                com.owncloud.gshare.utils.BitmapUtils.createAvatarWithStatus(textBitmap, StatusType.DND, "", targetContext),
                width,
                2,
                targetContext
            )

            fragment.addBitmap(
                com.owncloud.gshare.utils.BitmapUtils.createAvatarWithStatus(textBitmap, StatusType.AWAY, "", targetContext),
                width,
                2,
                targetContext
            )

            fragment.addBitmap(
                com.owncloud.gshare.utils.BitmapUtils.createAvatarWithStatus(textBitmap, StatusType.OFFLINE, "", targetContext),
                width,
                2,
                targetContext
            )
        }

        shortSleep()
        waitForIdleSync()
        screenshot(sut)
    }
}
