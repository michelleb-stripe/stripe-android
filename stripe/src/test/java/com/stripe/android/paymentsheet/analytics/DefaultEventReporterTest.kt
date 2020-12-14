package com.stripe.android.paymentsheet.analytics

import androidx.test.core.app.ApplicationProvider
import com.nhaarman.mockitokotlin2.argWhere
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.stripe.android.ApiKeyFixtures
import com.stripe.android.model.PaymentMethodFixtures
import com.stripe.android.networking.AnalyticsDataFactory
import com.stripe.android.networking.AnalyticsRequest
import com.stripe.android.networking.AnalyticsRequestExecutor
import com.stripe.android.paymentsheet.PaymentSheetFixtures
import com.stripe.android.paymentsheet.model.PaymentSelection
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.Test

@RunWith(RobolectricTestRunner::class)
class DefaultEventReporterTest {
    private val analyticsRequestExecutor = mock<AnalyticsRequestExecutor>()
    private val analyticsRequestFactory = AnalyticsRequest.Factory()
    private val analyticsDataFactory = AnalyticsDataFactory(
        ApplicationProvider.getApplicationContext(),
        ApiKeyFixtures.DEFAULT_PUBLISHABLE_KEY
    )

    private val eventReporter = DefaultEventReporter(
        mode = EventReporter.Mode.Complete,
        analyticsRequestExecutor,
        analyticsRequestFactory,
        analyticsDataFactory
    )

    @Test
    fun `onInit() should fire analytics request with expected event value`() {
        eventReporter.onInit(PaymentSheetFixtures.CONFIG_CUSTOMER_WITH_GOOGLEPAY)
        verify(analyticsRequestExecutor).executeAsync(
            argWhere { req ->
                req.compactParams?.get("event") == "mc_complete_init_customer_googlepay"
            }
        )
    }

    @Test
    fun `onPaymentSuccess() should fire analytics request with expected event value`() {
        eventReporter.onPaymentSuccess(
            PaymentSelection.Saved(PaymentMethodFixtures.CARD_PAYMENT_METHOD)
        )
        verify(analyticsRequestExecutor).executeAsync(
            argWhere { req ->
                req.compactParams?.get("event") == "mc_complete_payment_savedpm_success"
            }
        )
    }
}