package com.stripe.android.paymentsheet.elements.common

import com.stripe.android.paymentsheet.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest

/**
 * This class will provide the onValueChanged and onFocusChanged functionality to the element's
 * composable.  These functions will update the observables as needed.  It is responsible for
 * exposing immutable observers for its data
 */
@ExperimentalCoroutinesApi
internal class TextFieldElement(private val textFieldConfig: TextFieldConfig) {
    val debugLabel = textFieldConfig.debugLabel
    private val isDebug = true

    /** This is all the information that can be observed on the element */
    private val _input = MutableStateFlow("")
    val input: Flow<String> = _input

    private val _elementState = MutableStateFlow<TextFieldElementState>(Error.ShowAlways)

    private val _hasFocus = MutableStateFlow(false)

    private val _visibleError = combine(_elementState, _hasFocus) { elementState, hasFocus ->
        shouldShowError(elementState, hasFocus)
    }

    val visibleError: Flow<Boolean> = _visibleError
    val errorMessage: Flow<Int?> = _visibleError.mapLatest { visibleError ->
        _elementState.value.getErrorMessageResId()?.takeIf { visibleError }
    }

    val isFull: Flow<Boolean> = _elementState.mapLatest { it.isFull() }

    val isComplete: Flow<Boolean> = _elementState.mapLatest { it.isValid() }

    private val shouldShowErrorDebug: (TextFieldElementState, Boolean) -> Boolean =
        { state, hasFocus ->
            when (state) {
                is Valid.Full -> false
                is Error.ShowInFocus -> !hasFocus
                is Error.ShowAlways -> true
                else -> textFieldConfig.shouldShowError(state, hasFocus)
            }
        }

    private val shouldShowError: (TextFieldElementState, Boolean) -> Boolean =
        if (isDebug) {
            shouldShowErrorDebug
        } else { state, hasFocus ->
            textFieldConfig.shouldShowError(state, hasFocus)
        }

    private val determineStateDebug: (String) -> TextFieldElementState = { str ->
        when {
            str.contains("full") -> Valid.Full
            str.contains("focus") -> Error.ShowInFocus
            str.contains("always") -> Error.ShowAlways
            else -> textFieldConfig.determineState(str)
        }
    }

    private val determineState: (String) -> TextFieldElementState =
        if (isDebug) {
            determineStateDebug
        } else { str ->
            textFieldConfig.determineState(str)
        }

    init {
        onValueChange("")
    }

    fun onValueChange(displayFormatted: String) {
        _input.value = textFieldConfig.filter(displayFormatted)

        // Should be filtered value
        _elementState.value = determineState(_input.value)
    }

    fun onFocusChange(newHasFocus: Boolean) {
        _hasFocus.value = newHasFocus
    }

    companion object {
        sealed class Valid : TextFieldElementState.TextFieldElementStateValid() {
            object Full : Valid() {
                override fun isFull() = true
            }
        }

        sealed class Error(stringResId: Int) :
            TextFieldElementState.TextFieldElementStateError(stringResId) {
            object ShowInFocus : Error(R.string.invalid)
            object ShowAlways : Error(R.string.invalid)
        }
    }
}
