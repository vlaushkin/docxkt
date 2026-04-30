// Phase 26 mixed/nested math fixture. Round brackets
// surrounding (a + √b) over c — a fraction whose
// numerator is a bracketed expression containing a
// radical.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const {
  Document, Packer, Paragraph,
  Math, MathRun, MathFraction, MathRadical, MathRoundBrackets,
} = require("/opt/docx-ref");

const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new Math({ children: [
        new MathFraction({
          numerator: [
            new MathRoundBrackets({
              children: [
                new MathRun("a + "),
                new MathRadical({ children: [new MathRun("b")] }),
              ],
            }),
          ],
          denominator: [new MathRun("c")],
        }),
      ] }),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));
