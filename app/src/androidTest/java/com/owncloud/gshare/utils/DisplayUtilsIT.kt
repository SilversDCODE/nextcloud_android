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
package com.owncloud.gshare.utils

import com.owncloud.gshare.AbstractIT
import org.junit.Assert.assertEquals
import org.junit.Test

class DisplayUtilsIT : com.owncloud.gshare.AbstractIT() {
    @Test
    fun testPixelToDP() {
        val px = 123
        val dp = com.owncloud.gshare.utils.DisplayUtils.convertPixelToDp(px, targetContext)
        val newPx = com.owncloud.gshare.utils.DisplayUtils.convertDpToPixel(dp, targetContext)

        assertEquals(px.toLong(), newPx.toLong())
    }
}
