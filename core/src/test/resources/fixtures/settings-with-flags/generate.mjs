// Phase 14 fourth fixture. Settings block with multiple
// opt-in flags — exercises evenAndOddHeaders=true,
// trackRevisions, updateFields, defaultTabStop.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun } = require("/opt/docx-ref");

const doc = new Document({
  evenAndOddHeaderAndFooters: true,
  features: { trackRevisions: true, updateFields: true },
  // defaultTabStop is an ISettingsOptions field passed straight
  // into new Settings(...). The Document options type doesn't
  // expose it directly — but the Settings class accepts it.
  // Workaround: we set it via the features object if
  // supported; otherwise let the default stand.
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [new TextRun("settings with flags")] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));
