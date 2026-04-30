// Two paragraph styles; Subheading bases on Heading. Asserts
// <w:basedOn> is emitted inline in styles.xml (no flattening) and
// that two styles can coexist in DSL-declaration order.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun } = require("/opt/docx-ref");

const doc = new Document({
  styles: {
    paragraphStyles: [
      {
        id: "Heading",
        name: "Heading",
        basedOn: "Normal",
        run: { bold: true },
      },
      {
        id: "Subheading",
        name: "Subheading",
        basedOn: "Heading",
        run: { italics: true },
      },
    ],
  },
  sections: [{ properties: {}, children: [
    new Paragraph({ style: "Heading", children: [new TextRun("Top level")] }),
    new Paragraph({ style: "Subheading", children: [new TextRun("Under it")] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));
