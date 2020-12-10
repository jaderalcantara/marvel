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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jaderalcantara.marvel.R
import com.jaderalcantara.marvel.feature.data.CharacterResponse
import com.jaderalcantara.marvel.feature.data.DataCharacterResponse
import com.jaderalcantara.marvel.feature.presentation.characterDetail.CharacterDetailActivity
import com.jaderalcantara.marvel.infra.request.StateData
import com.jaderalcantara.marvel.infra.request.Status
import kotlinx.android.synthetic.main.all_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class AllFragment : Fragment() {
    val CHARACTER_DETAIL_REQUEST_CODE = 123

    companion object {
        fun newInstance() = AllFragment()
    }

    private val allViewModel: AllViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        allViewModel.itemSelectedLiveData.observe(viewLifecycleOwner, Observer { character ->
            context?.let { context ->
                startActivityForResult(
                        CharacterDetailActivity.intent(context, character),
                        CHARACTER_DETAIL_REQUEST_CODE
                )
            }

        })

        allViewModel.charactersLiveData.observe(viewLifecycleOwner, Observer { state ->
            when (state.status) {
                Status.SUCCESS -> {
                    emptySwipeRefresh.isRefreshing = false
                    swipeRefresh.isRefreshing = false

                    if (state.data?.isEmpty() == true) {
                        emptySwipeRefresh.visibility = VISIBLE
                        swipeRefresh.visibility = GONE
                    } else {
                        swipeRefresh.visibility = VISIBLE
                        emptySwipeRefresh.visibility = View.GONE
                        if (list is RecyclerView) {
                            if(list.adapter != null){
                                val adapter = list.adapter as AllRecyclerViewAdapter
                                state.data?.let {
                                    adapter.setItems(it)
                                }
                            }else{
                                with(list) {
                                    layoutManager = GridLayoutManager(context, 2)
                                    adapter =
                                        AllRecyclerViewAdapter(
                                            allViewModel,
                                            state.data as ArrayList<CharacterResponse>
                                        )
                                }
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

        allViewModel.disableEndlessLiveData.observe(viewLifecycleOwner, Observer {disableEndless ->
            if(disableEndless){
                list.removeOnScrollListener(listener)
            }else{
                list.addOnScrollListener(listener)
            }
        })

        return inflater.inflate(R.layout.all_fragment, container, false)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        (menu.findItem(R.id.app_bar_search).actionView as SearchView).apply {
            this.setOnCloseListener {
                allViewModel.clearSearch()
                allViewModel.reloadCharacters()
                false
            }

            this.setOnQueryTextListener(object : OnQueryTextListener {

                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        allViewModel.searchCharacter(it)
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

        allViewModel.loadCharacters()

        swipeRefresh.setOnRefreshListener {
            allViewModel.reloadCharacters()
        }

        emptySwipeRefresh.setOnRefreshListener {
            allViewModel.reloadCharacters()
        }
    }

    private fun loadMore() {
        allViewModel.loadCharacters()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(CHARACTER_DETAIL_REQUEST_CODE == requestCode){
            val allRecyclerViewAdapter = list.adapter as AllRecyclerViewAdapter
            allRecyclerViewAdapter.newState(data?.getSerializableExtra(CharacterDetailActivity.CHARACTER_DATA_RESULT) as CharacterResponse?)
        }
    }

     private val listener = object : RecyclerView.OnScrollListener() {
         val leftRowsToLoadMore = 5

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val gridLayoutManager = recyclerView.layoutManager as GridLayoutManager?
            if (gridLayoutManager != null &&
                    gridLayoutManager.findLastCompletelyVisibleItemPosition() == list.adapter?.itemCount?.minus(
                            leftRowsToLoadMore
                    )
            ) {
                list.removeOnScrollListener(this)
                loadMore()
            }
        }
    }

}