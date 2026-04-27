// Phase 24 third fixture. Checkbox with custom Wingdings
// symbols overriding the default MS Gothic 2612/2610.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, CheckBox } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new CheckBox({
        checked: true,
        alias: "Confirm",
        checkedState: { value: "F0FE", font: "Wingdings" },
        uncheckedState: { value: "F0A8", font: "Wingdings" },
      }),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));
