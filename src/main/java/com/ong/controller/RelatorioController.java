package com.ong.controller;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.ong.model.PessoaCarente;
import com.ong.service.AuxilioService;
import com.ong.service.PessoaCarenteService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/relatorio")
public class RelatorioController {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final Color COLOR_PRIMARY  = new Color(67, 97, 238);
    private static final Color COLOR_DARK     = new Color(26, 26, 46);
    private static final Color COLOR_SUCCESS  = new Color(10, 158, 95);
    private static final Color COLOR_LIGHT    = new Color(248, 250, 252);
    private static final Color COLOR_BORDER   = new Color(226, 232, 240);
    private static final Color COLOR_TEXT     = new Color(55, 65, 81);
    private static final Color COLOR_MUTED    = new Color(100, 116, 139);
    private static final Color COLOR_WHITE    = Color.WHITE;

    private final PessoaCarenteService pessoaCarenteService;
    private final AuxilioService auxilioService;

    public RelatorioController(PessoaCarenteService pessoaCarenteService, AuxilioService auxilioService) {
        this.pessoaCarenteService = pessoaCarenteService;
        this.auxilioService = auxilioService;
    }

    @GetMapping("/pdf")
    public void gerarRelatorioPdf(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=relatorio-acolhe.pdf");

        List<PessoaCarente> pessoas = pessoaCarenteService.listarTodos();

        Document document = new Document(PageSize.A4.rotate(), 36, 36, 50, 40);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());

        writer.setPageEvent(new PageFooter());

        document.open();

        addHeader(document, pessoas);
        addSummaryCards(document, pessoas);
        addTable(document, pessoas);

        document.close();
    }

    private void addHeader(Document doc, List<PessoaCarente> pessoas) throws DocumentException {
        Font brandFont   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, COLOR_PRIMARY);
        Font titleFont   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, COLOR_DARK);
        Font subFont     = FontFactory.getFont(FontFactory.HELVETICA, 9, COLOR_MUTED);

        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{6, 4});
        header.setSpacingAfter(18);

        PdfPCell left = new PdfPCell();
        left.setBorder(Rectangle.NO_BORDER);
        left.setPaddingBottom(8);

        Paragraph brand = new Paragraph("Acolhe+", brandFont);
        brand.setSpacingAfter(2);
        left.addElement(brand);
        left.addElement(new Paragraph("Relatório de Pessoas Cadastradas", titleFont));

        PdfPCell right = new PdfPCell();
        right.setBorder(Rectangle.NO_BORDER);
        right.setHorizontalAlignment(Element.ALIGN_RIGHT);
        right.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Paragraph dataPar = new Paragraph("Gerado em: " + LocalDate.now().format(DATE_FMT), subFont);
        dataPar.setAlignment(Element.ALIGN_RIGHT);
        right.addElement(dataPar);

        Paragraph totalPar = new Paragraph("Total de registros: " + pessoas.size(), subFont);
        totalPar.setAlignment(Element.ALIGN_RIGHT);
        right.addElement(totalPar);

        header.addCell(left);
        header.addCell(right);
        doc.add(header);

        PdfPTable divider = new PdfPTable(1);
        divider.setWidthPercentage(100);
        divider.setSpacingAfter(16);
        PdfPCell line = new PdfPCell();
        line.setBorderWidthTop(2);
        line.setBorderColorTop(COLOR_PRIMARY);
        line.setBorderWidthBottom(0);
        line.setBorderWidthLeft(0);
        line.setBorderWidthRight(0);
        line.setPadding(0);
        divider.addCell(line);
        doc.add(divider);
    }

    private void addSummaryCards(Document doc, List<PessoaCarente> pessoas) throws DocumentException {
        long recebem = pessoas.stream().filter(p -> Boolean.TRUE.equals(p.getRecebeAuxilio())).count();
        long naoRecebem = pessoas.size() - recebem;
        BigDecimal totalAuxilios = auxilioService.somarValorDistribuido();
        BigDecimal mediaRenda = pessoaCarenteService.calcularMediaRenda();

        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7, COLOR_MUTED);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, COLOR_DARK);

        PdfPTable cards = new PdfPTable(4);
        cards.setWidthPercentage(100);
        cards.setSpacingAfter(18);

        addSummaryCard(cards, "TOTAL", String.valueOf(pessoas.size()), COLOR_PRIMARY, labelFont, valueFont);
        addSummaryCard(cards, "RECEBEM AUX.", String.valueOf(recebem), COLOR_SUCCESS, labelFont, valueFont);
        addSummaryCard(cards, "SEM AUXÍLIO", String.valueOf(naoRecebem), new Color(124, 124, 151), labelFont, valueFont);
        addSummaryCard(cards, "VALOR TOTAL", "R$ " + totalAuxilios.toPlainString(), new Color(230, 111, 81), labelFont, valueFont);

        doc.add(cards);
    }

    private void addSummaryCard(PdfPTable table, String label, String value,
                                 Color accentColor, Font labelFont, Font valueFont) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(10);
        cell.setBorderColor(COLOR_BORDER);
        cell.setBackgroundColor(COLOR_LIGHT);
        cell.setBorderWidth(0.5f);

        Paragraph lPar = new Paragraph(label, labelFont);
        lPar.setSpacingAfter(4);
        cell.addElement(lPar);

        Font coloredValue = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15, accentColor);
        cell.addElement(new Paragraph(value, coloredValue));

        table.addCell(cell);
    }

    private void addTable(Document doc, List<PessoaCarente> pessoas) throws DocumentException {
        Font headFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7.5f, COLOR_WHITE);
        Font dataFont  = FontFactory.getFont(FontFactory.HELVETICA, 8f, COLOR_TEXT);
        Font mutedFont = FontFactory.getFont(FontFactory.HELVETICA, 7f, COLOR_MUTED);
        Font simFont   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7.5f, COLOR_SUCCESS);
        Font naoFont   = FontFactory.getFont(FontFactory.HELVETICA, 7.5f, COLOR_MUTED);

        String[] headers = {"Nome", "CPF", "Idade", "Telefone", "Endereço", "Depend.", "Renda Mensal", "Auxílio", "Cadastro"};
        float[] widths   = {3.5f, 2.5f, 1f, 2f, 3f, 1f, 1.8f, 1.2f, 1.5f};

        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);
        table.setWidths(widths);

        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headFont));
            cell.setBackgroundColor(COLOR_DARK);
            cell.setPadding(7);
            cell.setBorderWidth(0);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
        }

        boolean alt = false;
        for (PessoaCarente p : pessoas) {
            Color rowBg = alt ? new Color(248, 250, 252) : COLOR_WHITE;

            addCell(table, p.getNome(), dataFont, rowBg, Element.ALIGN_LEFT);
            addCell(table, p.getCpf(), mutedFont, rowBg, Element.ALIGN_LEFT);
            addCell(table, String.valueOf(p.getIdade()), dataFont, rowBg, Element.ALIGN_CENTER);
            addCell(table, p.getTelefone(), dataFont, rowBg, Element.ALIGN_LEFT);
            addCell(table, p.getEndereco() != null ? p.getEndereco() : "-", mutedFont, rowBg, Element.ALIGN_LEFT);
            addCell(table, String.valueOf(p.getQuantidadeDependentes()), dataFont, rowBg, Element.ALIGN_CENTER);
            addCell(table, "R$ " + p.getRendaMensal().toPlainString(), dataFont, rowBg, Element.ALIGN_RIGHT);

            PdfPCell statusCell = new PdfPCell();
            statusCell.setBackgroundColor(rowBg);
            statusCell.setBorderColor(COLOR_BORDER);
            statusCell.setBorderWidth(0.3f);
            statusCell.setPadding(6);
            statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            statusCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            if (Boolean.TRUE.equals(p.getRecebeAuxilio())) {
                statusCell.addElement(new Phrase("Sim", simFont));
            } else {
                statusCell.addElement(new Phrase("Não", naoFont));
            }
            table.addCell(statusCell);

            String dataCadastro = p.getDataCadastro() != null ? p.getDataCadastro().format(DATE_FMT) : "-";
            addCell(table, dataCadastro, mutedFont, rowBg, Element.ALIGN_CENTER);

            alt = !alt;
        }

        if (pessoas.isEmpty()) {
            PdfPCell empty = new PdfPCell(new Phrase("Nenhum registro encontrado.", mutedFont));
            empty.setColspan(headers.length);
            empty.setPadding(20);
            empty.setHorizontalAlignment(Element.ALIGN_CENTER);
            empty.setBorderColor(COLOR_BORDER);
            table.addCell(empty);
        }

        doc.add(table);
    }

    private void addCell(PdfPTable table, String text, Font font, Color bg, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "-", font));
        cell.setBackgroundColor(bg);
        cell.setBorderColor(COLOR_BORDER);
        cell.setBorderWidth(0.3f);
        cell.setPadding(6);
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    private static class PageFooter extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 7, COLOR_MUTED);

            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                    new Phrase("Acolhe+ — Sistema de Gestão Social", footerFont),
                    document.left(), document.bottom() - 10, 0);

            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                    new Phrase("Página " + writer.getPageNumber(), footerFont),
                    document.right(), document.bottom() - 10, 0);
        }
    }
}
