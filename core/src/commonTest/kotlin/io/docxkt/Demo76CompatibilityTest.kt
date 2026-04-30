package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.metadata.CompatibilityFlags
import io.docxkt.testing.DocxFixtureTest

internal class Demo76CompatibilityTest : DocxFixtureTest("demo-76-compatibility") {

    override fun build(): Document = document {
        settings {
            compatibility(
                CompatibilityFlags(
                    useSingleBorderforContiguousCells = true,
                    wordPerfectJustification = true,
                    noTabStopForHangingIndent = true,
                    noLeading = true,
                    spaceForUnderline = true,
                    noColumnBalance = true,
                    balanceSingleByteDoubleByteWidth = true,
                    noExtraLineSpacing = true,
                    doNotLeaveBackslashAlone = true,
                    underlineTrailingSpaces = true,
                    doNotExpandShiftReturn = true,
                    spacingInWholePoints = true,
                    lineWrapLikeWord6 = true,
                    printBodyTextBeforeHeader = true,
                    printColorsBlack = true,
                    spaceWidth = true,
                    showBreaksInFrames = true,
                    subFontBySize = true,
                    suppressBottomSpacing = true,
                    suppressTopSpacing = true,
                    suppressSpacingAtTopOfPage = true,
                    suppressTopSpacingWP = true,
                    suppressSpBfAfterPgBrk = true,
                    swapBordersFacingPages = true,
                    convertMailMergeEsc = true,
                    truncateFontHeightsLikeWP6 = true,
                    macWordSmallCaps = true,
                    usePrinterMetrics = true,
                    doNotSuppressParagraphBorders = true,
                    wrapTrailSpaces = true,
                    footnoteLayoutLikeWW8 = true,
                    shapeLayoutLikeWW8 = true,
                    alignTablesRowByRow = true,
                    forgetLastTabAlignment = true,
                    adjustLineHeightInTable = true,
                    autoSpaceLikeWord95 = true,
                    noSpaceRaiseLower = true,
                    doNotUseHTMLParagraphAutoSpacing = true,
                    layoutRawTableWidth = true,
                    layoutTableRowsApart = true,
                    useWord97LineBreakRules = true,
                    doNotBreakWrappedTables = true,
                    doNotSnapToGridInCell = true,
                    selectFieldWithFirstOrLastCharacter = true,
                    applyBreakingRules = true,
                    doNotWrapTextWithPunctuation = true,
                    doNotUseEastAsianBreakRules = true,
                    useWord2002TableStyleRules = true,
                    growAutofit = true,
                    useFELayout = true,
                    useNormalStyleForList = true,
                    doNotUseIndentAsNumberingTabStop = true,
                    useAlternateEastAsianLineBreakRules = true,
                    allowSpaceOfSameStyleInTable = true,
                    doNotSuppressIndentation = true,
                    doNotAutofitConstrainedTables = true,
                    autofitToFirstFixedWidthCell = true,
                    underlineTabInNumberingList = true,
                    displayHangulFixedWidth = true,
                    splitPgBreakAndParaMark = true,
                    doNotVerticallyAlignCellWithSp = true,
                    doNotBreakConstrainedForcedTable = true,
                    ignoreVerticalAlignmentInTextboxes = true,
                    useAnsiKerningPairs = true,
                    cachedColumnBalance = true,
                ),
            )
        }
        paragraph { text("Hello World") }
    }
}
