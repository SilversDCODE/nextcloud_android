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

package com.owncloud.gshare.ui.preview.pdf

import androidx.lifecycle.Lifecycle
import androidx.test.espresso.intent.rule.IntentsTestRule
import com.nextcloud.test.TestActivity
import org.junit.Rule
import org.junit.Test

class PreviewPdfFragmentScreenshotIT : com.owncloud.gshare.AbstractIT() {

    companion object {
        private const val PDF_FILE_ASSET = "test.pdf"
    }

    @get:Rule
    val testActivityRule = IntentsTestRule(TestActivity::class.java, true, false)

    @Test
    @com.owncloud.gshare.utils.ScreenshotTest
    fun showPdf() {
        val activity = testActivityRule.launchActivity(null)

        val pdfFile = getFile(PDF_FILE_ASSET)
        val ocFile = com.owncloud.gshare.datamodel.OCFile("/test.pdf").apply {
            storagePath = pdfFile.absolutePath
        }

        val sut = PreviewPdfFragment.newInstance(ocFile)
        activity.addFragment(sut)

        while (!sut.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            shortSleep()
        }

        activity.runOnUiThread {
            sut.dismissSnack()
        }

        shortSleep()
        waitForIdleSync()

        screenshot(activity)
    }
}
