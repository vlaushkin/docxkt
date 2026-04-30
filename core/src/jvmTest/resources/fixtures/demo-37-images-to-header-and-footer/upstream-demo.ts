// Demo 37 — three images in default header (image1, pizza, image1).
// Same image referenced twice gets one rel each (no dedup at rId
// level inside same part) but shares the media binary.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, Header, ImageRun } = require("/opt/docx-ref");

const image1 = fs.readFileSync("/opt/docx-ref/demo/images/image1.jpeg");
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
                                    type: "jpg",
                                    data: image1,
                                    transformation: { width: 100, height: 100 },
                                }),
                            ],
                        }),
                        new Paragraph({
                            children: [
                                new ImageRun({
                                    type: "gif",
                                    data: pizza,
                                    transformation: { width: 100, height: 100 },
                                }),
                            ],
                        }),
                        new Paragraph({
                            children: [
                                new ImageRun({
                                    type: "jpg",
                                    data: image1,
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
