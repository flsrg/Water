package dev.flsrg.water

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppAction {
    OpenAddDrinkDialog,
}

object AppActionStore {
    private val _pendingAction = MutableStateFlow<AppAction?>(null)
    val pendingAction: StateFlow<AppAction?> = _pendingAction.asStateFlow()

    fun openAddDrinkDialogFromPlatform() {
        _pendingAction.value = AppAction.OpenAddDrinkDialog
    }

    fun consume(action: AppAction) {
        if (_pendingAction.value == action) {
            _pendingAction.value = null
        }
    }
}
