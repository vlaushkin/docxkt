// Port of: src/file/settings/compatibility.ts (Compatibility class +
//          ICompatibilityOptions, L269-L537).
package io.docxkt.model.metadata

import io.docxkt.xml.selfClosingElement

/**
 * Legacy `<w:compat>` flags written into `word/settings.xml`. Each
 * flag is a boolean; only `true` flags emit. Emission order is
 * fixed and matches upstream's `Compatibility` constructor — when
 * upstream's emit order changes, this enum's wire mapping must
 * follow.
 *
 * Kotlin field name is `lowerCamel`; the wire name on the right
 * is the actual `<w:elementName/>` upstream emits (sometimes
 * abbreviated, e.g. `noTabStopForHangingIndent` → `noTabHangInd`).
 *
 * Each flag is plain `Boolean` (not `Boolean?`): the wire pattern
 * is "if true, emit `<w:flagName/>`; otherwise omit." Setting a
 * flag to `false` is byte-identical to leaving it unset, so
 * there is no meaningful three-state distinction at the user API.
 */
public data class CompatibilityFlags(
    val useSingleBorderforContiguousCells: Boolean = false,
    val wordPerfectJustification: Boolean = false,
    val noTabStopForHangingIndent: Boolean = false,
    val noLeading: Boolean = false,
    val spaceForUnderline: Boolean = false,
    val noColumnBalance: Boolean = false,
    val balanceSingleByteDoubleByteWidth: Boolean = false,
    val noExtraLineSpacing: Boolean = false,
    val doNotLeaveBackslashAlone: Boolean = false,
    val underlineTrailingSpaces: Boolean = false,
    val doNotExpandShiftReturn: Boolean = false,
    val spacingInWholePoints: Boolean = false,
    val lineWrapLikeWord6: Boolean = false,
    val printBodyTextBeforeHeader: Boolean = false,
    val printColorsBlack: Boolean = false,
    val spaceWidth: Boolean = false,
    val showBreaksInFrames: Boolean = false,
    val subFontBySize: Boolean = false,
    val suppressBottomSpacing: Boolean = false,
    val suppressTopSpacing: Boolean = false,
    val suppressSpacingAtTopOfPage: Boolean = false,
    val suppressTopSpacingWP: Boolean = false,
    val suppressSpBfAfterPgBrk: Boolean = false,
    val swapBordersFacingPages: Boolean = false,
    val convertMailMergeEsc: Boolean = false,
    val truncateFontHeightsLikeWP6: Boolean = false,
    val macWordSmallCaps: Boolean = false,
    val usePrinterMetrics: Boolean = false,
    val doNotSuppressParagraphBorders: Boolean = false,
    val wrapTrailSpaces: Boolean = false,
    val footnoteLayoutLikeWW8: Boolean = false,
    val shapeLayoutLikeWW8: Boolean = false,
    val alignTablesRowByRow: Boolean = false,
    val forgetLastTabAlignment: Boolean = false,
    val adjustLineHeightInTable: Boolean = false,
    val autoSpaceLikeWord95: Boolean = false,
    val noSpaceRaiseLower: Boolean = false,
    val doNotUseHTMLParagraphAutoSpacing: Boolean = false,
    val layoutRawTableWidth: Boolean = false,
    val layoutTableRowsApart: Boolean = false,
    val useWord97LineBreakRules: Boolean = false,
    val doNotBreakWrappedTables: Boolean = false,
    val doNotSnapToGridInCell: Boolean = false,
    val selectFieldWithFirstOrLastCharacter: Boolean = false,
    val applyBreakingRules: Boolean = false,
    val doNotWrapTextWithPunctuation: Boolean = false,
    val doNotUseEastAsianBreakRules: Boolean = false,
    val useWord2002TableStyleRules: Boolean = false,
    val growAutofit: Boolean = false,
    val useFELayout: Boolean = false,
    val useNormalStyleForList: Boolean = false,
    val doNotUseIndentAsNumberingTabStop: Boolean = false,
    val useAlternateEastAsianLineBreakRules: Boolean = false,
    val allowSpaceOfSameStyleInTable: Boolean = false,
    val doNotSuppressIndentation: Boolean = false,
    val doNotAutofitConstrainedTables: Boolean = false,
    val autofitToFirstFixedWidthCell: Boolean = false,
    val underlineTabInNumberingList: Boolean = false,
    val displayHangulFixedWidth: Boolean = false,
    val splitPgBreakAndParaMark: Boolean = false,
    val doNotVerticallyAlignCellWithSp: Boolean = false,
    val doNotBreakConstrainedForcedTable: Boolean = false,
    val ignoreVerticalAlignmentInTextboxes: Boolean = false,
    val useAnsiKerningPairs: Boolean = false,
    val cachedColumnBalance: Boolean = false,
) {
    /**
     * Emit each flag (in upstream emit order) as a self-closing
     * element when `true`. Skips entirely otherwise.
     */
    internal fun appendXml(out: Appendable) {
        if (useSingleBorderforContiguousCells) out.selfClosingElement("w:useSingleBorderforContiguousCells")
        if (wordPerfectJustification) out.selfClosingElement("w:wpJustification")
        if (noTabStopForHangingIndent) out.selfClosingElement("w:noTabHangInd")
        if (noLeading) out.selfClosingElement("w:noLeading")
        if (spaceForUnderline) out.selfClosingElement("w:spaceForUL")
        if (noColumnBalance) out.selfClosingElement("w:noColumnBalance")
        if (balanceSingleByteDoubleByteWidth) out.selfClosingElement("w:balanceSingleByteDoubleByteWidth")
        if (noExtraLineSpacing) out.selfClosingElement("w:noExtraLineSpacing")
        if (doNotLeaveBackslashAlone) out.selfClosingElement("w:doNotLeaveBackslashAlone")
        if (underlineTrailingSpaces) out.selfClosingElement("w:ulTrailSpace")
        if (doNotExpandShiftReturn) out.selfClosingElement("w:doNotExpandShiftReturn")
        if (spacingInWholePoints) out.selfClosingElement("w:spacingInWholePoints")
        if (lineWrapLikeWord6) out.selfClosingElement("w:lineWrapLikeWord6")
        if (printBodyTextBeforeHeader) out.selfClosingElement("w:printBodyTextBeforeHeader")
        if (printColorsBlack) out.selfClosingElement("w:printColBlack")
        if (spaceWidth) out.selfClosingElement("w:wpSpaceWidth")
        if (showBreaksInFrames) out.selfClosingElement("w:showBreaksInFrames")
        if (subFontBySize) out.selfClosingElement("w:subFontBySize")
        if (suppressBottomSpacing) out.selfClosingElement("w:suppressBottomSpacing")
        if (suppressTopSpacing) out.selfClosingElement("w:suppressTopSpacing")
        if (suppressSpacingAtTopOfPage) out.selfClosingElement("w:suppressSpacingAtTopOfPage")
        if (suppressTopSpacingWP) out.selfClosingElement("w:suppressTopSpacingWP")
        if (suppressSpBfAfterPgBrk) out.selfClosingElement("w:suppressSpBfAfterPgBrk")
        if (swapBordersFacingPages) out.selfClosingElement("w:swapBordersFacingPages")
        if (convertMailMergeEsc) out.selfClosingElement("w:convMailMergeEsc")
        if (truncateFontHeightsLikeWP6) out.selfClosingElement("w:truncateFontHeightsLikeWP6")
        if (macWordSmallCaps) out.selfClosingElement("w:mwSmallCaps")
        if (usePrinterMetrics) out.selfClosingElement("w:usePrinterMetrics")
        if (doNotSuppressParagraphBorders) out.selfClosingElement("w:doNotSuppressParagraphBorders")
        if (wrapTrailSpaces) out.selfClosingElement("w:wrapTrailSpaces")
        if (footnoteLayoutLikeWW8) out.selfClosingElement("w:footnoteLayoutLikeWW8")
        if (shapeLayoutLikeWW8) out.selfClosingElement("w:shapeLayoutLikeWW8")
        if (alignTablesRowByRow) out.selfClosingElement("w:alignTablesRowByRow")
        if (forgetLastTabAlignment) out.selfClosingElement("w:forgetLastTabAlignment")
        if (adjustLineHeightInTable) out.selfClosingElement("w:adjustLineHeightInTable")
        if (autoSpaceLikeWord95) out.selfClosingElement("w:autoSpaceLikeWord95")
        if (noSpaceRaiseLower) out.selfClosingElement("w:noSpaceRaiseLower")
        if (doNotUseHTMLParagraphAutoSpacing) out.selfClosingElement("w:doNotUseHTMLParagraphAutoSpacing")
        if (layoutRawTableWidth) out.selfClosingElement("w:layoutRawTableWidth")
        if (layoutTableRowsApart) out.selfClosingElement("w:layoutTableRowsApart")
        if (useWord97LineBreakRules) out.selfClosingElement("w:useWord97LineBreakRules")
        if (doNotBreakWrappedTables) out.selfClosingElement("w:doNotBreakWrappedTables")
        if (doNotSnapToGridInCell) out.selfClosingElement("w:doNotSnapToGridInCell")
        if (selectFieldWithFirstOrLastCharacter) out.selfClosingElement("w:selectFldWithFirstOrLastChar")
        if (applyBreakingRules) out.selfClosingElement("w:applyBreakingRules")
        if (doNotWrapTextWithPunctuation) out.selfClosingElement("w:doNotWrapTextWithPunct")
        if (doNotUseEastAsianBreakRules) out.selfClosingElement("w:doNotUseEastAsianBreakRules")
        if (useWord2002TableStyleRules) out.selfClosingElement("w:useWord2002TableStyleRules")
        if (growAutofit) out.selfClosingElement("w:growAutofit")
        if (useFELayout) out.selfClosingElement("w:useFELayout")
        if (useNormalStyleForList) out.selfClosingElement("w:useNormalStyleForList")
        if (doNotUseIndentAsNumberingTabStop) out.selfClosingElement("w:doNotUseIndentAsNumberingTabStop")
        if (useAlternateEastAsianLineBreakRules) out.selfClosingElement("w:useAltKinsokuLineBreakRules")
        if (allowSpaceOfSameStyleInTable) out.selfClosingElement("w:allowSpaceOfSameStyleInTable")
        if (doNotSuppressIndentation) out.selfClosingElement("w:doNotSuppressIndentation")
        if (doNotAutofitConstrainedTables) out.selfClosingElement("w:doNotAutofitConstrainedTables")
        if (autofitToFirstFixedWidthCell) out.selfClosingElement("w:autofitToFirstFixedWidthCell")
        if (underlineTabInNumberingList) out.selfClosingElement("w:underlineTabInNumList")
        if (displayHangulFixedWidth) out.selfClosingElement("w:displayHangulFixedWidth")
        if (splitPgBreakAndParaMark) out.selfClosingElement("w:splitPgBreakAndParaMark")
        if (doNotVerticallyAlignCellWithSp) out.selfClosingElement("w:doNotVertAlignCellWithSp")
        if (doNotBreakConstrainedForcedTable) out.selfClosingElement("w:doNotBreakConstrainedForcedTable")
        if (ignoreVerticalAlignmentInTextboxes) out.selfClosingElement("w:doNotVertAlignInTxbx")
        if (useAnsiKerningPairs) out.selfClosingElement("w:useAnsiKerningPairs")
        if (cachedColumnBalance) out.selfClosingElement("w:cachedColBalance")
    }
}
