package com.jaderalcantara.marvel.feature.presentation.favorites

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jaderalcantara.marvel.R
import com.jaderalcantara.marvel.feature.data.CharacterResponse
import com.jaderalcantara.marvel.feature.data.DataCharacterResponse
import com.jaderalcantara.marvel.infra.request.StateData
import com.jaderalcantara.marvel.infra.request.Status
import kotlinx.android.synthetic.main.all_fragment.*
import java.util.ArrayList

class FavoritesFragment : Fragment() {

    companion object {
        fun newInstance() = FavoritesFragment()
    }

    private lateinit var viewModel: FavoritesViewModel

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
                    viewModel.loadCharacters().observe(viewLifecycleOwner, Observer {
                        it?.let { resource ->
                            setupAdapter(resource, it)
                        }
                    })
                    return false
                }
            })

            this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        viewModel.loadCharacters(it).observe(viewLifecycleOwner, Observer {
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
        viewModel = ViewModelProvider(this).get(FavoritesViewModel::class.java)

        viewModel.loadCharacters().observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                setupAdapter(resource, it)
            }
        })

        swipeRefresh.setOnRefreshListener {
            viewModel.loadCharacters().observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    setupAdapter(resource, it)
                }
            })
        }

        emptySwipeRefresh.setOnRefreshListener {
            viewModel.loadCharacters().observe(viewLifecycleOwner, Observer {
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
                    showEmpty()
                } else {
                    emptySwipeRefresh.visibility = GONE
                    swipeRefresh.visibility = VISIBLE
                    if (list is RecyclerView) {
                        with(list) {
                            layoutManager = GridLayoutManager(context, 2)
                            adapter =
                                FavoritesRecyclerViewAdapter(
                                    viewModel,
                                    resource.data?.results as ArrayList<CharacterResponse>,
                                    object : FavoritesRecyclerViewAdapter.OnListListener {
                                        override fun onListIsEmpty() {
                                            showEmpty()
                                        }
                                    }
                                )
                        }
                    }
                }
            }
            Status.ERROR -> {
                swipeRefresh.isRefreshing = false
                emptySwipeRefresh.isRefreshing = false
                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showEmpty() {
        swipeRefresh.visibility = GONE
        emptySwipeRefresh.visibility = VISIBLE
    }

}