// No upstream analogue — DSL scope receivers are a Kotlin idiom.
package io.docxkt.dsl

import io.docxkt.model.math.MathBrackets
import io.docxkt.model.math.MathComponent
import io.docxkt.model.math.MathFraction
import io.docxkt.model.math.MathFunction
import io.docxkt.model.math.MathLimitLocation
import io.docxkt.model.math.MathNAry
import io.docxkt.model.math.MathPreSubSuperScript
import io.docxkt.model.math.MathRadical
import io.docxkt.model.math.MathRun
import io.docxkt.model.math.MathSubScript
import io.docxkt.model.math.MathSubSuperScript
import io.docxkt.model.math.MathSuperScript

/**
 * Builder for OMML content inside `<m:oMath>`. Produces a
 * list of [MathComponent]s.
 *
 * Composable: `radical { fraction { numerator { text("a") };
 * denominator { text("b") } } }` works recursively.
 */
@DocxktDsl
public class MathScope internal constructor() {
    private val components = mutableListOf<MathComponent>()

    /** Append a math run with plain text. */
    public fun text(value: String) {
        components += MathRun(value)
    }

    /**
     * Append a square root (no degree) wrapping the
     * components built inside [configure].
     */
    public fun radical(configure: MathScope.() -> Unit) {
        val inner = MathScope().apply(configure).build()
        components += MathRadical(children = inner)
    }

    /**
     * Append an n-th root with [degree] as a string body
     * (rendered as a single `MathRun`). For full math-component
     * degrees, use the lambda-based overload.
     */
    public fun radical(degree: String, configure: MathScope.() -> Unit) {
        val inner = MathScope().apply(configure).build()
        components += MathRadical(
            children = inner,
            degree = listOf(MathRun(degree)),
        )
    }

    /**
     * Append an n-th root where the degree is built via a
     * nested `MathScope`. Use when the degree contains math
     * structures (e.g. a fraction inside the degree).
     */
    public fun radicalWithDegree(
        degreeBuilder: MathScope.() -> Unit,
        configure: MathScope.() -> Unit,
    ) {
        val degree = MathScope().apply(degreeBuilder).build()
        val inner = MathScope().apply(configure).build()
        components += MathRadical(children = inner, degree = degree)
    }

    /** Append a fraction. Numerator and denominator are
     *  configured inside [configure].
     */
    public fun fraction(configure: FractionScope.() -> Unit) {
        val scope = FractionScope().apply(configure)
        components += MathFraction(
            numerator = scope.numerator(),
            denominator = scope.denominator(),
        )
    }

    /**
     * Append a bracket-delimited group. When [begin] / [end]
     * are null (default), upstream emits an empty `<m:dPr/>`
     * and Word renders round parentheses. Set them explicitly
     * for `[ ]` / `{ }` / `〈 〉` / etc.
     */
    public fun brackets(
        begin: String? = null,
        end: String? = null,
        configure: MathScope.() -> Unit,
    ) {
        val inner = MathScope().apply(configure).build()
        components += MathBrackets(children = inner, begin = begin, end = end)
    }

    // --- Advanced math ------------------------------------------------------

    /**
     * Append an n-ary operator. Use [sum] / [product] /
     * [integral] for the common cases; this generic builder
     * lets callers pin a custom operator character or limit
     * location.
     *
     * [accentChar] = null produces no `<m:chr>` element
     * (integral-style); set to e.g. `"∑"` for sum.
     * [limitLocation] defaults to `UNDER_OVER` (typical for
     * sum / product); switch to `SUB_SUP` for integral-style
     * limit placement.
     */
    public fun nary(
        accentChar: String? = null,
        limitLocation: MathLimitLocation = MathLimitLocation.UNDER_OVER,
        subScript: (MathScope.() -> Unit)? = null,
        superScript: (MathScope.() -> Unit)? = null,
        configure: MathScope.() -> Unit,
    ) {
        val inner = MathScope().apply(configure).build()
        val sub = subScript?.let { MathScope().apply(it).build() }
        val sup = superScript?.let { MathScope().apply(it).build() }
        components += MathNAry(
            children = inner,
            subScript = sub,
            superScript = sup,
            accentChar = accentChar,
            limitLocation = limitLocation,
        )
    }

    /**
     * Convenience: summation `∑` with `<m:limLoc m:val="undOvr"/>`
     * default. [subScript] / [superScript] are text bodies
     * (rendered as single `MathRun`s); use [nary] directly for
     * complex limit expressions.
     */
    public fun sum(
        subScript: String? = null,
        superScript: String? = null,
        configure: MathScope.() -> Unit,
    ) {
        nary(
            accentChar = MathNAry.SUM_CHAR,
            limitLocation = MathLimitLocation.UNDER_OVER,
            subScript = subScript?.let { s -> { text(s) } },
            superScript = superScript?.let { s -> { text(s) } },
            configure = configure,
        )
    }

    /** Convenience: product `∏` with `undOvr` limit location. */
    public fun product(
        subScript: String? = null,
        superScript: String? = null,
        configure: MathScope.() -> Unit,
    ) {
        nary(
            accentChar = MathNAry.PRODUCT_CHAR,
            limitLocation = MathLimitLocation.UNDER_OVER,
            subScript = subScript?.let { s -> { text(s) } },
            superScript = superScript?.let { s -> { text(s) } },
            configure = configure,
        )
    }

    /**
     * Convenience: integral. No `<m:chr>` emitted (Word
     * renders the default integral glyph). Limit location
     * defaults to `subSup` (matches upstream's
     * `MathIntegral`).
     */
    public fun integral(
        subScript: String? = null,
        superScript: String? = null,
        configure: MathScope.() -> Unit,
    ) {
        nary(
            accentChar = null,
            limitLocation = MathLimitLocation.SUB_SUP,
            subScript = subScript?.let { s -> { text(s) } },
            superScript = superScript?.let { s -> { text(s) } },
            configure = configure,
        )
    }

    /**
     * Append `<m:sSup>` — base raised to a superscript.
     * Two closures: [configure] builds the base, [superScript]
     * builds the exponent. Both required.
     */
    public fun sup(
        configure: MathScope.() -> Unit,
        superScript: MathScope.() -> Unit,
    ) {
        components += MathSuperScript(
            children = MathScope().apply(configure).build(),
            superScript = MathScope().apply(superScript).build(),
        )
    }

    /** Append `<m:sSub>` — base with subscript. */
    public fun sub(
        configure: MathScope.() -> Unit,
        subScript: MathScope.() -> Unit,
    ) {
        components += MathSubScript(
            children = MathScope().apply(configure).build(),
            subScript = MathScope().apply(subScript).build(),
        )
    }

    /**
     * Append `<m:sPre>` — pre-sub and pre-super on a base
     * (tensor-style). Three closures.
     */
    public fun preSubSuper(
        configure: MathScope.() -> Unit,
        subScript: MathScope.() -> Unit,
        superScript: MathScope.() -> Unit,
    ) {
        components += MathPreSubSuperScript(
            children = MathScope().apply(configure).build(),
            subScript = MathScope().apply(subScript).build(),
            superScript = MathScope().apply(superScript).build(),
        )
    }

    /**
     * Append `<m:sSubSup>` — combined sub and super on the
     * same base.
     */
    public fun subSuper(
        configure: MathScope.() -> Unit,
        subScript: MathScope.() -> Unit,
        superScript: MathScope.() -> Unit,
    ) {
        components += MathSubSuperScript(
            children = MathScope().apply(configure).build(),
            subScript = MathScope().apply(subScript).build(),
            superScript = MathScope().apply(superScript).build(),
        )
    }

    /**
     * Append a function application with [name] as text body
     * (rendered as a single `MathRun`). For complex names
     * (e.g. `logₐ`) use the lambda overload.
     */
    public fun function(name: String, configure: MathScope.() -> Unit) {
        components += MathFunction(
            name = listOf(MathRun(name)),
            children = MathScope().apply(configure).build(),
        )
    }

    /**
     * Append a function application where the name is built
     * via a nested `MathScope`.
     */
    public fun functionWith(
        name: MathScope.() -> Unit,
        configure: MathScope.() -> Unit,
    ) {
        components += MathFunction(
            name = MathScope().apply(name).build(),
            children = MathScope().apply(configure).build(),
        )
    }

    /**
     * Append a `<m:limLow>` lower-limit expression: [base] +
     * [limit] underneath. Useful for `lim x→0` or per-symbol
     * underset annotations.
     */
    public fun limitLower(
        base: MathScope.() -> Unit,
        limit: MathScope.() -> Unit,
    ) {
        components += io.docxkt.model.math.MathLimitLower(
            base = MathScope().apply(base).build(),
            limit = MathScope().apply(limit).build(),
        )
    }

    /** Append a `<m:limUpp>` upper-limit expression. */
    public fun limitUpper(
        base: MathScope.() -> Unit,
        limit: MathScope.() -> Unit,
    ) {
        components += io.docxkt.model.math.MathLimitUpper(
            base = MathScope().apply(base).build(),
            limit = MathScope().apply(limit).build(),
        )
    }

    internal fun build(): List<MathComponent> = components.toList()
}

/**
 * Builder for a `<m:f>` fraction's numerator + denominator.
 * Both `numerator { }` and `denominator { }` must be called;
 * calling either twice replaces.
 */
@DocxktDsl
public class FractionScope internal constructor() {
    private var numeratorComponents: List<MathComponent> = emptyList()
    private var denominatorComponents: List<MathComponent> = emptyList()

    public fun numerator(configure: MathScope.() -> Unit) {
        numeratorComponents = MathScope().apply(configure).build()
    }

    public fun denominator(configure: MathScope.() -> Unit) {
        denominatorComponents = MathScope().apply(configure).build()
    }

    internal fun numerator(): List<MathComponent> = numeratorComponents
    internal fun denominator(): List<MathComponent> = denominatorComponents
}
