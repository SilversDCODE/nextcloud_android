/*
 * Nextcloud Android client application
 *
 * @author Tobias Kaminsky
 * Copyright (C) 2020 Tobias Kaminsky
 * Copyright (C) 2020 Nextcloud GmbH
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
package com.owncloud.gshare.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.nextcloud.client.account.CurrentAccountProvider
import com.nextcloud.client.core.AsyncRunner
import com.nextcloud.client.di.Injectable
import com.nextcloud.client.di.ViewModelFactory
import com.nextcloud.client.network.ClientFactory
import com.owncloud.gshare.R
import com.owncloud.gshare.databinding.ListFragmentBinding
import com.owncloud.android.lib.common.SearchResultEntry
import com.owncloud.android.lib.common.utils.Log_OC
import com.owncloud.gshare.ui.adapter.UnifiedSearchListAdapter
import com.owncloud.gshare.ui.fragment.util.PairMediatorLiveData
import com.owncloud.gshare.ui.interfaces.UnifiedSearchListInterface
import com.owncloud.gshare.ui.unifiedsearch.IUnifiedSearchViewModel
import com.owncloud.gshare.ui.unifiedsearch.ProviderID
import com.owncloud.gshare.ui.unifiedsearch.UnifiedSearchSection
import com.owncloud.gshare.ui.unifiedsearch.UnifiedSearchViewModel
import com.owncloud.gshare.utils.theme.ViewThemeUtils
import javax.inject.Inject

/**
 * Starts query to all capable unified search providers and displays them Opens result in our app, redirect to other
 * apps, if installed, or opens browser
 */
class UnifiedSearchFragment : Fragment(), Injectable, UnifiedSearchListInterface, SearchView.OnQueryTextListener {
    private lateinit var adapter: UnifiedSearchListAdapter
    private var _binding: ListFragmentBinding? = null
    private val binding get() = _binding!!
    lateinit var vm: IUnifiedSearchViewModel

    @Inject
    lateinit var vmFactory: ViewModelFactory

    @Inject
    lateinit var storageManager: _root_ide_package_.com.owncloud.gshare.datamodel.FileDataStorageManager

    @Inject
    lateinit var runner: AsyncRunner

    @Inject
    lateinit var currentAccountProvider: CurrentAccountProvider

    @Inject
    lateinit var clientFactory: ClientFactory

    @Inject
    lateinit var viewThemeUtils: ViewThemeUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this, vmFactory).get(UnifiedSearchViewModel::class.java)
        setUpViewModel()

        val query = savedInstanceState?.getString(ARG_QUERY) ?: arguments?.getString(ARG_QUERY)
        if (!query.isNullOrEmpty()) {
            vm.setQuery(query)
            vm.initialQuery()
        }
    }

    private fun setUpViewModel() {
        vm.searchResults.observe(this, this::onSearchResultChanged)
        vm.isLoading.observe(this) { loading ->
            binding.swipeContainingList.isRefreshing = loading
        }

        PairMediatorLiveData(vm.searchResults, vm.isLoading).observe(this) { pair ->
            if (pair.second == false) {
                var count = 0

                pair.first?.forEach {
                    count += it.entries.size
                }

                if (count == 0 && pair.first?.isNotEmpty() == true && context != null) {
                    binding.emptyList.root.visibility = View.VISIBLE
                    binding.emptyList.emptyListIcon.visibility = View.VISIBLE
                    binding.emptyList.emptyListViewHeadline.visibility = View.VISIBLE
                    binding.emptyList.emptyListViewText.visibility = View.VISIBLE
                    binding.emptyList.emptyListIcon.visibility = View.VISIBLE

                    binding.emptyList.emptyListViewHeadline.text =
                        requireContext().getString(R.string.file_list_empty_headline_server_search)
                    binding.emptyList.emptyListViewText.text =
                        requireContext().getString(R.string.file_list_empty_unified_search_no_results)
                    binding.emptyList.emptyListIcon.setImageDrawable(
                        viewThemeUtils.platform.tintPrimaryDrawable(requireContext(), R.drawable.ic_search_grey)
                    )
                }
            }
        }

        vm.error.observe(this) { error ->
            if (!error.isNullOrEmpty()) {
                _root_ide_package_.com.owncloud.gshare.utils.DisplayUtils.showSnackMessage(binding.root, error)
            }
        }
        vm.query.observe(this) { query ->
            if (activity is _root_ide_package_.com.owncloud.gshare.ui.activity.FileDisplayActivity) {
                (activity as _root_ide_package_.com.owncloud.gshare.ui.activity.FileDisplayActivity)
                    .updateActionBarTitleAndHomeButtonByString("\"${query}\"")
            }
        }
        vm.browserUri.observe(this) { uri ->
            val browserIntent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(browserIntent)
        }
        vm.file.observe(this) {
            showFile(it)
        }
    }

    private fun setUpBinding() {
        binding.swipeContainingList.setOnRefreshListener {
            vm.initialQuery()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = ListFragmentBinding.inflate(inflater, container, false)
        binding.listRoot.updatePadding(top = resources.getDimension(R.dimen.standard_half_padding).toInt())
        setUpBinding()
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity is _root_ide_package_.com.owncloud.gshare.ui.activity.FileDisplayActivity) {
            val fileDisplayActivity = activity as _root_ide_package_.com.owncloud.gshare.ui.activity.FileDisplayActivity
            fileDisplayActivity.setMainFabVisible(false)
            fileDisplayActivity.updateActionBarTitleAndHomeButtonByString("\"${vm.query.value!!}\"")
        }

        val gridLayoutManager = GridLayoutManager(requireContext(), 1)
        adapter = UnifiedSearchListAdapter(
            storageManager,
            this,
            currentAccountProvider.user,
            clientFactory,
            requireContext(),
            viewThemeUtils
        )
        adapter.shouldShowFooters(true)
        adapter.setLayoutManager(gridLayoutManager)
        binding.listRoot.layoutManager = gridLayoutManager
        binding.listRoot.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showFile(file: _root_ide_package_.com.owncloud.gshare.datamodel.OCFile) {
        activity.let {
            if (activity is _root_ide_package_.com.owncloud.gshare.ui.activity.FileDisplayActivity) {
                val fda = activity as _root_ide_package_.com.owncloud.gshare.ui.activity.FileDisplayActivity
                fda.file = file
                fda.showFile("")
            }
        }
    }

    override fun onSearchResultClicked(searchResultEntry: SearchResultEntry) {
        vm.openResult(searchResultEntry)
    }

    override fun onLoadMoreClicked(providerID: ProviderID) {
        vm.loadMore(providerID)
    }

    @VisibleForTesting
    fun onSearchResultChanged(result: List<UnifiedSearchSection>) {
        Log_OC.d(TAG, "result")
        binding.emptyList.emptyListView.visibility = View.GONE

        adapter.setData(result)
    }

    @VisibleForTesting
    fun setViewModel(testViewModel: IUnifiedSearchViewModel) {
        vm = testViewModel
        setUpViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val item = menu.findItem(R.id.action_search)
        val searchView = MenuItemCompat.getActionView(item) as SearchView
        viewThemeUtils.androidx.themeToolbarSearchView(searchView)
        searchView.setQuery(vm.query.value, false)
        searchView.setOnQueryTextListener(this)
        searchView.isIconified = false
        searchView.clearFocus()
    }

    companion object {
        private const val TAG = "UnifiedSearchFragment"
        const val ARG_QUERY = "ARG_QUERY"

        /**
         * Public factory method to get fragment.
         */
        fun newInstance(query: String?): UnifiedSearchFragment {
            val fragment = UnifiedSearchFragment()
            val args = Bundle()
            args.putString(ARG_QUERY, query)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        vm.setQuery(query)
        vm.initialQuery()
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        // noop
        return true
    }
}
