package io.docxkt.part

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class RelationshipAllocatorTest {

    @Test
    fun `sequential nextId starts after the implicit prefix (rId7)`() {
        val a = RelationshipAllocator()
        assertEquals("rId7", a.nextId())
        assertEquals("rId8", a.nextId())
        assertEquals("rId9", a.nextId())
    }

    @Test
    fun `allocatedCount tracks dynamic issuance, not the reserved prefix`() {
        val a = RelationshipAllocator()
        assertEquals(0, a.allocatedCount)
        a.nextId()
        a.nextId()
        assertEquals(2, a.allocatedCount)
    }

    @Test
    fun `fresh allocator starts dynamic at rId7 (no global state)`() {
        val a = RelationshipAllocator()
        a.nextId()
        a.nextId()
        val b = RelationshipAllocator()
        assertEquals("rId7", b.nextId())
    }

    @Test
    fun `implicitRid is fixed by name and idempotent`() {
        val a = RelationshipAllocator()
        assertEquals("rId1", a.implicitRid("styles"))
        assertEquals("rId2", a.implicitRid("numbering"))
        assertEquals("rId3", a.implicitRid("footnotes"))
        assertEquals("rId4", a.implicitRid("endnotes"))
        assertEquals("rId5", a.implicitRid("settings"))
        assertEquals("rId6", a.implicitRid("comments"))
        // Calling again returns the same rId.
        assertEquals("rId5", a.implicitRid("settings"))
    }

    @Test
    fun `implicitRid does not consume the dynamic counter`() {
        val a = RelationshipAllocator()
        a.implicitRid("styles")
        a.implicitRid("settings")
        // Dynamic still starts at rId7 — implicit slots are reserved
        // by name independent of nextId() calls.
        assertEquals("rId7", a.nextId())
    }
}
