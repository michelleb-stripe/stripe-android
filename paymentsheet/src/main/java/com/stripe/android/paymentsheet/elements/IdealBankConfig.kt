package com.stripe.android.paymentsheet.elements

import com.stripe.android.paymentsheet.elements.common.DropdownConfig

internal class IdealBankConfig : DropdownConfig {
    override val debugLabel = "idealBank"
    override val label = com.stripe.android.R.string.title_bank_account

    override fun getDisplayItems() = DISPLAY_TO_PARAM.map { it.displayName }
    override fun getPaymentMethodParams() = DISPLAY_TO_PARAM.map { it.paymentMethodParamFieldValue }

    companion object {
        val DISPLAY_TO_PARAM: List<Item> = listOf(
            Item("ABN AMRO", "abn_amro"),
            Item("ASN Bank", "asn_bank"),
            Item("Bunq", "bunq"),
            Item("Handelsbanken", "handelsbanken"),
            Item("ING", "ing"),
            Item("Knab", "knab"),
            Item("Moneyou", "moneyou"),
            Item("Rabobank", "rabobank"),
            Item("Revolut", "revolut"),
            Item("RegioBank", "regiobank"),
            Item("SNS Bank (De Volksbank)", "sns_bank"),
            Item("Triodos Bank", "triodos_bank"),
            Item("Van Lanschot", "van_lanschot"),
        )
    }

    data class Item(val displayName: String, val paymentMethodParamFieldValue: String)
}
