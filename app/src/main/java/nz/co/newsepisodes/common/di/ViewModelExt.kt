package nz.co.newsepisodes.common.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.plus

fun ViewModel.dispatcherScope(dispatchers: CoroutineDispatchers): CoroutineScope =
    viewModelScope + dispatchers.io
