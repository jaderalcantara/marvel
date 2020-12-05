package com.jaderalcantara.marvel.feature.presentation.all

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jaderalcantara.marvel.R
import com.jaderalcantara.marvel.feature.data.CharacterResponse
import com.jaderalcantara.marvel.feature.data.DataCharacterResponse
import com.jaderalcantara.marvel.feature.presentation.characterDetail.CharacterDetailActivity
import com.jaderalcantara.marvel.infra.request.StateData
import com.jaderalcantara.marvel.infra.request.Status
import kotlinx.android.synthetic.main.all_fragment.*
import java.util.*

class AllFragment : Fragment() {
    val CHARACTER_DETAIL_REQUEST_CODE = 123
    var isLoading = false
    var disableEndless = false

    companion object {
        fun newInstance() = AllFragment()
    }

    private lateinit var viewModel: AllViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.all_fragment, container, false)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        (menu.findItem(R.id.app_bar_search).actionView as SearchView).apply {
            this.setOnCloseListener (object : SearchView.OnCloseListener {
                override fun onClose(): Boolean {
                    viewModel.clearSearch()
                    viewModel.reloadCharacters().observe(viewLifecycleOwner, Observer {
                        it?.let { resource ->
                            setupAdapter(resource, it)
                        }
                    })
                    return false
                }
            })

            this.setOnQueryTextListener(object : OnQueryTextListener {

                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {

                        viewModel.searchCharacter(it).observe(viewLifecycleOwner, Observer {
                            it?.let { resource ->
                                setupAdapter(resource, it)
                            }
                        })
                    }

                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AllViewModel::class.java)

        viewModel.loadCharacters().observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                setupAdapter(resource, it)
            }
        })

        swipeRefresh.setOnRefreshListener {
            viewModel.reloadCharacters().observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    setupAdapter(resource, it)
                }
            })
        }

        emptySwipeRefresh.setOnRefreshListener {
            viewModel.reloadCharacters().observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    setupAdapter(resource, it)
                }
            })
        }
    }

    private fun setupAdapter(
        resource: StateData<DataCharacterResponse>,
        it: StateData<DataCharacterResponse>
    ) {
        when (resource.status) {
            Status.SUCCESS -> {
                emptySwipeRefresh.isRefreshing = false
                swipeRefresh.isRefreshing = false

                if (resource.data?.results?.isEmpty() == true) {
                    emptySwipeRefresh.visibility = VISIBLE
                    swipeRefresh.visibility = GONE
                } else {
                    swipeRefresh.visibility = VISIBLE
                    emptySwipeRefresh.visibility = View.GONE
                    if (list is RecyclerView) {
                        with(list) {
                            layoutManager = GridLayoutManager(context, 2)
                            adapter =
                                AllRecyclerViewAdapter(
                                    context,
                                    viewModel,
                                    resource.data?.results as ArrayList<CharacterResponse>,
                                    object : AllRecyclerViewAdapter.OnItemClicked {
                                        override fun itemClicked(characterResponse: CharacterResponse) {
                                            startActivityForResult(
                                                CharacterDetailActivity.intent(context, characterResponse),
                                                CHARACTER_DETAIL_REQUEST_CODE
                                            )
                                        }
                                    }
                                )
                        }
                    }
                    initScrollListener()
                    disableEndless = false
                }
            }
            Status.ERROR -> {
                disableEndless = false
                swipeRefresh.isRefreshing = false
                emptySwipeRefresh.isRefreshing = false
                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
            }
            Status.LOADING -> {
                swipeRefresh.isRefreshing = true
            }
        }
    }

    private fun initScrollListener() {
        val leftRowsToLoadMore = 5
        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val gridLayoutManager = recyclerView.layoutManager as GridLayoutManager?
                if (!isLoading && !disableEndless) {
                    if (gridLayoutManager != null &&
                        gridLayoutManager.findLastCompletelyVisibleItemPosition() == list.adapter?.itemCount?.minus(
                            leftRowsToLoadMore
                        )
                    ) {
                        isLoading = true
                        loadMore()
                    }
                }
            }
        })
    }

    private fun loadMore() {
        viewModel.loadCharacters().observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { it ->
                            if (it.offset > it.total) {
                                disableEndless = true
                            }
                        }
                        val adapter = list.adapter as AllRecyclerViewAdapter?

                        resource.data?.results?.let { items ->
                            adapter?.add(items)
                        }

                        isLoading = false
                    }
                    Status.ERROR -> {
                        Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                        isLoading = false
                    }
                    Status.LOADING -> {
                        isLoading = true
                    }
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(CHARACTER_DETAIL_REQUEST_CODE == requestCode){
            val allRecyclerViewAdapter = list.adapter as AllRecyclerViewAdapter
            allRecyclerViewAdapter.newState(data?.getSerializableExtra(CharacterDetailActivity.CHARACTER_DATA_RESULT) as CharacterResponse?)
        }
    }

}