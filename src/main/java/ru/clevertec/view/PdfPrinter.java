package ru.clevertec.view;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.clevertec.dto.PlayerDto;
import ru.clevertec.service.PlayerService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Component
public class PdfPrinter implements Printer {
    private static final String OUTPUT_FILE = "players.pdf";
    private final String TEMPLATE_FILE = (Objects.requireNonNull(this.getClass().getClassLoader().getResource("Clevertec_Template.pdf"))).toString();

    private final PlayerService playerService;

    private final Font headerFont;
    private final Font regularFont;

    @Autowired
    public PdfPrinter(PlayerService playerService) {
        this.playerService = playerService;

        try {
            BaseFont baseFont = BaseFont.createFont(Objects.requireNonNull(this.getClass().getClassLoader().getResource("fonts/arial.ttf")).toString(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            this.headerFont = new Font(baseFont, 30, Font.BOLD);
            this.regularFont = new Font(baseFont, 20);
        } catch (DocumentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void print() {
        try {
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(OUTPUT_FILE));

            document.open();

            addTemplateToDocument(writer);
            addPlayersToDocument(document);

            document.close();
            writer.close();
        } catch (DocumentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void printPlayer(UUID id) {
        try {
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("player_report_" + id + ".pdf"));

            document.open();

            addTemplateToDocument(writer);
            addPlayerToDocument(document, id);

            document.close();
            writer.close();
        } catch (DocumentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addTemplateToDocument(PdfWriter writer) throws IOException {
        PdfContentByte underContent = writer.getDirectContentUnder();

        PdfReader reader = new PdfReader(TEMPLATE_FILE);
        PdfImportedPage page = writer.getImportedPage(reader, 1);
        underContent.addTemplate(page, 0, 0);
    }

    private void addPlayersToDocument(Document document) throws DocumentException, IOException {
        Paragraph paragraph = new Paragraph("\n".repeat(5));
        document.add(paragraph);
        Paragraph playerInfo = new Paragraph("Clevertec players:", headerFont);
        playerInfo.setSpacingAfter(80);
        playerInfo.setAlignment(Element.ALIGN_CENTER);
        document.add(playerInfo);

        for (PlayerDto player : playerService.getAll()) {
            Paragraph playerDetails = new Paragraph(
                    player.number() +
                            " - " + player.name() +
                            " " + player.surname() +
                            "(" + player.dateBirth() + ")"

                    , regularFont);
            playerDetails.setAlignment(Element.ALIGN_CENTER);
            document.add(playerDetails);
        }
    }

    private void addPlayerToDocument(Document document, UUID id) throws DocumentException, IOException {
        Paragraph paragraph = new Paragraph("\n".repeat(5));
        document.add(paragraph);
        PlayerDto player = playerService.get(id);
        if (player != null) {
            Paragraph playerInfo = new Paragraph(
                    "Player Info:\n" +
                            "Name: " + player.name() + "\n" +
                            "Surname: " + player.surname() + "\n" +
                            "Date of Birth: " + player.dateBirth() + "\n" +
                            "Number on T-shirt: " + player.number(), regularFont);

            playerInfo.setAlignment(Element.ALIGN_LEFT);
            document.add(playerInfo);
        } else {
            Paragraph notFound = new Paragraph("Player not found with ID: " + id, regularFont);
            document.add(notFound);
        }
    }
}
