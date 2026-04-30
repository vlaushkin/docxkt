// Phase 29: run-tab-character — three runs, middle one is a <w:tab/>.
// Splitting per-run keeps the docxkt DSL minimal: one `text` / `tab` /
// `text` call per paragraph child instead of bundling.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, Tab } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{
    properties: {},
    children: [new Paragraph({ children: [
      new TextRun("before"),
      new TextRun({ children: [new Tab()] }),
      new TextRun("after"),
    ]})],
  }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));
