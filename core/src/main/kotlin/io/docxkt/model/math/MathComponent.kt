// Port of: src/file/paragraph/math/math-component.ts.
package io.docxkt.model.math

import io.docxkt.xml.XmlComponent

/**
 * Marker interface for OMML elements that can appear inside
 * `<m:oMath>` / `<m:e>` / `<m:num>` / `<m:den>` / `<m:deg>`
 * etc. — i.e. any math-content holder.
 *
 * Implementations: [MathRun], [MathRadical], [MathFraction],
 * [MathBrackets], plus advanced math types.
 *
 * Combined with [XmlComponent] via interface delegation —
 * concrete classes extend `XmlComponent("m:…")` and implement
 * `MathComponent` to opt into the math-children typing.
 */
internal interface MathComponent
