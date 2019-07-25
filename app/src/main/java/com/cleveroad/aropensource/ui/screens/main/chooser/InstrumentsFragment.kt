package com.cleveroad.aropensource.ui.screens.main.chooser

import android.content.Context
import android.os.Bundle
import android.view.View
import com.cleveroad.aropensource.R
import com.cleveroad.aropensource.ui.base.BaseLifecycleFragment
import com.cleveroad.aropensource.utils.bindInterfaceOrThrow
import com.cleveroad.bootstrap.kotlin_ext.setClickListeners
import kotlinx.android.synthetic.main.fragment_instruments.*

class InstrumentsFragment : BaseLifecycleFragment<InstrumentsVM>(), View.OnClickListener {

    companion object {
        fun newInstance() =
            InstrumentsFragment().apply {
                arguments = Bundle()
            }
    }

    override val viewModelClass = InstrumentsVM::class.java

    override val layoutId = R.layout.fragment_instruments

    private var instrumentsCallback: InstrumentsCallback? = null

    override fun getScreenTitle() = R.string.instrument_selector

    override fun hasToolbar() = true

    override fun getToolbarId() = R.id.toolbar

    override fun observeLiveData() = Unit

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        instrumentsCallback = bindInterfaceOrThrow<InstrumentsCallback>(context)
    }

    override fun onDetach() {
        instrumentsCallback = null
        super.onDetach()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListeners(bArcore, bMlKit)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.bArcore -> instrumentsCallback?.arCoreSelected()
            R.id.bMlKit -> instrumentsCallback?.mlKitlSelected()
        }
    }
}