package com.chuckerteam.chucker.internal.ui.ws

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.chuckerteam.chucker.R
import com.chuckerteam.chucker.databinding.ChuckerFragmentWsTransactionMessageBinding
import com.chuckerteam.chucker.internal.data.entity.WsTransaction
import com.chuckerteam.chucker.internal.support.FormatUtils

internal class WsTransactionMessageFragment : Fragment() {

    private val viewModel: WsTransactionViewModel by activityViewModels { WsTransactionViewModelFactory() }

    private lateinit var messageBinding: ChuckerFragmentWsTransactionMessageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        messageBinding =
            ChuckerFragmentWsTransactionMessageBinding.inflate(inflater, container, false)
        return messageBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.transaction.observe(viewLifecycleOwner) { populateUI(it) }
    }

    private fun populateUI(transaction: WsTransaction?) {
        with(messageBinding) {
            urlValue.text = transaction?.host
            when (transaction?.sslEnabled == true) {
                true -> sslValue.setText(R.string.chucker_yes)
                false -> sslValue.setText(R.string.chucker_no)
            }
            timestampValue.text = transaction?.timestampString
            if (transaction?.code != null) {
                codeValue.text = transaction.code.toString()
            } else {
                codeGroup.visibility = View.GONE
            }
            if (transaction?.reason != null) {
                reasonValue.text = transaction.reason
            } else {
                reasonGroup.visibility = View.GONE
            }
            if (transaction?.sizeMessage != null) {
                sizeValue.text = transaction.sizeMessageString
                if (transaction.textMessage != null) {
                    messageValue.text =
                        FormatUtils.formatJsonWithGqlQuery(transaction.textMessage!!)
                } else {
                    messageValue.text = transaction.byteMessage.toString()
                }
            } else {
                messageGroup.visibility = View.GONE
            }
        }
    }
}
