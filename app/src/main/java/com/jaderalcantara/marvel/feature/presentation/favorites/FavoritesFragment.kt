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
import com.jaderalcantara.marvel.feature.presentation.all.AllRecyclerViewAdapter
import com.jaderalcantara.marvel.feature.presentation.all.AllViewModel
import com.jaderalcantara.marvel.infra.request.StateData
import com.jaderalcantara.marvel.infra.request.Status
import kotlinx.android.synthetic.main.all_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.ArrayList

class FavoritesFragment : Fragment() {

    companion object {
        fun newInstance() = FavoritesFragment()
    }

    private val viewModel: FavoritesViewModel by viewModel()

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
            this.setOnCloseListener {
                viewModel.loadCharacters()
                false
            }

            this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        viewModel.loadCharacters(it)
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

        viewModel.charactersLiveData.observe(viewLifecycleOwner, Observer { state ->
            when (state.status) {
                Status.SUCCESS -> {
                    emptySwipeRefresh.isRefreshing = false
                    swipeRefresh.isRefreshing = false

                    if (state.data?.isEmpty() == true) {
                        showEmpty()
                    } else {
                        emptySwipeRefresh.visibility = GONE
                        swipeRefresh.visibility = VISIBLE
                        if(list.adapter != null){
                            val adapter = list.adapter as FavoritesRecyclerViewAdapter
                            state.data?.let {
                                adapter.setItems(it)
                            }
                        }else{
                            with(list) {
                                layoutManager = GridLayoutManager(context, 2)
                                adapter =
                                    FavoritesRecyclerViewAdapter(
                                        viewModel,
                                        state.data as ArrayList<CharacterResponse>
                                    )
                            }
                        }
                    }
                }
                Status.ERROR -> {
                    swipeRefresh.isRefreshing = false
                    emptySwipeRefresh.isRefreshing = false
                    Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                }
                Status.LOADING -> {
                    swipeRefresh.isRefreshing = true
                }
            }
        })

        viewModel.loadCharacters()

        swipeRefresh.setOnRefreshListener {
            viewModel.loadCharacters()
        }

        emptySwipeRefresh.setOnRefreshListener {
            viewModel.loadCharacters()
        }
    }

    private fun showEmpty() {
        swipeRefresh.visibility = GONE
        emptySwipeRefresh.visibility = VISIBLE
    }

}