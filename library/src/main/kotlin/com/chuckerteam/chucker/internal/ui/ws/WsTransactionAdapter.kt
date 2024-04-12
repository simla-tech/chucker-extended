package com.chuckerteam.chucker.internal.ui.ws

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.chuckerteam.chucker.R
import com.chuckerteam.chucker.databinding.ChuckerListItemWsTransactionBinding
import com.chuckerteam.chucker.internal.data.entity.WsTransaction.TransactionType
import com.chuckerteam.chucker.internal.data.entity.WsTransactionTuple
import com.chuckerteam.chucker.internal.support.FormatUtils
import com.chuckerteam.chucker.internal.ui.transaction.DirectionResources
import java.text.DateFormat

internal class WsTransactionAdapter internal constructor(
    context: Context,
    private val listener: TransactionClickListListener?
) : RecyclerView.Adapter<WsTransactionAdapter.TransactionViewHolder>() {
    private var transactions: List<WsTransactionTuple> = arrayListOf()

    private val colorDefault: Int = ContextCompat.getColor(context, R.color.chucker_status_default)
    private val colorError: Int = ContextCompat.getColor(context, R.color.chucker_extended_ws_error)
    private val colorErrorBg: Int =
        ContextCompat.getColor(context, R.color.chucker_extended_ws_error_bg)
    private val colorInboundBg: Int =
        ContextCompat.getColor(context, R.color.chucker_extended_ws_inbound_bg)
    private val colorOutboundBg: Int =
        ContextCompat.getColor(context, R.color.chucker_extended_ws_outbound_bg)

    override fun getItemCount(): Int = transactions.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val viewBinding = ChuckerListItemWsTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) =
        holder.bind(transactions[position])

    @SuppressLint("NotifyDataSetChanged")
    fun setData(wsTransactions: List<WsTransactionTuple>) {
        this.transactions = wsTransactions
        notifyDataSetChanged()
    }

    inner class TransactionViewHolder(
        private val itemBinding: ChuckerListItemWsTransactionBinding
    ) : RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        private var transactionId: Long? = null

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            transactionId?.let {
                listener?.onTransactionClick(it, adapterPosition)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(transaction: WsTransactionTuple) {
            transactionId = transaction.id

            itemBinding.apply {
                type.text = transaction.type.name
                type.setTextColor(if (transaction.type.isError()) colorError else colorDefault)
                if (transaction.textMessage != null) {
                    message.visibility = View.VISIBLE
                    message.text = FormatUtils.extractGqlRoot(transaction.textMessage, "")
                        ?: transaction.textMessage
                    // size.visibility = View.VISIBLE
                    // size.text = transaction.sizeMessageString
                } else {
                    message.visibility = View.GONE
                    size.visibility = View.GONE
                }
                datetime.text = DateFormat.getTimeInstance().format(transaction.timestamp)

                direction.setDirectionImage(transaction.type)
                root.setBackgroundColor(transaction.type)
            }
        }

        private fun ImageView.setDirectionImage(type: TransactionType) {
            val resources = when (type) {
                TransactionType.SEND_MESSAGE,
                TransactionType.SEND_CANCEL,
                TransactionType.SEND_CLOSE -> DirectionResources.Send()
                TransactionType.RECEIVE_FAILURE -> DirectionResources.ReceiveError()
                else -> DirectionResources.Receive()
            }
            setImageDrawable(AppCompatResources.getDrawable(context, resources.icon))
            ImageViewCompat.setImageTintList(
                this,
                ColorStateList.valueOf(ContextCompat.getColor(context, resources.color))
            )
        }

        private fun View.setBackgroundColor(type: TransactionType) {
            setBackgroundColor(
                when (type) {
                    TransactionType.SEND_MESSAGE,
                    TransactionType.SEND_CANCEL,
                    TransactionType.SEND_CLOSE -> colorOutboundBg
                    TransactionType.RECEIVE_FAILURE -> colorErrorBg
                    else -> colorInboundBg
                }
            )
        }
    }

    interface TransactionClickListListener {
        fun onTransactionClick(transactionId: Long, position: Int)
    }
}
