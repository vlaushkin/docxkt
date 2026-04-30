// Generates a minimal .docx with a single paragraph "Hello, world!" and
// writes it to the path passed as argv[2]. Runs inside the docxkt-sandbox
// container, which bakes dolanmiu/docx into /opt/docx-ref. Pin recorded in
// README.md.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun } = require("/opt/docx-ref");

const doc = new Document({
  sections: [
    {
      properties: {},
      children: [
        new Paragraph({ children: [new TextRun("Hello, world!")] }),
      ],
    },
  ],
});

Packer.toBuffer(doc).then((buf) => {
  fs.writeFileSync(process.argv[2], buf);
});
