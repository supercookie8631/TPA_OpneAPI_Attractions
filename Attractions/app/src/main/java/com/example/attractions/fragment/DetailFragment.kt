package com.example.attractions.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.attractions.R
import com.example.attractions.adapter.GalleryAdapter
import com.example.attractions.databinding.FragmentDetailBinding
import com.example.attractions.model.AttractionsViewModel

class DetailFragment : Fragment() {
    private val mAttractionsViewModel: AttractionsViewModel by lazy {
        ViewModelProvider(requireActivity())[AttractionsViewModel::class.java]
    }
    private lateinit var binding: FragmentDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(R.transition.shared_image)
        sharedElementReturnTransition = TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.shared_image)
    }

    private fun initView() {
        val position = arguments?.getInt("position")
        val imageUrlList = arguments?.getStringArrayList("imageUrlList")

        // 加載圖片並開始共享元素過渡動畫
        postponeEnterTransition()
        ViewCompat.setTransitionName(binding.itemImageView, "itemImageView$position")
        val imageUrl = imageUrlList?.getOrNull(0)
        val placeholderImage = if (imageUrl.isNullOrEmpty()) R.drawable.noresult else imageUrl

        Glide.with(this).load(placeholderImage)
            .override(binding.itemImageView.width, binding.itemImageView.height)
            .placeholder(R.drawable.loading_icon).fitCenter()
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    startPostponedEnterTransition()
                    return false
                }
            }).into(binding.itemImageView)


        binding.itemTitleTv.text =
            position?.let { mAttractionsViewModel.contentList.value?.get(it)?.name }
        binding.itemIntroductionTv.text = position?.let {
            mAttractionsViewModel.contentList.value?.get(it)?.introduction
        }

        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val adapter = imageUrlList?.let {
            GalleryAdapter(requireContext(), it, object : GalleryAdapter.OnItemClickListener {
                override fun onItemClick(imageUrl: String) {
                    // 點擊 RecyclerView 中的項目時，更改 binding.itemImageView 的內容
                    Glide.with(this@DetailFragment).load(imageUrl)
                        .override(binding.itemImageView.width, binding.itemImageView.height)
                        .placeholder(R.drawable.loading_icon)
                        .fitCenter()
                        .into(binding.itemImageView)
                }
            })
        }
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        println("TransitionNameDetail:" + ViewCompat.getTransitionName(binding.itemImageView))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setupBackPressedCallback()
    }

    private fun setupBackPressedCallback() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 在這裡處理返回鍵按下事件的邏輯
                // 例如，返回上一個 Fragment 或退出當前 Activity
                findNavController().popBackStack() // 返回上一個 Fragment
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, onBackPressedCallback
        )
    }
}