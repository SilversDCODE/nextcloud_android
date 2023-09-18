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
package com.owncloud.gshare.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class BitmapUtilsIT {
    @Test
    @Suppress("MagicNumber")
    fun usernameToColor() {
        assertEquals(com.owncloud.gshare.utils.BitmapUtils.Color(0, 0, 0), com.owncloud.gshare.utils.BitmapUtils.Color(0, 0, 0))
        assertEquals(com.owncloud.gshare.utils.BitmapUtils.Color(221, 203, 85), com.owncloud.gshare.utils.BitmapUtils.usernameToColor("User"))
        assertEquals(com.owncloud.gshare.utils.BitmapUtils.Color(208, 158, 109), com.owncloud.gshare.utils.BitmapUtils.usernameToColor("Admin"))
        assertEquals(com.owncloud.gshare.utils.BitmapUtils.Color(0, 130, 201), com.owncloud.gshare.utils.BitmapUtils.usernameToColor(""))
        assertEquals(com.owncloud.gshare.utils.BitmapUtils.Color(201, 136, 121), com.owncloud.gshare.utils.BitmapUtils.usernameToColor("68b329da9893e34099c7d8ad5cb9c940"))

        // tests from server
        assertEquals(com.owncloud.gshare.utils.BitmapUtils.Color(208, 158, 109), com.owncloud.gshare.utils.BitmapUtils.usernameToColor("Alishia Ann Lowry"))
        assertEquals(com.owncloud.gshare.utils.BitmapUtils.Color(0, 130, 201), com.owncloud.gshare.utils.BitmapUtils.usernameToColor("Arham Johnson"))
        assertEquals(com.owncloud.gshare.utils.BitmapUtils.Color(208, 158, 109), com.owncloud.gshare.utils.BitmapUtils.usernameToColor("Brayden Truong"))
        assertEquals(com.owncloud.gshare.utils.BitmapUtils.Color(151, 80, 164), com.owncloud.gshare.utils.BitmapUtils.usernameToColor("Daphne Roy"))
        assertEquals(com.owncloud.gshare.utils.BitmapUtils.Color(195, 114, 133), com.owncloud.gshare.utils.BitmapUtils.usernameToColor("Ellena Wright Frederic Conway"))
        assertEquals(com.owncloud.gshare.utils.BitmapUtils.Color(214, 180, 97), com.owncloud.gshare.utils.BitmapUtils.usernameToColor("Gianluca Hills"))
        assertEquals(com.owncloud.gshare.utils.BitmapUtils.Color(214, 180, 97), com.owncloud.gshare.utils.BitmapUtils.usernameToColor("Haseeb Stephens"))
        assertEquals(com.owncloud.gshare.utils.BitmapUtils.Color(151, 80, 164), com.owncloud.gshare.utils.BitmapUtils.usernameToColor("Idris Mac"))
        assertEquals(com.owncloud.gshare.utils.BitmapUtils.Color(0, 130, 201), com.owncloud.gshare.utils.BitmapUtils.usernameToColor("Kristi Fisher"))
        assertEquals(com.owncloud.gshare.utils.BitmapUtils.Color(188, 92, 145), com.owncloud.gshare.utils.BitmapUtils.usernameToColor("Lillian Wall"))
        assertEquals(com.owncloud.gshare.utils.BitmapUtils.Color(221, 203, 85), com.owncloud.gshare.utils.BitmapUtils.usernameToColor("Lorelai Taylor"))
        assertEquals(com.owncloud.gshare.utils.BitmapUtils.Color(151, 80, 164), com.owncloud.gshare.utils.BitmapUtils.usernameToColor("Madina Knight"))
        assertEquals(com.owncloud.gshare.utils.BitmapUtils.Color(121, 90, 171), com.owncloud.gshare.utils.BitmapUtils.usernameToColor("Rae Hope"))
        assertEquals(com.owncloud.gshare.utils.BitmapUtils.Color(188, 92, 145), com.owncloud.gshare.utils.BitmapUtils.usernameToColor("Santiago Singleton"))
        assertEquals(com.owncloud.gshare.utils.BitmapUtils.Color(208, 158, 109), com.owncloud.gshare.utils.BitmapUtils.usernameToColor("Sid Combs"))
        assertEquals(com.owncloud.gshare.utils.BitmapUtils.Color(30, 120, 193), com.owncloud.gshare.utils.BitmapUtils.usernameToColor("Vivienne Jacobs"))
        assertEquals(com.owncloud.gshare.utils.BitmapUtils.Color(110, 166, 143), com.owncloud.gshare.utils.BitmapUtils.usernameToColor("Zaki Cortes"))
        assertEquals(com.owncloud.gshare.utils.BitmapUtils.Color(91, 100, 179), com.owncloud.gshare.utils.BitmapUtils.usernameToColor("a user"))
        assertEquals(com.owncloud.gshare.utils.BitmapUtils.Color(208, 158, 109), com.owncloud.gshare.utils.BitmapUtils.usernameToColor("admin"))
        assertEquals(com.owncloud.gshare.utils.BitmapUtils.Color(151, 80, 164), com.owncloud.gshare.utils.BitmapUtils.usernameToColor("admin@cloud.example.com"))
        assertEquals(com.owncloud.gshare.utils.BitmapUtils.Color(221, 203, 85), com.owncloud.gshare.utils.BitmapUtils.usernameToColor("another user"))
        assertEquals(com.owncloud.gshare.utils.BitmapUtils.Color(36, 142, 181), com.owncloud.gshare.utils.BitmapUtils.usernameToColor("asd"))
        assertEquals(com.owncloud.gshare.utils.BitmapUtils.Color(0, 130, 201), com.owncloud.gshare.utils.BitmapUtils.usernameToColor("bar"))
        assertEquals(com.owncloud.gshare.utils.BitmapUtils.Color(208, 158, 109), com.owncloud.gshare.utils.BitmapUtils.usernameToColor("foo"))
        assertEquals(com.owncloud.gshare.utils.BitmapUtils.Color(182, 70, 157), com.owncloud.gshare.utils.BitmapUtils.usernameToColor("wasd"))
    }

    @Test
    @Suppress("MagicNumber")
    fun checkEqual() {
        assertEquals(com.owncloud.gshare.utils.BitmapUtils.Color(208, 158, 109), com.owncloud.gshare.utils.BitmapUtils.Color(208, 158, 109))
        assertNotEquals(com.owncloud.gshare.utils.BitmapUtils.Color(208, 158, 109), com.owncloud.gshare.utils.BitmapUtils.Color(208, 158, 100))
    }

    @Test
    @Suppress("MagicNumber")
    fun checkHashCode() {
        assertEquals(com.owncloud.gshare.utils.BitmapUtils.Color(208, 158, 109).hashCode(), com.owncloud.gshare.utils.BitmapUtils.Color(208, 158, 109).hashCode())
        assertNotEquals(com.owncloud.gshare.utils.BitmapUtils.Color(208, 158, 109).hashCode(), com.owncloud.gshare.utils.BitmapUtils.Color(208, 158, 100).hashCode())
    }
}
