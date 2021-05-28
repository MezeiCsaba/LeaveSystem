package holiday.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;

import holiday.entity.User;

@Service
public class PdfService {

	private SpringTemplateEngine templateEngine;
	private UserService userService;
	private EventService eventService;

	
	@Autowired
	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Autowired
	public void setTemplateEngine(SpringTemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
	}
	@Autowired
	public PdfService() {

	}

	public File generatePdf() throws IOException, DocumentException {
		Context context = getContext();
		String html = loadAndFillTemplate(context);
		return renderPdf(html);
	}

	private File renderPdf(String html) throws IOException, DocumentException {
		File file = File.createTempFile("usertable", ".pdf");
		OutputStream outputStream = new FileOutputStream(file);
		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocumentFromString(html);
		renderer.layout();
		renderer.createPDF(outputStream);
		outputStream.close();
		file.deleteOnExit();
		return file;
	}

	private Context getContext() {
		Context context = new Context();

		List<Integer> lengthOfMonthList = new ArrayList<>();
		List<User> users = userService.getAllUser();

		for (int i = 1; i < 13; i++) { // hónapok hossza
			LocalDate month = LocalDate.of(LocalDate.now().getYear(), i, 1);
			int length = month.lengthOfMonth();
			lengthOfMonthList.add(length);
		}

		Map<Long, Map<LocalDate, Integer>> eventMap = new HashMap<>(); // a userek szabadságai
		users.forEach(user -> eventMap.put(user.getId(), eventService.googleEventTable(user.getId())));

		Integer thisYear = LocalDate.now().getYear();

		context.setVariable("thisYear", thisYear);
		context.setVariable("users", users);
		context.setVariable("usersEvents", eventMap);
		context.setVariable("lengthOfMonthList", lengthOfMonthList);

		return context;
	}

	private String loadAndFillTemplate(Context context) {
		return templateEngine.process("pdf_userstable", context);
	}

}
