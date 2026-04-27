// No upstream analogue — synthetic negative-path coverage for the
// DSL builder layer.
package io.docxkt.dsl

import io.docxkt.api.document
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Negative-path tests for the public DSL.
 *
 * The DSL is mostly forgiving — most validation happens at build
 * time, when the document is serialized — but a small set of
 * invariants throw at construction. This suite pins those throw
 * sites so future refactors don't relax them.
 */
internal class DslErrorTest {

    @Test
    fun `bookmark name uniqueness is enforced`() {
        val ex = assertFailsWith<IllegalArgumentException> {
            document {
                paragraph {
                    bookmarkStart("intro")
                    bookmarkEnd("intro")
                }
                paragraph {
                    bookmarkStart("intro")
                    bookmarkEnd("intro")
                }
            }.toByteArray()
        }
        assertTrue(
            ex.message!!.contains("already registered"),
            "expected 'already registered' in message; got: ${ex.message}",
        )
    }

    @Test
    fun `bookmarkEnd without matching start fails`() {
        val ex = assertFailsWith<IllegalStateException> {
            document {
                paragraph {
                    bookmarkEnd("nonexistent")
                }
            }.toByteArray()
        }
        assertTrue(
            ex.message!!.contains("no matching bookmarkStart"),
            "expected mention of missing start; got: ${ex.message}",
        )
    }

    @Test
    fun `empty table is rejected`() {
        assertFailsWith<IllegalArgumentException> {
            document {
                table { /* no rows */ }
            }.toByteArray()
        }
    }

    @Test
    fun `empty row is rejected`() {
        assertFailsWith<IllegalArgumentException> {
            document {
                table {
                    row { /* no cells */ }
                }
            }.toByteArray()
        }
    }

    @Test
    fun `gridSpan less than one is rejected`() {
        assertFailsWith<IllegalArgumentException> {
            document {
                table {
                    row {
                        cell {
                            gridSpan(0)
                            paragraph { text("x") }
                        }
                    }
                }
            }.toByteArray()
        }
    }

    @Test
    fun `numbering reference to undeclared list fails`() {
        val ex = assertFailsWith<IllegalStateException> {
            document {
                paragraph {
                    numbering(reference = "missing-list", level = 0)
                    text("item")
                }
            }.toByteArray()
        }
        assertTrue(
            ex.message!!.contains("no listTemplate") || ex.message!!.contains("missing-list"),
            "expected mention of missing listTemplate; got: ${ex.message}",
        )
    }
}
