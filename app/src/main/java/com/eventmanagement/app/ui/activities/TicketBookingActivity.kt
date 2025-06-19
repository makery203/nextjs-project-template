package com.eventmanagement.app.ui.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eventmanagement.app.databinding.ActivityTicketBookingBinding
import com.eventmanagement.app.services.PaymentService

class TicketBookingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTicketBookingBinding
    private lateinit var paymentService: PaymentService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTicketBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        paymentService = PaymentService(this)

        setupPaymentMethodsSpinner()
        setupPayButton()
    }

    private fun setupPaymentMethodsSpinner() {
        val paymentMethods = listOf("Mpesa", "Airtel Money", "HaloPesa", "MixbyYas")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, paymentMethods)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPaymentMethods.adapter = adapter
    }

    private fun setupPayButton() {
        binding.btnPay.setOnClickListener {
            val phoneNumber = binding.etPhoneNumber.text.toString().trim()
            val amount = binding.etAmount.text.toString().toDoubleOrNull()

            if (phoneNumber.isEmpty() || amount == null || amount <= 0) {
                Toast.makeText(this, "Please enter valid phone number and amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedMethod = binding.spinnerPaymentMethods.selectedItem.toString()
            when (selectedMethod) {
                "Mpesa" -> paymentService.payWithMpesa(phoneNumber, amount, ::paymentCallback)
                "Airtel Money" -> paymentService.payWithAirtelMoney(phoneNumber, amount, ::paymentCallback)
                "HaloPesa" -> paymentService.payWithHaloPesa(phoneNumber, amount, ::paymentCallback)
                "MixbyYas" -> paymentService.payWithMixbyYas(phoneNumber, amount, ::paymentCallback)
                else -> Toast.makeText(this, "Unsupported payment method", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun paymentCallback(success: Boolean, message: String) {
        if (success) {
            Toast.makeText(this, "Payment successful", Toast.LENGTH_SHORT).show()
            // TODO: Update ticket booking status
            finish()
        } else {
            Toast.makeText(this, "Payment failed: $message", Toast.LENGTH_SHORT).show()
        }
    }
}
