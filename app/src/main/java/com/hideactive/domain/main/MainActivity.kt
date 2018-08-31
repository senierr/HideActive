package com.hideactive.domain.main

import android.os.Bundle
import android.support.v4.view.ViewPager
import com.hideactive.R
import com.hideactive.domain.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * 首页
 *
 * @author zhouchunjie
 * @date 2018/8/31
 */
class MainActivity : BaseActivity() {

    private val homeFragment = HomeFragment.getInstance()
    private val contactsFragment = ContactsFragment.getInstance()
    private val mineFragment = MineFragment.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {
        tb_top.setTitle(R.string.app_name)
        tb_top.setTitleTextAppearance(this, R.style.ToolbarTitleTextAppearance)
        setSupportActionBar(tb_top)

        val homeViewPagerAdapter = MainVPAdapter(supportFragmentManager)
        homeViewPagerAdapter.addFragment(homeFragment)
        homeViewPagerAdapter.addFragment(contactsFragment)
        homeViewPagerAdapter.addFragment(mineFragment)
        vp_pager.adapter = homeViewPagerAdapter

        vp_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                bnv_bottom.menu.getItem(position).isChecked = true
            }
        })

        bnv_bottom.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.navigation_home -> {
                    vp_pager.currentItem = 0
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_contacts -> {
                    vp_pager.currentItem = 1
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_mine -> {
                    vp_pager.currentItem = 2
                    return@setOnNavigationItemSelectedListener true
                }
            }
            return@setOnNavigationItemSelectedListener false
        }
    }
}