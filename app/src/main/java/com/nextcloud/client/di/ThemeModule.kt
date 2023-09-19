/*
 * Nextcloud Android client application
 *
 * @author Tobias Kaminsky
 * Copyright (C) 2022 Tobias Kaminsky
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
package com.nextcloud.client.di

import com.nextcloud.android.common.ui.theme.MaterialSchemes
import com.owncloud.gshare.utils.theme.MaterialSchemesProvider
import com.owncloud.gshare.utils.theme.MaterialSchemesProviderImpl
// import com.owncloud.gshare.utils.theme.ThemeColorUtils
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal abstract class ThemeModule {

    @Binds
    abstract fun bindMaterialSchemesProvider(provider: MaterialSchemesProviderImpl): MaterialSchemesProvider

    companion object {

        @Provides
        @Singleton
        fun themeColorUtils(): com.owncloud.gshare.utils.theme.ThemeColorUtils {
            return com.owncloud.gshare.utils.theme.ThemeColorUtils()
        }

        @Provides
        @Singleton
        fun themeUtils(): com.owncloud.gshare.utils.theme.ThemeUtils {
            return com.owncloud.gshare.utils.theme.ThemeUtils()
        }

        @Provides
        fun provideMaterialSchemes(materialSchemesProvider: MaterialSchemesProvider): MaterialSchemes {
            return materialSchemesProvider.getMaterialSchemesForCurrentUser()
        }
    }
}
