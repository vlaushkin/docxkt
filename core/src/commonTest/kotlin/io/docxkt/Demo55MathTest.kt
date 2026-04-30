package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class Demo55MathTest : DocxFixtureTest("demo-55-math") {

    override fun build(): Document = document {
        // p1: 2+2 + 1/2 fraction; trailing bold "Foo Bar" run.
        paragraph {
            math {
                text("2+2")
                fraction {
                    numerator { text("hi") }
                    denominator { text("2") }
                }
            }
            text("Foo Bar") { bold = true }
        }
        // p2: (1·√2)/2 (radical inside numerator).
        paragraph {
            math {
                fraction {
                    numerator {
                        text("1")
                        radical { text("2") }
                    }
                    denominator { text("2") }
                }
            }
        }
        // p3: ∑ test ; ∑_i e^2 ; ∑_i^10 √i.
        paragraph {
            math {
                sum { text("test") }
                sum(subScript = "i") {
                    sup({ text("e") }, { text("2") })
                }
                sum(subScript = "i", superScript = "10") {
                    radical { text("i") }
                }
            }
        }
        // p4: ∫ test ; ∫_i e^2 ; ∫_i^10 √i.
        paragraph {
            math {
                integral { text("test") }
                integral(subScript = "i") {
                    sup({ text("e") }, { text("2") })
                }
                integral(subScript = "i", superScript = "10") {
                    radical { text("i") }
                }
            }
        }
        // p5: test^hello.
        paragraph {
            math { sup({ text("test") }, { text("hello") }) }
        }
        // p6: test_hello.
        paragraph {
            math { sub({ text("test") }, { text("hello") }) }
        }
        // p7: x_(y^2) — sub with super inside sub body.
        paragraph {
            math {
                sub({ text("x") }, { sup({ text("y") }, { text("2") }) })
            }
        }
        // p8: test_world^hello — combined sub+super.
        paragraph {
            math {
                subSuper(
                    configure = { text("test") },
                    subScript = { text("world") },
                    superScript = { text("hello") },
                )
            }
        }
        // p9: pre-sub-super.
        paragraph {
            math {
                preSubSuper(
                    configure = { text("test") },
                    subScript = { text("world") },
                    superScript = { text("hello") },
                )
            }
        }
        // p10: (1/2)_4.
        paragraph {
            math {
                sub(
                    configure = {
                        fraction {
                            numerator { text("1") }
                            denominator { text("2") }
                        }
                    },
                    subScript = { text("4") },
                )
            }
        }
        // p11: ⁴√(1/2)_x.
        paragraph {
            math {
                sub(
                    configure = {
                        radicalWithDegree(
                            degreeBuilder = { text("4") },
                            configure = {
                                fraction {
                                    numerator { text("1") }
                                    denominator { text("2") }
                                }
                            },
                        )
                    },
                    subScript = { text("x") },
                )
            }
        }
        // p12: √4.
        paragraph {
            math { radical { text("4") } }
        }
        // p13: cos^-1(100) × sin(360) = x.
        paragraph {
            math {
                functionWith(
                    name = { sup({ text("cos") }, { text("-1") }) },
                    configure = { text("100") },
                )
                text("×")
                function("sin") { text("360") }
                text("= x")
            }
        }
        // p14: (1/2)[1/2]{1/2}〈1/2〉.
        paragraph {
            math {
                brackets {
                    fraction {
                        numerator { text("1") }
                        denominator { text("2") }
                    }
                }
                brackets(begin = "[", end = "]") {
                    fraction {
                        numerator { text("1") }
                        denominator { text("2") }
                    }
                }
                brackets(begin = "{", end = "}") {
                    fraction {
                        numerator { text("1") }
                        denominator { text("2") }
                    }
                }
                // U+2329 / U+232A LEFT/RIGHT-POINTING ANGLE BRACKET
                // (matches upstream MathAngledBrackets char codes —
                // distinct from the visually-identical U+3008/U+3009).
                brackets(begin = "〈", end = "〉") {
                    fraction {
                        numerator { text("1") }
                        denominator { text("2") }
                    }
                }
            }
        }
        // p15: √4 / 2a.
        paragraph {
            math {
                fraction {
                    numerator { radical { text("4") } }
                    denominator { text("2a") }
                }
            }
        }
        // p16: x with overhead "-" = lim with limit "x→0".
        paragraph {
            math {
                limitUpper(
                    base = { text("x") },
                    limit = { text("-") },
                )
                text("=")
                limitLower(
                    base = { text("lim") },
                    limit = { text("x→0") },
                )
            }
        }
    }
}
