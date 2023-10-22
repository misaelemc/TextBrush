package com.mmunoz.textbrush.draw

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import com.mmunoz.textbrush.draw.model.PathItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class TextBrushDrawController internal constructor(
    val trackHistory: (undoCount: Int, redoCount: Int) -> Unit = { _, _ -> }
) {

    private val _redoPathList = mutableStateListOf<PathItem>()
    private val _undoPathList = mutableStateListOf<PathItem>()
    private val _linePathList = mutableStateListOf<PathItem>()

    internal val pathList: SnapshotStateList<PathItem> = _linePathList

    private val _historyTracker = MutableSharedFlow<String>(extraBufferCapacity = 1)
    private val historyTracker = _historyTracker.asSharedFlow()

    private val _finalPathList = MutableStateFlow<List<PathItem>>(listOf())
    internal val finalPathList: StateFlow<List<PathItem>> = _finalPathList

    fun trackHistory(
        scope: CoroutineScope,
        trackHistory: (undoCount: Int, redoCount: Int) -> Unit
    ) {
        historyTracker
            .onEach { trackHistory(_undoPathList.size, _redoPathList.size) }
            .launchIn(scope)
    }

    fun setFinalPath() {
        val value = _finalPathList.value
        val points = pathList.toList().flatMap { it.points }.toMutableList()
        val newPathItem = PathItem(points)
        _finalPathList.value = mutableListOf<PathItem>().apply {
            if (value.isNotEmpty()) {
                addAll(value)
            }
            add(newPathItem)
        }
        _linePathList.clear()
        _undoPathList.add(newPathItem)
        _redoPathList.clear()
        _historyTracker.tryEmit("${_undoPathList.size}")
    }

    fun unDo() {
        if (_undoPathList.isNotEmpty()) {
            val last = _undoPathList.last()
            _redoPathList.add(last)
            _undoPathList.remove(last)
            _finalPathList.update {
                val items = it.toMutableList()
                items.remove(last)
                items
            }
            trackHistory(_undoPathList.size, _redoPathList.size)
            _historyTracker.tryEmit("Undo - ${_undoPathList.size}")
        }
    }

    fun reDo() {
        if (_redoPathList.isNotEmpty()) {
            val last = _redoPathList.last()
            _undoPathList.add(last)
            _finalPathList.update {
                val items = it.toMutableList()
                items.add(last)
                items
            }
            _redoPathList.remove(last)
            trackHistory(_undoPathList.size, _redoPathList.size)
            _historyTracker.tryEmit("Redo - ${_redoPathList.size}")
        }
    }

    fun reset() {
        _redoPathList.clear()
        _undoPathList.clear()
        _historyTracker.tryEmit("-")
        _finalPathList.value = listOf()
    }

    fun updateLatestPath(newPoint: Offset) {
        _linePathList[_linePathList.lastIndex].points.add(newPoint)
    }

    fun insertNewPath(newPoint: Offset) {
        val pathData = PathItem(
            points = mutableStateListOf(newPoint),
        )
        _linePathList.add(pathData)
    }
}

@Composable
fun rememberTextBrushDrawController(): TextBrushDrawController {
    return remember { TextBrushDrawController() }
}