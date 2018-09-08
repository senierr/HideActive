package com.hideactive.domain.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hideactive.R

/**
 * 通讯录页
 *
 * @author zhouchunjie
 * @date 2018/8/31
 */
class ContactsFragment : Fragment() {

    companion object {
        fun getInstance(): ContactsFragment {
            return ContactsFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }


}