// Phase 14 first fixture. A minimal doc with no `properties` /
// `settings` DSL calls — asserts that core.xml/app.xml/settings.xml/
// fontTable.xml still emit with upstream-default content (e.g.
// `dc:creator` = "Un-named", revision = "1", evenAndOddHeaders =
// "false", compatSetting w:val="15").
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [new TextRun("metadata minimal")] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));
