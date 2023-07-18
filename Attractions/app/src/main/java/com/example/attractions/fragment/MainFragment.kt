package com.example.attractions.fragment

import android.graphics.Rect
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.attractions.R
import com.example.attractions.adapter.AttractionsAdapter
import com.example.attractions.base.LoadState
import com.example.attractions.databinding.FragmentMainBinding
import com.example.attractions.model.AttractionsViewModel
import com.example.attractions.utils.SizeUtils
import com.google.android.material.snackbar.Snackbar

class MainFragment : Fragment() {

    private val mAttractionsViewModel: AttractionsViewModel by lazy {
        ViewModelProvider(requireActivity())[AttractionsViewModel::class.java]
    }
    private lateinit var binding: FragmentMainBinding
    private val mAdapter by lazy { AttractionsAdapter() }
    private val mSnackbar by lazy {
        Snackbar.make(
            binding.contentListRv, "載入中請稍等...", Snackbar.LENGTH_SHORT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObservers()
    }

    private fun initView() {
        binding.data = mAttractionsViewModel
        binding.contentListRv.run {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
                ) {
                    outRect.apply {
                        val padding = SizeUtils.dip2px(requireContext(), 4.0f)
                        top = padding
                        left = padding
                        bottom = padding
                        right = padding
                    }
                }
            })
        }

        binding.contentListRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                val threshold = 2

                if (firstVisibleItemPosition < 1) binding.fabTop.visibility =
                    View.GONE else binding.fabTop.visibility = View.VISIBLE

                if (visibleItemCount + firstVisibleItemPosition + threshold >= totalItemCount) {
                    if (!mAttractionsViewModel.isLoading) {
                        mAttractionsViewModel.isLoading = true
                        mSnackbar.show()
                        mAttractionsViewModel.loadMoreData()
                    }
                }
            }
        })

        binding.fabTop.setOnClickListener {
            binding.contentListRv.smoothScrollToPosition(0)
        }
    }

    private fun initObservers() {
        mAttractionsViewModel.apply {
            contentList.observe(viewLifecycleOwner) {
                mAdapter.setData(it)
                isLoading = false
                mSnackbar.dismiss()
            }

            loadState.observe(viewLifecycleOwner) {
                hideAll()
                when (it) {
                    LoadState.SUCCESS -> binding.swipeRefreshLayout.visibility = View.VISIBLE
                    LoadState.LOADING -> binding.loadingView.visibility = View.VISIBLE
                    LoadState.EMPTY -> binding.emptyView.visibility = View.VISIBLE
                    LoadState.ERROR -> binding.errorView.visibility = View.VISIBLE
                    LoadState.NOMOREDATA -> {
                        binding.swipeRefreshLayout.visibility = View.VISIBLE
                        Snackbar.make(
                            binding.contentListRv, "已查無更多資料", Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

            binding.errorView.setOnClickListener {
                loadMoreData()
            }

            // 設置下拉刷新監聽器
            binding.swipeRefreshLayout.setOnRefreshListener {
                loadContent()
                binding.swipeRefreshLayout.isRefreshing = false
            }

            if (contentList.value == null) {
                loadContent()
            }
        }
    }

    private fun hideAll() {
        binding.swipeRefreshLayout.visibility = View.GONE
        binding.loadingView.visibility = View.GONE
        binding.emptyView.visibility = View.GONE
        binding.errorView.visibility = View.GONE
        binding.fabTop.visibility = View.GONE
    }
}
