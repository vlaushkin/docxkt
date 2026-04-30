// Hyperlink whose single run carries a character-style reference
// (`Hyperlink`, defined inline in the same document via Phase 11).
// Exercises Phase 11 × Phase 12: styles.xml holds the character
// style, document.xml's <w:hyperlink> wraps a run whose <w:rPr>
// carries <w:rStyle w:val="Hyperlink"/>.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, ExternalHyperlink } = require("/opt/docx-ref");

const doc = new Document({
  styles: {
    characterStyles: [{
      id: "Hyperlink",
      name: "Hyperlink",
      run: { color: "0563C1", underline: { type: "single" } },
    }],
  },
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new ExternalHyperlink({
        link: "https://example.com",
        children: [new TextRun({ text: "example.com", style: "Hyperlink" })],
      }),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));
