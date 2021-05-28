package holiday.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.lowagie.text.DocumentException;

import holiday.services.PdfService;

@Controller
public class PdfController {
	
	
	 private PdfService pdfService;
	 
	 
	 
	 @Autowired
	 public void setPdfService(PdfService pdfService) {
		this.pdfService = pdfService;
	}


	@GetMapping("/downloadpdf")
	    public void downloadPDFResource(HttpServletResponse response) {
	        try {
	            Path file = Paths.get(pdfService.generatePdf().getAbsolutePath());
	            if (Files.exists(file)) {
	                response.setContentType("application/pdf");
	                response.addHeader("Content-Disposition",
	                        "attachment; filename=" + file.getFileName());
	                Files.copy(file, response.getOutputStream());
	                response.getOutputStream().flush();
	            }
	        } catch (DocumentException | IOException ex) {
	            ex.printStackTrace();
	        }
	    }

}
