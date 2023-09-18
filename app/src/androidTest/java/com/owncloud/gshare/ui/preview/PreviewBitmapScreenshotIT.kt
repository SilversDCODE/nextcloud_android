/*
 * Nextcloud Android client application
 *
 * @author Álvaro Brey Vilas
 * Copyright (C) 2022 Álvaro Brey Vilas
 * Copyright (C) 2022 Nextcloud GmbH
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

package com.owncloud.gshare.ui.preview

import android.content.Intent
import androidx.test.espresso.intent.rule.IntentsTestRule
import com.owncloud.gshare.AbstractIT
import com.owncloud.gshare.utils.ScreenshotTest
import org.junit.Rule
import org.junit.Test

class PreviewBitmapScreenshotIT : com.owncloud.gshare.AbstractIT() {

    companion object {
        private const val PNG_FILE_ASSET = "imageFile.png"
    }

    @get:Rule
    val testActivityRule = IntentsTestRule(PreviewBitmapActivity::class.java, true, false)

    @Test
    @com.owncloud.gshare.utils.ScreenshotTest
    fun showBitmap() {
        val pngFile = getFile(PNG_FILE_ASSET)

        val activity = testActivityRule.launchActivity(
            Intent().putExtra(
                PreviewBitmapActivity.EXTRA_BITMAP_PATH,
                pngFile.absolutePath
            )
        )

        shortSleep()
        waitForIdleSync()

        screenshot(activity)
    }
}
