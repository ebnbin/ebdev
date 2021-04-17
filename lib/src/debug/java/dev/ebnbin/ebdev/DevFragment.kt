package dev.ebnbin.ebdev

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import dev.ebnbin.ebdev.databinding.EbdevDevFragmentBinding
import dev.ebnbin.ebui.openFragment
import dev.ebnbin.ebui.requireArg

fun Activity.openDev() {
    openFragment<DevFragment>(
        fragmentArgs = bundleOf(
            KEY_DEV_PAGE_LIST to ArrayList(EBDev.config.createDevPageList()(this)),
        ),
    )
}

private const val KEY_DEV_PAGE_LIST = "dev_pages"

internal class DevFragment : Fragment() {
    private lateinit var binding: EbdevDevFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = EbdevDevFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private lateinit var onPageChangeCallback: ViewPager2.OnPageChangeCallback

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ebdevToolbar.setNavigationOnClickListener {
            requireActivity().finish()
        }
        val devPageList = requireArg<List<DevPage>>(KEY_DEV_PAGE_LIST)
        val adapter = DevPagerAdapter(this, devPageList)
        binding.ebdevViewPager.adapter = adapter
        onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                EBDevPrefs.devPage.value = position
            }
        }
        binding.ebdevViewPager.registerOnPageChangeCallback(onPageChangeCallback)
        TabLayoutMediator(binding.ebdevTabLayout, binding.ebdevViewPager) { tab, position ->
            tab.text = devPageList[position].title
        }.attach()
        if (savedInstanceState == null) {
            binding.ebdevViewPager.setCurrentItem(EBDevPrefs.devPage.value, false)
        }
    }

    override fun onDestroyView() {
        binding.ebdevViewPager.unregisterOnPageChangeCallback(onPageChangeCallback)
        super.onDestroyView()
    }
}
