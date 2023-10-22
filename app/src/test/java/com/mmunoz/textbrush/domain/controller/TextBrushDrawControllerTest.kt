package com.mmunoz.textbrush.domain.controller

import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TextBrushDrawControllerTest {

    private lateinit var controller: TextBrushDrawController

    @Before
    fun setupController() {
        controller = TextBrushDrawController { _, _ -> }
    }

    @Test
    fun testSetFinalPath() = runBlocking(Dispatchers.Default) {
        controller.insertNewPath(Offset(10f, 10f))
        controller.setFinalPath()

        assertEquals(1, controller.finalPathList.value.size)
        assertEquals(1, controller.undoPathList.size)
        assertEquals(0, controller.redoPathList.size)
    }

    @Test
    fun testUnDo() = runBlocking(Dispatchers.Default) {
        controller.insertNewPath(Offset(10f, 10f))
        controller.setFinalPath()
        controller.unDo()

        assertEquals(0, controller.finalPathList.value.size)
        assertEquals(0, controller.undoPathList.size)
        assertEquals(1, controller.redoPathList.size)
    }

    @Test
    fun testReDo() = runBlocking(Dispatchers.Default) {
        controller.insertNewPath(Offset(10f, 10f))
        controller.setFinalPath()
        controller.unDo()
        controller.reDo()

        assertEquals(1, controller.finalPathList.value.size)
        assertEquals(1, controller.undoPathList.size)
        assertEquals(0, controller.redoPathList.size)
    }

    @Test
    fun testReset() = runBlocking(Dispatchers.Default) {
        controller.insertNewPath(Offset(10f, 10f))
        controller.setFinalPath()
        controller.reset()

        assertTrue(controller.finalPathList.value.isEmpty())
        assertTrue(controller.undoPathList.isEmpty())
        assertTrue(controller.redoPathList.isEmpty())
    }

    @Test
    fun testUpdateLatestPath() = runBlocking(Dispatchers.Default) {
        val updatedOffset = Offset(20f, 20f)
        controller.insertNewPath(Offset(10f, 10f))
        controller.updateLatestPath(updatedOffset)

        val pathItem = controller.pathList.last()

        assertEquals(2, pathItem.points.size)
        assertEquals(updatedOffset, pathItem.points[1])
    }
}
