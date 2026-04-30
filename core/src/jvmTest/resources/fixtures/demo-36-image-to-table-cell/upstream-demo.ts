// Demo 36 — same image-in-table-cell table in body AND header.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Header, ImageRun, Packer, Paragraph, Table, TableCell, TableRow } =
    require("/opt/docx-ref");

const table = new Table({
    rows: [
        new TableRow({
            children: [
                new TableCell({ children: [] }),
                new TableCell({ children: [] }),
                new TableCell({ children: [] }),
                new TableCell({ children: [] }),
            ],
        }),
        new TableRow({
            children: [
                new TableCell({ children: [] }),
                new TableCell({
                    children: [
                        new Paragraph({
                            children: [
                                new ImageRun({
                                    type: "jpg",
                                    data: fs.readFileSync("/opt/docx-ref/demo/images/image1.jpeg"),
                                    transformation: { width: 100, height: 100 },
                                }),
                            ],
                        }),
                    ],
                }),
            ],
        }),
        new TableRow({
            children: [
                new TableCell({ children: [] }),
                new TableCell({ children: [] }),
            ],
        }),
        new TableRow({
            children: [
                new TableCell({ children: [] }),
                new TableCell({ children: [] }),
            ],
        }),
    ],
});

const doc = new Document({
    sections: [
        {
            headers: { default: new Header({ children: [table] }) },
            children: [table],
        },
    ],
});

Packer.toBuffer(doc).then((buf) => fs.writeFileSync(process.argv[2], buf));
