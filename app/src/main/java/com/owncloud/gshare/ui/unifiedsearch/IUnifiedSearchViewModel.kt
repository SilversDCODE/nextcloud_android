package com.owncloud.gshare.ui.unifiedsearch

import android.net.Uri
import androidx.lifecycle.LiveData
import com.owncloud.gshare.datamodel.OCFile
import com.owncloud.android.lib.common.SearchResultEntry

interface IUnifiedSearchViewModel {
    val browserUri: LiveData<Uri>
    val error: LiveData<String>
    val file: LiveData<_root_ide_package_.com.owncloud.gshare.datamodel.OCFile>
    val isLoading: LiveData<Boolean>
    val query: LiveData<String>
    val searchResults: LiveData<List<UnifiedSearchSection>>

    fun initialQuery()
    fun loadMore(provider: ProviderID)
    fun openResult(result: SearchResultEntry)
    fun setQuery(query: String)
}
