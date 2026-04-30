// Demo 9 — images in default header AND footer.
// Verifies per-part image rels: header1.xml.rels and
// footer1.xml.rels each carry their own image rId.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, Header, Footer, ImageRun } = require("/opt/docx-ref");

const pizza = fs.readFileSync("/opt/docx-ref/demo/images/pizza.gif");

const doc = new Document({
    sections: [
        {
            headers: {
                default: new Header({
                    children: [
                        new Paragraph({
                            children: [
                                new ImageRun({
                                    type: "gif",
                                    data: pizza,
                                    transformation: { width: 100, height: 100 },
                                }),
                            ],
                        }),
                    ],
                }),
            },
            footers: {
                default: new Footer({
                    children: [
                        new Paragraph({
                            children: [
                                new ImageRun({
                                    type: "gif",
                                    data: pizza,
                                    transformation: { width: 100, height: 100 },
                                }),
                            ],
                        }),
                    ],
                }),
            },
            children: [new Paragraph("Hello World")],
        },
    ],
});

Packer.toBuffer(doc).then((buf) => fs.writeFileSync(process.argv[2], buf));
