import Docxkt

// Internal alias for the bridged Kotlin Document so facade source files
// can refer to our own Document type unqualified.
public typealias KotlinDocument = Docxkt.Document
public typealias KotlinDocumentScope = Docxkt.DocumentScope
public typealias KotlinParagraphScope = Docxkt.ParagraphScope
public typealias KotlinRunScope = Docxkt.RunScope
public typealias KotlinTableScope = Docxkt.TableScope
public typealias KotlinTableRowScope = Docxkt.TableRowScope
public typealias KotlinTableCellScope = Docxkt.TableCellScope
public typealias KotlinHeaderScope = Docxkt.HeaderScope
public typealias KotlinFooterScope = Docxkt.FooterScope
public typealias KotlinHyperlinkScope = Docxkt.HyperlinkScope
